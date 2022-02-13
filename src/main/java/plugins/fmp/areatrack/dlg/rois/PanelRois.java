package plugins.fmp.areatrack.dlg.rois;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import icy.gui.util.GuiUtil;

public class PanelRois extends JPanel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5602173584142661474L;
	private JButton		openROIsButton		= new JButton("Load...");
	private JButton		addROIsButton		= new JButton("Add...");
	private JButton		saveROIsButton		= new JButton("Save...");


	public void init(JPanel mainPanel) {
		final JPanel panel =  GuiUtil.generatePanel("ROIs");
		mainPanel.add(GuiUtil.besidesPanel(panel));
		
		JLabel commentText1 = new JLabel ("Use RoitoRoiArray plugin to create polygons ");
		commentText1.setHorizontalAlignment(SwingConstants.LEFT);
		panel.add(GuiUtil.besidesPanel(commentText1));
		JLabel emptyText1	= new JLabel (" ");
		panel.add(GuiUtil.besidesPanel(emptyText1, openROIsButton, addROIsButton, saveROIsButton));
	}
	
}
