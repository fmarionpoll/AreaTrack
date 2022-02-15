package plugins.fmp.areatrack;

import java.awt.GridLayout;
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

	
	
	public void init(Areatrack parent0, IcyFrame mainFrame, JPanel mainPanel) {
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
	}

}
