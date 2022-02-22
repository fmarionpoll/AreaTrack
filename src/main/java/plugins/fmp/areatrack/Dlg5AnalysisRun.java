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
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import icy.gui.component.PopupPanel;
import icy.gui.frame.IcyFrame;
import icy.gui.util.GuiUtil;
import icy.roi.ROI2D;
import plugins.fmp.fmpSequence.SequencePlus;
import plugins.fmp.fmpTools.EnumAreaDetection;



public class Dlg5AnalysisRun extends JPanel 
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 939455785786474853L;
	private JButton startComputationButton = new JButton("Start");
	private JButton stopComputationButton = new JButton("Stop");
	JSpinner startFrameSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 10000, 1));
	JSpinner endFrameSpinner = new JSpinner(new SpinnerNumberModel(99999999, 0, 99999999, 1));
	private JSpinner analyzeStepSpinner = new JSpinner(new SpinnerNumberModel(1, 0, 10000, 1));
	
	
	AreaAnalysisThread analysisThread = null;
	Areatrack areatrack = null;


	public void init (Areatrack areatrack, IcyFrame mainFrame, JPanel mainPanel, String title) {
		
		this.areatrack = areatrack;
		PopupPanel 	capPopupPanel = new PopupPanel(title);
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
		
		JLabel startLabel = new JLabel("from ");
		JLabel endLabel = new JLabel("to ");
		JLabel stepLabel = new JLabel("step ");
		
		FlowLayout layoutLeft = new FlowLayout(FlowLayout.LEFT); 
		JPanel panel0 = new JPanel(layoutLeft);
		((FlowLayout)panel0.getLayout()).setVgap(0);
		panel0.add(startLabel);
		panel0.add(startFrameSpinner);
		panel0.add(endLabel);
		panel0.add(endFrameSpinner);
		panel0.add(stepLabel);
		panel0.add(analyzeStepSpinner);
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
	
	public void updateStartAndEndFrameFromvSequence(SequencePlus vSequence)
	{
		endFrameSpinner.setValue( vSequence.analysisEnd);
		startFrameSpinner.setValue( vSequence.analysisStart);
	}
	
	private void startAnalysisThread() {
		
		stopAnalysisThread();
		
		analysisThread = new AreaAnalysisThread(); 

		areatrack.startFrame = (int) startFrameSpinner.getValue() ;
		areatrack.endFrame 	= (int) endFrameSpinner.getValue();
		areatrack.analyzeStep = (int) analyzeStepSpinner.getValue();
		areatrack.vSequence.analysisStep = areatrack.analyzeStep;
		
		if (areatrack.detectionParameters.detectArea) 
		{ 
			if (areatrack.detectionParameters.areaDetectionMode == EnumAreaDetection.SINGLE) 
			{
				analysisThread.initAreaDetectionFromFunction(areatrack.vSequence, areatrack.startFrame, areatrack.endFrame, 
						getROIsToAnalyze(),  
						areatrack.detectionParameters.simpletransformop, areatrack.detectionParameters.simplethreshold);
			} 
			else 
			{
				areatrack.detectionParameters.areaDetectionMode = EnumAreaDetection.COLORARRAY;
				analysisThread.initAreaDetectionFromColors(areatrack.vSequence, areatrack.startFrame, areatrack.endFrame,
						getROIsToAnalyze(),  
						areatrack.detectionParameters.colordistanceType, 
						areatrack.detectionParameters.colorthreshold, 
						areatrack.detectionParameters.colorarray);
			}
		}
		
		if (areatrack.detectionParameters.detectMovement) 
		{
			analysisThread.initMovementDetection(areatrack.vSequence, areatrack.startFrame, areatrack.endFrame,
					getROIsToAnalyze(),
					areatrack.detectionParameters.thresholdmovement);
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
