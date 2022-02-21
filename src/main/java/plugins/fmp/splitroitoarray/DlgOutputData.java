package plugins.fmp.splitroitoarray;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import icy.common.CollapsibleEvent;
import icy.common.listener.ChangeListener;
import icy.gui.component.PopupPanel;
import icy.gui.frame.IcyFrame;
import icy.gui.util.GuiUtil;
import icy.roi.ROI;
import plugins.fmp.areatrack.Areatrack;

public class DlgOutputData  extends JPanel implements ChangeListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4391541015534295004L;
	JLabel txtRootnameComboBox = new JLabel ("Names of ROIS begin with");
	JComboBox<String> ezRootnameComboBox = new JComboBox<String> (new String[] {"gridA", "gridB", "gridC" });
	JButton changeGridNameButton = new JButton("Set names of ROIs");
	JButton	saveXMLButton = new JButton("Save ROIs to XML file");
	Areatrack areatrack = null;
	
	
	public void init(Areatrack areatrack, IcyFrame dialogFrame, JPanel mainPanel) {
		
		this.areatrack = areatrack;
		PopupPanel popuppanel3 = new PopupPanel("output data");
		JPanel panel3 = popuppanel3.getMainPanel();
		panel3.setLayout(new GridLayout(3, 2));
		panel3.add(GuiUtil.besidesPanel(txtRootnameComboBox, ezRootnameComboBox));
		panel3.add(GuiUtil.besidesPanel(changeGridNameButton));
		panel3.add(GuiUtil.besidesPanel(saveXMLButton));
		mainPanel.add(popuppanel3);
		popuppanel3.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				dialogFrame.revalidate();
				dialogFrame.pack();
				dialogFrame.repaint();
			}});
		popuppanel3.expand();
		addActionListeners();
	}

	void addActionListeners() {
		saveXMLButton.addActionListener(new ActionListener() { 
		public void actionPerformed(ActionEvent e) { 
			saveXMLFile(); 
			}});

	changeGridNameButton .addActionListener(new ActionListener () {
		public void actionPerformed(ActionEvent e) { 
			changeGridName(); 
			}});
	}
	
	@Override
	public void onChanged(CollapsibleEvent event) {
		// TODO Auto-generated method stub
		
	}
	
	private void saveXMLFile() 
	{
		areatrack.vSequence.capillariesRoi2RoiArray.grouping = 1;
		areatrack.vSequence.capillariesRoi2RoiArray.xmlWriteROIsAndDataNoFilter("roisarray.xml", areatrack.vSequence);
	}
	
	private void changeGridName() 
	{
		List<ROI> roisList = areatrack.vSequence.seq.getROIs(true);
		String baseName = (String) ezRootnameComboBox.getSelectedItem();
		
		for (ROI roi : roisList) {
			String cs = roi.getName();
			int firstunderscore = cs.indexOf("_");
			if (firstunderscore > 0) {
				cs = baseName + cs.substring(firstunderscore);
				roi.setName(cs);
			}
		}
	}

}
