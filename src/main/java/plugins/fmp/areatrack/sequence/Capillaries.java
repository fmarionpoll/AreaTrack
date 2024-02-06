package plugins.fmp.areatrack.sequence;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import icy.roi.ROI;
import icy.roi.ROI2D;
import icy.sequence.edit.ROIAddsSequenceEdit;
import icy.util.XMLUtil;
import plugins.fmp.areatrack.tools.FmpTools;
import plugins.kernel.roi.roi2d.ROI2DLine;
import plugins.kernel.roi.roi2d.ROI2DPolyLine;
import plugins.kernel.roi.roi2d.ROI2DShape;

public class Capillaries {
	
	public String 	sourceName 			= null;
	public ArrayList <ROI2DShape> capillariesArrayList 	= new ArrayList <ROI2DShape>();	
	public long 	analysisStart 		= 0;
	public long 	analysisEnd 		= 0;
	public int 		analysisStep 		= 1;
	
	
	public boolean xmlReadCapillaryParameters (Document doc) {
		String nodeName = "capillaryTrack";
		// read local parameters
		Node node = XMLUtil.getElement(XMLUtil.getRootElement(doc), nodeName);
		if (node == null)
			return false;

		Element xmlElement = XMLUtil.getElement(node, "Parameters");
		if (xmlElement == null) 
			return false;

		Element xmlVal = XMLUtil.getElement(xmlElement, "file");
		sourceName = XMLUtil.getAttributeValue(xmlVal, "ID", null);
		
		xmlVal = XMLUtil.getElement(xmlElement, "analysis");
		if (xmlVal != null) {
			analysisStart 	= XMLUtil.getAttributeLongValue(xmlVal, "start", 0);
			analysisEnd 	= XMLUtil.getAttributeLongValue(xmlVal, "end", 0);
			analysisStep 	= XMLUtil.getAttributeIntValue(xmlVal, "step", 1);
		}

		return true;
	}
	
	private boolean xmlWriteCapillaryParameters (Document doc, SequenceVirtual sequenceVirtual) {
		String nodeName = "capillaryTrack";
		Node node = XMLUtil.addElement(XMLUtil.getRootElement(doc), nodeName);
		if (node == null)
			return false;
		
		Element xmlElement = XMLUtil.addElement(node, "Parameters");
		
		Element xmlVal = XMLUtil.addElement(xmlElement, "file");
		sourceName = sequenceVirtual.getFileName(0);
		XMLUtil.setAttributeValue(xmlVal, "ID", sourceName);
		
		xmlVal = XMLUtil.addElement(xmlElement, "analysis");
		XMLUtil.setAttributeLongValue(xmlVal, "start", sequenceVirtual.analysisStart);
		XMLUtil.setAttributeLongValue(xmlVal, "end", sequenceVirtual.analysisEnd); 
		XMLUtil.setAttributeIntValue(xmlVal, "step", sequenceVirtual.analysisStep); 
		
		return true;
	}
	
	public void extractLinesFromSequence(SequenceVirtual sequenceVirtual) {

		capillariesArrayList.clear();
		ArrayList<ROI2D> list = sequenceVirtual.seq.getROI2Ds();
		 
		for (ROI2D roi:list)
		{
			if ((roi instanceof ROI2DShape) == false)
				continue;
			if (!roi.getName().contains("line"))
				continue;
			if (roi instanceof ROI2DLine || roi instanceof ROI2DPolyLine)
				capillariesArrayList.add((ROI2DShape)roi);
		}
		Collections.sort(capillariesArrayList, new FmpTools.ROI2DNameComparator()); 
	}
	
	public void extractROIsWithPattern(SequenceVirtual sequenceVirtual, String pattern) {

		capillariesArrayList.clear();
		ArrayList<ROI2D> list = sequenceVirtual.seq.getROI2Ds();
		 
		for (ROI2D roi:list)
		{
			if ((roi instanceof ROI2DShape) == false)
				continue;
			if (!roi.getName().contains(pattern))
				continue;
			if (roi instanceof ROI2DLine || roi instanceof ROI2DPolyLine)
				capillariesArrayList.add((ROI2DShape)roi);
		}
		Collections.sort(capillariesArrayList, new FmpTools.ROI2DNameComparator()); 
	}
	
