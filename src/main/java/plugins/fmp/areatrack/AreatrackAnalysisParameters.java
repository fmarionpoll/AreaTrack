package plugins.fmp.areatrack;

import java.awt.Color;
import java.io.File;
import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import icy.gui.frame.progress.AnnounceFrame;
import icy.util.XMLUtil;
import plugins.fmp.fmpSequence.SequencePlus;
import plugins.fmp.fmpTools.EnumImageOp;
import plugins.fmp.fmpTools.EnumAreaDetection;
import plugins.fmp.fmpTools.FmpTools;

public class AreatrackAnalysisParameters {
	
	final	String filenameAreatrackXml 	= "areatrack.xml";
	
	public  boolean detectArea				= true;
	public 	EnumAreaDetection areaDetectionMode	= EnumAreaDetection.COLORARRAY; 
	public 	EnumImageOp simpletransformop 	= EnumImageOp.R2MINUS_GB;
	public 	int simplethreshold 			= 20;
	public 	EnumImageOp colortransformop 	= EnumImageOp.NONE;
	public 	int colordistanceType 			= 0;
	public 	int colorthreshold 				= 20;
	public 	ArrayList <Color> colorarray 	= new ArrayList <Color>();
	
	public	boolean detectMovement			= false;
	public 	int thresholdmovement 			= 20;

	String ID_PARAMETERS  = "Parameters";
	String ID_COLORMODESELECTED = "colormodeselected";
	String ID_COLORTRANSFORMOP = "colortransformop";
	String ID_SIMPLETRANSFORMOP = "simpletransformop";
	String ID_THRESHOLDMOVEMENT = "thresholdmovement";
	String ID_COLORDISTANCETYPE = "colordistanceType";
	String ID_COLORTHRESHOLD = "colorthreshold";
	String ID_SIMPLETHRESHOLD = "simplethreshold";
	String ID_NBCOLORS = "ncolors";
	String ID_COLOR = "color";

	
public void xmlLoadAreaTrackParameters(SequencePlus vSequence) {
		
		String directory = vSequence.getDirectory();
		String fileparameters = directory + File.separator + filenameAreatrackXml;
		final Document doc = XMLUtil.loadDocument(fileparameters);
		boolean flag = false;
		if (doc != null) {
			flag = loadAreaTrackParameters(doc);
			if (!flag)
				new AnnounceFrame("reading data failed");
		}
	}
	
	public void xmlSaveAreaTrackParameters(SequencePlus vSequence) {
		
		String csFile = FmpTools.saveFileAs(filenameAreatrackXml, vSequence.getDirectory(), "xml");
		csFile.toLowerCase();
		if (!csFile.contains(".xml")) 
			csFile += ".xml";
		
		final Document doc = XMLUtil.createDocument(true);
		boolean flag = false;
		if (doc != null)
		{
			flag = saveAreaTrackParameters(doc);
			XMLUtil.saveDocument(doc, csFile);
		}
		if (!flag)
			new AnnounceFrame("saving data failed");
	}

