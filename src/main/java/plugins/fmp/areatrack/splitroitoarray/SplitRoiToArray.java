package plugins.fmp.areatrack.splitroitoarray;

import java.awt.FlowLayout;
import javax.swing.JPanel;
import icy.gui.frame.IcyFrame;
import icy.gui.util.GuiUtil;
import plugins.fmp.areatrack.Areatrack;



public class SplitRoiToArray extends JPanel 
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 2623347638174416630L;
	IcyFrame dialogFrame = null;	
	
	DlgSTDDetectLines dlgSTDDetectLines = new DlgSTDDetectLines();
	DlgDefineLinesManually dlgDefineLinesManually = new DlgDefineLinesManually();
	DlgOutputData dlgOutputData = new DlgOutputData();
		
	// ----------------------------------

	public void initialize(Areatrack areatrack) {

		JPanel mainPanel = GuiUtil.generatePanelWithoutBorder();
		dialogFrame = new IcyFrame ("Split ROI Polygon into array of ROIs", true, true);
		dialogFrame.setLayout(new FlowLayout());
		dialogFrame.add(mainPanel, FlowLayout.CENTER);
		
		dlgSTDDetectLines.init(areatrack, dialogFrame, mainPanel, dlgOutputData);
		dlgDefineLinesManually.init(areatrack, dialogFrame, mainPanel, dlgOutputData);
		dlgOutputData.init(areatrack, dialogFrame, mainPanel);
		
		dialogFrame.pack();
		dialogFrame.addToDesktopPane();
		dialogFrame.requestFocus();
		dialogFrame.center();
		dialogFrame.setVisible(true);
	}
	
	public void close() 
	{
		dialogFrame.close();
	}
	
}

