package plugins.fmp.areatrack.splitroitoarray;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import icy.gui.component.PopupPanel;
import icy.gui.frame.IcyFrame;
import icy.gui.util.GuiUtil;
import plugins.fmp.areatrack.Areatrack;
import plugins.fmp.areatrack.sequence.SequencePlusOpen;
import plugins.fmp.areatrack.sequence.SequenceVirtual;

public class DlgSTDDetectLines extends JPanel implements ChangeListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5560218796103433288L;
	JButton	buildHistogramsButton = new JButton("Build histograms");
	JLabel txtThresholdSTD = new JLabel("threshold / selected filter");
	JSpinner thresholdSTD = new JSpinner(new SpinnerNumberModel(500, 1, 10000, 1));
	JLabel txtThresholdSTDFFromChanComboBox = new JLabel("Filter from");
	JComboBox<String> thresholdSTDFromChanComboBox = new JComboBox<String> (new String[] {"R", "G", "B", "R+B-2G"});
	JButton generateAutoGridButton = new JButton("Create lines / histograms > threshold");
	JLabel txtAreaShrink = new JLabel("area shrink (%)");
	JSpinner areaShrink = new JSpinner(new SpinnerNumberModel(5, -100, 100, 1));
	JButton convertLinesToSquaresButton = new JButton("Convert lines to squares");
	
	Areatrack areatrack = null;
	private SequenceVirtual sequenceVirtual = null;
	private DlgOutputData dlgOutputData = null;

	
	public void init(Areatrack areatrack, IcyFrame dialogFrame, JPanel mainPanel, DlgOutputData dlgOutputData) {
		
		this.areatrack  = areatrack;
		this.dlgOutputData = dlgOutputData;
		this.sequenceVirtual = areatrack.vSequence;
		
		PopupPanel popuppanel1 = new PopupPanel("detect lines using STD") ; 
		JPanel panel1 = popuppanel1.getMainPanel(); 
		panel1.setLayout(new GridLayout(6, 2));
		panel1.add(buildHistogramsButton);
		panel1.add(GuiUtil.besidesPanel(txtThresholdSTD, thresholdSTD));
		panel1.add(GuiUtil.besidesPanel(txtThresholdSTDFFromChanComboBox, thresholdSTDFromChanComboBox));
		panel1.add(GuiUtil.besidesPanel(generateAutoGridButton));
		panel1.add(GuiUtil.besidesPanel(txtAreaShrink, areaShrink));
		panel1.add(GuiUtil.besidesPanel(convertLinesToSquaresButton));
		mainPanel.add(popuppanel1);
		popuppanel1.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				dialogFrame.revalidate();
				dialogFrame.pack();
				dialogFrame.repaint();
			}});
		popuppanel1.expand();
		
		thresholdSTDFromChanComboBox.setSelectedIndex(3);
		
		addActionListeners();
	}

	void addActionListeners() {
		
		buildHistogramsButton.addActionListener(new ActionListener() { 
		public void actionPerformed(ActionEvent e) { 	
			SequencePlusOpen.initSequenceViewer(sequenceVirtual.seq);
			sequenceVirtual = new SequenceVirtual(sequenceVirtual.seq);
			DetectLinesSTD.findLines(sequenceVirtual.seq, sequenceVirtual.currentFrame); 
			}});
	
		generateAutoGridButton.addActionListener(new ActionListener() { 
		public void actionPerformed(ActionEvent e) { 
			int t = sequenceVirtual.currentFrame;
			String choice = (String) thresholdSTDFromChanComboBox.getSelectedItem();
			int threshold = (int) thresholdSTD.getValue();
			DetectLinesSTD.buildAutoGrid(sequenceVirtual.seq, t, choice, threshold); 
			}});
		
		convertLinesToSquaresButton.addActionListener(new ActionListener() { 
		public void actionPerformed(ActionEvent e) { 
			int areaShrinkPCT = (int) areaShrink.getValue();
			String rootname = (String) dlgOutputData.ezRootnameComboBox.getSelectedItem();
			DefineLinesManually.convertLinesToSquares(sequenceVirtual.seq, rootname, areaShrinkPCT); 
			}});
	}
	
	@Override
	public void stateChanged(ChangeEvent e) {
		// TODO Auto-generated method stub

	}

}
