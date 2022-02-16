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
	Areatrack areatrack = null;
	
	public void xmlReadAreaTrackParameters(Areatrack areatrack) {
		this.areatrack = areatrack;
		
		String directory = areatrack.vSequence.getDirectory();
		String fileparameters = directory + File.separator+ areatrack.filenameAreatrackXml;
		final Document doc = XMLUtil.loadDocument(fileparameters);
		boolean flag = false;
		if (doc != null) {
			flag = xmlReadAreaTrackParameters(doc);
			if (flag) 
				areatrack.dlgAnalysisParameters.transferParametersToDialog();
			else
				new AnnounceFrame("reading data failed");
		}
	}
	
	public void xmlWriteAreaTrackParameters(Areatrack areatrack) {
		this.areatrack = areatrack;
		
		String csFile = FmpTools.saveFileAs(areatrack.filenameAreatrackXml, areatrack.vSequence.getDirectory(), "xml");
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
		areatrack.dlgAnalysisParameters.rbFilterbyColor.setSelected(iscolorselected);
		
		xmlVal = XMLUtil.getElement(xmlElement, "colortransformop");	
		String codestring = XMLUtil.getAttributeValue(xmlVal, "descriptor", "none");		
		areatrack.colortransformop = EnumImageOp.findByText(codestring);
			
		xmlVal = XMLUtil.getElement(xmlElement, "simpletransformop");
		codestring = XMLUtil.getAttributeValue(xmlVal, "descriptor", "none");
		areatrack.simpletransformop = EnumImageOp.findByText(codestring);

		xmlVal = XMLUtil.getElement(xmlElement, "thresholdmovement");
		areatrack.thresholdmovement = XMLUtil.getAttributeIntValue(xmlVal, "value", 20);
		
		xmlVal = XMLUtil.getElement(xmlElement, "colordistanceType");
		areatrack.colordistanceType = XMLUtil.getAttributeIntValue(xmlVal, "value", 0);
		
		xmlVal = XMLUtil.getElement(xmlElement, "colorthreshold");
		areatrack.colorthreshold = XMLUtil.getAttributeIntValue(xmlVal, "value", 20);
		
		areatrack.colorarray.clear();
		xmlVal = XMLUtil.getElement(xmlElement, "ncolors");
		int ncolors = XMLUtil.getAttributeIntValue(xmlVal, "value", 0);
		for (int i= 0; i<ncolors; i++) {
			xmlVal = XMLUtil.getElement(xmlElement, "color"+Integer.toString(i));
			int alpha = XMLUtil.getAttributeIntValue(xmlVal, "a", 0);
			int red = XMLUtil.getAttributeIntValue(xmlVal, "r", 0);
			int blue = XMLUtil.getAttributeIntValue(xmlVal, "b", 0);
			int green = XMLUtil.getAttributeIntValue(xmlVal, "g", 0);
			Color color = new Color(red, green, blue, alpha);
			areatrack.colorarray.add(color);
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
		XMLUtil.setAttributeBooleanValue(xmlVal, "value", areatrack.dlgAnalysisParameters.rbFilterbyColor.isSelected() );
	
		xmlVal = XMLUtil.addElement(xmlElement, "simpletransformop");
		XMLUtil.setAttributeValue(xmlVal, "descriptor", areatrack.simpletransformop.toString());
		
		xmlVal = XMLUtil.addElement(xmlElement, "simplethreshold");
		XMLUtil.setAttributeIntValue(xmlVal, "value", areatrack.simplethreshold);
		
		xmlVal = XMLUtil.addElement(xmlElement, "colortransformop");
		XMLUtil.setAttributeValue(xmlVal, "descriptor", areatrack.colortransformop.toString());
		
		xmlVal = XMLUtil.addElement(xmlElement, "thresholdtype");
		XMLUtil.setAttributeValue(xmlVal, "descriptor", areatrack.thresholdtype.toString());	
		
		xmlVal = XMLUtil.addElement(xmlElement, "colordistanceType");
		XMLUtil.setAttributeIntValue(xmlVal, "value", areatrack.colordistanceType);

		xmlVal = XMLUtil.addElement(xmlElement, "thresholdmovement");
		XMLUtil.setAttributeIntValue(xmlVal, "value", areatrack.thresholdmovement);
		
		xmlVal = XMLUtil.addElement(xmlElement, "colorthreshold");
		XMLUtil.setAttributeIntValue(xmlVal, "value", areatrack.colorthreshold);
		
		xmlVal = XMLUtil.addElement(xmlElement, "ncolors");
		XMLUtil.setAttributeIntValue(xmlVal, "value", areatrack.colorarray.size());
		for (int i = 0; i < areatrack.colorarray.size(); i++) {
			Color color = areatrack.colorarray.get(i);
			xmlVal = XMLUtil.addElement(xmlElement, "color"+Integer.toString(i));
			XMLUtil.setAttributeIntValue(xmlVal, "a", color.getAlpha());
			XMLUtil.setAttributeIntValue(xmlVal, "r", color.getRed());
			XMLUtil.setAttributeIntValue(xmlVal, "g", color.getGreen());
			XMLUtil.setAttributeIntValue(xmlVal, "b", color.getBlue());
		}
		return true;
	}

	
}
