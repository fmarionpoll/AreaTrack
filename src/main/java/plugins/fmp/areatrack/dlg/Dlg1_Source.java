package plugins.fmp.areatrack.dlg;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Rectangle;
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
import icy.gui.util.FontUtil;
import icy.gui.viewer.Viewer;
import icy.main.Icy;
import icy.preferences.XMLPreferences;
import icy.sequence.Sequence;
import icy.sequence.SequenceEvent;
import icy.sequence.SequenceListener;
import plugins.fmp.areatrack.Areatrack;
import plugins.fmp.areatrack.sequence.SequencePlus;
import plugins.fmp.areatrack.sequence.SequencePlusOpen;

public class Dlg1_Source extends JPanel implements SequenceListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6623935409683076615L;
	private JButton openButton = new JButton("Open...");
	private JButton closeButton = new JButton("Close");
	Areatrack areatrack = null;

	public void init(Areatrack areatrack, IcyFrame mainFrame, JPanel mainPanel, String title) {

		this.areatrack = areatrack;
		PopupPanel capPopupPanel = new PopupPanel(title);
		JPanel capPanel = capPopupPanel.getMainPanel();
		capPanel.setLayout(new BorderLayout());
		capPopupPanel.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				mainFrame.revalidate();
				mainFrame.pack();
				mainFrame.repaint();
			}
		});
		capPopupPanel.expand();
		mainPanel.add(capPopupPanel);

		JLabel loadsaveText1 = new JLabel("-> File (jpg, jpeg, bmp, tiff) ", SwingConstants.RIGHT);
		loadsaveText1.setFont(FontUtil.setStyle(loadsaveText1.getFont(), Font.ITALIC));
		FlowLayout layoutRight = new FlowLayout(FlowLayout.RIGHT);
		JPanel panel2 = new JPanel(layoutRight);
		panel2.add(loadsaveText1);
		panel2.add(openButton);
		panel2.add(closeButton);
		capPanel.add(panel2);

		declareActionListeners();
	}

	private void declareActionListeners() {
		openButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				openVideoOrStack();
			}
		});

		closeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				if (areatrack.displayCharts != null && areatrack.displayCharts.mainChartFrame != null) {
					areatrack.displayCharts.mainChartFrame.removeAll();
					areatrack.displayCharts.mainChartFrame.close();
					areatrack.displayCharts.mainChartFrame = null;
				}
				areatrack.vSequence.close();
			}
		});
	}

	public void openVideoOrStack() {
		String path = null;
		if (areatrack.vSequence != null)
			areatrack.vSequence.close();

		SequencePlus sequencePlus = SequencePlusOpen.openImagesOrAvi(null);
		if (sequencePlus == null)
			return;
		sequencePlus.seq.addListener(this);
		Viewer v = SequencePlusOpen.initSequenceViewer(sequencePlus.seq);
		v.addListener(areatrack);
		placeViewerNextToDialogBox(v, areatrack.mainFrame);

		areatrack.vSequence = sequencePlus;
		path = areatrack.vSequence.getDirectory();
		if (path != null) {
			XMLPreferences guiPrefs = areatrack.getPreferences("gui");
			guiPrefs.put("lastUsedPath", path);
		}
		updateGuiEndFrame();
	}

	private void placeViewerNextToDialogBox(Viewer v, IcyFrame mainFrame) {
		Rectangle rectv = v.getBoundsInternal();
		Rectangle rect0 = mainFrame.getBoundsInternal();
		if (rect0.x + rect0.width < Icy.getMainInterface().getMainFrame().getDesktopWidth()) {
			rectv.setLocation(rect0.x + rect0.width, rect0.y);
			v.setBounds(rectv);
		}
	}

	private void updateGuiEndFrame() {
		if (areatrack.vSequence == null)
			return;
		areatrack.vSequence.analysisEnd = areatrack.vSequence.nTotalFrames - 1;
		areatrack.dlg5_AnalysisRun.updateStartAndEndFrameFromvSequence(areatrack.vSequence);
	}

	@Override
	public void sequenceChanged(SequenceEvent sequenceEvent) {
		// updateGuiEndFrame();
	}

	@Override
	public void sequenceClosed(Sequence sequence) {
		// TODO Auto-generated method stub

	}
}
