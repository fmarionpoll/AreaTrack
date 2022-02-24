package plugins.fmp.areatrack;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.swing.SwingWorker;

import icy.canvas.Canvas2D;
import icy.gui.frame.progress.ProgressFrame;
import icy.gui.viewer.Viewer;
import icy.image.IcyBufferedImage;
import icy.image.colormap.JETColorMap;
import icy.main.Icy;
import icy.roi.BooleanMask2D;
import icy.roi.ROI2D;

import icy.sequence.Sequence;
import icy.sequence.SequenceDataIterator;
import icy.system.profile.Chronometer;
import icy.system.thread.Processor;
import icy.type.DataType;
import icy.type.collection.array.Array1DUtil;
import plugins.fmp.fmpSequence.SequenceVirtual;
import plugins.fmp.fmpTools.FmpTools;
import plugins.fmp.fmpTools.EnumColorDistanceType;
import plugins.fmp.fmpTools.EnumImageOp;
import plugins.fmp.fmpTools.ImageOperations;




public class AreaAnalysisThread extends SwingWorker<Integer, Integer>  {
	
	public	boolean 			stopFlag 		= false;
	public 	boolean 			threadRunning 	= false;
	
	SequenceVirtual vSequence = null;
	private ArrayList<ROI2D> roiList = null;

	private int startFrame = 0;
	private int endFrame = 1;
	private int analyzeStep = 1;
	private boolean measureROIsEvolution = false;
	private boolean measureROIsMove = false;
//	private int thresholdForHeatMap = 230;
	
	public IcyBufferedImage resultOFFImage = null;
	public Sequence resultOFFSequence = null;
	public Viewer 	resultOFFViewer = null;
	public Canvas2D resultOFFCanvas = null;
	
	public IcyBufferedImage resultONImage = null;
	public Sequence resultONSequence = null;
	public Viewer 	resultONViewer = null;
	public Canvas2D resultONCanvas = null;
	
	public ArrayList<MeasureAndName> results = null;
	
	private ImageOperations imgOp1;
	private ImageOperations imgOp2;
	 
	// --------------------------------------------------------------------------------------
	
	public void initAreaDetectionFromFunction(SequenceVirtual sequenceVirtual, 
			ArrayList<ROI2D> roiList,  
			EnumImageOp transformOpForOperation1, int thresholdForSurface) {
		
		vSequence = sequenceVirtual;
		this.roiList = roiList;
		this.startFrame = vSequence.analysisStart;
		this.endFrame = vSequence.analysisEnd;
		
		imgOp1 = new ImageOperations (sequenceVirtual);
		imgOp1.setTransform(transformOpForOperation1);
		
		imgOp1.setThresholdToSingleValue(thresholdForSurface);
		measureROIsEvolution = true;
	}
	
	public void initAreaDetectionFromColors(SequenceVirtual sequenceVirtual,   
			ArrayList<ROI2D> roiList,  
			EnumColorDistanceType distanceType, int colorthreshold, ArrayList<Color> colorarray) {
		
		vSequence = sequenceVirtual;
		this.roiList = roiList;
		this.startFrame = vSequence.analysisStart;
		this.endFrame = vSequence.analysisEnd;
		
		imgOp1 = new ImageOperations (sequenceVirtual);
		imgOp1.setTransform(EnumImageOp.NONE);
		
		imgOp1.setThresholdToColorArray(colorarray, distanceType, colorthreshold);
		measureROIsEvolution = true;
	}
	
	public void initMovementDetection(SequenceVirtual sequenceVirtual,   
			ArrayList<ROI2D> roiList,
			int thresholdForHeatMap) {
		prepareImagesForMovementDetection (sequenceVirtual, roiList, thresholdForHeatMap);
		measureROIsMove = true;
	}
		
