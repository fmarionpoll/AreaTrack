package plugins.fmp.fmpSequence;


import java.awt.Color;

import javax.swing.JButton;
import javax.swing.JComboBox;

import icy.image.IcyBufferedImage;
import icy.sequence.Sequence;

import plugins.fmp.areatrack.DetectionParameters;
import plugins.fmp.fmpTools.EnumImageOp;
import plugins.fmp.fmpTools.OverlayThreshold;
import plugins.fmp.fmpTools.OverlayTrapMouse;



public class SequencePlus extends SequenceVirtual  {

	public 	OverlayThreshold thresholdOverlay 		= null;
	public 	OverlayTrapMouse trapOverlay 			= null;
	
	// -----------------------------------------------------
	
	public SequencePlus() {
		super ();
	}
	
	public SequencePlus(Sequence seq) {
		super(seq);
	}
	
	public SequencePlus(String name, IcyBufferedImage image) {
		super (name, image);
	}

	// ----------------------------
	
	public void setThresholdOverlay(boolean bActive) {
		if (bActive) {
			if (thresholdOverlay == null) 
				thresholdOverlay = new OverlayThreshold(this);
			if (!seq.contains(thresholdOverlay)) 
				seq.addOverlay(thresholdOverlay);
			thresholdOverlay.setSequence (this);
		}
		else {
			if (thresholdOverlay != null && seq.contains(thresholdOverlay) )
				seq.removeOverlay(thresholdOverlay);
			thresholdOverlay = null;
		}
	}
	
	public void setThresholdOverlayParametersSingle(EnumImageOp transf, int threshold) {
		thresholdOverlay.setTransform(transf);
		thresholdOverlay.setThresholdSingle(threshold);
		thresholdOverlay.painterChanged();
	}
	
	public void setThresholdOverlayParametersColors(EnumImageOp transf, DetectionParameters areatrackParameters) {
		
		thresholdOverlay.setTransform(transf);
		thresholdOverlay.setThresholdColor(areatrackParameters.colorarray, areatrackParameters.colordistanceType, areatrackParameters.colorthreshold);
		thresholdOverlay.painterChanged();
	}

	public void setMouseTrapOverlay (boolean bActive, JButton pickColorButton, JComboBox<Color> colorPickCombo) {
		if (bActive) {
			if (trapOverlay == null)
				trapOverlay = new OverlayTrapMouse (pickColorButton, colorPickCombo);
			if (!seq.contains(trapOverlay))
				seq.addOverlay(trapOverlay);
		}
		else {
			if (trapOverlay != null && seq.contains(trapOverlay))
				seq.removeOverlay(trapOverlay);
			trapOverlay = null;
		}
	}
	
}
