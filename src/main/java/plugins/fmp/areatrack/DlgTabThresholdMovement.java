package plugins.fmp.areatrack;

import java.awt.GridLayout;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;

import icy.gui.util.GuiUtil;

public class DlgTabThresholdMovement extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9098711267327847337L;
	JSpinner threshold2Spinner = new JSpinner(new SpinnerNumberModel(20, 0, 255, 1));

	
	public void init(JTabbedPane tab, GridLayout capLayout) {
		JComponent panel = new JPanel(false);
		panel.setLayout(capLayout);
		
		JLabel thresholdLabel2 = new JLabel("'move' threshold ");
		thresholdLabel2.setHorizontalAlignment(SwingConstants.RIGHT);
		panel.add( GuiUtil.besidesPanel( thresholdLabel2, threshold2Spinner));
		tab.addTab("Movement", null, panel, "Display parameters for thresholding movements (image n - (n-1)");
	}
	
}
