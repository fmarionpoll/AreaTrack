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

import plugins.fmp.fmpTools.EnumThresholdType;

public class Dlg4AnalysisRun extends JPanel 
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
	Areatrack areatrack = null;


	public void init (Areatrack areatrack, IcyFrame mainFrame, JPanel mainPanel) {
		
		this.areatrack = areatrack;
		PopupPanel 	capPopupPanel = new PopupPanel("4 - RUN ANALYSIS");
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

		areatrack.startFrame 	= Integer.parseInt( startFrameTextField.getText() );
		areatrack.endFrame 	= Integer.parseInt( endFrameTextField.getText() );
		areatrack.analyzeStep = Integer.parseInt( analyzeStepTextField.getText() );
		areatrack.vSequence.analysisStep = areatrack.analyzeStep;
		
		if (areatrack.dlgAnalysisParameters.measureSurfacesCheckBox.isSelected()) 
		{ 
			if (areatrack.dlgAnalysisParameters.rbFilterbyFunction.isSelected()) 
			{
				areatrack.thresholdtype = EnumThresholdType.SINGLE;
				analysisThread.initAreaDetectionFromFunction(areatrack.vSequence, areatrack.startFrame, areatrack.endFrame, 
						getROIsToAnalyze(),  
						areatrack.simpletransformop, areatrack.simplethreshold);
			} 
			else 
			{
				areatrack.thresholdtype = EnumThresholdType.COLORARRAY;
				analysisThread.initAreaDetectionFromColors(areatrack.vSequence, areatrack.startFrame, areatrack.endFrame,
						getROIsToAnalyze(),  
						areatrack.colordistanceType, areatrack.colorthreshold, areatrack.colorarray);
			}
		}
		
		if (areatrack.dlgAnalysisParameters.measureHeatmapCheckBox.isSelected()) 
		{
			analysisThread.initMovementDetection(areatrack.vSequence, areatrack.startFrame, areatrack.endFrame,
					getROIsToAnalyze(),
					areatrack.thresholdmovement);
		}	
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
	
	// TODO : filter out ROIS that are not defining a "cage"
	private ArrayList<ROI2D> getROIsToAnalyze() {
		return areatrack.vSequence.seq.getROI2Ds();
	}
	
	
}
