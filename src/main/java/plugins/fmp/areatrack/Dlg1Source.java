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
import icy.gui.viewer.Viewer;
import icy.preferences.XMLPreferences;
import icy.sequence.Sequence;
import icy.sequence.SequenceEvent;
import icy.sequence.SequenceListener;
import plugins.fmp.fmpSequence.OpenSequencePlus;
import plugins.fmp.fmpSequence.SequencePlus;


public class Dlg1Source extends JPanel implements SequenceListener {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6623935409683076615L;
	private JButton setVideoSourceButton	= new JButton("Open...");
	private JButton closeAllButton = new JButton("Close");
	Areatrack areatrack = null;

	
	public void init (Areatrack areatrack, IcyFrame mainFrame, JPanel mainPanel) {
		
		this.areatrack = areatrack;
		PopupPanel 	capPopupPanel = new PopupPanel("1 - IMAGES STACK");
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
		
		declareActionListeners();
	}
		
	private void declareActionListeners() {
		setVideoSourceButton.addActionListener(new ActionListener () { 
			@Override public void actionPerformed( final ActionEvent e ) {
				openVideoOrStack();
			} } );
		
		closeAllButton.addActionListener(new ActionListener () { 
			@Override public void actionPerformed( final ActionEvent e ) { 
				if (areatrack.displayCharts != null && areatrack.displayCharts.mainChartFrame != null) 
				{
					areatrack.displayCharts.mainChartFrame.removeAll();
					areatrack.displayCharts.mainChartFrame.close();
					areatrack.displayCharts.mainChartFrame = null;
				}
				areatrack.vSequence.close();
			} } );
	}
	
	public void openVideoOrStack() {
		String path = null;
		if (areatrack.vSequence != null)
			areatrack.vSequence.close();

		SequencePlus sequencePlus = OpenSequencePlus.openImagesOrAvi(null);
		sequencePlus.seq.addListener(this);
		Viewer v = OpenSequencePlus.initSequenceViewer(sequencePlus.seq);
		v.addListener(areatrack);
		
		
		areatrack.vSequence = sequencePlus;
		path = areatrack.vSequence.getDirectory();
		if (path != null) {
			XMLPreferences guiPrefs = areatrack.getPreferences("gui");
			guiPrefs.put("lastUsedPath", path);
		}
		updateGuiEndFrame();
	}
	
	private void updateGuiEndFrame () {
		if (areatrack.vSequence == null) return;
		areatrack.endFrame = areatrack.vSequence.nTotalFrames-1;
		areatrack.dlgAnalysisRun.endFrameTextField.setText( Integer.toString(areatrack.endFrame));
	}

	@Override
	public void sequenceChanged(SequenceEvent sequenceEvent) {
		updateGuiEndFrame();
	}

	@Override
	public void sequenceClosed(Sequence sequence) {
		// TODO Auto-generated method stub
		
	}
}
