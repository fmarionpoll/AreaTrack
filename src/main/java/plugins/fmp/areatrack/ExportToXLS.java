package plugins.fmp.areatrack;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import icy.roi.ROI2D;
import icy.util.XLSUtil;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import plugins.fmp.fmpSequence.SequencePlus;
import plugins.fmp.fmpTools.FmpTools;

public class ExportToXLS {

	Areatrack areatrack = null;
	SequencePlus vSequence = null;
	int startFrame= 0;
	int endFrame = 1;
	int span = 1;
	int	analyzeStep = 1;
	String distanceString;
	String threshold2String;
	boolean detectMovement = false;
	private ArrayList<MeasureAndName> resultsHeatMap = null;
	

	private void exportToXLSWorksheet(WritableWorkbook xlsWorkBook, String worksheetname) {
		
		int it = 0;
		int irow = 0;
		int nrois = vSequence.data_filtered.length;
		int icol0 = 0;
		List<String> listofFiles = null;
		boolean blistofFiles = false;
		if (vSequence.isFileStack() )
		{
			listofFiles = vSequence.getListofFiles();
			blistofFiles = true;
		}
		
		// xls output
		// --------------
		WritableSheet filteredDataPage = XLSUtil.createNewPage( xlsWorkBook , worksheetname );
		XLSUtil.setCellString( filteredDataPage , 0, irow, "name:" );
		XLSUtil.setCellString( filteredDataPage , 1, irow, vSequence.seq.getName() );
		// write  type of data exported
		irow++;
		String cs = worksheetname;
		if (!worksheetname.contains("raw")) {
			cs = cs + " - over " + span +" points - ";
		}
		XLSUtil.setCellString(filteredDataPage,  0,  irow, worksheetname);
		// write filter and threshold applied
		irow++;
		//cs = "Detect surface: "+ transformsComboBox.getSelectedItem().toString() + " threshold=" + distance.getValue().toString();
		cs = "Detect surface: colors array with distance=" + distanceString;
		XLSUtil.setCellString(filteredDataPage,  0,  irow, cs);	
		irow++;
		cs = "Detect movement using image (n) - (n-1) threshold=" + threshold2String;
		XLSUtil.setCellString(filteredDataPage,  0,  irow, cs);	
		// write table
		irow=4;
		// table header
		icol0 = 0;
		if (blistofFiles) icol0 = 1;
		
		XLSUtil.setCellString( filteredDataPage , icol0, irow, "index" );
		icol0++;
		int icol1 = icol0;
		ArrayList<ROI2D> roisList = vSequence.seq.getROI2Ds();
		XLSUtil.setCellString( filteredDataPage, 0, irow, "column");
		XLSUtil.setCellString( filteredDataPage, 0, irow+1, "roi surface (pixels)");
		Collections.sort(roisList, new FmpTools.ROI2DNameComparator());
		for (ROI2D roi: roisList) {
			XLSUtil.setCellString( filteredDataPage, icol1, irow, roi.getName());
			XLSUtil.setCellNumber( filteredDataPage, icol1, irow+1, roi.getNumberOfPoints());
			icol1++;
		}
		
		if (detectMovement ) {
			icol1 = icol0;
			XLSUtil.setCellString( filteredDataPage, 0, irow+2, "column");
			XLSUtil.setCellString( filteredDataPage, 0, irow+3, "activity(npixels>"+threshold2String+")");
			XLSUtil.setCellString( filteredDataPage, 0, irow+4, "count");
			for (MeasureAndName result: resultsHeatMap) {
				if (result.name != "background") {
					XLSUtil.setCellString( filteredDataPage, icol1, irow+2, result.name);
					XLSUtil.setCellNumber( filteredDataPage, icol1, irow+3, result.data/result.count);
					XLSUtil.setCellNumber( filteredDataPage, icol1, irow+4, result.count);
					icol1++;
				}
				else {
					XLSUtil.setCellString( filteredDataPage, icol0-1, irow+2, result.name);
					XLSUtil.setCellNumber( filteredDataPage, icol0-1, irow+3, result.data/result.count);
					XLSUtil.setCellNumber( filteredDataPage, icol0-1, irow+4, result.count);
				}
			}		
		}
		
		icol1 = icol0;
		irow+=7;
		if (blistofFiles)
			XLSUtil.setCellString( filteredDataPage , 0, irow, "name");
		for (int iroi=0; iroi < nrois; iroi++, icol1++) 
			XLSUtil.setCellString( filteredDataPage , icol1, irow, vSequence.seriesname[iroi]);
		irow++;

		// data
		it = 1;
		for ( int t = startFrame ; t <= endFrame;  t += analyzeStep, it++, irow++ )
		{
			icol0 = 0;
			if (blistofFiles) {
				XLSUtil.setCellString( filteredDataPage , icol0,   irow, listofFiles.get(it) );
				icol0++;
			}
			double value = t; 
			XLSUtil.setCellNumber( filteredDataPage, icol0 , irow , value ); 
			icol0++;
			for (int iroi=0; iroi < nrois; iroi++, icol0++) 
			{
				value = vSequence.data_filtered[iroi][t-startFrame];
				XLSUtil.setCellNumber( filteredDataPage, icol0 , irow , value ); 
			}
		}
	}
	
	public void exportToXLS(Areatrack areatrack, String filename) {
		
		this.areatrack = areatrack;
		vSequence = areatrack.vSequence;
		startFrame = vSequence.analysisStart;
		endFrame = vSequence.analysisEnd;
		if (areatrack.dlg5AnalysisRun.analysisThread != null)
			resultsHeatMap = areatrack.dlg5AnalysisRun.analysisThread.results;
		span = Integer.parseInt(areatrack.dlg6ResultsExport.spanTextField.getText());
		analyzeStep = vSequence.analysisStep;
		distanceString = String.valueOf(areatrack.detectionParameters.colorthreshold);
		threshold2String = String.valueOf(areatrack.detectionParameters.thresholdmovement);
		detectMovement = areatrack.detectionParameters.detectMovement;
		
		System.out.println("XLS output");
		try {
			WritableWorkbook xlsWorkBook = XLSUtil.createWorkbook( filename);
			FilterTimeSeries.filterMeasures (vSequence, 0, span);
			exportToXLSWorksheet(xlsWorkBook, "raw");
			if (span / 2 < (endFrame - startFrame)) 
			{
				FilterTimeSeries.filterMeasures (vSequence, 1, span);
				exportToXLSWorksheet(xlsWorkBook, "avg");
				FilterTimeSeries.filterMeasures (vSequence, 2, span);
				exportToXLSWorksheet(xlsWorkBook, "median");
			}

			// --------------
			XLSUtil.saveAndClose( xlsWorkBook );
		} catch (IOException e) {
			e.printStackTrace();
		} catch (WriteException e) {
			e.printStackTrace();
		}
		System.out.println("XLS output done");
	}

}
