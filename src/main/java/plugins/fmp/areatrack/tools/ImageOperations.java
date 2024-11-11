package plugins.fmp.areatrack.tools;

import java.awt.Color;
import java.util.ArrayList;

import icy.image.IcyBufferedImage;
import plugins.fmp.areatrack.sequence.SequenceVirtual;

public class ImageOperations {

	private SequenceVirtual seq = null;
	private ImageOperationsStruct opTransf = new ImageOperationsStruct();
	private ImageOperationsStruct opThresh = new ImageOperationsStruct();
	private ImageTransformTools imgTransf = new ImageTransformTools();
	private ImageThresholdTools imgThresh = new ImageThresholdTools();

	public ImageOperations(SequenceVirtual seq) {
		setSequence(seq);
	}

	public void setSequence(SequenceVirtual seq) {
		this.seq = seq;
		imgTransf.setSequence(seq);
	}

	public void setTransform(EnumImageOp transformop) {
		opTransf.transformop = transformop;
	}

	public void setThresholdToSingleValue(int threshold, boolean thresholdUp) {
		opThresh.thresholdtype = EnumAreaDetection.SINGLE;
		opThresh.simplethreshold = threshold;
		opThresh.thresholdUp = thresholdUp;
		imgThresh.setSingleThreshold(threshold, thresholdUp);
	}

	public void setThresholdToColorArray(ArrayList<Color> colorarray, EnumColorDistanceType distanceType,
			int colorthreshold) {
		opThresh.thresholdtype = EnumAreaDetection.COLORARRAY;
		opThresh.colorarray = colorarray;
		opThresh.colordistanceType = distanceType;
		opThresh.colorthreshold = colorthreshold;
		imgThresh.setColorArrayThreshold(distanceType, colorthreshold, colorarray);
	}

	public IcyBufferedImage run(int frame) {
		// step 1
		opTransf.fromFrame = frame;
		if (seq.cacheTransformedImage == null || !opTransf.isValidTransformCache(seq.cacheTransformOp)) {
			seq.cacheTransformedImage = imgTransf.transformImageFromVirtualSequence(frame, opTransf.transformop);
			if (seq.cacheTransformedImage == null) {
				return null;
			}
			opTransf.copyTransformOpTo(seq.cacheTransformOp);
			seq.cacheThresholdOp.fromFrame = -1;
		}

		// step 2
		opThresh.fromFrame = frame;
		if (!opThresh.isValidThresholdCache(seq.cacheThresholdOp)) {
			if (opThresh.thresholdtype == EnumAreaDetection.COLORARRAY)
				seq.cacheThresholdedImage = imgThresh.getBinaryInt_FromColorsThreshold(seq.cacheTransformedImage);
			else
				seq.cacheThresholdedImage = imgThresh.getBinaryInt_FromThreshold(seq.cacheTransformedImage);
			opThresh.copyThresholdOpTo(seq.cacheThresholdOp);
		}
		return seq.cacheThresholdedImage;
	}

	public IcyBufferedImage run_nocache() {
		// step 1
		int frame = seq.currentFrame;
		IcyBufferedImage transformedImage = imgTransf.transformImageFromVirtualSequence(frame, opTransf.transformop);
		if (transformedImage == null)
			return null;

		// step 2
		IcyBufferedImage thresholdedImage;
		if (opThresh.thresholdtype == EnumAreaDetection.COLORARRAY)
			thresholdedImage = imgThresh.getBinaryInt_FromColorsThreshold(transformedImage);
		else
			thresholdedImage = imgThresh.getBinaryInt_FromThreshold(transformedImage);

		return thresholdedImage;
	}

	public boolean[] convertToBoolean(IcyBufferedImage binaryMap) {
		return imgThresh.getBoolMap_FromBinaryInt(binaryMap);
	}
}
