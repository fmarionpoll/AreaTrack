package plugins.fmp.areatrack;

import java.util.Arrays;

import plugins.fmp.fmpSequence.SequencePlus;



public class FilterTimeSeries {

	static SequencePlus vSequence = null;
	static int startFrame= 0;
	static int endFrame = 1;
	
	public static void filterMeasures (SequencePlus sequence, int start, int end, int filteroption, int span) 
	{	
		vSequence = sequence;
		startFrame = start;
		endFrame = end;
		filterMeasures_parameters (filteroption, span);
	}

	private static void filterMeasures_parameters (int filteroption, int span) 
	{
		int nrois = vSequence.data_raw.length;
		if (vSequence.data_filtered == null 
			|| vSequence.data_filtered.length != vSequence.data_raw.length) {
			int nbins = (endFrame-startFrame+1)/vSequence.analysisStep +1;
			vSequence.data_filtered = new double [nrois][nbins];
		}
		
		if (span/2 > endFrame - startFrame+1)
			filteroption = 0;
		
		switch (filteroption) 
		{
			case 1: // running average over "span" points
				filterMeasures_RunningAverage(span);
				break;
			case 2:
				filterMeasures_RunningMedian(span);
				break;
			default:
				for ( int t = 0; t < endFrame-startFrame +1;  t += vSequence.analysisStep ) {
					int bin = t /  vSequence.analysisStep ;	
					for (int iroi = 0; iroi < nrois; iroi++) 
						vSequence.data_filtered[iroi][bin] = vSequence.data_raw[iroi][t];
				}
				break;
		}
	}
	
	private static void filterMeasures_RunningAverage(int span) 
	{
		int nrois = vSequence.data_raw.length;
		
		for (int iroi = 0; iroi < nrois; iroi++) 
		{
			double sum = 0;
			for ( int t = 0 ; t < endFrame-startFrame +1;  t += vSequence.analysisStep )  {
				int bin = t / vSequence.analysisStep ;
				sum += vSequence.data_raw[iroi][t];
				if (t < span/2)
					vSequence.data_filtered[iroi][bin] = vSequence.data_raw[iroi][t];
			}
			sum -= vSequence.data_raw[iroi][span] - vSequence.data_raw[iroi][0];
			
			for ( int t = endFrame-startFrame-span/2 ; t < endFrame-startFrame;  t += vSequence.analysisStep) {
				int bin = t / vSequence.analysisStep ;
				vSequence.data_filtered[iroi][bin] = vSequence.data_raw[iroi][t];
			}

			for (int t = span/2; t < endFrame-startFrame-span/2; t++) {
				int bin = t / vSequence.analysisStep ;
				int t0 = (t - span/2);
				int t1 = (t + span/2);
				sum += vSequence.data_raw[iroi][t1] - vSequence.data_raw[iroi][t0];
				vSequence.data_filtered[iroi][bin] = sum/span;
			}
		}
	}
		
	private static void filterMeasures_RunningMedian(int span) 
	{	
		int nrois = vSequence.data_raw.length;
		int nbspan = span/2;
		
		for (int iroi = 0; iroi < nrois; iroi++) {
			
			int sizeTempArray = nbspan*2+1;
			int [] tempArraySorted = new int [sizeTempArray];
			int [] tempArrayCircular = new int [sizeTempArray];
			
			for (int t = 0; t< sizeTempArray; t++) {	
				int bin = t / vSequence.analysisStep ;
				int value = vSequence.data_raw[iroi][bin];
				tempArrayCircular[bin] = value;
				vSequence.data_filtered[iroi][bin] = value;
			}

			int iarraycircular = sizeTempArray -1;
			for (int t = nbspan; t < endFrame-startFrame-nbspan; t++) {
				int bin = (t + nbspan)/ vSequence.analysisStep ;
				int newvalue = vSequence.data_raw[iroi][t];
				tempArrayCircular[iarraycircular]= newvalue;
				tempArraySorted = tempArrayCircular.clone();
				Arrays.sort(tempArraySorted);
				int median = tempArraySorted[nbspan];
				bin = t / vSequence.analysisStep ;
				vSequence.data_filtered[iroi][bin] = median;
				
				iarraycircular++;
				if (iarraycircular >= sizeTempArray)
					iarraycircular = 0;
			}
		}
	}
}
