package plugins.fmp.areatrack;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.util.ArrayList;



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
import icy.util.XMLUtil;

import plugins.fmp.fmpTools.ComboBoxColorRenderer;
import plugins.fmp.fmpTools.EnumImageOp;
import plugins.fmp.fmpTools.EnumThresholdType;
import plugins.fmp.fmpTools.FmpTools;
import plugins.fmp.fmpSequence.OpenVirtualSequence;
import plugins.fmp.fmpSequence.SequencePlus;



public class Areatrack extends PluginActionable implements ActionListener, ChangeListener, ViewerListener
{	
	// -------------------------------------- interface
			IcyFrame mainFrame 				= new IcyFrame("AreaTrack 14-02-2022", true, true, true, true);
			DisplayCharts displayCharts 	= null;
	
	// ---------------------------------------- video
	private JButton setVideoSourceButton	= new JButton("Open...");
	private JButton closeAllButton			= new JButton("Close");

	private JButton openROIsButton			= new JButton("Load...");
	private JButton addROIsButton			= new JButton("Add...");
	private JButton	saveROIsButton			= new JButton("Save...");
	
	private JCheckBox measureSurfacesCheckBox = new JCheckBox("Measure surface using");
			JRadioButton rbFilterbyColor 	= new JRadioButton("color array");
	private JRadioButton rbFilterbyFunction	= new JRadioButton("filters");
			JCheckBox measureHeatmapCheckBox = new JCheckBox("Detect movement and build image heatmap");
	// TODO
	private JButton startComputationButton 	= new JButton("Start");
	private JButton stopComputationButton	= new JButton("Stop");
	private JTextField startFrameTextField	= new JTextField("0");
	private JTextField endFrameTextField	= new JTextField("99999999");
	
			JComboBox<EnumImageOp> transformsComboBox = new JComboBox<EnumImageOp> (new EnumImageOp[] {
					EnumImageOp.R_RGB, EnumImageOp.G_RGB, EnumImageOp.B_RGB, 
					EnumImageOp.R2MINUS_GB, EnumImageOp.G2MINUS_RB, EnumImageOp.B2MINUS_RG, 
					EnumImageOp.NORM_BRMINUSG, EnumImageOp.RGB,
					EnumImageOp.H_HSB, EnumImageOp.S_HSB, EnumImageOp.B_HSB	});
			JSpinner thresholdSpinner 		= new JSpinner(new SpinnerNumberModel(70, 0, 255, 1));
	private JLabel videochannel 			= new JLabel("filter  ");
	private JLabel thresholdLabel 			= new JLabel("threshold ");
			JSpinner threshold2Spinner 				= new JSpinner(new SpinnerNumberModel(20, 0, 255, 1));
	private JTextField analyzeStepTextField	= new JTextField("1");
		
	//---------------------------------------------------------------------------
			JTabbedPane tabbedPane 			= new JTabbedPane();
			JComboBox<Color> colorPickCombo = new JComboBox<Color>();
	private ComboBoxColorRenderer colorPickComboRenderer = new ComboBoxColorRenderer(colorPickCombo);
	
	private String textPickAPixel 			= "Pick a pixel";
	private JButton pickColorButton			= new JButton(textPickAPixel);
	private JButton	deleteColorButton		= new JButton("Delete color");
			JRadioButton rbL1 				= new JRadioButton("L1");
			JRadioButton rbL2 				= new JRadioButton("L2");
			JSpinner distanceSpinner 		= new JSpinner(new SpinnerNumberModel(10, 0, 800, 5));
			JRadioButton rbRGB 				= new JRadioButton("RGB");
			JRadioButton rbHSV 				= new JRadioButton("HSV");
			JRadioButton rbH1H2H3 			= new JRadioButton("H1H2H3");
	private JLabel distanceLabel 			= new JLabel("Distance  ");
	private JLabel colorspaceLabel 			= new JLabel("Color space ");
	private JButton openFiltersButton		= new JButton("Load...");
	private JButton saveFiltersButton		= new JButton("Save...");
	
	//---------------------------------------------------------------------------
			JComboBox<String> filterComboBox = new JComboBox<String> (new String[] {"raw data", "average", "median"});
			JTextField 	spanTextField 		= new JTextField("10");

	private JButton updateChartsButton 		= new JButton("Chart window");
	private JButton setGraphsOverlayButton	= new JButton("Curves");
	
	private JButton exportToXLSButton 		= new JButton("Save XLS file..");
	
	//------------------------------------------- global variables
			SequencePlus vSequence 			= null;
			int	 analyzeStep 				= 1;
			int  startFrame 				= 1;
			int  endFrame 					= 99999999;
			AreaAnalysisThread analysisThread = null;
	
