package plugins.fmp.AreaTrack;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Point;
import java.util.Arrays;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.title.LegendTitle;
import org.jfree.chart.ui.RectangleEdge;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import icy.gui.util.GuiUtil;

public class DisplayCharts {
	public void updateCharts() {
		filterMeasures ();
		
		String title = "Measures from " + vSequence.getFileName(0);
		Point pt = new Point(10, 10);
		
		// create window or get handle to it
		if (mainChartFrame != null)
		{
			mainChartFrame.removeAll();
			mainChartFrame.close();
		}
		mainChartFrame = GuiUtil.generateTitleFrame(title, new JPanel(), new Dimension(300, 70), true, true, true, true);
		mainChartPanel = new JPanel(); 
		mainChartPanel.setLayout( new BoxLayout( mainChartPanel, BoxLayout.LINE_AXIS ) );
		mainChartFrame.add(mainChartPanel);
		
		mainChartPanel.removeAll();
		int rows = 1;
		int cols = 1;
		XYSeriesCollection xyDataset = new XYSeriesCollection();
		mainChartPanel.setLayout(new GridLayout(rows, cols));
		
		int nrois = vSequence.data_filtered.length;
		XYSeries [] cropSeries = new XYSeries [nrois];
		for (int iroi=0; iroi < nrois; iroi++) {
			cropSeries[iroi] = new XYSeries (vSequence.seriesname[iroi]);
			cropSeries[iroi].clear();
			for (int t= startFrame; t <= endFrame; t++) {
				cropSeries[iroi].add(t, vSequence.data_filtered[iroi][t-startFrame]);
			}
		}
		
		int ncurves = cropSeries.length;
		for (int i=0; i< ncurves; i++)
			xyDataset.addSeries(cropSeries[i]);
		
		String TitleString = "Results";
		boolean displayLegend = false; //true;
		JFreeChart chart = ChartFactory.createXYLineChart(
				TitleString, "time", "pixels",
				xyDataset,
				PlotOrientation.VERTICAL, displayLegend,true,false ); 
		
		int minWidth = 800;
		int minHeight = 200;
		int width = 800;
		int height = 200;
		int maxWidth = 100000;
		int maxHeight = 100000;
		XYPlot plot = chart.getXYPlot();
		ValueAxis axis = plot.getDomainAxis();
		axis.setRange(startFrame, endFrame);
		LegendTitle legendTitle = chart.getLegend();

		if (legendTitle != null)
			legendTitle.setPosition(RectangleEdge.RIGHT); 
		mainChartPanel.add( new ChartPanel(  chart, width , height , minWidth, minHeight, maxWidth , maxHeight, false , false, true , true , true, true));
		mainChartPanel.validate();
		mainChartPanel.repaint();
		
		mainChartFrame.pack();
		mainChartFrame.setLocation(pt );
		mainChartFrame.addToDesktopPane ();
		mainChartFrame.setVisible(true);
		mainChartFrame.toFront();
	}
	
	private void filterMeasures () {
		int filteroption = filterComboBox.getSelectedIndex();
		int span = Integer.parseInt(spanTextField.getText());
		filterMeasures_parameters (filteroption, span);
	}

	private void filterMeasures_parameters (int filteroption, int span) {
		int nrois = vSequence.data_raw.length;
		if (vSequence.data_filtered == null 
			|| vSequence.data_filtered.length != vSequence.data_raw.length)
			vSequence.data_filtered = new double [nrois][endFrame-startFrame+1];
		
		System.out.println("data_raw_length="+vSequence.data_raw.length +" data_filtered_length="+vSequence.data_filtered.length);
		switch (filteroption) {
			case 1: // running average over "span" points
				filterMeasures_RunningAverage(span);
				break;
			case 2:
				filterMeasures_RunningMedian(span);
				break;
			default:	
				for (int iroi=0; iroi < nrois; iroi++) {
					for ( int t = 0 ; t < endFrame-startFrame +1;  t++ ) {
						vSequence.data_filtered[iroi][t] = vSequence.data_raw[iroi][t];
					}
				}
				break;
		}
	}
	

	private void filterMeasures_RunningAverage(int span) {
		int nrois = vSequence.data_raw.length;
		for (int iroi=0; iroi < nrois; iroi++) {
			double sum = 0;
			for (int t= 0; t< span; t++) {
				sum += vSequence.data_raw[iroi][t];
				if (t < span/2)
					vSequence.data_filtered[iroi][t] = vSequence.data_raw[iroi][t];
			}
			sum -= vSequence.data_raw[iroi][span] - vSequence.data_raw[iroi][0];
			
			for ( int t = endFrame-startFrame-span/2 ; t < endFrame-startFrame;  t++ )
				vSequence.data_filtered[iroi][t] = vSequence.data_raw[iroi][t];
			int t0= 0;
			int t1 =span;
			for (int t= span/2; t< endFrame-startFrame-span/2; t++, t0++, t1++) {
				sum += vSequence.data_raw[iroi][t1] - vSequence.data_raw[iroi][t0];
				vSequence.data_filtered[iroi][t] = sum/span;
			}
		}
	}
		
	private void filterMeasures_RunningMedian(int span) {
		
		int nrois = vSequence.data_raw.length;
		int nbspan = span/2;
		
		for (int iroi=0; iroi < nrois; iroi++) {
			
			int sizeTempArray = nbspan*2+1;
			int [] tempArraySorted = new int [sizeTempArray];
			int [] tempArrayCircular = new int [sizeTempArray];
			for (int t= 0; t< sizeTempArray; t++) {			
				int value = vSequence.data_raw[iroi][t];
				tempArrayCircular[t] = value;
				vSequence.data_filtered[iroi][t] = value;
			}

			int iarraycircular = sizeTempArray -1;
			for (int t=nbspan; t< endFrame-startFrame-nbspan; t++) {
				int newvalue = vSequence.data_raw[iroi][t+nbspan];
				tempArrayCircular[iarraycircular]= newvalue;
				tempArraySorted = tempArrayCircular.clone();
				Arrays.sort(tempArraySorted);
				int median = tempArraySorted[nbspan];
				vSequence.data_filtered[iroi][t] = median;
				
				iarraycircular++;
				if (iarraycircular >= sizeTempArray)
					iarraycircular=0;
			}
		}
	}
	
	
	
}
