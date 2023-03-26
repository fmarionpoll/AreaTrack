package plugins.fmp.fmpSequence;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import icy.file.FileUtil;
import icy.gui.dialog.LoaderDialog;


public class ExperimentDirectories {

		public String cameraImagesDirectory = null;
		public List<String> cameraImagesList = null;
		
		public String resultsDirectory = null;
		public String binSubDirectory = null;
		public List<String> kymosImagesList = null;
		
		  
		
		
		public static List<String> keepOnlyAcceptedNames_List(List<String> namesList, String strExtension) 
		{
			int count = namesList.size();
			List<String> outList = new ArrayList<String> (count);
			String ext = strExtension.toLowerCase();
			for (String name: namesList) 
			{
				String nameGeneric = FileUtil.getGenericPath(name);
				if (nameGeneric.toLowerCase().endsWith(ext))
					outList.add(nameGeneric);
			}
			return outList;
		}
		
		public static List<String> getV2ImagesListFromPath(String strDirectory) 
		{
			List<String> list = new ArrayList<String> ();
			Path pathDir = Paths.get(strDirectory);
			if (Files.exists(pathDir)) 
			{
				try (DirectoryStream<Path> stream = Files.newDirectoryStream(pathDir)) 
				{
					for (Path entry: stream) 
					{
						String toAdd = FileUtil.getGenericPath(entry.toString());
						list.add(toAdd);
					}
				} 
				catch (IOException e) 
				{
					e.printStackTrace();
				}
			}
			return list;
		}
		
		public static List<String> getV2ImagesListFromDialog(String strPath) 
		{
			List<String> list = new ArrayList<String> ();
			LoaderDialog dialog = new LoaderDialog(false);
			if (strPath != null) 
				dialog.setCurrentDirectory(new File(strPath));
		    File[] selectedFiles = dialog.getSelectedFiles();
		    if (selectedFiles.length == 0)
		    	return null;
		    
		    // TODO check strPath and provide a way to skip the dialog part (or different routine)
		    String strDirectory = Directories.getDirectoryFromName(selectedFiles[0].toString());
			if (strDirectory != null ) 
			{
				if (selectedFiles.length == 1) 
					list = getV2ImagesListFromPath(strDirectory);
			}
			return list;
		}
		
		public boolean checkCameraImagesList() 
		{
			boolean isOK = false;
			if (!(cameraImagesList == null)) 
			{
				boolean imageFound = false;
				String jpg = "jpg";
				String grabs = "grabs";
				String grabsDirectory = null;
				for (String name: cameraImagesList) 
				{
					if (name.toLowerCase().endsWith(jpg)) 
					{
						imageFound = true;
						break;
					}
					if (name.toLowerCase().endsWith(grabs))
						grabsDirectory = name;
				}
				if (imageFound) 
				{
					cameraImagesList = keepOnlyAcceptedNames_List(cameraImagesList, "jpg");
					isOK = true;
				}
				else if (grabsDirectory != null)
				{
					cameraImagesList = getV2ImagesListFromPath(grabsDirectory);
					isOK = checkCameraImagesList();
				}
			}
			return isOK;
		}

		
		static public String getParentIf(String filename, String filter) 
		{
			if (filename .contains(filter)) 
				filename = Paths.get(filename).getParent().toString();
			return filename;
		}
		
		static public String getImagesDirectoryAsParentFromFileName(String filename) 
		{
			String 	RESULTS				= "results";
			String 	BIN					= "bin_";
			
			filename = getParentIf(filename, BIN);
			filename = getParentIf(filename, RESULTS);
			return filename;
		}

		

}
