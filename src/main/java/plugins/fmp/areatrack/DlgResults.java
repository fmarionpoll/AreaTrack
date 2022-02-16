package plugins.fmp.areatrack;

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
import icy.gui.util.GuiUtil;
import plugins.fmp.fmpTools.FmpTools;

public class DlgResults extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 487691225410920887L;
	JComboBox<String> filterComboBox = new JComboBox<String> (new String[] {"raw data", "average", "median"});
	JTextField 	spanTextField = new JTextField("10");
	private JButton updateChartsButton = new JButton("Chart window");
	private JButton setGraphsOverlayButton	= new JButton("Curves");
	private JButton exportToXLSButton = new JButton("Save XLS file..");
	Areatrack parent0 = null;

	
	
	public void init(Areatrack parent0, IcyFrame mainFrame, JPanel mainPanel) {
		
		this.parent0 = parent0;
		PopupPanel 	capPopupPanel = new PopupPanel("RESULTS DISPLAY/EXPORT");
		JPanel capPanel = capPopupPanel.getMainPanel();
		capPanel.setLayout(new GridLayout(3, 2));
		capPopupPanel.collapse();
		capPopupPanel.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				mainFrame.revalidate();
				mainFrame.pack();
				mainFrame.repaint();
			}});
		mainPanel.add(capPopupPanel);
		
		JLabel outputLabel = new JLabel ("output ");
		outputLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		JLabel spanLabel = new JLabel ("span ");
		spanLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		capPanel.add(GuiUtil.besidesPanel(outputLabel, filterComboBox, spanLabel, spanTextField));
		capPanel.add(GuiUtil.besidesPanel(updateChartsButton, setGraphsOverlayButton));
		capPanel.add(GuiUtil.besidesPanel(exportToXLSButton)); 
		
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
				exportToXLS();
			} } );
	}
	
	private void updateCharts() {
		parent0.displayCharts = new GraphsWindow();
		int span = Integer.parseInt(spanTextField.getText());
		int filteroption = filterComboBox.getSelectedIndex();
		parent0.displayCharts.updateCharts(parent0.vSequence, parent0.startFrame, parent0.endFrame, filteroption, span, parent0.analyzeStep); 
	}
	
	private void setGraphsOverlay() {
		GraphsOverlay displayGraphs = new GraphsOverlay();
		int span = Integer.parseInt(spanTextField.getText());
		int filteroption = filterComboBox.getSelectedIndex();
		displayGraphs.updateCharts(parent0.vSequence, parent0.startFrame, parent0.endFrame, filteroption, span, parent0.analyzeStep); 
	}
	
	private void exportToXLS() {
		String file = FmpTools.saveFileAs(null, parent0.vSequence.getDirectory(), "xls");
		if (file != null) {	
			ExportToXLS exportToXLS = new ExportToXLS();
			final String filename = file; 
			exportToXLS.exportToXLS(parent0, filename);
		}
	}


}
