package plugins.fmp.areatrack;


import java.awt.BorderLayout;
import java.awt.Color;
import java.util.ArrayList;

import javax.swing.JPanel;

import icy.gui.frame.IcyFrame;
import icy.gui.util.GuiUtil;
import icy.gui.viewer.Viewer;
import icy.gui.viewer.ViewerEvent;
import icy.gui.viewer.ViewerEvent.ViewerEventType;
import icy.gui.viewer.ViewerListener;
import icy.main.Icy;
import icy.plugin.PluginLauncher;
import icy.plugin.PluginLoader;
import icy.plugin.abstract_.PluginActionable;
import icy.sequence.DimensionId;

import plugins.fmp.fmpTools.EnumImageOp;
import plugins.fmp.fmpTools.EnumThresholdType;
import plugins.fmp.fmpSequence.SequencePlus;



public class Areatrack extends PluginActionable implements ViewerListener
{	
			IcyFrame mainFrame = new IcyFrame("AreaTrack 19-02-2022", true, true, true, true);
			GraphsWindow displayCharts = null;
	
			DlgSourcePanel dlgSourcePanel = new DlgSourcePanel();
			DlgRoisPanel dlgRoisPanel = new DlgRoisPanel();
			DlgAnalysisParameters dlgAnalysisParameters = new DlgAnalysisParameters();
			DlgAnalysisRun dlgAnalysisRun = new DlgAnalysisRun();
			DlgResults dlgResults = new DlgResults();

			int	 analyzeStep 				= 1;
			int  startFrame 				= 1;
			int  endFrame 					= 99999999;
	
			EnumThresholdType thresholdtype	= EnumThresholdType.COLORARRAY; 
			EnumImageOp simpletransformop 	= EnumImageOp.R2MINUS_GB;
			int simplethreshold 			= 20;
			EnumImageOp colortransformop 	= EnumImageOp.NONE;
			int colordistanceType 			= 0;
			int colorthreshold 				= 20;
			ArrayList <Color> colorarray 	= new ArrayList <Color>();
			int thresholdmovement 			= 20;
			
	public 	SequencePlus vSequence 			= null;
	final 	String filenameAreatrackXml 	= "areatrack.xml";
	
	// --------------------------------------------------------------------------

	@Override
	public void run() {
		
		JPanel mainPanel = GuiUtil.generatePanelWithoutBorder();
		mainFrame.setLayout(new BorderLayout());
		mainFrame.add(mainPanel, BorderLayout.CENTER);
			
		DlgMenuBar.panelSetMenuBar(mainFrame, mainPanel);
		dlgSourcePanel.init(this, mainFrame, mainPanel);
		dlgRoisPanel.init(this, mainFrame, mainPanel);
		dlgAnalysisParameters.init(this, mainFrame, mainPanel);
		dlgAnalysisRun.init(this, mainFrame, mainPanel);
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

	public static void main(String[] args) {
		Icy.main(args);
		PluginLauncher.start(PluginLoader.getPlugin(Areatrack.class.getName()));
	}

	public void setOverlayParameters(boolean activateThreshold, EnumImageOp transformOpForOverlay, EnumThresholdType thresholdTypeForOverlay, int thresholdForOverlay) {
		if (vSequence == null) return;
				
		vSequence.setThresholdOverlay(activateThreshold);
		if (activateThreshold ) {
			if (thresholdTypeForOverlay == EnumThresholdType.SINGLE)
				vSequence.setThresholdOverlayParametersSingle(transformOpForOverlay, thresholdForOverlay);
			else
				vSequence.setThresholdOverlayParametersColors(transformOpForOverlay, colorarray, colordistanceType, colorthreshold);
		}
	}
	
	
}
