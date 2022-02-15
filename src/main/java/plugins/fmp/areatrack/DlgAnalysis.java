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
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;

import icy.gui.component.PopupPanel;
import icy.gui.frame.IcyFrame;
import icy.gui.util.FontUtil;
import icy.gui.util.GuiUtil;

public class DlgAnalysis extends JPanel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -359095233032653215L;
	JCheckBox measureSurfacesCheckBox = new JCheckBox("Measure surface using");
	JRadioButton rbFilterbyColor 	= new JRadioButton("color array");
	JRadioButton rbFilterbyFunction	= new JRadioButton("filters");
	JCheckBox measureHeatmapCheckBox = new JCheckBox("movement");
	JButton openFiltersButton		= new JButton("Load...");
	JButton saveFiltersButton		= new JButton("Save...");
	JTabbedPane tabbedPane 			= new JTabbedPane(JTabbedPane.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);
	Areatrack parent0 = null;

	

	public void init(Areatrack parent0, IcyFrame mainFrame, JPanel mainPanel) {
		
		this.parent0 = parent0;
		
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
		panelAnalysisAdd_ThresholdOnColors(tabbedPane, capLayout);
		panelAnalysisAdd_ThresholdOnFilter(tabbedPane, capLayout);
		panelAnalysisAdd_MovementThreshold(tabbedPane, capLayout);
		panelAnalysisAdd_DisplayImagewithoutOverlay(tabbedPane, capLayout);
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
		xmlAreaTrack.xmlReadAreaTrackParameters(parent0);
	}
	
	private void xmlWriteAreaTrackParameters() {
		XmlAreaTrack xmlAreaTrack = new XmlAreaTrack();
		xmlAreaTrack.xmlWriteAreaTrackParameters(parent0);
	}
	
	
	private void panelAnalysisAdd_ThresholdOnColors(JTabbedPane tab, GridLayout capLayout) {
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
	}
	
	
	private void panelAnalysisAdd_ThresholdOnFilter(JTabbedPane tab, GridLayout capLayout) {
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
	}
	
	private void panelAnalysisAdd_MovementThreshold(JTabbedPane tab, GridLayout capLayout) {
		JComponent panel = new JPanel(false);
		panel.setLayout(capLayout);
		
		JLabel thresholdLabel2 = new JLabel("'move' threshold ");
		thresholdLabel2.setHorizontalAlignment(SwingConstants.RIGHT);
		panel.add( GuiUtil.besidesPanel( thresholdLabel2, threshold2Spinner));
		tab.addTab("Movement", null, panel, "Display parameters for thresholding movements (image n - (n-1)");
	}
	
	private void panelAnalysisAdd_DisplayImagewithoutOverlay(JTabbedPane tab, GridLayout capLayout) {
		JComponent panel = new JPanel(false);
		panel.setLayout(capLayout);
		
		panel.add( GuiUtil.besidesPanel( new JLabel("display image with no overlay")));
		tabbedPane.addTab("None", null, panel, "Display image without overlay");
	}

}
