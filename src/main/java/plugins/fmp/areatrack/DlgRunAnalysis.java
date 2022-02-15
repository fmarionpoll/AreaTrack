package plugins.fmp.areatrack;

import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import icy.gui.component.PopupPanel;
import icy.gui.frame.IcyFrame;
import icy.gui.util.GuiUtil;

public class DlgRunAnalysis extends JPanel 
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 939455785786474853L;
	private JButton startComputationButton 	= new JButton("Start");
	private JButton stopComputationButton = new JButton("Stop");
	JTextField startFrameTextField	= new JTextField("0");
	JTextField endFrameTextField = new JTextField("99999999");
	
	private JTextField analyzeStepTextField	= new JTextField("1");


	public void init (Areatrack parent0, IcyFrame mainFrame, JPanel mainPanel) {		
		PopupPanel 	capPopupPanel = new PopupPanel("RUN ANALYSIS");
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
		
		capPanel.add( GuiUtil.besidesPanel( startComputationButton, stopComputationButton ) );
		
		JLabel startLabel 	= new JLabel("from ");
		JLabel endLabel 	= new JLabel("to ");
		JLabel stepLabel 	= new JLabel("step ");
		
		FlowLayout layoutLeft = new FlowLayout(FlowLayout.LEFT); 
		JPanel panel0 = new JPanel(layoutLeft);
		((FlowLayout)panel0.getLayout()).setVgap(0);
		panel0.add(startLabel);
		panel0.add(startFrameTextField);
		panel0.add(endLabel);
		panel0.add(endFrameTextField);
		panel0.add(stepLabel);
		panel0.add(analyzeStepTextField);
		capPanel.add(panel0);

	}
}
