package plugins.fmp.areatrack;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

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
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import plugins.fmp.fmpTools.ComboBoxColorRenderer;
import plugins.fmp.fmpTools.EnumImageOp;
import plugins.fmp.fmpTools.EnumThresholdType;

public class DlgTabThresholdColors extends JPanel implements ChangeListener {

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
	Areatrack parent0 = null;



	public void init(JTabbedPane tab, GridLayout capLayout, Areatrack parent0) {
		
		this.parent0 = parent0;
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
		
		distanceSpinner.addChangeListener(this);
		declareActionListeners();
	}
	
	private void declareActionListeners() {
		rbRGB.addActionListener(new ActionListener () { 
			@Override public void actionPerformed( final ActionEvent e ) { 
				parent0.colortransformop = EnumImageOp.NONE;
				updateThresholdOverlayParameters();
			} } );
		
		rbHSV.addActionListener(new ActionListener () { 
			@Override public void actionPerformed( final ActionEvent e ) { 
				parent0.colortransformop = EnumImageOp.RGB_TO_HSV;
				updateThresholdOverlayParameters();
			} } );
		
		rbH1H2H3.addActionListener(new ActionListener () { 
			@Override public void actionPerformed( final ActionEvent e ) { 
				parent0.colortransformop = EnumImageOp.RGB_TO_H1H2H3;
				updateThresholdOverlayParameters();
			} } );
		
		rbL1.addActionListener(new ActionListener () { 
			@Override public void actionPerformed( final ActionEvent e ) { 
				updateThresholdOverlayParameters();
			} } );
		
		rbL2.addActionListener(new ActionListener () { 
			@Override public void actionPerformed( final ActionEvent e ) { 
				updateThresholdOverlayParameters();
			} } );
		
		deleteColorButton.addActionListener(new ActionListener () { 
			@Override public void actionPerformed( final ActionEvent e ) { 
				if (colorPickCombo.getItemCount() > 0 && colorPickCombo.getSelectedIndex() >= 0)
					colorPickCombo.removeItemAt(colorPickCombo.getSelectedIndex());
				updateThresholdOverlayParameters();
			} } );
		
		pickColorButton.addActionListener(new ActionListener () { 
			@Override public void actionPerformed( final ActionEvent e ) { 
				pickColor(); 
			} } );
		
		class ItemChangeListener implements ItemListener {
		    @Override
		    public void itemStateChanged(ItemEvent event) {
		       if (event.getStateChange() == ItemEvent.SELECTED) {
		    	   updateThresholdOverlayParameters();
		       }
		    }       
		}
		colorPickCombo.addItemListener(new ItemChangeListener());
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		if (e.getSource() == distanceSpinner)  
			updateThresholdOverlayParameters();
		
	}
	
    void updateThresholdOverlayParameters() {
    	
		parent0.colorthreshold = Integer.parseInt(distanceSpinner.getValue().toString());
		parent0.thresholdtype = EnumThresholdType.COLORARRAY;
		parent0.colorarray.clear();
		for (int i = 0; i < colorPickCombo.getItemCount(); i++) {
			parent0.colorarray.add(colorPickCombo.getItemAt(i));
		}
		parent0.colordistanceType = 1;
		if (rbL2.isSelected()) 
			parent0.colordistanceType = 2;

		parent0.setOverlayParameters(true, parent0.colortransformop, parent0.thresholdtype, parent0.colorthreshold);
	}
	
	private void pickColor() {
		
		boolean bActiveTrapOverlay = false;
		
		if (pickColorButton.getText().contains("*") || pickColorButton.getText().contains(":")) {
			pickColorButton.setBackground(Color.LIGHT_GRAY);
			pickColorButton.setText(textPickAPixel);
			bActiveTrapOverlay = false;
		}
		else
		{
			pickColorButton.setText("*"+textPickAPixel+"*");
			pickColorButton.setBackground(Color.DARK_GRAY);
			bActiveTrapOverlay = true;
		}	
		parent0.vSequence.setMouseTrapOverlay(bActiveTrapOverlay, pickColorButton, colorPickCombo);
	}
	
	public void transferParametersToDialog() {
		
		distanceSpinner.setValue(parent0.colorthreshold);
		switch (parent0.colortransformop) {
			case RGB_TO_HSV:
				rbHSV.setSelected(true);
				break;
			case RGB_TO_H1H2H3:
				rbH1H2H3.setSelected(true);
				break;
			case NONE:
			default:
				rbRGB.setSelected(true);
				break;
		}
		colorPickCombo.removeAll();
		for (int i = 0; i < parent0.colorarray.size(); i++)
			colorPickCombo.addItem(parent0.colorarray.get(i));
		if (parent0.colordistanceType == 1)
			rbL1.setSelected(true);
		else
			rbL2.setSelected(true);
	}

}
