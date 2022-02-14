package plugins.fmp.areatrack;

import plugins.fmp.fmpSequence.SequencePlus;

public class GraphsOverlay {
	
	public void updateCharts(SequencePlus vSequence, int startFrame, int endFrame, int filteroption, int span, int step) {
		
		FilterTimeSeries.filterMeasures (vSequence, startFrame, endFrame, filteroption, span);
		int nrois = vSequence.data_filtered.length;
		for (int iroi = 0; iroi < nrois; iroi++) {
		}
		
	}

}
