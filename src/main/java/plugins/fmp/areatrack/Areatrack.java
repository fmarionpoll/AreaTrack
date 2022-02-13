package plugins.fmp.areatrack;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

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
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import icy.gui.frame.IcyFrame;
import icy.gui.frame.progress.AnnounceFrame;
import icy.gui.util.FontUtil;
import icy.gui.util.GuiUtil;
import icy.gui.viewer.Viewer;
import icy.gui.viewer.ViewerEvent;
import icy.gui.viewer.ViewerEvent.ViewerEventType;
import icy.gui.viewer.ViewerListener;
import icy.main.Icy;
import icy.plugin.PluginLauncher;
import icy.plugin.PluginLoader;
import icy.plugin.abstract_.PluginActionable;
import icy.preferences.XMLPreferences;
import icy.roi.ROI2D;
import icy.sequence.DimensionId;
import icy.sequence.Sequence;
import icy.system.thread.ThreadUtil;
import icy.util.XLSUtil;
import icy.util.XMLUtil;

import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

import plugins.fmp.fmpTools.ComboBoxColorRenderer;
import plugins.fmp.fmpTools.EnumImageOp;
import plugins.fmp.fmpTools.EnumThresholdType;
import plugins.fmp.fmpTools.FmpTools;
import plugins.fmp.areatrack.dlg.analysisParameters.PanelAnalysis;
import plugins.fmp.areatrack.dlg.results.DisplayCharts;
import plugins.fmp.areatrack.dlg.results.ExportToXLS;
import plugins.fmp.areatrack.dlg.rois.PanelRois;
import plugins.fmp.areatrack.dlg.source.PanelSource;
import plugins.fmp.fmpSequence.OpenVirtualSequence;
import plugins.fmp.fmpSequence.SequencePlus;


public class Areatrack extends PluginActionable implements ActionListener, ChangeListener, ViewerListener
{	
	// -------------------------------------- interface
	IcyFrame mainFrame = new IcyFrame("AreaTrack 10-02-2022", true, true, true, true);
	IcyFrame mainChartFrame = null;
	JPanel 	mainChartPanel = null;
	
	public PanelSource 	panelSource = new PanelSource();
	public PanelRois 	panelRois = new PanelRois();
	public PanelAnalysis panelAnalysis = new PanelAnalysis();
	
	// ---------------------------------------- video
	
	
	// TODO
	private JButton startComputationButton 	= new JButton("Start");
	private JButton stopComputationButton	= new JButton("Stop");
	private JTextField 	startFrameTextField	= new JTextField("0");
	private JTextField 	endFrameTextField	= new JTextField("99999999");
	
	private JComboBox<EnumImageOp> transformsComboBox = new JComboBox<EnumImageOp> (new EnumImageOp[] {
					EnumImageOp.R_RGB, EnumImageOp.G_RGB, EnumImageOp.B_RGB, 
					EnumImageOp.R2MINUS_GB, EnumImageOp.G2MINUS_RB, EnumImageOp.B2MINUS_RG, EnumImageOp.NORM_BRMINUSG, EnumImageOp.RGB,
					EnumImageOp.H_HSB, EnumImageOp.S_HSB, EnumImageOp.B_HSB	});
	private JSpinner 	thresholdSpinner	= new JSpinner(new SpinnerNumberModel(70, 0, 255, 1));
	private JLabel 		videochannel 		= new JLabel("filter  ");
	private JLabel 		thresholdLabel 		= new JLabel("threshold ");
	private JSpinner 	threshold2Spinner	= new JSpinner(new SpinnerNumberModel(20, 0, 255, 1));
	private JTextField 	analyzeStepTextField= new JTextField("1");
		
	//---------------------------------------------------------------------------
	private JTabbedPane tabbedPane 			= new JTabbedPane();
	private JComboBox<Color> colorPickCombo = new JComboBox<Color>();
	private ComboBoxColorRenderer colorPickComboRenderer = new ComboBoxColorRenderer(colorPickCombo);
	