	public boolean xmlWriteROIsAndData(String name, SequenceVirtual seq) {

		String csFile = FmpTools.saveFileAs(name, seq.getDirectory(), "xml");
		csFile.toLowerCase();
		if (!csFile.contains(".xml")) {
			csFile += ".xml";
		}
		return xmlWriteROIsAndDataNoQuestion(csFile, seq);
	}
	
	public boolean xmlWriteROIsAndDataNoQuestion(String csFile, SequenceVirtual seq) {

		if (csFile != null) 
		{
			extractLinesFromSequence(seq);
			if (capillariesArrayList.size() > 0)
			{
				final Document doc = XMLUtil.createDocument(true);
				if (doc != null)
				{
					List<ROI> roisList = new ArrayList<ROI>();
					for (ROI roi: capillariesArrayList)
						roisList.add(roi);
					ROI.saveROIsToXML(XMLUtil.getRootElement(doc), roisList);
					xmlWriteCapillaryParameters (doc, seq);
					XMLUtil.saveDocument(doc, csFile);
					return true;
				}
			}
		}
		return false;
	}
	
	public boolean xmlWriteROIsAndDataNoFilter(String name, SequenceVirtual sequenceVirtual) {

		String csFile = FmpTools.saveFileAs(name, sequenceVirtual.getDirectory(), "xml");
		csFile.toLowerCase();
		if (!csFile.contains(".xml")) {
			csFile += ".xml";
		}
		
		final Document doc = XMLUtil.createDocument(true);
		if (doc != null)
		{
			List<ROI> roisList = sequenceVirtual.seq.getROIs();
			ROI.saveROIsToXML(XMLUtil.getRootElement(doc), roisList);
			xmlWriteCapillaryParameters (doc, sequenceVirtual);
			XMLUtil.saveDocument(doc, csFile);
			return true;
		}
		
		return false;
	}
	
	public boolean xmlReadROIsAndData(SequenceVirtual sequenceVirtual) {

		String directory = sequenceVirtual.getDirectory();
		String [] filedummy = FmpTools.selectFiles(directory, "xml");
		boolean wasOk = false;
		if (filedummy != null) {
			for (int i= 0; i< filedummy.length; i++) {
				String csFile = filedummy[i];
				wasOk &= xmlReadROIsAndData(csFile, sequenceVirtual);
			}
		}
		return wasOk;
	}
	
	public boolean xmlReadROIsAndData(String csFileName, SequenceVirtual sequenceVirtual) {
		
		if (csFileName != null)  {
			final Document doc = XMLUtil.loadDocument(csFileName);
			if (doc != null) {
				xmlReadCapillaryParameters(doc);
				List<ROI> listOfROIs = ROI.loadROIsFromXML(XMLUtil.getRootElement(doc));
				capillariesArrayList.clear();
				for (ROI roi: listOfROIs)
					capillariesArrayList.add((ROI2DShape) roi);
				Collections.sort(capillariesArrayList, new FmpTools.ROINameComparator()); 
				try  {  
					for (ROI roi : capillariesArrayList)  {
						sequenceVirtual.seq.addROI(roi);
					}
				}
				finally {
				}
				// add to undo manager
				sequenceVirtual.seq.addUndoableEdit(new ROIAddsSequenceEdit(sequenceVirtual.seq, listOfROIs) {
					@Override
					public String getPresentationName() {
						return getROIs().size() + " ROI(s) loaded from XML file"; };
				});
				return true;
			}
		}
		return false;
	}

	public Capillaries copy (Capillaries cap) {
		analysisStart = cap.analysisStart;
		analysisEnd = cap.analysisEnd;
		analysisStep = cap.analysisStep;
		return cap;
	}
	
	public boolean isChanged (Capillaries cap) {
		boolean flag = false; 
		flag = (cap.analysisStart != analysisStart) || flag;
		flag = (cap.analysisEnd != analysisEnd) || flag;
		flag = (cap.analysisStep != analysisStep) || flag;
		return flag;
	}

}
