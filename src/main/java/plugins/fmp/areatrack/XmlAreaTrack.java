package plugins.fmp.areatrack;

import java.awt.Color;
import java.io.File;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import icy.gui.frame.progress.AnnounceFrame;
import icy.util.XMLUtil;
import plugins.fmp.fmpTools.EnumImageOp;
import plugins.fmp.fmpTools.FmpTools;

public class XmlAreaTrack {
	Areatrack parent0 = null;
	
	public void xmlReadAreaTrackParameters(Areatrack parent0) {
		this.parent0 = parent0;
		
		String directory = parent0.vSequence.getDirectory();
		String fileparameters = directory + File.separator+ parent0.filename;
		final Document doc = XMLUtil.loadDocument(fileparameters);
		boolean flag = false;
		if (doc != null) {
			flag = xmlReadAreaTrackParameters(doc);
			if (flag) 
				transferParametersToDialog();
			else
				new AnnounceFrame("reading data failed");
		}
	}
	
	public void xmlWriteAreaTrackParameters(Areatrack parent0) {
		this.parent0 = parent0;
		
		String csFile = FmpTools.saveFileAs(parent0.filename, parent0.vSequence.getDirectory(), "xml");
		csFile.toLowerCase();
		if (!csFile.contains(".xml")) 
			csFile += ".xml";
		
		final Document doc = XMLUtil.createDocument(true);
		boolean flag = false;
		if (doc != null)
		{
			flag = xmlWriteAreaTrackParameters (doc);
			XMLUtil.saveDocument(doc, csFile);
		}
		if (!flag)
			new AnnounceFrame("saving data failed");
	}

	private boolean xmlReadAreaTrackParameters (Document doc) {

		String nodeName = "areaTrack";
		// read local parameters
		Node node = XMLUtil.getElement(XMLUtil.getRootElement(doc), nodeName);
		if (node == null)
			return false;

		Element xmlElement = XMLUtil.getElement(node, "Parameters");
		if (xmlElement == null) 
			return false;

		Element xmlVal = XMLUtil.getElement(xmlElement, "colormodeselected");
		boolean iscolorselected = XMLUtil.getAttributeBooleanValue(xmlVal, "value", true );
		parent0.dlgAnalysis.rbFilterbyColor.setSelected(iscolorselected);
		
		xmlVal = XMLUtil.getElement(xmlElement, "colortransformop");	
		String codestring = XMLUtil.getAttributeValue(xmlVal, "descriptor", "none");		
		parent0.colortransformop = EnumImageOp.findByText(codestring);
			
		xmlVal = XMLUtil.getElement(xmlElement, "simpletransformop");
		codestring = XMLUtil.getAttributeValue(xmlVal, "descriptor", "none");
		parent0.simpletransformop = EnumImageOp.findByText(codestring);

		xmlVal = XMLUtil.getElement(xmlElement, "thresholdmovement");
		parent0.thresholdmovement = XMLUtil.getAttributeIntValue(xmlVal, "value", 20);
		
		xmlVal = XMLUtil.getElement(xmlElement, "colordistanceType");
		parent0.colordistanceType = XMLUtil.getAttributeIntValue(xmlVal, "value", 0);
		
		xmlVal = XMLUtil.getElement(xmlElement, "colorthreshold");
		parent0.colorthreshold = XMLUtil.getAttributeIntValue(xmlVal, "value", 20);
		
		parent0.colorarray.clear();
		xmlVal = XMLUtil.getElement(xmlElement, "ncolors");
		int ncolors = XMLUtil.getAttributeIntValue(xmlVal, "value", 0);
		for (int i= 0; i<ncolors; i++) {
			xmlVal = XMLUtil.getElement(xmlElement, "color"+Integer.toString(i));
			int alpha = XMLUtil.getAttributeIntValue(xmlVal, "a", 0);
			int red = XMLUtil.getAttributeIntValue(xmlVal, "r", 0);
			int blue = XMLUtil.getAttributeIntValue(xmlVal, "b", 0);
			int green = XMLUtil.getAttributeIntValue(xmlVal, "g", 0);
			Color color = new Color(red, green, blue, alpha);
			parent0.colorarray.add(color);
		}
		return true;
	}
	