	@Override
	protected Integer doInBackground() throws Exception 
	{
		threadRunning = true;
		analyzeSequence();
		threadRunning = false;
		return 1;
	}
	
	
	protected void analyzeSequence()  
	{	
		// global parameters
		analyzeStep = vSequence.analysisStep;
		roiList = vSequence.seq.getROI2Ds();
		Collections.sort(roiList, new FmpTools.ROI2DNameComparator());
		if ( vSequence.nTotalFrames < endFrame+1 )
			endFrame = (int) vSequence.nTotalFrames - 1;
		int nbframes = (int) (endFrame - startFrame +1);
		int nrois = roiList.size();
		vSequence.data_raw = new int [nrois][nbframes];
		ArrayList<BooleanMask2D> areaMasks = getMasksFromRois();

		System.out.println("Computation over frames: " + startFrame + " - " + endFrame );
		Chronometer chrono = new Chronometer("Tracking computation" );
		ProgressFrame progress = new ProgressFrame("Anayze images...");
		
		Viewer viewer = Icy.getMainInterface().getFirstViewer(vSequence.seq);
		if (!measureROIsEvolution)
			viewer = resultOFFViewer;
		vSequence.seq.beginUpdate();
		
//		final Processor processor = new Processor(SystemUtil.getNumberOfCPUs());
//	    processor.setThreadName("buildkymo2");
//	    processor.setPriority(Processor.NORM_PRIORITY);
//        ArrayList<Future<?>> futuresArray = new ArrayList<Future<?>>(nbframes);
//		futuresArray.clear();
//		ProgressFrame progressBar = new ProgressFrame("Processing with subthreads started");
		
		// loop over all images
		for (int iiframe = startFrame ; iiframe <= endFrame; iiframe  += analyzeStep ) 
		{				
			final int iframe = iiframe;	
			
//			futuresArray.add(processor.submit(new Runnable () {
//				@Override
//				public void run() {						
//					IcyBufferedImage sourceImage = vSequence.imageIORead(vSequence.getFileName(iframe));
					
					viewer.setPositionT(iframe);
					vSequence.currentFrame = iframe;
					updateDisplay (iframe, nbframes, chrono, progress);
		
					if (measureROIsEvolution) 
					{
						IcyBufferedImage binaryMap = imgOp1.run(iframe);	
						boolean[] boolMap = imgOp1.convertToBoolean(binaryMap);
						BooleanMask2D maskAll2D = new BooleanMask2D(binaryMap.getBounds(), boolMap); 
						for (int iiroi = 0; iiroi < areaMasks.size(); iiroi++ )
						{
							BooleanMask2D areaMask = areaMasks.get(iiroi);
							BooleanMask2D intersectionMask = maskAll2D.getIntersection( areaMask );
							int sum = intersectionMask.getNumberOfPoints();
							int index = iframe - startFrame;
							vSequence.data_raw[iiroi][index] = sum;
						}
					}
					
					if (measureROIsMove && iframe < startFrame+20) 
					{
						IcyBufferedImage binaryMap = imgOp2.run_nocache();
						int [] binaryArray = Array1DUtil.arrayToIntArray(binaryMap.getDataXY(0), binaryMap.isSignedDataType());
						double [] resultOFFArray = resultOFFImage.getDataXYAsDouble(0);
						double [] resultONArray = resultONImage.getDataXYAsDouble(0);
						for (int i= 0; i< binaryArray.length; i++) 
						{
							if (binaryArray[i] == 0)
								resultOFFArray[i] += 1;
							else
								resultONArray[i] += 1;
						}
					}
//				}}));
		}
//		waitFuturesCompletion(processor, futuresArray, progressBar);
		
		progress.close();
		vSequence.seq.endUpdate();
		
		if (measureROIsMove) 
			detectMovements(areaMasks);
		
		chrono.displayInSeconds();
		System.out.println("Computation finished.");
	}
	
	private ArrayList<BooleanMask2D> getMasksFromRois() {
		ArrayList<BooleanMask2D> areaMasks = new ArrayList<BooleanMask2D>();
		int iroi = 0;
		int nrois = roiList.size();
		vSequence.seriesname = new String[nrois];
		for (ROI2D roi: roiList)
		{
			String csName = roi.getName();
			vSequence.seriesname[iroi] = csName;
			areaMasks.add(roi.getBooleanMask2D( 0 , 0, 1, true ));
			iroi++;
		}
		return areaMasks;
	}
	
	void updateDisplay(int t, int nbframes, Chronometer chrono, ProgressFrame progress) {
		
		int pos = (int)(100d * (double)t / (double) nbframes);
		progress.setPosition( pos );
		int nbSeconds =  (int) (chrono.getNanos() / 1000000000f);
		int timeleft = (int) ((nbSeconds* nbframes /(t+1)) - nbSeconds);
		progress.setMessage( "Processing: " + pos + " % - Elapsed time: " + nbSeconds + " s - Estimated time left: " + timeleft + " s");
	}
	
