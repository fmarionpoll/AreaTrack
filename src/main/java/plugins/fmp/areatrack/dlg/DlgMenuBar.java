package plugins.fmp.areatrack.dlg;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import icy.gui.frame.IcyFrame;

public class DlgMenuBar {
	
	public static void panelSetMenuBar (IcyFrame mainFrame, JPanel mainPanel) {
		JMenuBar menuBar = new JMenuBar();
		JMenu aboutMenu = new JMenu("About");
		menuBar.add(aboutMenu);
		
		JMenuItem manualItem = new JMenuItem("Manual");
		aboutMenu.add(manualItem);
		manualItem.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(mainPanel,
					    "Please refer to the online help:\n http://icy.bioimageanalysis.org/plugin/...", "Manual", JOptionPane.INFORMATION_MESSAGE );
			}
		});
		JMenuItem aboutItem = new JMenuItem("About");
		aboutMenu.add(aboutItem);
		aboutItem.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(mainPanel,
					    "This plugin is distributed under GPL v3 license.\n Author: Frederic Marion-Poll" +
					    "\n Email frederic.marion-poll@egce.cnrs-gif.fr", "About", JOptionPane.INFORMATION_MESSAGE);
			}
		});
		mainFrame.setJMenuBar(menuBar);
	}
}
