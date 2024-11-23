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
import plugins.fmp.areatrack.commons.DetectionParameters;
import plugins.fmp.areatrack.commons.GraphsWindow;
import plugins.fmp.areatrack.dlg.Dlg1_Source;
import plugins.fmp.areatrack.dlg.Dlg2_Grids;
import plugins.fmp.areatrack.dlg.Dlg3_ParametersArea;
import plugins.fmp.areatrack.dlg.Dlg4_ParametersMovements;
import plugins.fmp.areatrack.dlg.Dlg5_AnalysisRun;
import plugins.fmp.areatrack.dlg.Dlg6_ResultsExport;
import plugins.fmp.areatrack.sequence.SequencePlus;
import plugins.fmp.areatrack.tools.EnumAreaDetection;

public class Areatrack extends PluginActionable implements ViewerListener {
	public IcyFrame mainFrame = new IcyFrame("AreaTrack 3-Nov-2024", true, true, true, true);
	public GraphsWindow displayCharts = null;

	Dlg1_Source dlg1_Source = new Dlg1_Source();
	Dlg2_Grids dlg2_Grids = new Dlg2_Grids();
	Dlg3_ParametersArea dlg3_ParametersArea = new Dlg3_ParametersArea();
	Dlg4_ParametersMovements dlg4_ParametersMovements = new Dlg4_ParametersMovements();
	public Dlg5_AnalysisRun dlg5_AnalysisRun = new Dlg5_AnalysisRun();
	public Dlg6_ResultsExport dlg6_ResultsExport = new Dlg6_ResultsExport();
	public DetectionParameters detectionParameters = new DetectionParameters();
	public SequencePlus vSequence = null;

	// --------------------------------------------------------------------------

	@Override
	public void run() {

		JPanel mainPanel = GuiUtil.generatePanelWithoutBorder();

		dlg1_Source.init(this, mainFrame, mainPanel, "1 - Images stack");
		dlg2_Grids.init(this, mainFrame, mainPanel, "2 - Cells grid define/load");
		dlg3_ParametersArea.init(this, mainFrame, mainPanel, "3 - Area measure parameters");
		dlg4_ParametersMovements.init(this, mainFrame, mainPanel, "4 - Movements detection parameters");
		dlg5_AnalysisRun.init(this, mainFrame, mainPanel, "5 - Run analysis");
		dlg6_ResultsExport.init(this, mainFrame, mainPanel, "6 - Display/export results");

		mainFrame.setLayout(new BorderLayout());
		mainFrame.add(mainPanel, BorderLayout.WEST);

		mainFrame.pack();
		mainFrame.center();
		mainFrame.setVisible(true);
		mainFrame.addToDesktopPane();
	}

	@Override
	public void viewerChanged(ViewerEvent event) {
		if ((event.getType() == ViewerEventType.POSITION_CHANGED) && (event.getDim() == DimensionId.T))
			vSequence.currentFrame = event.getSource().getPositionT();
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
		if (vSequence == null)
			return;
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
