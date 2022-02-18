package plugins.fmp.splitroitoarray;

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

import icy.common.CollapsibleEvent;
import icy.common.listener.ChangeListener;
import icy.gui.component.PopupPanel;
import icy.gui.frame.IcyFrame;
import icy.gui.util.GuiUtil;
import icy.sequence.Sequence;
import plugins.fmp.areatrack.Areatrack;
import plugins.fmp.fmpSequence.SequenceVirtual;

public class DlgDefineLinesManually extends JPanel implements ChangeListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9143775308853070401L;
	JLabel txtSplitAsComboBox = new JLabel("Split polygon as ");
	JComboBox<String> splitAsComboBox = new JComboBox<String> (new String[] {"vertical lines", "polygons", "circles"});
	JLabel txtNumberOfColumns = new JLabel("N columns ");
	JSpinner ezNumberOfColumns = new JSpinner(new SpinnerNumberModel(5, 1, 1000, 1));
	JLabel txtColumnWidth = new JLabel ("column width");
	JSpinner columnWidth = new JSpinner (new SpinnerNumberModel(10, 0, 1000, 1));
	JLabel txtColumnSpan = new JLabel("space btw. col. ");
	JSpinner columnSpan = new JSpinner (new SpinnerNumberModel( 1, 0, 1000, 1));
	JLabel txtNumberOfRows = new JLabel("N rows ");
	JSpinner ezNumberOfRows = new JSpinner(new SpinnerNumberModel(10, 1, 1000, 1));
	JLabel txtRowHeight = new JLabel ("row height ");
	JSpinner rowHeight = new JSpinner(new SpinnerNumberModel( 0, 0, 1000, 1)); 
	JLabel txtRowSpan = new JLabel("space btw. row ");
	JSpinner rowInterval = new JSpinner(new SpinnerNumberModel(1, 0, 1000, 1));
	JButton generateGridButton = new JButton("Create grid");

	Areatrack areatrack = null;
	private SequenceVirtual sequenceVirtual = null;
	private DlgOutputData dlgOutputData = null;

	
	public void init(Areatrack areatrack, IcyFrame dialogFrame, JPanel mainPanel, DlgOutputData dlgOutputData) {
		
		this.areatrack  = areatrack;
		this.dlgOutputData = dlgOutputData;
		sequenceVirtual = areatrack.vSequence;
		
		PopupPanel popuppanel2 = new PopupPanel("define lines manually");
		JPanel panel2 = popuppanel2.getMainPanel();
		panel2.setLayout(new GridLayout(8, 2));
		panel2.add(GuiUtil.besidesPanel(txtSplitAsComboBox, splitAsComboBox));
		panel2.add(GuiUtil.besidesPanel(txtNumberOfColumns, ezNumberOfColumns));
		panel2.add(GuiUtil.besidesPanel(txtColumnWidth, columnWidth));
		panel2.add(GuiUtil.besidesPanel(txtColumnSpan, columnSpan));
		panel2.add(GuiUtil.besidesPanel(txtNumberOfRows, ezNumberOfRows));
		panel2.add(GuiUtil.besidesPanel(txtRowHeight, rowHeight));
		panel2.add(GuiUtil.besidesPanel(txtRowSpan, rowInterval));
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
			execute(); 
			}});
	}
	
	protected void execute() 
	{
		String choice = (String) splitAsComboBox.getSelectedItem();
		Sequence seq = sequenceVirtual.seq;
		double colSpan = (double) columnSpan.getValue();
		double colSize = (double) columnWidth.getValue();
		double nbcols = (double) ezNumberOfColumns.getValue(); 
		double rowSpan = (double) rowInterval.getValue();
		double rowSize = (double) rowHeight.getValue();
		double nbrows = (double) ezNumberOfRows.getValue();
		String rootName = (String) dlgOutputData.ezRootnameComboBox.getSelectedItem();
		
		if (choice == "vertical lines") 
		{
			DefineLinesManually.createROISFromSelectedPolygon(seq, 0, rootName, colSpan, colSize, nbcols, rowSpan, rowSize, nbrows);
		}
		else if (choice == "polygons") 
		{
			DefineLinesManually.createROISFromSelectedPolygon(seq, 1, rootName, colSpan, colSize, nbcols, rowSpan, rowSize, nbrows);
		}
		else if (choice == "circles") 
		{
			DefineLinesManually.createROISFromSelectedPolygon(seq, 2, rootName, colSpan, colSize, nbcols, rowSpan, rowSize, nbrows);
		}		
	}
	
	@Override
	public void onChanged(CollapsibleEvent event) {
		// TODO Auto-generated method stub
		
	}

}