	private boolean loadAreaTrackParameters (Document doc) {

		String nodeName = "areaTrack";
		// read local parameters
		Node node = XMLUtil.getElement(XMLUtil.getRootElement(doc), nodeName);
		if (node == null)
			return false;

		Element xmlElement = XMLUtil.getElement(node, ID_PARAMETERS);
		if (xmlElement == null) 
			return false;

		Element xmlVal = XMLUtil.getElement(xmlElement, ID_COLORMODESELECTED);
		boolean iscolorselected = XMLUtil.getAttributeBooleanValue(xmlVal, "value", true );
		if (iscolorselected)
			areaDetectionMode = EnumAreaDetection.COLORARRAY;
		else
			areaDetectionMode = EnumAreaDetection.SINGLE;
		
		xmlVal = XMLUtil.getElement(xmlElement, ID_SIMPLETRANSFORMOP);
		String codestring = XMLUtil.getAttributeValue(xmlVal, "descriptor", "none");
		simpletransformop = EnumImageOp.findByText(codestring);

		xmlVal = XMLUtil.getElement(xmlElement, ID_SIMPLETHRESHOLD);
		simplethreshold = XMLUtil.getAttributeIntValue(xmlVal, "value", 35);
		
		xmlVal = XMLUtil.getElement(xmlElement, ID_THRESHOLDMOVEMENT);
		thresholdmovement = XMLUtil.getAttributeIntValue(xmlVal, "value", 20);
		
		xmlVal = XMLUtil.getElement(xmlElement, ID_COLORTRANSFORMOP);	
		colortransformop = EnumImageOp.findByText(codestring);
		
		xmlVal = XMLUtil.getElement(xmlElement, ID_COLORDISTANCETYPE);
		colordistanceType = XMLUtil.getAttributeIntValue(xmlVal, "value", 0);
		
		xmlVal = XMLUtil.getElement(xmlElement, ID_COLORTHRESHOLD);
		colorthreshold = XMLUtil.getAttributeIntValue(xmlVal, "value", 20);
		
		colorarray.clear();
		xmlVal = XMLUtil.getElement(xmlElement, ID_NBCOLORS);
		int ncolors = XMLUtil.getAttributeIntValue(xmlVal, "value", 0);
		for (int i= 0; i<ncolors; i++) {
			xmlVal = XMLUtil.getElement(xmlElement, ID_COLOR+Integer.toString(i));
			int alpha = XMLUtil.getAttributeIntValue(xmlVal, "a", 0);
			int red = XMLUtil.getAttributeIntValue(xmlVal, "r", 0);
			int blue = XMLUtil.getAttributeIntValue(xmlVal, "b", 0);
			int green = XMLUtil.getAttributeIntValue(xmlVal, "g", 0);
			Color color = new Color(red, green, blue, alpha);
			colorarray.add(color);
		}
		return true;
	}
	
	private boolean saveAreaTrackParameters (Document doc) {

		// save local parameters
		String nodeName = "areaTrack";
		Node node = XMLUtil.addElement(XMLUtil.getRootElement(doc), nodeName);
		if (node == null)
			return false;
		
		Element xmlElement = XMLUtil.addElement(node, ID_PARAMETERS);
		
		Element xmlVal = XMLUtil.addElement(xmlElement, ID_COLORMODESELECTED);
		boolean iscolorselected = (areaDetectionMode == EnumAreaDetection.COLORARRAY);
		XMLUtil.setAttributeBooleanValue(xmlVal, "value", iscolorselected);
		
		xmlVal = XMLUtil.addElement(xmlElement, ID_SIMPLETRANSFORMOP);
		XMLUtil.setAttributeValue(xmlVal, "descriptor", simpletransformop.toString());
		
		xmlVal = XMLUtil.addElement(xmlElement, ID_SIMPLETHRESHOLD);
		XMLUtil.setAttributeIntValue(xmlVal, "value", simplethreshold);
		
		xmlVal = XMLUtil.addElement(xmlElement, ID_THRESHOLDMOVEMENT);
		XMLUtil.setAttributeIntValue(xmlVal, "value", thresholdmovement);
		
		xmlVal = XMLUtil.addElement(xmlElement, ID_COLORTRANSFORMOP);
		XMLUtil.setAttributeValue(xmlVal, "descriptor", colortransformop.toString());
	
		xmlVal = XMLUtil.addElement(xmlElement, ID_COLORDISTANCETYPE);
		XMLUtil.setAttributeIntValue(xmlVal, "value", colordistanceType);

		xmlVal = XMLUtil.addElement(xmlElement, ID_COLORTHRESHOLD);
		XMLUtil.setAttributeIntValue(xmlVal, "value", colorthreshold);
		
		xmlVal = XMLUtil.addElement(xmlElement, ID_NBCOLORS);
		XMLUtil.setAttributeIntValue(xmlVal, "value", colorarray.size());
		for (int i = 0; i < colorarray.size(); i++) {
			Color color = colorarray.get(i);
			xmlVal = XMLUtil.addElement(xmlElement, ID_COLOR+Integer.toString(i));
			XMLUtil.setAttributeIntValue(xmlVal, "a", color.getAlpha());
			XMLUtil.setAttributeIntValue(xmlVal, "r", color.getRed());
			XMLUtil.setAttributeIntValue(xmlVal, "g", color.getGreen());
			XMLUtil.setAttributeIntValue(xmlVal, "b", color.getBlue());
		}
		return true;
	}


}