			EnumThresholdType thresholdtype	= EnumThresholdType.COLORARRAY; 
			EnumImageOp simpletransformop 	= EnumImageOp.R2MINUS_GB;
			int simplethreshold 			= 20;
			EnumImageOp colortransformop 	= EnumImageOp.NONE;
			int colordistanceType 			= 0;
			int colorthreshold 				= 20;
			ArrayList <Color> colorarray 	= new ArrayList <Color>();
			int thresholdmovement 			= 20;	
	final 	String filename 				= "areatrack.xml";
	
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
		panel.add( GuiUtil.besidesPanel(setVideoSourceButton, closeAllButton));
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

		FlowLayout layoutLeft = new FlowLayout(FlowLayout.LEFT); 
		JPanel panel0 = new JPanel(layoutLeft);
		((FlowLayout)panel0.getLayout()).setVgap(0);
		panel0.add(measureSurfacesCheckBox);
		panel0.add(rbFilterbyColor);
		panel0.add(rbFilterbyFunction);
		panel.add(panel0);
		
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
		panel.add( GuiUtil.besidesPanel(videochannel, transformsComboBox));			
		thresholdLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		panel.add( GuiUtil.besidesPanel(thresholdLabel));
		panel.add( GuiUtil.besidesPanel(thresholdSpinner));
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
		JLabel endLabel 	= new JLabel("to ");
		JLabel stepLabel 	= new JLabel("step ");
		
