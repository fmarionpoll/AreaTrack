package plugins.fmp.areatrack;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Point;

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

import icy.gui.frame.IcyFrame;
import icy.gui.util.GuiUtil;
import plugins.fmp.fmpSequence.SequencePlus;


public class GraphsWindow {
	
	IcyFrame mainChartFrame = null;
	JPanel 	mainChartPanel = null;
	
	
	public void updateCharts(SequencePlus vSequence, int startFrame, int endFrame, int filteroption, int span, int step) {
		
		FilterTimeSeries.filterMeasures (vSequence, startFrame, endFrame, filteroption, span);
		
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
		for (int iroi = 0; iroi < nrois; iroi++) {
			cropSeries[iroi] = new XYSeries (vSequence.seriesname[iroi]);
			cropSeries[iroi].clear();
			for (int t = startFrame; t <= endFrame; t += step) {
				int bin = (t-startFrame) / step;
				cropSeries[iroi].add(t, vSequence.data_filtered[iroi][bin]);
			}
			xyDataset.addSeries(cropSeries[iroi]);
		}
				
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
		// TODO step between xpoints?
		
		LegendTitle legendTitle = chart.getLegend();

		if (legendTitle != null)
			legendTitle.setPosition(RectangleEdge.RIGHT); 
		mainChartPanel.add( new ChartPanel( chart, 
				width, height, minWidth, minHeight, maxWidth, maxHeight, 
				false , false, true , true , true, true));
		mainChartPanel.validate();
		mainChartPanel.repaint();
		
		mainChartFrame.pack();
		mainChartFrame.setLocation(pt );
		mainChartFrame.addToDesktopPane ();
		mainChartFrame.setVisible(true);
		mainChartFrame.toFront();
	}

}
