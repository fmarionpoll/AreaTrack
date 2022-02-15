package plugins.fmp.areatrack;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.SpinnerNumberModel;

import plugins.fmp.fmpTools.ComboBoxColorRenderer;
import plugins.fmp.fmpTools.EnumImageOp;

public class DlgTabThresholdColors extends JPanel 
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4359876050505295400L;
	JComboBox<Color> colorPickCombo = new JComboBox<Color>();
	private ComboBoxColorRenderer colorPickComboRenderer = new ComboBoxColorRenderer(colorPickCombo);
	
	private String textPickAPixel 			= "Pick a pixel";
	private JButton pickColorButton			= new JButton(textPickAPixel);
	private JButton	deleteColorButton		= new JButton("Delete color");
		JRadioButton rbL1 				= new JRadioButton("L1");
		JRadioButton rbL2 				= new JRadioButton("L2");
		JSpinner distanceSpinner 		= new JSpinner(new SpinnerNumberModel(10, 0, 800, 5));
		JRadioButton rbRGB 				= new JRadioButton("RGB");
		JRadioButton rbHSV 				= new JRadioButton("HSV");
		JRadioButton rbH1H2H3 			= new JRadioButton("H1H2H3");
	private JLabel distanceLabel 			= new JLabel("Distance  ");
	private JLabel colorspaceLabel 			= new JLabel("Color space ");



	public void init(JTabbedPane tab, GridLayout capLayout) {
		JComponent panel = new JPanel(false);
		panel.setLayout(capLayout);
		
		colorPickCombo.setRenderer(colorPickComboRenderer);
		FlowLayout layoutLeft = new FlowLayout(FlowLayout.LEFT); 
		JPanel panel0 = new JPanel(layoutLeft);
		panel0.add(pickColorButton);
		panel0.add(colorPickCombo);
		panel0.add(deleteColorButton);
		panel.add( panel0);
		
		JPanel panel1 = new JPanel(layoutLeft);
		ButtonGroup bgd = new ButtonGroup();
		bgd.add(rbL1);
		bgd.add(rbL2);
		panel1.add(distanceLabel);
		panel1.add(rbL1);
		panel1.add(rbL2);
		panel1.add(distanceSpinner);
		panel.add( panel1);
		
		ButtonGroup bgcs = new ButtonGroup();
		bgcs.add(rbRGB);
		bgcs.add(rbHSV);
		bgcs.add(rbH1H2H3);
		JPanel panel2 = new JPanel(layoutLeft);
		panel2.add(colorspaceLabel);
		panel2.add(rbRGB);
		panel2.add(rbHSV);
		panel2.add(rbH1H2H3);
		panel.add( panel2);
		tab.addTab("Colors", null, panel, "Display parameters for thresholding an image with different colors and a distance");
	
		rbL1.setSelected(true);
		rbRGB.setSelected(true);
		
	}

}
