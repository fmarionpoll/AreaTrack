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
import plugins.fmp.fmpSequence.OpenVirtualSequence;
import plugins.fmp.fmpSequence.SequencePlus;



public class Areatrack extends PluginActionable implements ActionListener, ChangeListener, ViewerListener
{	
	// -------------------------------------- interface
	IcyFrame mainFrame = new IcyFrame("AreaTrack 10-02-2022", true, true, true, true);

	DisplayCharts displayCharts = new DisplayCharts();
	
	// ---------------------------------------- video
	private JButton 	setVideoSourceButton= new JButton("Open...");
	private JButton		openROIsButton		= new JButton("Load...");
	private JButton		addROIsButton		= new JButton("Add...");
	private JButton		saveROIsButton		= new JButton("Save...");
	
	private JCheckBox measureSurfacesCheckBox = new JCheckBox("Measure surface of objects over threshold");
	private JRadioButton rbFilterbyColor	= new JRadioButton("filter by color array");
	private JRadioButton rbFilterbyFunction	= new JRadioButton("filter by function");
	private JCheckBox measureHeatmapCheckBox= new JCheckBox("Detect movement and build image heatmap");
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
	JComboBox<String> filterComboBox= new JComboBox<String> (new String[] {"raw data", "average", "median"});
	JTextField 	spanTextField		= new JTextField("10");

	private JButton 	updateChartsButton 	= new JButton("Display charts");
	private JButton 	exportToXLSButton 	= new JButton("Save XLS file..");
	private JButton		closeAllButton		= new JButton("Close views");

	//------------------------------------------- global variables
	SequencePlus vSequence 			= null;
	private ArrayList<MeasureAndName> resultsHeatMap = null;

