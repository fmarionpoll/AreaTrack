package plugins.fmp.areatrack.dlg;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import icy.gui.component.PopupPanel;
import icy.gui.frame.IcyFrame;
import icy.roi.ROI2D;
import icy.util.StringUtil;
import plugins.fmp.areatrack.AreaAnalysisThread;
import plugins.fmp.areatrack.Areatrack;
import plugins.fmp.areatrack.sequence.SequencePlus;
import plugins.fmp.areatrack.tools.EnumAreaDetection;



public class Dlg5AnalysisRun extends JPanel implements PropertyChangeListener
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 939455785786474853L;
	private JButton startComputationButton = new JButton("Start");
	private JButton stopComputationButton = new JButton("Stop");
	JSpinner analysisStartSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 10000, 1));
	JSpinner analysisEndSpinner = new JSpinner(new SpinnerNumberModel(9999999, 0, 9999999, 1));
	private JSpinner analyzeStepSpinner = new JSpinner(new SpinnerNumberModel(1, 0, 10000, 1));
	
	public AreaAnalysisThread analysisThread = null;
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
		
		FlowLayout layoutLeft = new FlowLayout(FlowLayout.LEFT); 
		JPanel panel1 = new JPanel(layoutLeft);
		((FlowLayout)panel1.getLayout()).setVgap(0);
		panel1.add(startComputationButton);
		panel1.add(stopComputationButton);
		stopComputationButton.setEnabled(false);
		capPanel.add(panel1 );
		
		JLabel startLabel = new JLabel("from ");
		JLabel endLabel = new JLabel("to ");
		JLabel stepLabel = new JLabel("step ");
		
		int bWidth = 50;
		int bHeight = 21;
		analysisStartSpinner.setPreferredSize(new Dimension(bWidth, bHeight));
		analysisEndSpinner.setPreferredSize(new Dimension(100, bHeight));
		analyzeStepSpinner.setPreferredSize(new Dimension(bWidth, bHeight));
		
		JPanel panel0 = new JPanel(layoutLeft);
		((FlowLayout)panel0.getLayout()).setVgap(0);
		panel0.add(startLabel);
		panel0.add(analysisStartSpinner);
		panel0.add(endLabel);
		panel0.add(analysisEndSpinner);
		panel0.add(stepLabel);
		panel0.add(analyzeStepSpinner);
		capPanel.add(panel0);

		declareActionListeners();
	}
	
	private void declareActionListeners() {
		
		stopComputationButton.addActionListener(new ActionListener () { 
			@Override public void actionPerformed( final ActionEvent e ) { 
				stopAnalysisThread();
				setButtonsStateAsAnalysisRunning(false);
			} } );
		
		startComputationButton.addActionListener(new ActionListener () { 
			@Override public void actionPerformed( final ActionEvent e ) {  
				startAnalysisThread(); 
				setButtonsStateAsAnalysisRunning(true);
			} } );	
	}
	
	private void setButtonsStateAsAnalysisRunning(boolean isRunning)
	{
		stopComputationButton.setEnabled(isRunning);
		startComputationButton.setEnabled(!isRunning);
	}
	
	public void updateStartAndEndFrameFromvSequence(SequencePlus vSequence)
	{
		if (vSequence == null) return;
		analysisEndSpinner.setValue( vSequence.analysisEnd);
		analysisStartSpinner.setValue( vSequence.analysisStart);
		analyzeStepSpinner.setValue( vSequence.analysisStep);
	}
	
	private void startAnalysisThread() {
		
		stopAnalysisThread();
		
		analysisThread = new AreaAnalysisThread(); 

		areatrack.vSequence.analysisStart = (int) analysisStartSpinner.getValue() ;
		areatrack.vSequence.analysisEnd = (int) analysisEndSpinner.getValue();
		areatrack.vSequence.analysisStep = (int) analyzeStepSpinner.getValue();
		
		if (areatrack.detectionParameters.detectArea) 
		{ 
			if (areatrack.detectionParameters.areaDetectionMode == EnumAreaDetection.SINGLE) 
			{
				analysisThread.initAreaDetectionFromFunction(areatrack.vSequence,  
						getROIsToAnalyze(),  
						areatrack.detectionParameters.simpletransformop, areatrack.detectionParameters.simplethreshold);
			} 
			else 
			{
				areatrack.detectionParameters.areaDetectionMode = EnumAreaDetection.COLORARRAY;
				analysisThread.initAreaDetectionFromColors(areatrack.vSequence, 
						getROIsToAnalyze(),  
						areatrack.detectionParameters.colordistanceType, 
						areatrack.detectionParameters.colorthreshold, 
						areatrack.detectionParameters.colorarray);
			}
		}
		
		if (areatrack.detectionParameters.detectMovement) 
		{
			analysisThread.initMovementDetection(areatrack.vSequence, 
					getROIsToAnalyze(),
					areatrack.detectionParameters.thresholdmovement);
		}
		
		analysisThread.addPropertyChangeListener(this);
		analysisThread.execute();	
	}
	
	private void stopAnalysisThread() {
		
		if (analysisThread != null && !analysisThread.stopFlag) {
			analysisThread.stopFlag = true;
		}
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (StringUtil.equals("thread_ended", evt.getPropertyName())) {
			setButtonsStateAsAnalysisRunning(false);
		 }
	}
	
	// TODO : filter out ROIS that are not defining a "cage"
		private ArrayList<ROI2D> getROIsToAnalyze() {
			return areatrack.vSequence.seq.getROI2Ds();
		}
		
}
