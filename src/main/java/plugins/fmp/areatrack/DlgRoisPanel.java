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

public class DlgRoisPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 323538974587966282L;
	private JButton openROIsButton			= new JButton("Load...");
	private JButton addROIsButton			= new JButton("Add...");
	private JButton	saveROIsButton			= new JButton("Save...");
	Areatrack areatrack = null;

	public void init(Areatrack areatrack, IcyFrame mainFrame, JPanel mainPanel) {
		
		this.areatrack = areatrack;
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
				loadRois.openROIs(areatrack.vSequence); 
				updateStartAndEndFrameFromvSequence();
			} } );
		
		saveROIsButton.addActionListener(new ActionListener () { 
			@Override public void actionPerformed( final ActionEvent e ) {
				areatrack.vSequence.analysisStart = areatrack.startFrame;
				areatrack.vSequence.analysisEnd = areatrack.endFrame;
				LoadRois loadRois = new LoadRois();
				loadRois.saveROIs(areatrack.vSequence); 
			} } );
		
		addROIsButton.addActionListener(new ActionListener () { 
			@Override public void actionPerformed( final ActionEvent e ) {
				LoadRois loadRois = new LoadRois();
				loadRois.addROIs(areatrack.vSequence);
				updateStartAndEndFrameFromvSequence();
			} } );
	}
	
	private void updateStartAndEndFrameFromvSequence()
	{
		areatrack.startFrame = (int) areatrack.vSequence.analysisStart;
		areatrack.endFrame = (int) areatrack.vSequence.analysisEnd;
		areatrack.dlgAnalysisRun.endFrameTextField.setText( Integer.toString(areatrack.endFrame));
		areatrack.dlgAnalysisRun.startFrameTextField.setText( Integer.toString(areatrack.startFrame));
	}
}