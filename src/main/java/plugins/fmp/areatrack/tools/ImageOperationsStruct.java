package plugins.fmp.areatrack.tools;

import java.awt.Color;
import java.util.ArrayList;

public class ImageOperationsStruct {

	int fromFrame = -1;
	EnumColorDistanceType colordistanceType = EnumColorDistanceType.L1;
	boolean thresholdUp = true;
	int simplethreshold = 255;
	int colorthreshold = 0;
	ArrayList<Color> colorarray = null;
	EnumImageOp transformop = EnumImageOp.NONE;
	EnumAreaDetection thresholdtype = EnumAreaDetection.NONE;

	// -----------------------------------

	public ImageOperationsStruct() {
		this.fromFrame = -1;
		this.transformop = EnumImageOp.NONE;
		this.thresholdtype = EnumAreaDetection.NONE;
		this.colorthreshold = 0;
	}

	public ImageOperationsStruct(int framenumber, EnumImageOp transformop, EnumAreaDetection thresholdtype,
			boolean thresholdUp, int thresholdvalue) {
		this.fromFrame = framenumber;
		this.transformop = transformop;
		this.thresholdtype = thresholdtype;
		this.thresholdUp = thresholdUp;
		this.colorthreshold = thresholdvalue;
	}

	public ImageOperationsStruct(int framenumber, EnumImageOp transformop) {
		this.fromFrame = framenumber;
		this.transformop = transformop;
		this.thresholdtype = EnumAreaDetection.NONE;
		this.colorthreshold = 0;
	}

	public boolean isValidTransformCache(ImageOperationsStruct op) {
		if (op.fromFrame != this.fromFrame)
			return false;

		if (op.transformop != this.transformop)
			return false;
		return true;
	}

	public void copyTransformOpTo(ImageOperationsStruct op) {
		op.transformop = transformop;
		op.fromFrame = fromFrame;
	}

	public void copyThresholdOpTo(ImageOperationsStruct op) {

		op.thresholdtype = thresholdtype;
		if (thresholdtype == EnumAreaDetection.SINGLE) {
			op.simplethreshold = simplethreshold;
			op.thresholdUp = thresholdUp;
		} else if (thresholdtype == EnumAreaDetection.COLORARRAY) {
			op.colorthreshold = colorthreshold;
			if (op.colorarray == null)
				op.colorarray = new ArrayList<Color>();
			else
				op.colorarray.clear();
			for (Color c : colorarray)
				op.colorarray.add(c);
			op.colordistanceType = colordistanceType;
		}
		op.fromFrame = fromFrame;
	}

	public boolean isValidThresholdCache(ImageOperationsStruct op) {
		if (op.fromFrame != this.fromFrame)
			return false;

		if (op.thresholdtype != this.thresholdtype)
			return false;

		if (op.thresholdtype == EnumAreaDetection.COLORARRAY) {
			if (op.colorthreshold != this.colorthreshold)
				return false;
			if (op.colordistanceType != this.colordistanceType)
				return false;
			if (op.colorarray.size() != this.colorarray.size())
				return false;
		} else {
			if (op.simplethreshold != this.simplethreshold)
				return false;
			if (op.thresholdUp != this.thresholdUp)
				return false;
		}
		return true;
	}
}
