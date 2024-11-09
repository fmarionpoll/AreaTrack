package plugins.fmp.areatrack.commons;

import plugins.fmp.areatrack.sequence.SequencePlus;

public class GraphsOverlay {
	
	public void updateCharts(SequencePlus vSequence, int filteroption, int span) {
		
		FilterTimeSeries.filterMeasures (vSequence, filteroption, span);
		int nrois = vSequence.data_filtered.length;
		for (int iroi = 0; iroi < nrois; iroi++) {
		}
		
	}

}
