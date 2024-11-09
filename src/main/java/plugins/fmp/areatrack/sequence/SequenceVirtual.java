package plugins.fmp.areatrack.sequence;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import icy.image.IcyBufferedImage;
import icy.sequence.Sequence;
import plugins.fmp.areatrack.tools.ImageOperationsStruct;

public class SequenceVirtual {
	public Sequence seq = null;
	private List<String> imagesList = null;
	private String csFileName = null;
	private String directory = null;
	protected String csCamFileName = null;
	public IcyBufferedImage refImage = null;

	public int analysisStart = 0;
	public int analysisEnd = 99999999;
	public int analysisStep = 1;
	public int currentFrame = 0;
	public int nTotalFrames = 0;

	public EnumStatus statusSequenceVirtual = EnumStatus.REGULAR;

	public Capillaries capillariesRoi2RoiArray = new Capillaries();

	public String[] seriesname = null;
	public int[][] data_raw = null;
	public double[][] data_filtered = null;

	// image cache
	public IcyBufferedImage cacheTransformedImage = null;
	public ImageOperationsStruct cacheTransformOp = new ImageOperationsStruct();
	public IcyBufferedImage cacheThresholdedImage = null;
	public ImageOperationsStruct cacheThresholdOp = new ImageOperationsStruct();

	// ----------------------------------------

	public SequenceVirtual() {
		seq = new Sequence();
		statusSequenceVirtual = EnumStatus.REGULAR;
	}

	public SequenceVirtual(Sequence seq) {
		this.seq = seq;
		statusSequenceVirtual = EnumStatus.REGULAR;
		nTotalFrames = seq.getSizeT();
		analysisEnd = nTotalFrames - 1;
		String filename = seq.getFilename();
		Path path = Paths.get(filename);
		Path parent = path.getParent();
		directory = parent.toString();
	}

	public SequenceVirtual(String name, IcyBufferedImage image) {
		seq = new Sequence(name, image);
		statusSequenceVirtual = EnumStatus.FILESTACK;
	}

	public SequenceVirtual(List<String> listNames) {
		setV2ImagesList(listNames);
		statusSequenceVirtual = EnumStatus.FILESTACK;
	}

	public void setV2ImagesList(List<String> extImagesList) {
		if (imagesList != null)
			imagesList.clear();
		else
			imagesList = new ArrayList<String>(extImagesList.size());
		imagesList.addAll(extImagesList);
		nTotalFrames = imagesList.size();
		statusSequenceVirtual = EnumStatus.FILESTACK;
	}

	public void close() {
		seq.close();
	}

	public String getDirectory() {
		if (directory == null)
			return getDirectoryFromSequence();
		return directory;
	}

	public String getDirectoryFromSequence() {
		if (seq == null)
			return null;

		String filename = seq.getFilename();
		Path path = Paths.get(filename);
		Path parent = path.getParent();
		directory = parent.toString();
		return directory;
	}

	public IcyBufferedImage getSeqImage(int t, int z) {
		currentFrame = t;
		return seq.getImage(t, z);
	}

	public List<String> getListofFiles() {
		return imagesList;
	}

	/*
	 * getSizeT (non-Javadoc)
	 * 
	 * @see icy.sequence.Sequence#getSizeT() getSizeT is used to evaluate if
	 * volumetric images are stored in the sequence SequenceVirtual does not support
	 * volumetric images
	 */

	public int getSizeT() {
		if (statusSequenceVirtual == EnumStatus.REGULAR || statusSequenceVirtual == EnumStatus.FILESTACK)
			return seq.getSizeT();
		else
			return (int) nTotalFrames;
	}

	public int getT() {
		return currentFrame;
	}

	public String getFileName(int t) {
		String csName = null;
		if (statusSequenceVirtual == EnumStatus.FILESTACK)
			csName = imagesList.get(t);
		else if (statusSequenceVirtual == EnumStatus.AVIFILE)
			csName = csFileName;
		return csName;
	}

	public boolean isFileStack() {
		return (statusSequenceVirtual == EnumStatus.FILESTACK);
	}

	public void setSequenceImage(int t, int z, BufferedImage bimage) throws IllegalArgumentException {
		seq.setImage(t, z, bimage);
		currentFrame = t;
	}

	public void attachSequence(Sequence seq) {
		this.seq = seq;
		statusSequenceVirtual = EnumStatus.FILESTACK;
		analysisStart = 0;
	}

	// --------------------------------------------------------------------

	public IcyBufferedImage imageIORead(String name) {
		BufferedImage image = null;
		try {
			image = ImageIO.read(new File(name));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return IcyBufferedImage.createFrom(image);
	}
}