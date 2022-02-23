package plugins.fmp.areatrack;


import java.awt.BorderLayout;

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

import plugins.fmp.fmpTools.EnumAreaDetection;
import plugins.fmp.fmpSequence.SequencePlus;



public class Areatrack extends PluginActionable implements ViewerListener
{	
	IcyFrame mainFrame = new IcyFrame("AreaTrack 22-02-2022", true, true, true, true);
	GraphsWindow displayCharts = null;

	Dlg1Source dlg1Source = new Dlg1Source();
	Dlg2Grids dlg2Grids = new Dlg2Grids();
	Dlg3ParametersArea dlg3ParametersArea = new Dlg3ParametersArea();
	Dlg4ParametersMovements dlg4ParametersMovements = new Dlg4ParametersMovements();
	Dlg5AnalysisRun dlg5AnalysisRun = new Dlg5AnalysisRun();
	Dlg6ResultsExport dlg6ResultsExport = new Dlg6ResultsExport();

	DetectionParameters detectionParameters = new DetectionParameters();
			
	public 	SequencePlus vSequence 			= null;
		
	// --------------------------------------------------------------------------

	@Override
	public void run() {
		
		JPanel mainPanel = GuiUtil.generatePanelWithoutBorder();
		mainFrame.setLayout(new BorderLayout());
		mainFrame.add(mainPanel, BorderLayout.CENTER);
			
		DlgMenuBar.panelSetMenuBar(mainFrame, mainPanel);
		dlg1Source.init(this, mainFrame, mainPanel, "1 - IMAGES STACK");
		dlg2Grids.init(this, mainFrame, mainPanel, "2 - DEFINE/LOAD GRID");
		dlg3ParametersArea.init(this, mainFrame, mainPanel, "3 - PARAMETERS: AREA");
		dlg4ParametersMovements.init(this, mainFrame, mainPanel, "4 - PARAMETERS: MOVEMENTS");
		dlg5AnalysisRun.init(this, mainFrame, mainPanel, "5 - RUN ANALYSIS");
		dlg6ResultsExport.init(this, mainFrame, mainPanel, "6 - RESULTS DISPLAY/EXPORT");
		
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

	public void setOverlay(boolean displayOverlay) {
		if (vSequence == null) return;
		detectionParameters.displayOverlay = displayOverlay;
		vSequence.setThresholdOverlay(displayOverlay);
		if (displayOverlay) {
			if (detectionParameters.areaDetectionMode == EnumAreaDetection.SINGLE)
				vSequence.setThresholdOverlayParametersSingle(detectionParameters);
			else
				vSequence.setThresholdOverlayParametersColors(detectionParameters);
		}
	}
	
	
}
