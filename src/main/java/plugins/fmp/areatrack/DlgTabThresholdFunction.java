package plugins.fmp.areatrack;
import java.awt.FlowLayout;
import java.awt.GridLayout;
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

import plugins.fmp.fmpTools.EnumImageOp;
import plugins.fmp.fmpTools.EnumThresholdType;

public class DlgTabThresholdFunction extends JPanel implements ChangeListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8921207247623517524L;
	private JLabel videochannel 			= new JLabel("filter  ");
	private JLabel thresholdLabel 			= new JLabel("threshold ");
	JComboBox<EnumImageOp> transformsComboBox = new JComboBox<EnumImageOp> (new EnumImageOp[] {
			EnumImageOp.R_RGB, EnumImageOp.G_RGB, EnumImageOp.B_RGB, 
			EnumImageOp.R2MINUS_GB, EnumImageOp.G2MINUS_RB, EnumImageOp.B2MINUS_RG, 
			EnumImageOp.NORM_BRMINUSG, EnumImageOp.RGB,
			EnumImageOp.H_HSB, EnumImageOp.S_HSB, EnumImageOp.B_HSB	});
	JSpinner thresholdSpinner 		= new JSpinner(new SpinnerNumberModel(70, 0, 255, 1));
	
	
	public void init(JTabbedPane tab, GridLayout capLayout) {
		JComponent panel = new JPanel(false);
		panel.setLayout(capLayout);
		
		FlowLayout layoutLeft = new FlowLayout(FlowLayout.LEFT); 
		JPanel panel0 = new JPanel(layoutLeft);
		panel0.add( videochannel);
		panel0.add(transformsComboBox);
		panel.add(panel0);
		JPanel panel1 = new JPanel(layoutLeft);
		panel1.add( thresholdLabel);
		panel1.add(thresholdSpinner);
		panel.add(panel1);
		tab.addTab("Filters", null, panel, "Display parameters for thresholding a transformed image with different filters");
		
		transformsComboBox.setSelectedIndex(EnumImageOp.B2MINUS_RG.ordinal());
		
		thresholdSpinner.addChangeListener(this);
		
	}


	@Override
	public void stateChanged(ChangeEvent e) {
		if (e.getSource() == thresholdSpinner) 
				updateThresholdOverlayParameters();
		
	}
	
	private void updateThresholdOverlayParameters() {
		
		if (vSequence == null)
			return;
		
		boolean activateThreshold = true;
		int thresholdForOverlay = 0;
		EnumImageOp transformOpForOverlay = EnumImageOp.NONE;
		EnumThresholdType thresholdTypeForOverlay = EnumThresholdType.SINGLE;
		
		
				simpletransformop = (EnumImageOp) transformsComboBox.getSelectedItem();
				transformOpForOverlay = simpletransformop;
				simplethreshold = Integer.parseInt(thresholdSpinner.getValue().toString());
				thresholdForOverlay = simplethreshold; 
				thresholdtype = EnumThresholdType.SINGLE;
				thresholdTypeForOverlay = thresholdtype;	
		
		//--------------------------------
		
		activateSequenceThresholdOverlay(activateThreshold);
		if (activateThreshold && vSequence != null) {
			vSequence.setThresholdOverlay(activateThreshold);
			if (thresholdTypeForOverlay == EnumThresholdType.SINGLE)
				vSequence.setThresholdOverlayParametersSingle(transformOpForOverlay, thresholdForOverlay);
			else
				vSequence.setThresholdOverlayParametersColors(transformOpForOverlay, colorarray, colordistanceType, colorthreshold);
		}
	}
	
}
