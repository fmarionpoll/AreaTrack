package plugins.fmp.areatrack.dlg;

import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import plugins.fmp.areatrack.Areatrack;
import plugins.fmp.areatrack.commons.DetectionParameters;
import plugins.fmp.areatrack.tools.EnumImageOp;

public class Dlg3_TabFilter extends JPanel implements ChangeListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8921207247623517524L;
	private JLabel videochannel = new JLabel("filter  ");
	private JLabel thresholdLabel = new JLabel("threshold ");
	JComboBox<EnumImageOp> transformsComboBox = new JComboBox<EnumImageOp>(
			new EnumImageOp[] { EnumImageOp.R_RGB, EnumImageOp.G_RGB, EnumImageOp.B_RGB, EnumImageOp.R2MINUS_GB,
					EnumImageOp.G2MINUS_RB, EnumImageOp.B2MINUS_RG, EnumImageOp.NORM_BRMINUSG, EnumImageOp.RGB,
					EnumImageOp.H_HSB, EnumImageOp.S_HSB, EnumImageOp.B_HSB });
	private String[] directions = new String[] { " threshold >", " threshold <" };
	private JComboBox<String> directionComboBox = new JComboBox<String>(directions);
	JSpinner thresholdSpinner = new JSpinner(new SpinnerNumberModel(35, 0, 255, 1));
	Areatrack areatrack = null;

	public void init(JTabbedPane tab, GridLayout capLayout, Areatrack areatrack) {

		this.areatrack = areatrack;
		JComponent panel = new JPanel(false);
		panel.setLayout(capLayout);

		FlowLayout layoutLeft = new FlowLayout(FlowLayout.LEFT);
		JPanel panel0 = new JPanel(layoutLeft);
		panel0.add(videochannel);
		panel0.add(transformsComboBox);
		panel.add(panel0);
		JPanel panel1 = new JPanel(layoutLeft);
		panel1.add(thresholdLabel);
		panel1.add(directionComboBox);
		panel1.add(thresholdSpinner);
		panel.add(panel1);
		tab.addTab("Filters", null, panel,
				"Display parameters for thresholding a transformed image with different filters");

		transformsComboBox.setSelectedItem(EnumImageOp.NORM_BRMINUSG);

		thresholdSpinner.addChangeListener(this);

		declareActionListeners();
	}

	private void declareActionListeners() {
		transformsComboBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				updateThresholdOverlayParameters();
			}
		});

		directionComboBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				updateThresholdOverlayParameters();
			}
		});
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		if (e.getSource() == thresholdSpinner)
			updateThresholdOverlayParameters();
	}

	void updateThresholdOverlayParameters() {
		transferDialogToParameters(areatrack.detectionParameters);
		areatrack.setOverlay(areatrack.detectionParameters.displayOverlay);
	}

	public void transferParametersToDialog(DetectionParameters detectionParameters) {
		transformsComboBox.setSelectedItem(detectionParameters.simpletransformop);
		directionComboBox.setSelectedItem(detectionParameters.thresholdUp ? 0 : 1);
		thresholdSpinner.setValue(detectionParameters.simplethreshold);
	}

	public void transferDialogToParameters(DetectionParameters detectionParameters) {
		detectionParameters.simpletransformop = (EnumImageOp) transformsComboBox.getSelectedItem();
		detectionParameters.thresholdUp = directionComboBox.getSelectedIndex() == 0;
		detectionParameters.simplethreshold = (int) thresholdSpinner.getValue();
	}

}
