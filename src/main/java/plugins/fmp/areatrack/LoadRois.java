package plugins.fmp.areatrack;


import plugins.fmp.fmpSequence.SequencePlus;


public class LoadRois {
	
	public void openROIs(SequencePlus vSequence) {
		if (vSequence == null) return;
		
		vSequence.seq.removeAllROI();
		vSequence.capillariesRoi2RoiArray.xmlReadROIsAndData(vSequence);
	}
	
	public void saveROIs(SequencePlus vSequence) {
		if (vSequence == null) return;
		
		vSequence.capillariesRoi2RoiArray.xmlWriteROIsAndData("areatrack.xml", vSequence);
	}
	
	public void addROIs(SequencePlus vSequence) {
		if (vSequence == null) return;
		
		vSequence.capillariesRoi2RoiArray.xmlReadROIsAndData(vSequence);
	}
	

}
