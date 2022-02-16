package plugins.fmp.areatrack;

import java.awt.GridLayout;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import icy.gui.util.GuiUtil;
import plugins.fmp.fmpTools.EnumImageOp;
import plugins.fmp.fmpTools.EnumThresholdType;

public class DlgTabThresholdMovement extends JPanel implements ChangeListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9098711267327847337L;
	JSpinner threshold2Spinner = new JSpinner(new SpinnerNumberModel(20, 0, 255, 1));
	Areatrack parent0 = null;

	
	public void init(JTabbedPane tab, GridLayout capLayout, Areatrack parent0) {
		
		this.parent0 = parent0;
		JComponent panel = new JPanel(false);
		panel.setLayout(capLayout);
		
		JLabel thresholdLabel2 = new JLabel("'move' threshold ");
		thresholdLabel2.setHorizontalAlignment(SwingConstants.RIGHT);
		panel.add( GuiUtil.besidesPanel( thresholdLabel2, threshold2Spinner));
		tab.addTab("Movement", null, panel, "Display parameters for thresholding movements (image n - (n-1)");
	
		threshold2Spinner.addChangeListener(this);
	}


	@Override
	public void stateChanged(ChangeEvent e) {
		if (e.getSource() == threshold2Spinner)
			updateThresholdOverlayParameters();
	}
	
	void updateThresholdOverlayParameters() {
		parent0.thresholdmovement = Integer.parseInt(threshold2Spinner.getValue().toString());
		parent0.setOverlayParameters(true, EnumImageOp.REF_PREVIOUS, EnumThresholdType.SINGLE, parent0.thresholdmovement);
	}
	
	public void transferParametersToDialog() {	
		threshold2Spinner.setValue(parent0.thresholdmovement);
	}
	
}