	private void detectMovements(ArrayList<BooleanMask2D> areaMasks) {
		// update resultIlmage to make it like a regular image and add heatmap scale to it
		resultOFFImage.dataChanged();
		resultOFFImage.setColorMap (0, new JETColorMap (), true);
		resultOFFViewer.setVisible(true);
		resultOFFSequence.removeAllROI();
		resultOFFSequence.addROIs(vSequence.seq.getROI2Ds(), false);
		
		resultONImage.dataChanged();
		resultONImage.setColorMap (0, new JETColorMap (), true);
		resultONViewer.setVisible(true);
		resultONSequence.removeAllROI();
		resultONSequence.addROIs(vSequence.seq.getROI2Ds(), false);
		
		ArrayList<ROI2D> roiList2 = resultOFFSequence.getROI2Ds();
		
		// ------ get big sum
		double sumall = 0;
		double countall = 0;
		int cmax = resultOFFImage.getSizeC();
		for (int c=0; c< cmax; c++) {
			double[] resultDoubleArray = Array1DUtil.arrayToDoubleArray(resultONImage.getDataXY(c), resultONImage.isSignedDataType());
			for (int i=0; i< resultDoubleArray.length; i++) {
				sumall += resultDoubleArray[i];
			}
			countall += resultDoubleArray.length;
		}

		for (ROI2D roi: roiList2)
			areaMasks.add(roi.getBooleanMask2D( 0 , 0, 1, true ));

		// ------------------------ loop over all the cages of the stack & count n pixels above threshold
		results = new ArrayList<MeasureAndName> ();
		for (ROI2D roi: roiList2) {
			SequenceDataIterator iterator = new SequenceDataIterator(resultOFFSequence, roi, true, 0, 0 , -1);
			double sum = 0;
			double sample = 0;
			while (!iterator.done()) {
				sum += iterator.get();
				iterator.next();
				sample++;
			}
			sumall -= sum;
			countall -= sample;
			results.add(new MeasureAndName(roi.getName(), sum, sample));
		}
		results.add(new MeasureAndName("background", sumall, countall));
		
		// compute movements over the rest of the image and store it as reference
	}

	private void prepareImagesForMovementDetection (SequenceVirtual sequenceVirtual, ArrayList<ROI2D> roiList,
			int thresholdForHeatMap) {
		
		imgOp2 = new ImageOperations (sequenceVirtual);
		imgOp2.setTransform(EnumImageOp.REF_PREVIOUS);
		imgOp2.setThresholdToSingleValue(thresholdForHeatMap);
		
		IcyBufferedImage image = vSequence.seq.getImage(vSequence.currentFrame, 0);
		resultOFFImage = new IcyBufferedImage(image.getSizeX(), image.getSizeY(), 1, DataType.DOUBLE);
		resultOFFSequence = new Sequence(resultOFFImage);
		resultOFFSequence.setName("Heatmap OFF thresh:"+ thresholdForHeatMap);
		resultOFFViewer = new Viewer(resultOFFSequence, false);
		resultOFFCanvas = new Canvas2D(resultOFFViewer);
		
		resultONImage = new IcyBufferedImage(image.getSizeX(), image.getSizeY(), 1, DataType.DOUBLE);
		resultONSequence = new Sequence(resultONImage);
		resultONSequence.setName("Heatmap ON thresh:"+ thresholdForHeatMap);
		resultONViewer = new Viewer(resultONSequence, false);
		resultONCanvas = new Canvas2D(resultONViewer);
	}
	
	protected void waitFuturesCompletion(Processor processor, ArrayList<Future<?>> futuresArray,  ProgressFrame progressBar) 
	{  	
		int frame= 1;
		int nframes = futuresArray.size();
		while (!futuresArray.isEmpty())
		{
			final Future<?> f = futuresArray.get(futuresArray.size() - 1);
			if (progressBar != null)
				 progressBar.setMessage("Analyze frame: " + (frame) + "//" + nframes);
			try
			{
			     f.get();
			}
			catch (ExecutionException e)
			{
			     System.out.println("series analysis - Warning: " + e);
			}
			catch (InterruptedException e)
			{
			// ignore
			}
			futuresArray.remove(f);
			frame ++;
		}
	}

	@Override
	protected void done() 
	{
		int statusMsg = 0;
		try 
		{
			statusMsg = get();
		} 
		catch (InterruptedException | ExecutionException e) 
		{
			e.printStackTrace();
		} 
		if (!threadRunning || stopFlag) 
		{
			firePropertyChange("thread_ended", null, statusMsg);
		} 
		else 
		{
			firePropertyChange("thread_done", null, statusMsg);
		}
    }

}
