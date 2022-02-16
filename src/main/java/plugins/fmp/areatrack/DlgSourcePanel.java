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
	Areatrack parent0 = null;

	
	public void init (Areatrack parent0, IcyFrame mainFrame, JPanel mainPanel) {
		
		this.parent0 = parent0;
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
		
		declareActionListeners();
	}
		
	private void declareActionListeners() {
		setVideoSourceButton.addActionListener(new ActionListener () { 
			@Override public void actionPerformed( final ActionEvent e ) {
				openVideoOrStack();
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
	
	public void openVideoOrStack() {
		String path = null;
		if (parent0.vSequence != null)
			parent0.vSequence.close();

		Sequence seq = OpenVirtualSequence.openImagesOrAvi(null);
		Viewer v = OpenVirtualSequence.initSequenceViewer(seq);
		v.addListener(parent0);
		parent0.vSequence = new SequencePlus(seq);
		
		path = parent0.vSequence.getDirectory();
		if (path != null) {
			XMLPreferences guiPrefs = parent0.getPreferences("gui");
			guiPrefs.put("lastUsedPath", path);
		}
		
		updateGuiEndFrame();
		XmlAreaTrack xmlAreaTrack = new XmlAreaTrack();
		xmlAreaTrack.xmlReadAreaTrackParameters(parent0);
	}
	
	private void updateGuiEndFrame () {
		parent0.endFrame = parent0.vSequence.getSizeT()-1;
		parent0.dlgAnalysisRun.endFrameTextField.setText( Integer.toString(parent0.endFrame));
	}
}