	private String 		textPickAPixel 		= "Pick a pixel";
	private JButton		pickColorButton		= new JButton(textPickAPixel);
	private JButton		deleteColorButton	= new JButton("Delete color");
	private JRadioButton rbL1				= new JRadioButton("L1");
	private JRadioButton rbL2				= new JRadioButton("L2");
	private JSpinner    distanceSpinner 	= new JSpinner(new SpinnerNumberModel(10, 0, 800, 5));
	private JRadioButton rbRGB				= new JRadioButton("RGB");
	private JRadioButton rbHSV				= new JRadioButton("HSV");
	private JRadioButton rbH1H2H3			= new JRadioButton("H1H2H3");
	private JLabel 		distanceLabel 		= new JLabel("Distance  ");
	private JLabel 		colorspaceLabel 	= new JLabel("Color space ");
	private JButton		openFiltersButton	= new JButton("Load...");
	private JButton		saveFiltersButton	= new JButton("Save...");
	
	//---------------------------------------------------------------------------
	private JComboBox<String> filterComboBox= new JComboBox<String> (new String[] {"raw data", "average", "median"});
	private JTextField 	spanTextField		= new JTextField("10");

	private JButton 	updateChartsButton 	= new JButton("Display charts");
	private JButton 	exportToXLSButton 	= new JButton("Save XLS file..");
	private JButton		closeAllButton		= new JButton("Close views");

	//------------------------------------------- global variables
	public SequencePlus vSequence 			= null;
	private ArrayList<MeasureAndName> resultsHeatMap = null;

	private int			analyzeStep 		= 1;
	private int 		startFrame 			= 1;
	private int 		endFrame 			= 99999999;
	private AreaAnalysisThread analysisThread = null;
	
	// parameters saved/read in xml file
	private EnumThresholdType thresholdtype 	= EnumThresholdType.COLORARRAY; 
	// simple
	private EnumImageOp simpletransformop 	= EnumImageOp.R2MINUS_GB;
	private int 		simplethreshold 	= 20;
	// colors
	private EnumImageOp colortransformop 	= EnumImageOp.NONE;
	private int 		colordistanceType 	= 0;
	private int 		colorthreshold 		= 20;
	private ArrayList <Color> colorarray 	= new ArrayList <Color>();
	// movement detection
	private int 		thresholdmovement 	= 20;	
	final private String filename 			= "areatrack.xml";
	
	// --------------------------------------------------------------------------
	
