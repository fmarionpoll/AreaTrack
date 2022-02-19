package plugins.fmp.fmpSequence;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;


import icy.file.Loader;
import icy.file.SequenceFileImporter;
import icy.gui.dialog.LoaderDialog;
import icy.gui.dialog.MessageDialog;
import icy.gui.frame.progress.ProgressFrame;
import icy.gui.viewer.Viewer;
import icy.image.IcyBufferedImage;
import icy.image.ImageUtil;
import icy.main.Icy;
import icy.sequence.Sequence;
import icy.system.thread.ThreadUtil;
import plugins.fmp.fmpTools.StringSorter;
import plugins.stef.importer.xuggler.VideoImporter;



public class OpenVirtualSequence {
	
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
	
	public static Sequence openImagesOrAvi(String path) 
	{
		LoaderDialog dialog = new LoaderDialog(false);
		if (path != null) 
			dialog.setCurrentDirectory(new File(path));
	    File[] selectedFiles = dialog.getSelectedFiles();
	    if (selectedFiles.length == 0)
	    	return null;
	    
	    String directory;
	    if (selectedFiles[0].isDirectory())
	    	directory = selectedFiles[0].getAbsolutePath();
	    else
	    	directory = selectedFiles[0].getParentFile().getAbsolutePath();
		if (directory == null )
			return null;

		Sequence seq = null;
		String [] list;
		if (selectedFiles.length == 1) 
		{
			list = (new File(directory)).list();
			if (list ==null)
				return null;
			
			if (!selectedFiles[0].isDirectory())  
			{
				if (selectedFiles[0].getName().toLowerCase().contains(".avi"))
					seq = loadSequenceAVI(selectedFiles[0].getAbsolutePath());
				else
				{
					String[] imagesArray = getAcceptedNamesFromImagesList(list, directory);
					List <String> imagesList = Arrays.asList(imagesArray);
					seq = loadV2SequenceFromImagesList(imagesList);
				}
			}
		}
		else
		{
			list = new String[selectedFiles.length];
			  for (int i = 0; i < selectedFiles.length; i++) {
				if (selectedFiles[i].getName().toLowerCase().contains(".avi"))
					continue;
			    list[i] = selectedFiles[i].getAbsolutePath();
			}
			String[] imagesArray = getAcceptedNamesFromImagesList(list, directory);
			List <String> imagesList = Arrays.asList(imagesArray);
			seq = loadV2SequenceFromImagesList(imagesList);
		}
		return seq;
	}
	
	private static Sequence loadSequenceAVI(String fileName) 
	{
		Sequence sequenceVirtual = null;
		if (importer != null )
		{
			try 
			{
				importer.close();
			} catch (IOException e) 
			{
				e.printStackTrace();
			}
		}
		
		try
		{
			importer = new VideoImporter();
			statusSequence = EnumStatus.AVIFILE;
			importer.open( fileName, 0 );
//			OMEXMLMetadata metaData = importer.getOMEXMLMetaData();
//			nTotalFrames = MetaDataUtil.getSizeT( metaData, 0 ) - 2 ; 
			// get one frame less as there is a little bug in the decompression of the video in h264
		}
		catch (Exception exc)
		{
			MessageDialog.showDialog( "File type or video-codec not supported.", MessageDialog.ERROR_MESSAGE );
			statusSequence = EnumStatus.FAILURE;
		}
		return sequenceVirtual;
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
		// -----------------------------------------------
		// subroutines borrowed from FolderOpener
		/* Keep only "accepted" names (file extension)*/
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

	public static Sequence loadV1SequenceFromImagesList(List <String> imagesList) 
	{
		SequenceFileImporter seqFileImporter = Loader.getSequenceFileImporter(imagesList.get(0), true);
		Sequence seq = Loader.loadSequence(seqFileImporter, imagesList, false);
		return seq;
	}
	
	public static Sequence loadV2SequenceFromImagesList(List <String> imagesList) 
	{
		  SequenceFileImporter seqFileImporter = Loader.getSequenceFileImporter(imagesList.get(0), true);
		  Sequence seq = Loader.loadSequence(seqFileImporter, imagesList.get(0), 0, false);
		  ThreadUtil.bgRun( new Runnable() { 
			@Override public void run() 
			{
				ProgressFrame progress = new ProgressFrame("Loading images...");
				seq.setVolatile(true);
				seq.beginUpdate();
				try
				{
					final int nbframes = imagesList.size();
					for (int t = 1; t < nbframes; t++)
					{
						int pos = (int)(100d * (double)t / (double) nbframes);
						progress.setPosition( pos );
						
						BufferedImage img = ImageUtil.load(imagesList.get(t));
						progress.setMessage( "Processing image: " + pos + "/" + nbframes);
							
						if (img != null)
						{
							IcyBufferedImage icyImg = IcyBufferedImage.createFrom(img);
							icyImg.setVolatile(true);
							seq.setImage(t, 0, icyImg);
						}
					}
				}
				finally
				{
					seq.endUpdate();
					progress.close();
				}
			}});	
		return seq;
	 }
	
}
