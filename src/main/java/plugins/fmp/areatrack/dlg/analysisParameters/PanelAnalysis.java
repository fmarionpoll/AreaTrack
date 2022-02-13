package plugins.fmp.areatrack.dlg.analysisParameters;

import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;

import icy.gui.util.FontUtil;
import icy.gui.util.GuiUtil;

public class PanelAnalysis extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6421417411235586565L;
	private JCheckBox measureSurfacesCheckBox = new JCheckBox("Measure surface of objects over threshold");
	private JRadioButton rbFilterbyColor	= new JRadioButton("filter by color array");
	private JRadioButton rbFilterbyFunction	= new JRadioButton("filter by function");
	private JCheckBox measureHeatmapCheckBox= new JCheckBox("Detect movement and build image heatmap");
	
	public void init(JPanel mainPanel) {
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
	

	private void panelAnalysisAdd_DisplayImagewithoutOverlay(JTabbedPane tab, GridLayout capLayout) {
		JComponent panel = new JPanel(false);
		//panel.setLayout(capLayout);
		
		panel.add( GuiUtil.besidesPanel( new JLabel("display image with no overlay")));
		tabbedPane.addTab("None", null, panel, "Display image without overlay");
	}
	

}