	private void panelSetMenuBar (JPanel mainPanel) {
		
		JMenuBar menuBar = new JMenuBar();
		JMenu aboutMenu = new JMenu("About");
		menuBar.add(aboutMenu);
		
		JMenuItem manualItem = new JMenuItem("Manual");
		aboutMenu.add(manualItem);
		manualItem.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(mainPanel,
						"Please refer to the online help:\n http://icy.bioimageanalysis.org/plugin/...", 
						"Manual", 
						JOptionPane.INFORMATION_MESSAGE );
			}
		});
		JMenuItem aboutItem = new JMenuItem("About");
		aboutMenu.add(aboutItem);
		aboutItem.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(mainPanel,
					    "This plugin is distributed under GPL v3 license.\n Author: Frederic Marion-Poll" +
					    "\n Email frederic.marion-poll@egce.cnrs-gif.fr", "About", JOptionPane.INFORMATION_MESSAGE);
			}
		});
		mainFrame.setJMenuBar(menuBar);
	}
	
	
	private void panelSetRunInterface (JPanel mainPanel) {
		final JPanel panel =  GuiUtil.generatePanel("RUN ANALYSIS");
		mainPanel.add(GuiUtil.besidesPanel(panel));
		
		panel.add( GuiUtil.besidesPanel( startComputationButton, stopComputationButton ) );
		JLabel startLabel 	= new JLabel("from ");
		JLabel endLabel 	= new JLabel("to end ");
		JLabel stepLabel 	= new JLabel("step ");
		startLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		endLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		stepLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		panel.add( GuiUtil.besidesPanel( startLabel, startFrameTextField, endLabel, endFrameTextField ) );
		panel.add( GuiUtil.besidesPanel( stepLabel, analyzeStepTextField, new JLabel (" "), new JLabel (" ")));	
	}
	
	private void panelSetResultsInterface(JPanel mainPanel) {
		final JPanel panel = GuiUtil.generatePanel("RESULTS DISPLAY/EXPORT");
		mainPanel.add(GuiUtil.besidesPanel(panel));
		
		JLabel outputLabel = new JLabel ("output ");
		outputLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		JLabel spanLabel = new JLabel ("span ");
		spanLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		panel.add(GuiUtil.besidesPanel(outputLabel, filterComboBox, spanLabel, spanTextField));
		panel.add(GuiUtil.besidesPanel(updateChartsButton, exportToXLSButton)); 
		panel.add(GuiUtil.besidesPanel(closeAllButton));
	}
	
	@Override
	public void run() {
		
		Icy.getMainInterface().getMainFrame().getInspector().setVirtualMode(false);
		Icy.getMainInterface().getMainFrame().getInspector().imageCacheDisabled();

		// build and display the GUI
		JPanel mainPanel = GuiUtil.generatePanelWithoutBorder();
		mainFrame.setLayout(new BorderLayout());
		mainFrame.add(mainPanel, BorderLayout.CENTER);
			
		panelSetMenuBar(mainPanel);
		panelSource.init(mainPanel, "SOURCE", this);
		panelRois.init(mainPanel);
		panelAnalysis.init(mainPanel);
		
		panelSetRunInterface(mainPanel);
		panelSetResultsInterface(mainPanel);
		
		mainFrame.pack();
		mainFrame.center();
		mainFrame.setVisible(true);
		mainFrame.addToDesktopPane();
		mainFrame.requestFocus();
		
		declareActionListeners();
		declareChangeListeners();
		
		// -------------------------------------------- default selection
		filterComboBox.setSelectedIndex(2);
		measureSurfacesCheckBox.setSelected(true);
		measureHeatmapCheckBox.setSelected(true);

		tabbedPane.setSelectedIndex(3);
		rbFilterbyColor.setSelected(true);
		rbL1.setSelected(true);
		rbRGB.setSelected(true);
		colortransformop = EnumImageOp.NONE;
		transformsComboBox.setSelectedIndex(EnumImageOp.B2MINUS_RG.ordinal());
		filterComboBox.setSelectedIndex(0);
	}

	private void declareChangeListeners() {
		thresholdSpinner.addChangeListener(this);
		tabbedPane.addChangeListener(this);
		distanceSpinner.addChangeListener(this);
		threshold2Spinner.addChangeListener(this);
	}
	
	private void declareActionListeners() {
		closeAllButton.addActionListener(new ActionListener () { @Override public void actionPerformed( final ActionEvent e ) { 
				if (mainChartFrame != null) {
					mainChartFrame.removeAll();
					mainChartFrame.close();
					mainChartFrame = null;
				}
				vSequence.close();
			} } );
		
		rbRGB.addActionListener(new ActionListener () { @Override public void actionPerformed( final ActionEvent e ) { 
				colortransformop = EnumImageOp.NONE;
				updateThresholdOverlayParameters();
			} } );
		
		rbHSV.addActionListener(new ActionListener () { @Override public void actionPerformed( final ActionEvent e ) { 
				colortransformop = EnumImageOp.RGB_TO_HSV;
				updateThresholdOverlayParameters();
			} } );
		
		rbH1H2H3.addActionListener(new ActionListener () { @Override public void actionPerformed( final ActionEvent e ) { 
				colortransformop = EnumImageOp.RGB_TO_H1H2H3;
				updateThresholdOverlayParameters();
			} } );
		
		rbL1.addActionListener(new ActionListener () { @Override public void actionPerformed( final ActionEvent e ) { 
				updateThresholdOverlayParameters();
			} } );
		
		rbL2.addActionListener(new ActionListener () { @Override public void actionPerformed( final ActionEvent e ) { 
				updateThresholdOverlayParameters();
			} } );
		
		stopComputationButton.addActionListener(new ActionListener () { @Override public void actionPerformed( final ActionEvent e ) { 
				stopAnalysisThread();
			} } );
		
		startComputationButton.addActionListener(new ActionListener () { @Override public void actionPerformed( final ActionEvent e ) {  
				startAnalysisThread(); 
			} } );		
		
		updateChartsButton.addActionListener(new ActionListener () { @Override public void actionPerformed( final ActionEvent e ) {
				DisplayCharts displayCharts = new DisplayCharts();
				displayCharts.updateCharts(); 
			} } );
		
		deleteColorButton.addActionListener(new ActionListener () { @Override public void actionPerformed( final ActionEvent e ) { 
				if (colorPickCombo.getItemCount() > 0 && colorPickCombo.getSelectedIndex() >= 0)
					colorPickCombo.removeItemAt(colorPickCombo.getSelectedIndex());
				updateThresholdOverlayParameters();
			} } );
			
		transformsComboBox.addActionListener(new ActionListener () { @Override public void actionPerformed( final ActionEvent e ) { 
				updateThresholdOverlayParameters(); 
			} } );
		
		openFiltersButton.addActionListener(new ActionListener () { @Override public void actionPerformed( final ActionEvent e ) { 
				loadParametersFromXMLFile(); 
			} } );
		
		saveFiltersButton.addActionListener(new ActionListener () { @Override public void actionPerformed( final ActionEvent e ) { 
				saveParametersToXMLFile(); 
			} } );
		
		openROIsButton.addActionListener(new ActionListener () { @Override public void actionPerformed( final ActionEvent e ) { 
				openROIs(); 
			} } );
		
		saveROIsButton.addActionListener(new ActionListener () { @Override public void actionPerformed( final ActionEvent e ) { 
				saveROIs(); 
			} } );
		
		addROIsButton.addActionListener(new ActionListener () { @Override public void actionPerformed( final ActionEvent e ) { 
				addROIs(); 
			} } );
		
		pickColorButton.addActionListener(new ActionListener () { @Override public void actionPerformed( final ActionEvent e ) { 
				pickColor(); 
			} } );

		exportToXLSButton.addActionListener(new ActionListener () { @Override public void actionPerformed( final ActionEvent e ) {
				String file = FmpTools.saveFileAs(null, vSequence.getDirectory(), "xls");
				if (file != null) {
					ThreadUtil.bgRun( new Runnable() { @Override public void run() { 
						ExportToXLS exportToXLS = new ExportToXLS();
						final String filename = file; 
						exportToXLS.exportToXLS(filename);
						}});
				}
			} } );
		
		
		rbFilterbyColor.addActionListener(new ActionListener () { @Override public void actionPerformed( final ActionEvent e ) {
			if (rbFilterbyColor.isSelected())
				selectTab(0);
		} } );
		
		rbFilterbyFunction.addActionListener(new ActionListener () { @Override public void actionPerformed( final ActionEvent e ) {
			if (rbFilterbyFunction.isSelected())
				selectTab(1);
		} } );
		
		class ItemChangeListener implements ItemListener{
		    @Override
		    public void itemStateChanged(ItemEvent event) {
		       if (event.getStateChange() == ItemEvent.SELECTED) {
		    	   updateThresholdOverlayParameters();
		       }
		    }       
		}
		colorPickCombo.addItemListener(new ItemChangeListener());
	}
	
	private void selectTab(int index) {
		tabbedPane.setSelectedIndex(index);
	}
	
	@Override
	public void viewerChanged(ViewerEvent event) {
		if ((event.getType() == ViewerEventType.POSITION_CHANGED) && (event.getDim() == DimensionId.T))        
			vSequence.currentFrame = event.getSource().getPositionT() ; 
	}

	@Override
	public void viewerClosed(Viewer viewer) {
		viewer.removeListener(this);
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		if ((e.getSource() == thresholdSpinner)  
		|| (e.getSource() == tabbedPane) 
		|| (e.getSource() == distanceSpinner) 
		|| (e.getSource() == threshold2Spinner)) 
			updateThresholdOverlayParameters();
	}

	
		
	private void openROIs() {
		if (vSequence != null) {
			vSequence.seq.removeAllROI();
			vSequence.capillariesRoi2RoiArray.xmlReadROIsAndData(vSequence);
			endFrameTextField.setText( Integer.toString(endFrame));
			startFrameTextField.setText( Integer.toString(startFrame));
		}
	}
	
	private void saveROIs() {
		vSequence.analysisStart = startFrame;
		vSequence.analysisEnd = endFrame;
		vSequence.capillariesRoi2RoiArray.xmlWriteROIsAndData("areatrack.xml", vSequence);
	}
	
	private void addROIs( ) {
		if (vSequence != null) {
			vSequence.capillariesRoi2RoiArray.xmlReadROIsAndData(vSequence);
			endFrameTextField.setText( Integer.toString(endFrame));
			startFrameTextField.setText( Integer.toString(startFrame));
		}
	}
	
	private void transferParametersToDialog() {
		distanceSpinner.setValue(colorthreshold);
		tabbedPane.setSelectedIndex(3);
		switch (colortransformop) {
		case RGB_TO_HSV:
			rbHSV.setSelected(true);
			break;
		case RGB_TO_H1H2H3:
			rbH1H2H3.setSelected(true);
			break;
		case NONE:
		default:
			rbRGB.setSelected(true);
			break;
		}
		colorPickCombo.removeAll();
		for (int i=0; i < colorarray.size(); i++)
			colorPickCombo.addItem(colorarray.get(i));
		if (colordistanceType == 1)
			rbL1.setSelected(true);
		else
			rbL2.setSelected(true);
		transformsComboBox.setSelectedItem(simpletransformop);
		thresholdSpinner.setValue(simplethreshold);
		threshold2Spinner.setValue(thresholdmovement);
	}
	
	private void startAnalysisThread() {
		stopAnalysisThread();
		
		analysisThread = new AreaAnalysisThread(); 
		updateThresholdOverlayParameters();
		
		startFrame 	= Integer.parseInt( startFrameTextField.getText() );
		endFrame 	= Integer.parseInt( endFrameTextField.getText() );
		analyzeStep = Integer.parseInt( analyzeStepTextField.getText() );
		vSequence.analysisStep = analyzeStep;
		
		EnumImageOp transformop = EnumImageOp.NONE;
		if (rbFilterbyFunction.isSelected())
			transformop = (EnumImageOp) transformsComboBox.getSelectedItem();
		int thresholdforsurface = Integer.parseInt(thresholdSpinner.getValue().toString());
		int thresholdformovement = Integer.parseInt(threshold2Spinner.getValue().toString());
		
		analysisThread.setAnalysisThreadParameters(vSequence, getROIsToAnalyze(), startFrame, endFrame,  
			transformop, 
			thresholdforsurface,
			thresholdformovement,
			measureSurfacesCheckBox.isSelected(), 
			measureHeatmapCheckBox.isSelected());
		analysisThread.setAnalysisThreadParametersColors (thresholdtype, colordistanceType, colorthreshold, colorarray);
		analysisThread.start();	
	}
	
	private void stopAnalysisThread() {
		
		if (analysisThread != null && analysisThread.isAlive()) {
			analysisThread.interrupt();
			try {
				analysisThread.join();
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
		}
	}
	
	private void activateSequenceThresholdOverlay(boolean activate) {
//		System.out.println("activateSequenceThresholdOverlay "+activate);
		if (vSequence == null)
			return;
		vSequence.setThresholdOverlay(activate);
	}
	
	private void updateGuiEndFrame () {
		endFrame = vSequence.getSizeT()-1;
		endFrameTextField.setText( Integer.toString(endFrame));
	}
	
	private ArrayList<ROI2D> getROIsToAnalyze() {
		return vSequence.seq.getROI2Ds();
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	public static void main(String[] args)
	{
		Icy.main(args);
		PluginLauncher.start(PluginLoader.getPlugin(Areatrack.class.getName()));
	}

}
