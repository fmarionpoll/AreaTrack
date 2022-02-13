package plugins.fmp.areatrack.dlg.analysisParameters;

import java.awt.GridLayout;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;

import icy.gui.util.GuiUtil;

public class PanelFunctions {
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
}
