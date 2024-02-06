package plugins.fmp.areatrack.dlg;

import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import icy.gui.component.PopupPanel;
import icy.gui.frame.IcyFrame;


public class Dlg4ParametersMovements extends JPanel implements ChangeListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2921692090190746442L;
	JCheckBox detectCheckBox = new JCheckBox("Detect ");
	JSpinner threshold2Spinner = new JSpinner(new SpinnerNumberModel(20, 0, 255, 1));
	Areatrack areatrack = null;

	

	public void init(Areatrack areatrack, IcyFrame mainFrame, JPanel mainPanel, String title) {
		
		this.areatrack = areatrack;
		PopupPanel 	capPopupPanel = new PopupPanel(title);
		capPopupPanel.collapse();
		capPopupPanel.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				mainFrame.revalidate();
				mainFrame.pack();
				mainFrame.repaint();
			}});
		mainPanel.add(capPopupPanel);
		
		JPanel capPanel = capPopupPanel.getMainPanel();
		capPanel.setLayout(new GridLayout(1, 1));
		
		FlowLayout layoutRight = new FlowLayout(FlowLayout.LEFT); 
		JPanel panel1 = new JPanel(layoutRight);
		JLabel thresholdLabel2 = new JLabel("'move' threshold ");
		panel1.add(detectCheckBox);
		panel1.add(thresholdLabel2);
		panel1.add(threshold2Spinner);
		capPanel.add(panel1);
		
		threshold2Spinner.addChangeListener(this);
	}


	@Override
	public void stateChanged(ChangeEvent e) {
		if (e.getSource() == threshold2Spinner)
			updateThresholdOverlayParameters();
	}
	
	void updateThresholdOverlayParameters() {
		areatrack.detectionParameters.thresholdmovement = (int) threshold2Spinner.getValue();
		//areatrack.setOverlayParameters(true, EnumImageOp.REF_PREVIOUS, EnumAreaDetection.SINGLE, areatrack.detectionParameters.thresholdmovement);
	}
	
	public void transferParametersToDialog(DetectionParameters detectionParameters) {
		
		detectCheckBox.setSelected(detectionParameters.detectMovement);
		threshold2Spinner.setValue(detectionParameters.thresholdmovement);
	}
	
	public void transferDialogToParameters(DetectionParameters detectionParameters) {
		
		detectionParameters.thresholdmovement = (int) threshold2Spinner.getValue();
		detectionParameters.detectMovement = detectCheckBox.isSelected();
	}
}
