package plugins.fmp.areatrack.dlg;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import icy.gui.component.PopupPanel;
import icy.gui.frame.IcyFrame;
import icy.gui.util.FontUtil;
import plugins.fmp.areatrack.tools.EnumAreaDetection;



public class Dlg3ParametersArea extends JPanel implements ChangeListener {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -359095233032653215L;
	Dlg3TabColors dlgTabThresholdColors = new Dlg3TabColors();
	Dlg3TabFilter dlgTabThresholdFunction = new Dlg3TabFilter();
	Dlg3TabOverlay dlgTabOverlay = new Dlg3TabOverlay();
	
	JCheckBox detectAreaCheckBox = new JCheckBox("Detect ");
	JRadioButton rbFilterbyColor = new JRadioButton("color array");
	JRadioButton rbFilterbyFunction	= new JRadioButton("filters");
	JCheckBox overlayCheckBox = new JCheckBox("overlay");
	JButton loadButton = new JButton("Load...");
	JButton saveButton = new JButton("Save...");
	JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);
	Areatrack areatrack = null;

	

	public void init(Areatrack areatrack, IcyFrame mainFrame, JPanel mainPanel, String title) {
		
		this.areatrack = areatrack;
		
		PopupPanel 	capPopupPanel = new PopupPanel(title);
		JPanel capPanel = capPopupPanel.getMainPanel();
		capPanel.setLayout(new BorderLayout());
//		capPopupPanel.expand();
		capPopupPanel.collapse();
		capPopupPanel.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				mainFrame.revalidate();
				mainFrame.pack();
				mainFrame.repaint();
			}});
		mainPanel.add(capPopupPanel);

		FlowLayout layoutLeft = new FlowLayout(FlowLayout.LEFT); 
		JPanel panel0 = new JPanel(layoutLeft);
		((FlowLayout)panel0.getLayout()).setVgap(0);
		panel0.add(detectAreaCheckBox);
		panel0.add(rbFilterbyColor);
		panel0.add(rbFilterbyFunction);
		ButtonGroup bgchoice = new ButtonGroup();
		bgchoice.add(rbFilterbyColor);
		bgchoice.add(rbFilterbyFunction);
		panel0.add(overlayCheckBox);
		capPanel.add(panel0, BorderLayout.PAGE_START);
		
		GridLayout capLayout = new GridLayout(3, 2);
		dlgTabThresholdColors.init(tabbedPane, capLayout, areatrack);
		dlgTabThresholdFunction.init(tabbedPane, capLayout, areatrack);
		dlgTabOverlay.init(tabbedPane, capLayout);
		capPanel.add(tabbedPane, BorderLayout.CENTER);
		
		JLabel loadsaveText1 = new JLabel ("-> File (xml) ");
		loadsaveText1.setHorizontalAlignment(SwingConstants.RIGHT); 
		loadsaveText1.setFont(FontUtil.setStyle(loadsaveText1.getFont(), Font.ITALIC));
		FlowLayout layoutRight = new FlowLayout(FlowLayout.RIGHT); 
		JPanel panel2 = new JPanel(layoutRight);
		panel2.add(loadsaveText1);
		panel2.add(loadButton);
		panel2.add(saveButton);
		capPanel.add(panel2, BorderLayout.PAGE_END);
		
		detectAreaCheckBox.setSelected(true);
		tabbedPane.setSelectedIndex(0);
		rbFilterbyColor.setSelected(true);
		
		declareActionListeners();
		
		tabbedPane.addChangeListener(this);
	}
	
	private void declareActionListeners() {
		loadButton.addActionListener(new ActionListener () { 
			@Override public void actionPerformed( final ActionEvent e ) { 
				loadParameters(); 
			}});
		
		saveButton.addActionListener(new ActionListener () {
			@Override public void actionPerformed( final ActionEvent e ) { 
				saveParameters(); 
			}});
		
		rbFilterbyColor.addActionListener(new ActionListener () { 
			@Override public void actionPerformed( final ActionEvent e ) {
			if (rbFilterbyColor.isSelected())
				selectTab(0);
			}});
		
		rbFilterbyFunction.addActionListener(new ActionListener () { 
			@Override public void actionPerformed( final ActionEvent e ) {
			if (rbFilterbyFunction.isSelected())
				selectTab(1);
			}});
		
		overlayCheckBox.addActionListener(new ActionListener () {
			@Override public void actionPerformed( final ActionEvent e) {
				areatrack.setOverlay(overlayCheckBox.isSelected());
			}});
	}
	
	private void selectTab(int index) {
		
		tabbedPane.setSelectedIndex(index);
	}
	
	private void loadParameters() {
		
		if (areatrack.detectionParameters.xmlLoadAreaTrackParameters(areatrack.vSequence)) {
			transferParametersToDialog(areatrack.detectionParameters);
			updateThresholdOverlayParameters(tabbedPane.getSelectedIndex());
		}
	}
	
	private void saveParameters() {
		
		transferDialogToParameters(areatrack.detectionParameters);
		areatrack.detectionParameters.xmlSaveAreaTrackParameters(areatrack.vSequence);
	}

	@Override
	public void stateChanged (ChangeEvent e) {
		
		if (e.getSource() == tabbedPane) {
			int selectedTab = tabbedPane.getSelectedIndex();
			updateThresholdOverlayParameters(selectedTab);
			if (selectedTab == 0) {
				rbFilterbyColor.setSelected(true);
				areatrack.detectionParameters.areaDetectionMode = EnumAreaDetection.COLORARRAY;
			}
			else if (selectedTab == 1) {
				rbFilterbyFunction.setSelected(true);
				areatrack.detectionParameters.areaDetectionMode = EnumAreaDetection.SINGLE;
			}
		}
	}

	public void updateThresholdOverlayParameters(int selectedTab) {
		
		switch( selectedTab) {
			case 1:
				areatrack.detectionParameters.areaDetectionMode = EnumAreaDetection.SINGLE;
				dlgTabThresholdFunction.updateThresholdOverlayParameters();
				break;

			case 0:
			default:
				areatrack.detectionParameters.areaDetectionMode = EnumAreaDetection.COLORARRAY;
				dlgTabThresholdColors.updateThresholdOverlayParameters();
				break;
		}
	}
	
	public void transferParametersToDialog(DetectionParameters detectionParameters) {
			
		dlgTabThresholdColors.transferParametersToDialog(detectionParameters);
		dlgTabThresholdFunction.transferParametersToDialog(detectionParameters);
		
		if (detectionParameters.areaDetectionMode == EnumAreaDetection.COLORARRAY)
			rbFilterbyColor.setSelected(true);
		else
			rbFilterbyFunction.setSelected(true);
		detectAreaCheckBox.setSelected(detectionParameters.detectArea);
		overlayCheckBox.setSelected(detectionParameters.displayOverlay);
	}
	
public void transferDialogToParameters(DetectionParameters detectionParameters) {
		
		dlgTabThresholdColors.transferDialogToParameters(detectionParameters);
		dlgTabThresholdFunction.transferDialogToParameters(detectionParameters);
		detectionParameters.detectArea = detectAreaCheckBox.isSelected();
		detectionParameters.areaDetectionMode = rbFilterbyColor.isSelected()? EnumAreaDetection.COLORARRAY : EnumAreaDetection.SINGLE;
		detectionParameters.displayOverlay = overlayCheckBox.isSelected();
	}

}
