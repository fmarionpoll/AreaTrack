package plugins.fmp.fmpSequence;


import java.util.Arrays;
import java.util.List;

import icy.file.Loader;
import icy.file.SequenceFileImporter;
import icy.gui.viewer.Viewer;
import icy.main.Icy;

import icy.sequence.Sequence;
import plugins.fmp.fmpTools.StringSorter;
import plugins.stef.importer.xuggler.VideoImporter;




public class SequencePlusOpen 
{	
	public static EnumStatus statusSequence = EnumStatus.REGULAR;
	protected static VideoImporter importer = null;
	private static final String[] acceptedTypes = {".jpg", ".jpeg", ".bmp", "tiff"};

	
	public static Viewer initSequenceViewer(Sequence seq) 
	{
		if (seq == null)
			return null;
		
		Viewer v = seq.getFirstViewer();
		if (v != null) 
			v.close();
		
		Icy.getMainInterface().addSequence(seq);
		v = seq.getFirstViewer();
		return v;
	}
	
	public static SequencePlus openImagesOrAvi(String path) 
	{
//		LoaderDialog dialog = new LoaderDialog(false);
//		if (path != null) 
//			dialog.setCurrentDirectory(new File(path));
//	    File[] selectedFiles = dialog.getSelectedFiles();
//	    if (selectedFiles.length == 0)
//	    	return null;
//	    
//	    String directory;
//	    if (selectedFiles[0].isDirectory())
//	    	directory = selectedFiles[0].getAbsolutePath();
//	    else
//	    	directory = selectedFiles[0].getParentFile().getAbsolutePath();
//		if (directory == null )
//			return null;
//
//		Sequence seq = null;
//		int nTotalFrames = 1;
//		String [] list;
//		if (selectedFiles.length == 1) 
//		{
//			list = (new File(directory)).list();
//			if (list ==null)
//				return null;
//			
//			if (!selectedFiles[0].isDirectory())  
//			{
//				if (selectedFiles[0].getName().toLowerCase().contains(".avi")) 
//				{
//					seq = loadSequenceAVI(selectedFiles[0].getAbsolutePath());
//					SequencePlus sequencePlus = new SequencePlus(seq);
//					sequencePlus.nTotalFrames = nTotalFrames;
//					return sequencePlus;
//				}
//				else
//				{
//					SequencePlus seqPlus = loadSequencePlusFromList(list, directory) ;
//					return seqPlus;
//				}
//			}
//		}
//		else
//		{
//			list = new String[selectedFiles.length];
//			  for (int i = 0; i < selectedFiles.length; i++) {
//				if (selectedFiles[i].getName().toLowerCase().contains(".avi"))
//					continue;
//			    list[i] = selectedFiles[i].getAbsolutePath();
//			}
//			SequencePlus seqPlus = loadSequencePlusFromList(list, directory) ;
//			return seqPlus;
//		}
//		
//		return null;
		
		List<String> cameraImagesList = ExperimentDirectories.getV2ImagesListFromDialog(path);
		String cameraImagesDirectory = Directories.getDirectoryFromName(cameraImagesList.get(0));
		
		String strImagesDirectory = ExperimentDirectories.getImagesDirectoryAsParentFromFileName(cameraImagesDirectory);			
		List<String> imagesList = ExperimentDirectories.getV2ImagesListFromPath(strImagesDirectory);
		imagesList = ExperimentDirectories.keepOnlyAcceptedNames_List(imagesList, "jpg");
		SequencePlus seqPlus = null;
		if (imagesList.size() > 0) 
		{
			seqPlus = new SequencePlus();
			seqPlus.setV2ImagesList(imagesList);
			seqPlus.attachSequence(loadSequenceFromImagesList_V2(imagesList));
		}
		return seqPlus;
		
	}
	
	private static String[] getAcceptedNamesFromImagesList(String[] list, String directory) 
	{
		statusSequence = EnumStatus.FAILURE;
		String[] imagesList = keepOnlyAcceptedNames(list);
		if (list==null) 
			return null;

		int j = 0;
		for (int i = 0; i < list.length; i++) 
		{
			if (list[i]!= null)
				imagesList [j++] = directory + '/'+ list[i];
		}
		imagesList = StringSorter.sortNumerically(imagesList);
		statusSequence = EnumStatus.FILESTACK;
		return imagesList;
	}
	
	private static String[] keepOnlyAcceptedNames(String[] rawlist) 
	{
		int count = 0;
		for (int i = 0; i < rawlist.length; i++) {
			String name = rawlist[i];
			if ( !acceptedFileType(name) )
				rawlist[i] = null;
			else
				count++;
		}
		if (count==0) return null;

		String[] list = rawlist;
		if (count<rawlist.length) {
			list = new String[count];
			int index = 0;
			for (int i = 0; i < rawlist.length; i++) {
				if (rawlist[i]!=null)
					list[index++] = rawlist[i];
			}
		}
		return list;
	}

	private static boolean acceptedFileType(String name) 
	{
		if (name == null) 
			return false;
		for (int i = 0; i < acceptedTypes.length; i++) 
		{
			if (name.endsWith(acceptedTypes[i]))
				return true;
		}
		return false;
	}	

	static SequencePlus loadSequencePlusFromList(String [] list, String directory) 
	{
		String[] imagesArray = getAcceptedNamesFromImagesList(list, directory);
		List <String> imagesList = Arrays.asList(imagesArray);
		int nTotalFrames = imagesList.size();
		Sequence seq = loadSequenceFromImagesList_V2(imagesList);
		SequencePlus sequencePlus = new SequencePlus(seq);
		sequencePlus.nTotalFrames = nTotalFrames;
		return sequencePlus;
	}
	
	public static Sequence loadSequenceFromImagesList_V2(List <String> imagesList) 
	{
		SequenceFileImporter seqFileImporter = Loader.getSequenceFileImporter(imagesList.get(0), true);
		Sequence seq = Loader.loadSequences(seqFileImporter, imagesList, 
				0,          // series index to load
			    true, 		// force volatile 
			    false,      // separate       
			    false,      // auto-order
			    false,      // directory
			    false,      // add to recent
			    false // show progress
			).get(0);
		return seq;
	 }
	

}
