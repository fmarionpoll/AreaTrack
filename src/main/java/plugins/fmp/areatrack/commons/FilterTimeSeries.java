package plugins.fmp.areatrack.commons;

import java.util.Arrays;

import plugins.fmp.areatrack.sequence.SequencePlus;

public class FilterTimeSeries {

	public static void filterMeasures(SequencePlus vSequence, int filteroption, int span) {

		int startFrame = vSequence.analysisStart;
		int endFrame = vSequence.analysisEnd;
		if ((endFrame - startFrame) > (vSequence.nTotalFrames - 1)) {
			vSequence.analysisEnd = vSequence.nTotalFrames - 1;
		}
		filterMeasures_run(vSequence, filteroption, span);
	}

	private static void filterMeasures_run(SequencePlus vSequence, int filteroption, int span) {
		int nrois = vSequence.seq.getROI2Ds().size();
		if (vSequence.data_filtered == null || vSequence.data_filtered[0].length != vSequence.data_raw.length) {
			int nbins = 1 + (vSequence.analysisEnd - vSequence.analysisStart + 1) / vSequence.analysisStep;
			vSequence.data_filtered = new double[nrois][nbins];
		}

		if (span / 2 > vSequence.analysisEnd - vSequence.analysisStart + 1)
			filteroption = 0;

		switch (filteroption) {
		case 1: // running average over "span" points
			filterMeasures_RunningAverage(vSequence, span);
			break;
		case 2:
			filterMeasures_RunningMedian(vSequence, span);
			break;
		default:
			filterMeasures_copy(vSequence, span);
			break;
		}
	}

	private static void filterMeasures_RunningAverage(SequencePlus vSequence, int span) {
		int nrois = vSequence.data_filtered.length;
		int npoints = (vSequence.analysisEnd - vSequence.analysisStart + 1) / vSequence.analysisStep;

		for (int iroi = 0; iroi < nrois; iroi++) {
			int nsum = span / 2;
			int bin = 0;
			double sum = vSequence.data_raw[iroi][0] * (nsum - 1);

			for (int t = 0; t < npoints; t += vSequence.analysisStep) {
				sum += vSequence.data_raw[iroi][t];
				nsum++;
				if (nsum >= span) {
					vSequence.data_filtered[iroi][bin] = sum / nsum;
					bin++;
					nsum = 0;
					sum = 0.;
				}
			}
			if (nsum > 0) {
				vSequence.data_filtered[iroi][bin] = sum / nsum;
			}
		}
	}

	private static void filterMeasures_RunningMedian(SequencePlus vSequence, int span) {
		int nrois = vSequence.data_filtered.length;
		int nbspan = span / 2;

		for (int iroi = 0; iroi < nrois; iroi++) {

			int sizeTempArray = nbspan * 2 + 1;
			int[] tempArraySorted = new int[sizeTempArray];
			int[] tempArrayCircular = new int[sizeTempArray];

			for (int t = 0; t < sizeTempArray; t += vSequence.analysisStep) {
				int bin = t / vSequence.analysisStep;
				int value = vSequence.data_raw[iroi][bin];
				tempArrayCircular[bin] = value;
				vSequence.data_filtered[iroi][bin] = value;
			}

			int iarraycircular = sizeTempArray - 1;
			for (int t = nbspan; t < vSequence.analysisEnd - vSequence.analysisStart - nbspan; t++) {
				int bin = (t + nbspan) / vSequence.analysisStep;
				int newvalue = vSequence.data_raw[iroi][t];
				tempArrayCircular[iarraycircular] = newvalue;
				tempArraySorted = tempArrayCircular.clone();
				Arrays.sort(tempArraySorted);
				int median = tempArraySorted[nbspan];
				bin = t / vSequence.analysisStep;
				vSequence.data_filtered[iroi][bin] = median;

				iarraycircular++;
				if (iarraycircular >= sizeTempArray)
					iarraycircular = 0;
			}
		}
	}

	private static void filterMeasures_copy(SequencePlus vSequence, int span) {
		int nrois = vSequence.data_filtered.length;
		int bin = 0;
		for (int t = vSequence.analysisStart; t <= vSequence.analysisEnd; bin++, t += vSequence.analysisStep) {
			for (int iroi = 0; iroi < nrois; iroi++)
				vSequence.data_filtered[iroi][bin] = vSequence.data_raw[iroi][t];
		}
	}

}
