package plugins.fmp.areatrack;

import java.awt.GridLayout;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import icy.gui.util.GuiUtil;

public class DlgTabOverlay extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 74570047959847295L;

	
	public void init(JTabbedPane tab, GridLayout capLayout) {
		JComponent panel = new JPanel(false);
		panel.setLayout(capLayout);
		
		panel.add( GuiUtil.besidesPanel( new JLabel("display image with no overlay")));
//		tabbedPane.addTab("None", null, panel, "Display image without overlay");
	}
}
