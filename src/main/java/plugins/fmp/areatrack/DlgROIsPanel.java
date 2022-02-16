package plugins.fmp.areatrack;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import icy.gui.component.PopupPanel;
import icy.gui.frame.IcyFrame;
import icy.gui.util.GuiUtil;

public class DlgROIsPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 323538974587966282L;
	private JButton openROIsButton			= new JButton("Load...");
	private JButton addROIsButton			= new JButton("Add...");
	private JButton	saveROIsButton			= new JButton("Save...");
	Areatrack parent0 = null;

	public void init(Areatrack parent0, IcyFrame mainFrame, JPanel mainPanel) {
		
		this.parent0 = parent0;
		PopupPanel 	capPopupPanel = new PopupPanel("ROIs");
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
		
		JLabel commentText1 = new JLabel ("Use ROItoArray plugin to create polygons ");
		commentText1.setHorizontalAlignment(SwingConstants.LEFT);
		capPanel.add(GuiUtil.besidesPanel(commentText1));
		JLabel emptyText1	= new JLabel (" ");
		capPanel.add(GuiUtil.besidesPanel(emptyText1, openROIsButton, addROIsButton, saveROIsButton));
		
		declareActionListeners();
	}
	
	private void declareActionListeners() {
		openROIsButton.addActionListener(new ActionListener () { 
			@Override public void actionPerformed( final ActionEvent e ) { 
				LoadRois loadRois = new LoadRois();
				loadRois.openROIs(parent0.vSequence); 
				updateStartAndEndFrameFromvSequence();
			} } );
		
		saveROIsButton.addActionListener(new ActionListener () { 
			@Override public void actionPerformed( final ActionEvent e ) {
				parent0.vSequence.analysisStart = parent0.startFrame;
				parent0.vSequence.analysisEnd = parent0.endFrame;
				LoadRois loadRois = new LoadRois();
				loadRois.saveROIs(parent0.vSequence); 
			} } );
		
		addROIsButton.addActionListener(new ActionListener () { 
			@Override public void actionPerformed( final ActionEvent e ) {
				LoadRois loadRois = new LoadRois();
				loadRois.addROIs(parent0.vSequence);
				updateStartAndEndFrameFromvSequence();
			} } );
	}
	
	private void updateStartAndEndFrameFromvSequence()
	{
		parent0.startFrame = (int) parent0.vSequence.analysisStart;
		parent0.endFrame = (int) parent0.vSequence.analysisEnd;
		parent0.dlgAnalysisRun.endFrameTextField.setText( Integer.toString(parent0.endFrame));
		parent0.dlgAnalysisRun.startFrameTextField.setText( Integer.toString(parent0.startFrame));
	}
}
