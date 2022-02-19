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
import plugins.fmp.fmpSequence.OpenVirtualSequence;
import plugins.fmp.fmpSequence.SequencePlus;

public class DlgSourcePanel extends JPanel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6623935409683076615L;
	private JButton setVideoSourceButton	= new JButton("Open...");
	private JButton closeAllButton = new JButton("Close");
	Areatrack areatrack = null;

	
	public void init (Areatrack areatrack, IcyFrame mainFrame, JPanel mainPanel) {
		
		this.areatrack = areatrack;
		PopupPanel 	capPopupPanel = new PopupPanel("IMAGES STACK");
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

		Sequence seq = OpenVirtualSequence.openImagesOrAvi(null);
		Viewer v = OpenVirtualSequence.initSequenceViewer(seq);
		v.addListener(areatrack);
		areatrack.vSequence = new SequencePlus(seq);
		
		path = areatrack.vSequence.getDirectory();
		if (path != null) {
			XMLPreferences guiPrefs = areatrack.getPreferences("gui");
			guiPrefs.put("lastUsedPath", path);
		}
		
		updateGuiEndFrame();
		XmlAreaTrack xmlAreaTrack = new XmlAreaTrack();
		xmlAreaTrack.xmlReadAreaTrackParameters(areatrack);
	}
	
	private void updateGuiEndFrame () {
		areatrack.endFrame = areatrack.vSequence.getSizeT()-1;
		areatrack.dlgAnalysisRun.endFrameTextField.setText( Integer.toString(areatrack.endFrame));
	}
}
