package plugins.fmp.areatrack;

import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import icy.gui.component.PopupPanel;
import icy.gui.frame.IcyFrame;
import icy.gui.util.GuiUtil;
import icy.roi.ROI2D;
import plugins.fmp.fmpTools.EnumImageOp;
import plugins.fmp.fmpTools.EnumThresholdType;

public class DlgAnalysisRun extends JPanel 
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
	AreaAnalysisThread analysisThread = null;
	Areatrack parent0 = null;


	public void init (Areatrack parent0, IcyFrame mainFrame, JPanel mainPanel) {
		
		this.parent0 = parent0;
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

		declareActionListeners();
		
	}
	
	private void declareActionListeners() {
		stopComputationButton.addActionListener(new ActionListener () { 
			@Override public void actionPerformed( final ActionEvent e ) { 
				stopAnalysisThread();
			} } );
		
		startComputationButton.addActionListener(new ActionListener () { 
			@Override public void actionPerformed( final ActionEvent e ) {  
				startAnalysisThread(); 
			} } );	
	}
	
	private void startAnalysisThread() {
		stopAnalysisThread();
		
		analysisThread = new AreaAnalysisThread(); 
		
		parent0.startFrame 	= Integer.parseInt( startFrameTextField.getText() );
		parent0.endFrame 	= Integer.parseInt( endFrameTextField.getText() );
		parent0.analyzeStep = Integer.parseInt( analyzeStepTextField.getText() );
		parent0.vSequence.analysisStep = parent0.analyzeStep;
		
		EnumImageOp transformop = EnumImageOp.NONE;
		int thresholdforsurface = parent0.simplethreshold;
		if (parent0.dlgAnalysisParameters.rbFilterbyFunction.isSelected()) {
			transformop = parent0.simpletransformop;
			parent0.thresholdtype = EnumThresholdType.SINGLE;
		} else if (parent0.dlgAnalysisParameters.rbFilterbyFunction.isSelected()) {
			transformop = EnumImageOp.NONE;
			parent0.thresholdtype = EnumThresholdType.COLORARRAY;
		}
		
		int thresholdformovement = parent0.thresholdmovement;
		
		analysisThread.setAnalysisThreadParameters(
			parent0.vSequence, 
			getROIsToAnalyze(), 
			parent0.startFrame, 
			parent0.endFrame,  
			transformop, 
			thresholdforsurface,
			thresholdformovement,
			parent0.dlgAnalysisParameters.measureSurfacesCheckBox.isSelected(), 
			parent0.dlgAnalysisParameters.measureHeatmapCheckBox.isSelected());
		
		analysisThread.setAnalysisThreadParametersColors (
			parent0.thresholdtype, 
			parent0.colordistanceType, 
			parent0.colorthreshold, 
			parent0.colorarray);
		
		analysisThread.start();	
	}
	
	private void stopAnalysisThread() {
		
		if (analysisThread != null && analysisThread.isAlive()) {
			analysisThread.interrupt();
			try {
				analysisThread.join();
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
		}
	}
	
	private ArrayList<ROI2D> getROIsToAnalyze() {
		return parent0.vSequence.seq.getROI2Ds();
	}
	
	
}
