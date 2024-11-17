package plugins.fmp.areatrack.dlg;

import java.awt.FlowLayout;
import java.awt.Font;
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
import icy.gui.util.FontUtil;
import plugins.fmp.areatrack.Areatrack;
import plugins.fmp.areatrack.commons.LoadRois;
import plugins.fmp.areatrack.splitroitoarray.SplitRoiToArray;

public class Dlg2_Grids extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 323538974587966282L;
	private JButton buildROISButton = new JButton("Build ROIs array...");
	private JButton openROIsButton = new JButton("Load...");
	private JButton addROIsButton = new JButton("Add...");
	private JButton saveROIsButton = new JButton("Save...");
	Areatrack areatrack = null;

	public void init(Areatrack areatrack, IcyFrame mainFrame, JPanel mainPanel, String title) {

		this.areatrack = areatrack;

		PopupPanel capPopupPanel = new PopupPanel(title);
		capPopupPanel.collapse();
		capPopupPanel.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				mainFrame.revalidate();
				mainFrame.pack();
				mainFrame.repaint();
			}
		});
		mainPanel.add(capPopupPanel);

		JPanel capPanel = capPopupPanel.getMainPanel();
		capPanel.setLayout(new GridLayout(2, 2));

		JLabel emptyText0 = new JLabel("Draw Polygon and split into array: ", SwingConstants.RIGHT);
		FlowLayout layoutRight = new FlowLayout(FlowLayout.RIGHT);
		JPanel panel1 = new JPanel(layoutRight);
		panel1.add(emptyText0);
		panel1.add(buildROISButton);
		capPanel.add(panel1);

		JLabel emptyText1 = new JLabel("-> File ", SwingConstants.RIGHT);
		emptyText1.setFont(FontUtil.setStyle(emptyText1.getFont(), Font.ITALIC));
		JPanel panel2 = new JPanel(layoutRight);
		panel2.add(emptyText1);
		panel2.add(openROIsButton);
		panel2.add(addROIsButton);
		panel2.add(saveROIsButton);
		capPanel.add(panel2);
		declareActionListeners();
	}

	private void declareActionListeners() {
		buildROISButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				SplitRoiToArray buildRois = new SplitRoiToArray();
				buildRois.initialize(areatrack);
//				loadRois.openROIs(areatrack.vSequence); 
//				updateStartAndEndFrameFromvSequence();
			}
		});

		openROIsButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				LoadRois loadRois = new LoadRois();
				loadRois.openROIs(areatrack.vSequence);
				updateStartAndEndFrameFromvSequence();
			}
		});

		saveROIsButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				LoadRois loadRois = new LoadRois();
				loadRois.saveROIs(areatrack.vSequence);
			}
		});

		addROIsButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				LoadRois loadRois = new LoadRois();
				loadRois.addROIs(areatrack.vSequence);
				updateStartAndEndFrameFromvSequence();
			}
		});
	}

	private void updateStartAndEndFrameFromvSequence() {
		areatrack.dlg5AnalysisRun.updateStartAndEndFrameFromvSequence(areatrack.vSequence);
	}
}