		FlowLayout layoutLeft = new FlowLayout(FlowLayout.LEFT); 
		JPanel panel0 = new JPanel(layoutLeft);
		((FlowLayout)panel0.getLayout()).setVgap(0);
		panel0.add(startLabel);
		panel0.add(startFrameTextField);
		panel0.add(endLabel);
		panel0.add(endFrameTextField);
		panel0.add(stepLabel);
		panel0.add(analyzeStepTextField);
		panel.add(panel0);
	}
	
	private void panelSetResultsInterface(JPanel mainPanel) {
		final JPanel panel = GuiUtil.generatePanel("RESULTS DISPLAY/EXPORT");
		mainPanel.add(GuiUtil.besidesPanel(panel));
		
		JLabel outputLabel = new JLabel ("output ");
		outputLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		JLabel spanLabel = new JLabel ("span ");
		spanLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		panel.add(GuiUtil.besidesPanel(outputLabel, filterComboBox, spanLabel, spanTextField));
		panel.add(GuiUtil.besidesPanel(updateChartsButton, setGraphsOverlayButton))
;		panel.add(GuiUtil.besidesPanel(exportToXLSButton)); 
	}
	
	@Override
	public void run() {
		
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
		measureHeatmapCheckBox.setSelected(false);

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
		closeAllButton.addActionListener(new ActionListener () { 
			@Override public void actionPerformed( final ActionEvent e ) { 
				if (displayCharts != null && displayCharts.mainChartFrame != null) 
				{
					displayCharts.mainChartFrame.removeAll();
					displayCharts.mainChartFrame.close();
					displayCharts.mainChartFrame = null;
				}
				vSequence.close();
			} } );
		
		rbRGB.addActionListener(new ActionListener () { 
			@Override public void actionPerformed( final ActionEvent e ) { 
				colortransformop = EnumImageOp.NONE;
				updateThresholdOverlayParameters();
			} } );
		
		rbHSV.addActionListener(new ActionListener () { 
			@Override public void actionPerformed( final ActionEvent e ) { 
				colortransformop = EnumImageOp.RGB_TO_HSV;
				updateThresholdOverlayParameters();
			} } );
		
		rbH1H2H3.addActionListener(new ActionListener () { 
			@Override public void actionPerformed( final ActionEvent e ) { 
				colortransformop = EnumImageOp.RGB_TO_H1H2H3;
				updateThresholdOverlayParameters();
			} } );
		
		rbL1.addActionListener(new ActionListener () { 
			@Override public void actionPerformed( final ActionEvent e ) { 
				updateThresholdOverlayParameters();
			} } );
		
		rbL2.addActionListener(new ActionListener () { 
			@Override public void actionPerformed( final ActionEvent e ) { 
				updateThresholdOverlayParameters();
			} } );
		
		stopComputationButton.addActionListener(new ActionListener () { 
			@Override public void actionPerformed( final ActionEvent e ) { 
				stopAnalysisThread();
			} } );
		
		startComputationButton.addActionListener(new ActionListener () { 
			@Override public void actionPerformed( final ActionEvent e ) {  
				startAnalysisThread(); 
			} } );		
		
		updateChartsButton.addActionListener(new ActionListener () { 
			@Override public void actionPerformed( final ActionEvent e ) {
				updateCharts(); 
			} } );
		
		setGraphsOverlayButton.addActionListener(new ActionListener () { 
			@Override public void actionPerformed( final ActionEvent e ) {
				setGraphsOverlay(); 
			} } );
		
		deleteColorButton.addActionListener(new ActionListener () { 
			@Override public void actionPerformed( final ActionEvent e ) { 
				if (colorPickCombo.getItemCount() > 0 && colorPickCombo.getSelectedIndex() >= 0)
					colorPickCombo.removeItemAt(colorPickCombo.getSelectedIndex());
				updateThresholdOverlayParameters();
			} } );
			
		transformsComboBox.addActionListener(new ActionListener () {
			@Override public void actionPerformed( final ActionEvent e ) { 
				updateThresholdOverlayParameters(); 
			} } );
		
		openFiltersButton.addActionListener(new ActionListener () { 
			@Override public void actionPerformed( final ActionEvent e ) { 
				xmlReadAreaTrackParameters(); 
			} } );
		
		saveFiltersButton.addActionListener(new ActionListener () {
			@Override public void actionPerformed( final ActionEvent e ) { 
				xmlWriteAreaTrackParameters(); 
			} } );
		
		openROIsButton.addActionListener(new ActionListener () { 
			@Override public void actionPerformed( final ActionEvent e ) { 
				LoadRois loadRois = new LoadRois();
				loadRois.openROIs(vSequence); 
				updateStartAndEndFrameFromvSequence();
			} } );
		
		saveROIsButton.addActionListener(new ActionListener () { 
			@Override public void actionPerformed( final ActionEvent e ) {
				vSequence.analysisStart = startFrame;
				vSequence.analysisEnd = endFrame;
				LoadRois loadRois = new LoadRois();
				loadRois.saveROIs(vSequence); 
			} } );
		
		addROIsButton.addActionListener(new ActionListener () { 
			@Override public void actionPerformed( final ActionEvent e ) {
				LoadRois loadRois = new LoadRois();
				loadRois.addROIs(vSequence);
				updateStartAndEndFrameFromvSequence();
			} } );
		
		pickColorButton.addActionListener(new ActionListener () { 
			@Override public void actionPerformed( final ActionEvent e ) { 
				pickColor(); 
			} } );

		exportToXLSButton.addActionListener(new ActionListener () { 
			@Override public void actionPerformed( final ActionEvent e ) {
				exportToXLS();
			} } );
		
		setVideoSourceButton.addActionListener(new ActionListener () { 
			@Override public void actionPerformed( final ActionEvent e ) {
				openVideoOrStack();
			} } );
		
		rbFilterbyColor.addActionListener(new ActionListener () { 
			@Override public void actionPerformed( final ActionEvent e ) {
			if (rbFilterbyColor.isSelected())
				selectTab(0);
		} } );
		
		rbFilterbyFunction.addActionListener(new ActionListener () { 
			@Override public void actionPerformed( final ActionEvent e ) {
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
	
	private void updateStartAndEndFrameFromvSequence()
	{
		startFrame = (int) vSequence.analysisStart;
		endFrame = (int) vSequence.analysisEnd;
		endFrameTextField.setText( Integer.toString(endFrame));
		startFrameTextField.setText( Integer.toString(startFrame));
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
		
		updateGuiEndFrame();
		xmlReadAreaTrackParameters();
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
		
		analysisThread.setAnalysisThreadParameters(
			vSequence, 
			getROIsToAnalyze(), 
			startFrame, 
			endFrame,  
			transformop, 
			thresholdforsurface,
			thresholdformovement,
			measureSurfacesCheckBox.isSelected(), 
			measureHeatmapCheckBox.isSelected());
		analysisThread.setAnalysisThreadParametersColors (
			thresholdtype, 
			colordistanceType, 
			colorthreshold, 
			colorarray);
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
		if (vSequence == null) return;
		
		vSequence.setThresholdOverlay(activate);
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
	
	private void xmlReadAreaTrackParameters() {
		XmlAreaTrack xmlAreaTrack = new XmlAreaTrack();
		xmlAreaTrack.xmlReadAreaTrackParameters(this);
	}
	
	private void xmlWriteAreaTrackParameters() {
		XmlAreaTrack xmlAreaTrack = new XmlAreaTrack();
		xmlAreaTrack.xmlWriteAreaTrackParameters(this);
	}
	
	private void updateCharts() {
		displayCharts = new DisplayCharts();
		int span = Integer.parseInt(spanTextField.getText());
		int filteroption = filterComboBox.getSelectedIndex();
		displayCharts.updateCharts(vSequence, startFrame, endFrame, filteroption, span, analyzeStep); 
	}
	
	private void setGraphsOverlay() {
		GraphsOverlay displayGraphs = new GraphsOverlay();
		int span = Integer.parseInt(spanTextField.getText());
		int filteroption = filterComboBox.getSelectedIndex();
		displayGraphs.updateCharts(vSequence, startFrame, endFrame, filteroption, span, analyzeStep); 
	}
	
	private void exportToXLS() {
		String file = FmpTools.saveFileAs(null, vSequence.getDirectory(), "xls");
		if (file != null) {	
			ExportToXLS exportToXLS = new ExportToXLS();
			final String filename = file; 
			exportToXLS.exportToXLS(this, filename);
		}
	}
	
	public static void main(String[] args) {
		Icy.main(args);
		PluginLauncher.start(PluginLoader.getPlugin(Areatrack.class.getName()));
	}

}
