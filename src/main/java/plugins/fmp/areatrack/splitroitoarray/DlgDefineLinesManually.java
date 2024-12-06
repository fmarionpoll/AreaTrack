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

import icy.gui.component.PopupPanel;
import icy.gui.frame.IcyFrame;
import icy.gui.util.GuiUtil;
import icy.sequence.Sequence;
import plugins.fmp.areatrack.Areatrack;
import plugins.fmp.areatrack.sequence.SequenceVirtual;

public class DlgDefineLinesManually extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9143775308853070401L;
		
	JComboBox<String> splitAsComboBox = new JComboBox<String> (new String[] {"polygons", "ellipses"});
	JSpinner ezNumberOfColumnsJSpinner = new JSpinner(new SpinnerNumberModel(5, 1, 10000, 1));
	JSpinner columnWidthJSpinner = new JSpinner (new SpinnerNumberModel(10, 0, 10000, 1));
	JSpinner ezNumberOfRowsJSpinner = new JSpinner(new SpinnerNumberModel(10, 1, 10000, 1));
	JSpinner rowHeightJSpinner = new JSpinner(new SpinnerNumberModel(10, 0, 10000, 1)); 
	JSpinner columnSpaceJSpinner = new JSpinner(new SpinnerNumberModel(1, 0, 10000, 1));
	JSpinner rowSpaceJSpinner = new JSpinner(new SpinnerNumberModel(1, 0, 10000, 1));
	JButton generateGridButton = new JButton("Create grid");

	Areatrack areatrack = null;
	private SequenceVirtual sequenceVirtual = null;
	private DlgOutputData dlgOutputData = null;

	
	public void init(Areatrack areatrack, IcyFrame dialogFrame, JPanel mainPanel, DlgOutputData dlgOutputData) {
		
		this.areatrack  = areatrack;
		this.dlgOutputData = dlgOutputData;
		sequenceVirtual = areatrack.vSequence;
		splitAsComboBox.setSelectedIndex(1);
		
		PopupPanel popuppanel2 = new PopupPanel("define lines manually");
		JPanel panel2 = popuppanel2.getMainPanel();
		panel2.setLayout(new GridLayout(8, 2));
		panel2.add(GuiUtil.besidesPanel(new JLabel("Split polygon as "), splitAsComboBox));
		panel2.add(GuiUtil.besidesPanel(new JLabel("N columns "), ezNumberOfColumnsJSpinner));
		panel2.add(GuiUtil.besidesPanel(new JLabel ("column width"), columnWidthJSpinner));
		panel2.add(GuiUtil.besidesPanel(new JLabel("space btw. col. "), columnSpaceJSpinner));
		panel2.add(GuiUtil.besidesPanel(new JLabel("N rows "), ezNumberOfRowsJSpinner));
		panel2.add(GuiUtil.besidesPanel(new JLabel ("row height "), rowHeightJSpinner));
		panel2.add(GuiUtil.besidesPanel(new JLabel("space btw. row "), rowSpaceJSpinner));
		panel2.add(GuiUtil.besidesPanel(generateGridButton));
		mainPanel.add(popuppanel2);
		popuppanel2.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				dialogFrame.revalidate();
				dialogFrame.pack();
				dialogFrame.repaint();
			}});
		popuppanel2.collapse();
		addActionListeners();
	}

	void addActionListeners() {
		generateGridButton.addActionListener(new ActionListener() { 
		public void actionPerformed(ActionEvent e) { 
			generateGrid(); 
			}});
	}
	
	protected void generateGrid() 
	{
		Sequence seq = sequenceVirtual.seq;
		double colSpace = (double)((int) columnSpaceJSpinner.getValue());
		double colSize = (double) ((int) columnWidthJSpinner.getValue());
		double nbcols = (double) ((int) ezNumberOfColumnsJSpinner.getValue()); 
		double rowSpace = (double)((int) rowSpaceJSpinner.getValue());
		double rowSize = (double) ((int) rowHeightJSpinner.getValue());
		double nbrows = (double) ((int) ezNumberOfRowsJSpinner.getValue());
		String rootName = (String) dlgOutputData.ezRootnameComboBox.getSelectedItem();
		
		int choice = splitAsComboBox.getSelectedIndex();
		switch (choice) {
			case 1:
				DefineLinesManually.createROISFromSelectedPolygon(seq, 2, rootName, colSpace, colSize, nbcols, rowSpace, rowSize, nbrows);
				break;
			default:
				DefineLinesManually.createROISFromSelectedPolygon(seq, 1, rootName, colSpace, colSize, nbcols, rowSpace, rowSize, nbrows);
				break;
		}
	}

}
