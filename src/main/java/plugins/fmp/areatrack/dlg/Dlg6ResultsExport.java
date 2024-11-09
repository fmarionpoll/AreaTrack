package plugins.fmp.areatrack.dlg;

import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import icy.gui.component.PopupPanel;
import icy.gui.frame.IcyFrame;
import icy.gui.util.FontUtil;
import plugins.fmp.areatrack.Areatrack;
import plugins.fmp.areatrack.commons.ExportToXLS;
import plugins.fmp.areatrack.commons.GraphsOverlay;
import plugins.fmp.areatrack.commons.GraphsWindow;
import plugins.fmp.areatrack.tools.FmpTools;

public class Dlg6ResultsExport extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 487691225410920887L;
	JComboBox<String> filterComboBox = new JComboBox<String> (new String[] {"raw data", "average", "median"});
	public JTextField 	spanTextField = new JTextField("10");
	private JButton updateChartsButton = new JButton("Chart window");
	private JButton setGraphsOverlayButton	= new JButton("Curves");
	private JButton exportToXLSButton = new JButton("Save XLS file..");
	Areatrack areatrack = null;
	
	public void init(Areatrack areatrack, IcyFrame mainFrame, JPanel mainPanel, String title) {
		
		this.areatrack = areatrack;
		PopupPanel 	capPopupPanel = new PopupPanel(title);
		JPanel capPanel = capPopupPanel.getMainPanel();
		capPanel.setLayout(new GridLayout(2, 2));
		capPopupPanel.collapse();
		capPopupPanel.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				mainFrame.revalidate();
				mainFrame.pack();
				mainFrame.repaint();
			}});
		mainPanel.add(capPopupPanel);
		
		JLabel outputLabel = new JLabel ("output ", SwingConstants.RIGHT);
		JLabel spanLabel = new JLabel ("span ", SwingConstants.RIGHT);
		FlowLayout layoutLeft = new FlowLayout(FlowLayout.LEFT); 
		JPanel panel1 = new JPanel(layoutLeft);
		panel1.add(updateChartsButton);
		panel1.add(setGraphsOverlayButton);
		setGraphsOverlayButton.setEnabled(false);
		panel1.add(outputLabel);
		panel1.add(filterComboBox);
		panel1.add(spanLabel);
		panel1.add(spanTextField);
		capPanel.add(panel1);
		
		FlowLayout layoutRight = new FlowLayout(FlowLayout.RIGHT); 
		JPanel panel3 = new JPanel(layoutRight);
		JLabel emptyText1 = new JLabel ("-> File ", SwingConstants.RIGHT);
		emptyText1.setFont(FontUtil.setStyle(emptyText1.getFont(), Font.ITALIC));
		panel3.add(emptyText1);
		panel3.add(exportToXLSButton);
		capPanel.add(panel3); 
		
		filterComboBox.setSelectedIndex(0);
		
		declareActionListeners();		
	}
	
	private void declareActionListeners() {
		updateChartsButton.addActionListener(new ActionListener () { 
			@Override public void actionPerformed( final ActionEvent e ) {
				updateCharts(); 
			} } );
		
		setGraphsOverlayButton.addActionListener(new ActionListener () { 
			@Override public void actionPerformed( final ActionEvent e ) {
				setGraphsOverlay(); 
			} } );
		
		exportToXLSButton.addActionListener(new ActionListener () { 
			@Override public void actionPerformed( final ActionEvent e ) {
				try {
					exportToXLS();
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			} } );
	}
	
	private void updateCharts() {
		areatrack.displayCharts = new GraphsWindow();
		int span = Integer.parseInt(spanTextField.getText());
		int filteroption = filterComboBox.getSelectedIndex();
		areatrack.displayCharts.updateCharts(areatrack.vSequence, filteroption, span); 
	}
	
	private void setGraphsOverlay() {
		GraphsOverlay displayGraphs = new GraphsOverlay();
		int span = Integer.parseInt(spanTextField.getText());
		int filteroption = filterComboBox.getSelectedIndex();
		displayGraphs.updateCharts(areatrack.vSequence, filteroption, span); 
	}
	
	private void exportToXLS() throws InterruptedException {
		String file = FmpTools.saveFileAs(null, areatrack.vSequence.getDirectory(), "xls");
		if (file != null) {	
			ExportToXLS exportToXLS = new ExportToXLS();
			final String filename = file; 
			exportToXLS.exportToXLS(areatrack, filename);
		}
	}


}
