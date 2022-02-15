package plugins.fmp.fmpSequence;

import org.w3c.dom.Node;

import icy.file.xml.XMLPersistent;
import icy.roi.ROI2D;
import icy.util.XMLUtil;



public class Cage  implements XMLPersistent, Comparable <Cage>  {
	
	public ROI2D roi;
	public	XYTaSeries measures;
	public int [] rawArray = null;
	public double [] filteredArray = null;
	
	public int row = 0;
	public int column = 0;
	public String grid = "gridA";
	public int insectNumber = 1;
	public String insectDescriptor = "";
	public String stimulus = "";
	public String concentration = "";
	
	private final String 	ID_META 		= "CAGE";
	private final String	ID_INSECTNB		= "insectNumber";
	private final String	ID_INSECTDESC	= "insectDescriptor";
	
	private final String 	ID_ROW 			= "row";
	private final String 	ID_COLUMN 		= "column";
	private final String 	ID_GRID 		= "grid";
	private final String 	ID_STIMULUS 	= "stimulus";
	private final String 	ID_CONCENTRATION= "concentration";
	
	// -------------------------------------------------
	
	@Override
	public int compareTo(Cage o) {
		if (o != null)
			return this.roi.getName().compareTo(o.roi.getName());
		return 1;
	}
	@Override
	public boolean loadFromXML(Node node) {
		boolean result = loadFromXML_CageOnly(node);	
//		result |= loadFromXML_MeasuresOnly( node);
		return result;
	}
	@Override
	public boolean saveToXML(Node node) {
		saveToXML_CageOnly(node);
//		saveToXML_MeasuresOnly(node); 
        return true;
	}
	
	public boolean loadFromXML_CageOnly(Node node) {
		final Node nodeMeta = XMLUtil.getElement(node, ID_META);
	    boolean flag = (nodeMeta != null); 
	    if (flag) 
	    {
	    	row 			= XMLUtil.getElementIntValue(nodeMeta, ID_ROW, row);
	    	column 			= XMLUtil.getElementIntValue(nodeMeta, ID_COLUMN, column);
	    	grid 			= XMLUtil.getElementValue(nodeMeta, ID_GRID, grid);
	    	insectNumber 	= XMLUtil.getElementIntValue(nodeMeta, ID_INSECTNB, insectNumber);
	    	insectDescriptor = XMLUtil.getElementValue(nodeMeta, ID_INSECTDESC, insectDescriptor);
	    	stimulus 		= XMLUtil.getElementValue(nodeMeta, ID_STIMULUS, stimulus);
	        concentration	= XMLUtil.getElementValue(nodeMeta, ID_CONCENTRATION, concentration);
	     
	    	roi = ROI2DUtilities.loadFromXML_ROI(nodeMeta);
	    }
		return flag;
	}
	
	public boolean saveToXML_CageOnly(Node node) 
	{
	    final Node nodeMeta = XMLUtil.setElement(node, ID_META);
	    if (nodeMeta == null)
	    	return false;
		
    	XMLUtil.setElementIntValue(nodeMeta, ID_ROW, row);
    	XMLUtil.setElementIntValue(nodeMeta, ID_COLUMN, column);
    	XMLUtil.setElementValue(nodeMeta, ID_GRID, grid);
    	XMLUtil.setElementIntValue(nodeMeta, ID_INSECTNB, insectNumber);
    	XMLUtil.setElementValue(nodeMeta, ID_INSECTDESC, insectDescriptor);
    	XMLUtil.setElementValue(nodeMeta, ID_STIMULUS, stimulus);
        XMLUtil.setElementValue(nodeMeta, ID_CONCENTRATION, concentration);
     
    	ROI2DUtilities.saveToXML_ROI(nodeMeta, roi);

	    return true;
	}
	
}
