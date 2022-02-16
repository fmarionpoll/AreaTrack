package plugins.fmp.areatrack;

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



public class DlgAnalysisParameters extends JPanel implements ChangeListener {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -359095233032653215L;
	DlgTabThresholdColors dlgTabThresholdColors = new DlgTabThresholdColors();
	DlgTabThresholdFunction dlgTabThresholdFunction = new DlgTabThresholdFunction();
	DlgTabThresholdMovement dlgTabThresholdMovement = new DlgTabThresholdMovement();
	DlgTabOverlay dlgTabOverlay = new DlgTabOverlay();
	
	JCheckBox measureSurfacesCheckBox = new JCheckBox("Measure surface using");
	JRadioButton rbFilterbyColor 	= new JRadioButton("color array");
	JRadioButton rbFilterbyFunction	= new JRadioButton("filters");
	JCheckBox measureHeatmapCheckBox = new JCheckBox("movement");
	JButton openFiltersButton		= new JButton("Load...");
	JButton saveFiltersButton		= new JButton("Save...");
	JTabbedPane tabbedPane 			= new JTabbedPane(JTabbedPane.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);
	Areatrack areatrack = null;

	

	public void init(Areatrack areatrack, IcyFrame mainFrame, JPanel mainPanel) {
		
		this.areatrack = areatrack;
		
		PopupPanel 	capPopupPanel = new PopupPanel("ANALYSIS PARAMETERS");
		JPanel capPanel = capPopupPanel.getMainPanel();
		capPanel.setLayout(new BorderLayout());
		capPopupPanel.expand();
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
		panel0.add(measureSurfacesCheckBox);
		panel0.add(rbFilterbyColor);
		panel0.add(rbFilterbyFunction);
		ButtonGroup bgchoice = new ButtonGroup();
		bgchoice.add(rbFilterbyColor);
		bgchoice.add(rbFilterbyFunction);
		panel0.add(measureHeatmapCheckBox);
		capPanel.add(panel0, BorderLayout.PAGE_START);
		
		GridLayout capLayout = new GridLayout(3, 2);
		dlgTabThresholdColors.init(tabbedPane, capLayout, areatrack);
		dlgTabThresholdFunction.init(tabbedPane, capLayout, areatrack);
		dlgTabThresholdMovement.init(tabbedPane, capLayout, areatrack);
		dlgTabOverlay.init(tabbedPane, capLayout);
		capPanel.add(tabbedPane, BorderLayout.CENTER);
		
		JLabel loadsaveText1 = new JLabel ("-> File (xml) ");
		loadsaveText1.setHorizontalAlignment(SwingConstants.RIGHT); 
		loadsaveText1.setFont(FontUtil.setStyle(loadsaveText1.getFont(), Font.ITALIC));
		FlowLayout layoutRight = new FlowLayout(FlowLayout.RIGHT); 
		JPanel panel2 = new JPanel(layoutRight);
		panel2.add(loadsaveText1);
		panel2.add(openFiltersButton);
		panel2.add(saveFiltersButton);
		capPanel.add(panel2, BorderLayout.PAGE_END);
		
		measureSurfacesCheckBox.setSelected(true);
		measureHeatmapCheckBox.setSelected(false);
		tabbedPane.setSelectedIndex(0);
		rbFilterbyColor.setSelected(true);
		
		declareActionListeners();
		
		tabbedPane.addChangeListener(this);
		
	}
	
	private void declareActionListeners() {
		openFiltersButton.addActionListener(new ActionListener () { 
			@Override public void actionPerformed( final ActionEvent e ) { 
				xmlReadAreaTrackParameters(); 
			} } );
		
		saveFiltersButton.addActionListener(new ActionListener () {
			@Override public void actionPerformed( final ActionEvent e ) { 
				xmlWriteAreaTrackParameters(); 
			} } );
		
		rbFilterbyColor.addActionListener(new ActionListener () { 
			@Override public void actionPerformed( final ActionEvent e ) {
			if (rbFilterbyColor.isSelected())
				selectTab(0);
		} } );
		
		rbFilterbyFunction.addActionListener(new ActionListener () { 
			@Override public void actionPerformed( final ActionEvent e ) {
			if (rbFilterbyFunction.isSelected())
				selectTab(1);
		} } );
	}
	
	private void selectTab(int index) {
		tabbedPane.setSelectedIndex(index);
	}
	
	private void xmlReadAreaTrackParameters() {
		XmlAreaTrack xmlAreaTrack = new XmlAreaTrack();
		xmlAreaTrack.xmlReadAreaTrackParameters(areatrack);
	}
	
	private void xmlWriteAreaTrackParameters() {
		XmlAreaTrack xmlAreaTrack = new XmlAreaTrack();
		xmlAreaTrack.xmlWriteAreaTrackParameters(areatrack);
	}

	@Override
	public void stateChanged (ChangeEvent e) {
		if (e.getSource() == tabbedPane) {
			int selectedTab = tabbedPane.getSelectedIndex();
			updateThresholdOverlayParameters(selectedTab);
		}
	}

	public void updateThresholdOverlayParameters(int selectedTab) {
		
		switch( selectedTab) {
		case 0:
			dlgTabThresholdColors.updateThresholdOverlayParameters();
			break;
		case 1:
			dlgTabThresholdFunction.updateThresholdOverlayParameters();
			break;
		case 2:
			dlgTabThresholdMovement.updateThresholdOverlayParameters();
			break;
		case 3:
			areatrack.setOverlayParameters(false, null, null, 0);
			break;
		default:
			areatrack.setOverlayParameters(false, null, null, 0);
			break;
		}
	}
	
	public void transferParametersToDialog() {
		
		dlgTabThresholdColors.transferParametersToDialog();
		dlgTabThresholdFunction.transferParametersToDialog();
		dlgTabThresholdMovement.transferParametersToDialog();
	}

}