	private int			analyzeStep 		= 1;
	int 		startFrame 			= 1;
	int 		endFrame 			= 99999999;
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
					    "Please refer to the online help:\n http://icy.bioimageanalysis.org/plugin/...", "Manual", JOptionPane.INFORMATION_MESSAGE );
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
	
	private void panelSetSourceInterface (JPanel mainPanel) {
		final JPanel panel = GuiUtil.generatePanel("SOURCE");
		mainPanel.add(GuiUtil.besidesPanel(panel));
		
		JPanel k0Panel = new JPanel();
		panel.add( GuiUtil.besidesPanel(setVideoSourceButton, k0Panel));
	}

	private void panelSetROIsInterface(JPanel mainPanel) {
		final JPanel panel =  GuiUtil.generatePanel("ROIs");
		mainPanel.add(GuiUtil.besidesPanel(panel));
		
		JLabel commentText1 = new JLabel ("Use ROItoArray plugin to create polygons ");
		commentText1.setHorizontalAlignment(SwingConstants.LEFT);
		panel.add(GuiUtil.besidesPanel(commentText1));
		JLabel emptyText1	= new JLabel (" ");
		panel.add(GuiUtil.besidesPanel(emptyText1, openROIsButton, addROIsButton, saveROIsButton));
	}
	
	private void panelSetAnalysisInterface(JPanel mainPanel) {
		final JPanel panel =  GuiUtil.generatePanel("ANALYSIS PARAMETERS");
		mainPanel.add(GuiUtil.besidesPanel(panel));

		panel.add( GuiUtil.besidesPanel(measureSurfacesCheckBox));
		panel.add( GuiUtil.besidesPanel(rbFilterbyColor, rbFilterbyFunction));
		ButtonGroup bgchoice = new ButtonGroup();
		bgchoice.add(rbFilterbyColor);
		bgchoice.add(rbFilterbyFunction);
		panel.add( GuiUtil.besidesPanel(measureHeatmapCheckBox ));
		
		GridLayout capLayout = new GridLayout(3, 2);
		panelAnalysisAdd_ThresholdOnColors(tabbedPane, capLayout);
		panelAnalysisAdd_ThresholdOnFilter(tabbedPane, capLayout);
		panelAnalysisAdd_MovementThreshold(tabbedPane, capLayout);
		panelAnalysisAdd_DisplayImagewithoutOverlay(tabbedPane, capLayout);
		tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		panel.add(GuiUtil.besidesPanel(tabbedPane));
		
		JLabel loadsaveText1 = new JLabel ("-> File (xml) ");
		loadsaveText1.setHorizontalAlignment(SwingConstants.RIGHT); 
		loadsaveText1.setFont(FontUtil.setStyle(loadsaveText1.getFont(), Font.ITALIC));
		panel.add(GuiUtil.besidesPanel( new JLabel (" "), loadsaveText1, openFiltersButton, saveFiltersButton));
	}
	
	private void panelAnalysisAdd_ThresholdOnColors(JTabbedPane tab, GridLayout capLayout) {
		JComponent panel = new JPanel(false);
		panel.setLayout(capLayout);
		
		colorPickCombo.setRenderer(colorPickComboRenderer);
		panel.add( GuiUtil.besidesPanel(pickColorButton, colorPickCombo, deleteColorButton));
		distanceLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		ButtonGroup bgd = new ButtonGroup();
		bgd.add(rbL1);
		bgd.add(rbL2);
		panel.add( GuiUtil.besidesPanel(distanceLabel, rbL1, rbL2, distanceSpinner));
		colorspaceLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		ButtonGroup bgcs = new ButtonGroup();
		bgcs.add(rbRGB);
		bgcs.add(rbHSV);
		bgcs.add(rbH1H2H3);
		panel.add( GuiUtil.besidesPanel(colorspaceLabel, rbRGB, rbHSV, rbH1H2H3));
		tab.addTab("Colors", null, panel, "Display parameters for thresholding an image with different colors and a distance");
	}
	
	private void panelAnalysisAdd_ThresholdOnFilter(JTabbedPane tab, GridLayout capLayout) {
		JComponent panel = new JPanel(false);
		//panel.setLayout(capLayout);
		
		videochannel.setHorizontalAlignment(SwingConstants.RIGHT);
		panel.add( GuiUtil.besidesPanel( videochannel, transformsComboBox));			
		thresholdLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		panel.add( GuiUtil.besidesPanel( thresholdLabel));
		panel.add( GuiUtil.besidesPanel( thresholdSpinner));
		tab.addTab("Filters", null, panel, "Display parameters for thresholding a transformed image with different filters");
	}
	
	private void panelAnalysisAdd_MovementThreshold(JTabbedPane tab, GridLayout capLayout) {
		JComponent panel = new JPanel(false);
		//panel.setLayout(capLayout);
		
		JLabel thresholdLabel2 = new JLabel("'move' threshold ");
		thresholdLabel2.setHorizontalAlignment(SwingConstants.RIGHT);
		panel.add( GuiUtil.besidesPanel( thresholdLabel2, threshold2Spinner));
		tab.addTab("Movement", null, panel, "Display parameters for thresholding movements (image n - (n-1)");
	}
	
	private void panelAnalysisAdd_DisplayImagewithoutOverlay(JTabbedPane tab, GridLayout capLayout) {
		JComponent panel = new JPanel(false);
		//panel.setLayout(capLayout);
		
		panel.add( GuiUtil.besidesPanel( new JLabel("display image with no overlay")));
		tabbedPane.addTab("None", null, panel, "Display image without overlay");
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
		panelSetSourceInterface(mainPanel);
		panelSetROIsInterface(mainPanel);
		panelSetAnalysisInterface(mainPanel);
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
//		thresholdOverlay = new OverlayThreshold();
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
				if (displayCharts.mainChartFrame != null) 
				{
					displayCharts.mainChartFrame.removeAll();
					displayCharts.mainChartFrame.close();
					displayCharts.mainChartFrame = null;
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
				updateCharts(); 
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
		
		setVideoSourceButton.addActionListener(new ActionListener () { @Override public void actionPerformed( final ActionEvent e ) {
				openVideoOrStack();
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

	private void openVideoOrStack() {
		String path = null;
		if (vSequence != null)
			vSequence.close();

		Sequence seq = OpenVirtualSequence.openImagesOrAvi(null);
		Viewer v = OpenVirtualSequence.initSequenceViewer(seq);
		v.addListener(Areatrack.this);
		vSequence = new SequencePlus(seq);
		
		path = vSequence.getDirectory();
		if (path != null) {
			XMLPreferences guiPrefs = this.getPreferences("gui");
			guiPrefs.put("lastUsedPath", path);
		}
		
		vSequence.capillariesRoi2RoiArray.capillariesArrayList.clear();
		updateGuiEndFrame();
		loadParametersFromXMLFile();
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
	
	private void loadParametersFromXMLFile() {
		String directory = vSequence.getDirectory();
		String fileparameters = directory + File.separator+ filename;
		final Document doc = XMLUtil.loadDocument(fileparameters);
		boolean flag = false;
		if (doc != null) {
			flag = xmlReadAreaTrackParameters(doc);
			if (flag) 
				transferParametersToDialog();
			else
				new AnnounceFrame("reading data failed");
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
	
	private void saveParametersToXMLFile() {
		
		String csFile = FmpTools.saveFileAs(filename, vSequence.getDirectory(), "xml");
		csFile.toLowerCase();
		if (!csFile.contains(".xml")) 
			csFile += ".xml";
		
		final Document doc = XMLUtil.createDocument(true);
		boolean flag = false;
		if (doc != null)
		{
			flag = xmlWriteAreaTrackParameters (doc);
			XMLUtil.saveDocument(doc, csFile);
		}
		if (!flag)
			new AnnounceFrame("saving data failed");
	}
	
	private boolean xmlReadAreaTrackParameters (Document doc) {

		String nodeName = "areaTrack";
		// read local parameters
		Node node = XMLUtil.getElement(XMLUtil.getRootElement(doc), nodeName);
		if (node == null)
			return false;

		Element xmlElement = XMLUtil.getElement(node, "Parameters");
		if (xmlElement == null) 
			return false;

		Element xmlVal = XMLUtil.getElement(xmlElement, "colormodeselected");
		boolean iscolorselected = XMLUtil.getAttributeBooleanValue(xmlVal, "value", true );
		rbFilterbyColor.setSelected(iscolorselected);
		
		xmlVal = XMLUtil.getElement(xmlElement, "colortransformop");	
		String codestring = XMLUtil.getAttributeValue(xmlVal, "descriptor", "none");		
		colortransformop = EnumImageOp.findByText(codestring);
			
		xmlVal = XMLUtil.getElement(xmlElement, "simpletransformop");
		codestring = XMLUtil.getAttributeValue(xmlVal, "descriptor", "none");
		simpletransformop = EnumImageOp.findByText(codestring);

		xmlVal = XMLUtil.getElement(xmlElement, "thresholdmovement");
		thresholdmovement = XMLUtil.getAttributeIntValue(xmlVal, "value", 20);
		
		xmlVal = XMLUtil.getElement(xmlElement, "colordistanceType");
		colordistanceType = XMLUtil.getAttributeIntValue(xmlVal, "value", 0);
		
		xmlVal = XMLUtil.getElement(xmlElement, "colorthreshold");
		colorthreshold = XMLUtil.getAttributeIntValue(xmlVal, "value", 20);
		
		colorarray.clear();
		xmlVal = XMLUtil.getElement(xmlElement, "ncolors");
		int ncolors = XMLUtil.getAttributeIntValue(xmlVal, "value", 0);
		for (int i= 0; i<ncolors; i++) {
			xmlVal = XMLUtil.getElement(xmlElement, "color"+Integer.toString(i));
			int alpha = XMLUtil.getAttributeIntValue(xmlVal, "a", 0);
			int red = XMLUtil.getAttributeIntValue(xmlVal, "r", 0);
			int blue = XMLUtil.getAttributeIntValue(xmlVal, "b", 0);
			int green = XMLUtil.getAttributeIntValue(xmlVal, "g", 0);
			Color color = new Color(red, green, blue, alpha);
			colorarray.add(color);
		}
		return true;
	}
	
	private boolean xmlWriteAreaTrackParameters (Document doc) {

		// save local parameters
		String nodeName = "areaTrack";
		Node node = XMLUtil.addElement(XMLUtil.getRootElement(doc), nodeName);
		if (node == null)
			return false;
		
		Element xmlElement = XMLUtil.addElement(node, "Parameters");
		
		Element xmlVal = XMLUtil.addElement(xmlElement, "colormodeselected");
		XMLUtil.setAttributeBooleanValue(xmlVal, "value", rbFilterbyColor.isSelected() );
	
		xmlVal = XMLUtil.addElement(xmlElement, "simpletransformop");
		XMLUtil.setAttributeValue(xmlVal, "descriptor", simpletransformop.toString());
		
		xmlVal = XMLUtil.addElement(xmlElement, "simplethreshold");
		XMLUtil.setAttributeIntValue(xmlVal, "value", Integer.parseInt(thresholdSpinner.getValue().toString()));
		
		xmlVal = XMLUtil.addElement(xmlElement, "colortransformop");
		XMLUtil.setAttributeValue(xmlVal, "descriptor", colortransformop.toString());
		
		xmlVal = XMLUtil.addElement(xmlElement, "thresholdtype");
		XMLUtil.setAttributeValue(xmlVal, "descriptor", thresholdtype.toString());	
		
		xmlVal = XMLUtil.addElement(xmlElement, "colordistanceType");
		XMLUtil.setAttributeIntValue(xmlVal, "value", colordistanceType);

		xmlVal = XMLUtil.addElement(xmlElement, "thresholdmovement");
		XMLUtil.setAttributeIntValue(xmlVal, "value", thresholdmovement);
		
		xmlVal = XMLUtil.addElement(xmlElement, "colorthreshold");
		XMLUtil.setAttributeIntValue(xmlVal, "value", colorthreshold);
		
		xmlVal = XMLUtil.addElement(xmlElement, "ncolors");
		XMLUtil.setAttributeIntValue(xmlVal, "value", colorarray.size());
		for (int i=0; i<colorarray.size(); i++) {
			Color color = colorarray.get(i);
			xmlVal = XMLUtil.addElement(xmlElement, "color"+Integer.toString(i));
			XMLUtil.setAttributeIntValue(xmlVal, "a", color.getAlpha());
			XMLUtil.setAttributeIntValue(xmlVal, "r", color.getRed());
			XMLUtil.setAttributeIntValue(xmlVal, "g", color.getGreen());
			XMLUtil.setAttributeIntValue(xmlVal, "b", color.getBlue());
		}
		
		return true;
	}
	
	private void pickColor() {
		
		boolean bActiveTrapOverlay = false;
		
		if (pickColorButton.getText().contains("*") || pickColorButton.getText().contains(":")) {
			pickColorButton.setBackground(Color.LIGHT_GRAY);
			pickColorButton.setText(textPickAPixel);
			bActiveTrapOverlay = false;
		}
		else
		{
			pickColorButton.setText("*"+textPickAPixel+"*");
			pickColorButton.setBackground(Color.DARK_GRAY);
			bActiveTrapOverlay = true;
		}	
		vSequence.setMouseTrapOverlay(bActiveTrapOverlay, pickColorButton, colorPickCombo);
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
//		thresholdOverlayON = activate;
//		if (activate) {
//			if (!thresholdOverlayON) {
//				if (thresholdOverlay == null) {
//					//System.out.println("create overlay");
//					thresholdOverlay = new OverlayThreshold(vSequence);
//				}
//				if (!vSequence.contains(thresholdOverlay)) 
//					vSequence.addOverlay(thresholdOverlay);
//				thresholdOverlayON = true;
//			}			
//		}
//		else {
//			if (thresholdOverlayON && thresholdOverlay != null) {
//				if (vSequence.contains(thresholdOverlay) ) {
//					vSequence.removeOverlay(thresholdOverlay);
//					//System.out.println("remove overlay");
//				}
//			}
//			thresholdOverlayON = false;
//		}
	}
	
	private void updateThresholdOverlayParameters() {
		
		if (vSequence == null)
			return;
		
		boolean activateThreshold = true;
		int thresholdForOverlay = 0;
		EnumImageOp transformOpForOverlay = EnumImageOp.NONE;
		EnumThresholdType thresholdTypeForOverlay = EnumThresholdType.SINGLE;
		
		switch (tabbedPane.getSelectedIndex()) {
			case 0:  // color array
				colorthreshold = Integer.parseInt(distanceSpinner.getValue().toString());
				thresholdForOverlay = colorthreshold;
				thresholdtype = EnumThresholdType.COLORARRAY;
				thresholdTypeForOverlay = thresholdtype;
				transformOpForOverlay = colortransformop;
				colorarray.clear();
				for (int i=0; i<colorPickCombo.getItemCount(); i++) {
					colorarray.add(colorPickCombo.getItemAt(i));
				}
				colordistanceType = 1;
				if (rbL2.isSelected()) 
					colordistanceType = 2;
				break;
				
			case 1:	// simple filter & single threshold
				simpletransformop = (EnumImageOp) transformsComboBox.getSelectedItem();
				transformOpForOverlay = simpletransformop;
				simplethreshold = Integer.parseInt(thresholdSpinner.getValue().toString());
				thresholdForOverlay = simplethreshold; 
				thresholdtype = EnumThresholdType.SINGLE;
				thresholdTypeForOverlay = thresholdtype;	
				break;

			case 2:	// movement threshold
				thresholdmovement = Integer.parseInt(threshold2Spinner.getValue().toString());
				thresholdForOverlay = thresholdmovement; 
				thresholdTypeForOverlay = EnumThresholdType.SINGLE;
				transformOpForOverlay = EnumImageOp.REF_PREVIOUS;
				break;
			
			case 3:	// nothing
			default:
				activateThreshold = false;
				break;
		}
		
		//--------------------------------
		activateSequenceThresholdOverlay(activateThreshold);
		
		if (activateThreshold && vSequence != null) {
			vSequence.setThresholdOverlay(activateThreshold);
			if (thresholdTypeForOverlay == EnumThresholdType.SINGLE)
				vSequence.setThresholdOverlayParametersSingle(transformOpForOverlay, thresholdForOverlay);
			else
				vSequence.setThresholdOverlayParametersColors(transformOpForOverlay, colorarray, colordistanceType, colorthreshold);
		}
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
	
	private void updateCharts() 
	{
		displayCharts.updateCharts(this); 
	}
	
	public static void main(String[] args)
	{
		Icy.main(args);
		PluginLauncher.start(PluginLoader.getPlugin(Areatrack.class.getName()));
	}

}
