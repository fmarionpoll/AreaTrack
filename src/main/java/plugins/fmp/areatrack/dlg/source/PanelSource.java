package plugins.fmp.areatrack.dlg.source;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

import icy.gui.component.PopupPanel;
import icy.gui.util.GuiUtil;
import icy.gui.viewer.Viewer;
import icy.preferences.XMLPreferences;
import icy.sequence.Sequence;
import plugins.fmp.areatrack.Areatrack;
import plugins.fmp.fmpSequence.OpenVirtualSequence;
import plugins.fmp.fmpSequence.SequencePlus;

public class PanelSource extends JPanel {
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 3618148278728944991L;
	private Areatrack parent0 = null;
	public	PopupPanel capPopupPanel = null;
	
	private JButton setVideoSourceButton = new JButton("Open...");
	
	
	public void init (JPanel mainPanel, String string, Areatrack parent0) {
		
		this.parent0 = parent0;
		capPopupPanel = new PopupPanel(string);			
		capPopupPanel.collapse();
		JPanel capPanel = capPopupPanel.getMainPanel();
		mainPanel.add(capPopupPanel);
		capPanel.setLayout(new BorderLayout());
		
		JPanel k0Panel = new JPanel();
		capPanel.add( GuiUtil.besidesPanel(setVideoSourceButton, k0Panel));
		
		setVideoSourceButton.addActionListener(new ActionListener () { @Override public void actionPerformed( final ActionEvent e ) {
			openVideoOrStack();
		} } );
	}
	
	private void openVideoOrStack() {
		String path = null;
		if (parent0.vSequence != null)
			parent0.vSequence.close();

		Sequence seq = OpenVirtualSequence.openImagesOrAvi(null);
		Viewer v = OpenVirtualSequence.initSequenceViewer(seq);
		v.addListener(Areatrack.this);
		parent0.vSequence = new SequencePlus(seq);
		
		path = parent0.vSequence.getDirectory();
		if (path != null) {
			XMLPreferences guiPrefs = this.getPreferences("gui");
			guiPrefs.put("lastUsedPath", path);
		}
		
		parent0.vSequence.capillariesRoi2RoiArray.capillariesArrayList.clear();
		updateGuiEndFrame();
		loadParametersFromXMLFile();
	}
}
