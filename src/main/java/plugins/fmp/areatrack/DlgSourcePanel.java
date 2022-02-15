package plugins.fmp.areatrack;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JButton;
import javax.swing.JPanel;

import icy.gui.component.PopupPanel;
import icy.gui.frame.IcyFrame;
import icy.gui.util.GuiUtil;

public class DlgSourcePanel extends JPanel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6623935409683076615L;
	private JButton setVideoSourceButton	= new JButton("Open...");
	private JButton closeAllButton = new JButton("Close");

	
	public void init (Areatrack parent0, IcyFrame mainFrame, JPanel mainPanel) {
		
		PopupPanel 	capPopupPanel = new PopupPanel("Source data");
		JPanel capPanel = capPopupPanel.getMainPanel();
		capPanel.setLayout(new BorderLayout());
		capPopupPanel.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				mainFrame.revalidate();
				mainFrame.pack();
				mainFrame.repaint();
			}});
		capPopupPanel.expand();
		mainPanel.add(capPopupPanel);

		capPanel.add( GuiUtil.besidesPanel(setVideoSourceButton, closeAllButton));
		
		setVideoSourceButton.addActionListener(new ActionListener () { 
			@Override public void actionPerformed( final ActionEvent e ) {
				parent0.openVideoOrStack();
			} } );
		
		closeAllButton.addActionListener(new ActionListener () { 
			@Override public void actionPerformed( final ActionEvent e ) { 
				if (parent0.displayCharts != null && parent0.displayCharts.mainChartFrame != null) 
				{
					parent0.displayCharts.mainChartFrame.removeAll();
					parent0.displayCharts.mainChartFrame.close();
					parent0.displayCharts.mainChartFrame = null;
				}
				parent0.vSequence.close();
			} } );
	}
}
