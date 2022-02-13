package plugins.fmp.areatrack;

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

import icy.gui.frame.IcyFrame;
import icy.gui.util.GuiUtil;
import plugins.fmp.fmpSequence.SequencePlus;


public class DisplayCharts {
	IcyFrame mainChartFrame = null;
	JPanel 	mainChartPanel = null;
	
	Areatrack parent0 = null;
	SequencePlus vSequence = null;
	int startFrame= 0;
	int endFrame = 1;
	
	public void updateCharts(Areatrack parent0) {
		
		vSequence = parent0.vSequence;
		startFrame = parent0.startFrame;
		endFrame = parent0.endFrame;
		int filteroption = parent0.filterComboBox.getSelectedIndex();
		FilterMeasures.filterMeasures (parent0, filteroption);
		
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

}