	private boolean xmlWriteAreaTrackParameters (Document doc) {

		// save local parameters
		String nodeName = "areaTrack";
		Node node = XMLUtil.addElement(XMLUtil.getRootElement(doc), nodeName);
		if (node == null)
			return false;
		
		Element xmlElement = XMLUtil.addElement(node, "Parameters");
		
		Element xmlVal = XMLUtil.addElement(xmlElement, "colormodeselected");
		XMLUtil.setAttributeBooleanValue(xmlVal, "value", parent0.dlgAnalysis.rbFilterbyColor.isSelected() );
	
		xmlVal = XMLUtil.addElement(xmlElement, "simpletransformop");
		XMLUtil.setAttributeValue(xmlVal, "descriptor", parent0.simpletransformop.toString());
		
		xmlVal = XMLUtil.addElement(xmlElement, "simplethreshold");
		XMLUtil.setAttributeIntValue(xmlVal, "value", Integer.parseInt(parent0.thresholdSpinner.getValue().toString()));
		
		xmlVal = XMLUtil.addElement(xmlElement, "colortransformop");
		XMLUtil.setAttributeValue(xmlVal, "descriptor", parent0.colortransformop.toString());
		
		xmlVal = XMLUtil.addElement(xmlElement, "thresholdtype");
		XMLUtil.setAttributeValue(xmlVal, "descriptor", parent0.thresholdtype.toString());	
		
		xmlVal = XMLUtil.addElement(xmlElement, "colordistanceType");
		XMLUtil.setAttributeIntValue(xmlVal, "value", parent0.colordistanceType);

		xmlVal = XMLUtil.addElement(xmlElement, "thresholdmovement");
		XMLUtil.setAttributeIntValue(xmlVal, "value", parent0.thresholdmovement);
		
		xmlVal = XMLUtil.addElement(xmlElement, "colorthreshold");
		XMLUtil.setAttributeIntValue(xmlVal, "value", parent0.colorthreshold);
		
		xmlVal = XMLUtil.addElement(xmlElement, "ncolors");
		XMLUtil.setAttributeIntValue(xmlVal, "value", parent0.colorarray.size());
		for (int i = 0; i < parent0.colorarray.size(); i++) {
			Color color = parent0.colorarray.get(i);
			xmlVal = XMLUtil.addElement(xmlElement, "color"+Integer.toString(i));
			XMLUtil.setAttributeIntValue(xmlVal, "a", color.getAlpha());
			XMLUtil.setAttributeIntValue(xmlVal, "r", color.getRed());
			XMLUtil.setAttributeIntValue(xmlVal, "g", color.getGreen());
			XMLUtil.setAttributeIntValue(xmlVal, "b", color.getBlue());
		}
		return true;
	}
	
	private void transferParametersToDialog() {
		
		parent0.distanceSpinner.setValue(parent0.colorthreshold);
		parent0.dlgAnalysis.tabbedPane.setSelectedIndex(3);
		switch (parent0.colortransformop) {
		case RGB_TO_HSV:
			parent0.rbHSV.setSelected(true);
			break;
		case RGB_TO_H1H2H3:
			parent0.rbH1H2H3.setSelected(true);
			break;
		case NONE:
		default:
			parent0.rbRGB.setSelected(true);
			break;
		}
		parent0.colorPickCombo.removeAll();
		for (int i=0; i < parent0.colorarray.size(); i++)
			parent0.colorPickCombo.addItem(parent0.colorarray.get(i));
		if (parent0.colordistanceType == 1)
			parent0.rbL1.setSelected(true);
		else
			parent0.rbL2.setSelected(true);
		parent0.transformsComboBox.setSelectedItem(parent0.simpletransformop);
		parent0.thresholdSpinner.setValue(parent0.simplethreshold);
		parent0.threshold2Spinner.setValue(parent0.thresholdmovement);
	}
	
}
