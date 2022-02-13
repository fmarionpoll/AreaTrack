package plugins.fmp.areatrack.dlg.analysisParameters;

import java.awt.GridLayout;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;

import icy.gui.util.GuiUtil;

public class PanelMovement {
	private void panelAnalysisAdd_MovementThreshold(JTabbedPane tab, GridLayout capLayout) {
		JComponent panel = new JPanel(false);
		//panel.setLayout(capLayout);
		
		JLabel thresholdLabel2 = new JLabel("'move' threshold ");
		thresholdLabel2.setHorizontalAlignment(SwingConstants.RIGHT);
		panel.add( GuiUtil.besidesPanel( thresholdLabel2, threshold2Spinner));
		tab.addTab("Movement", null, panel, "Display parameters for thresholding movements (image n - (n-1)");
	}
}
