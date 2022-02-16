package plugins.fmp.areatrack;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;

import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import icy.gui.component.PopupPanel;
import icy.gui.frame.IcyFrame;
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

import plugins.fmp.fmpTools.ComboBoxColorRenderer;
import plugins.fmp.fmpTools.EnumImageOp;
import plugins.fmp.fmpTools.EnumThresholdType;
import plugins.fmp.fmpTools.FmpTools;
import plugins.fmp.fmpSequence.OpenVirtualSequence;
import plugins.fmp.fmpSequence.SequencePlus;



public class Areatrack extends PluginActionable implements ActionListener, ViewerListener
{	
	// -------------------------------------- interface
			IcyFrame mainFrame 				= new IcyFrame("AreaTrack 15-02-2022", true, true, true, true);
			GraphsWindow displayCharts 		= null;
	
			DlgSourcePanel dlgSourcePanel = new DlgSourcePanel();
			DlgROIsPanel dlgRoisPanel = new DlgROIsPanel();
			DlgAnalysis dlgAnalysis = new DlgAnalysis();
			DlgRunAnalysis dlgRunAnalysis = new DlgRunAnalysis();
			DlgResults dlgResults = new DlgResults();

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

	
	@Override
	public void run() {
		
		// build and display the GUI
		JPanel mainPanel = GuiUtil.generatePanelWithoutBorder();
		mainFrame.setLayout(new BorderLayout());
		mainFrame.add(mainPanel, BorderLayout.CENTER);
			
		DlgMenuBar.panelSetMenuBar(mainFrame, mainPanel);
		dlgSourcePanel.init(this, mainFrame, mainPanel);
		dlgRoisPanel.init(this, mainFrame, mainPanel);
		dlgAnalysis.init(this, mainFrame, mainPanel);
		dlgRunAnalysis.init(this, mainFrame, mainPanel);
		dlgResults.init(this, mainFrame, mainPanel);
		
		mainFrame.pack();
		mainFrame.center();
		mainFrame.setVisible(true);
		mainFrame.addToDesktopPane();
		mainFrame.requestFocus();	
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

	
	private ArrayList<ROI2D> getROIsToAnalyze() {
		return vSequence.seq.getROI2Ds();
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	private void updateCharts() {
		displayCharts = new GraphsWindow();
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
