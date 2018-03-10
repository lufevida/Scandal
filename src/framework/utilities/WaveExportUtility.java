package framework.utilities;

import java.io.File;

import framework.effects.Loop;
import framework.effects.Speed;
import framework.effects.Splice;
import framework.generators.AudioTask;
import framework.generators.WaveFile;

public class WaveExportUtility {
	
	private static final String folderPath = "/Users/luisfelipe/Downloads";
	private static final String exportPath = "/Users/luisfelipe/Downloads/bounce/";
	private static final File folder = new File(folderPath);
	private static File[] fileList = folder.listFiles();
	private static WaveFile file;
	private static String filePath;
	private static String fileName;

	public static void main(String[] args) throws Exception {
		//generateAdjacentNotes(-2);
		//generateAdjacentNotes(-1);
		//generateAdjacentNotes(1);
		//generateAdjacentNotes(2);
		//rename();
		normalizeAndExport();
	}
	
	static void normalizeAndExport() throws Exception {
		AudioTask task = new AudioTask();
		for (int i = 0; i < fileList.length; i++) {
			if (fileList[i].isFile() && fileList[i].getName().endsWith("wav")) {
				filePath = folderPath + "/" + fileList[i].getName();
				file = new WaveFile(filePath);
				float[] samples = file.getMonoSum();
				samples = new Loop().process(samples, 0 * 44100, 4 * 44100, 1);
				Splice s = new Splice();
				//samples = Functions.normalize(samples);
				samples = s.fadeIn(samples, 2205);
				samples = s.fadeOut(samples, 2205);
				task.export(samples, exportPath + fileList[i].getName(), 1);
			}
		}
	}
	
	static void exportAsText() throws Exception {
		for (int i = 0; i < fileList.length; i++) {
			if (fileList[i].isFile() && fileList[i].getName().endsWith("wav")) {
				filePath = folderPath + "/" + fileList[i].getName();
				file = new WaveFile(filePath);
				filePath = filePath.substring(0, filePath.lastIndexOf('.'));
				fileName = fileList[i].getName().substring(0, fileList[i].getName().lastIndexOf('.'));
				fileName = nameToNumber(fileName, 0);
				filePath = folderPath + "/" + fileName;
				file.exportText(filePath + ".hpp", fileName, 88200);
				System.out.println(fileName);
			}
		}
	}
	
	static void generateAdjacentNotes(int correction) throws Exception {		
		AudioTask task = new AudioTask();
		float[] speed;
		for (int i = 0; i < fileList.length; i++) {
			if (fileList[i].isFile() && fileList[i].getName().endsWith("wav")) {
				file = new WaveFile(folderPath + "/" + fileList[i].getName());
				speed = new Speed().process(file.getMonoSum(), (float) Math.pow(2, correction / 12.0));
				fileName = fileList[i].getName().substring(0, fileList[i].getName().lastIndexOf('.'));
				fileName = nameToNumber(fileName, correction) + ".wav";
				task.export(speed, folderPath + "/" + fileName, 1);
			}
		}
	}
	
	static void rename() {
		for (int i = 0; i < fileList.length; i++) {
			if (fileList[i].isFile() && fileList[i].getName().endsWith("wav")) {
				fileName = fileList[i].getName().substring(0, fileList[i].getName().lastIndexOf('.'));
				fileName = nameToNumber(fileName, 0) + ".wav";
				fileList[i].renameTo(new File(folderPath + fileName));
			}
		}
	}
	
	private static String nameToNumber(String name, int correction) {
		for (int i = 0; i < 10; i++) {
			if (name.contains("C" + i)) return name.replace("C" + i, 12 * (i + 1) + correction + "");
			if (name.contains("C#" + i)) return name.replace("C#" + i, 12 * (i + 1) + 1 + correction + "");
			if (name.contains("Db" + i)) return name.replace("Db" + i, 12 * (i + 1) + 1 + correction + "");
			if (name.contains("D" + i)) return name.replace("D" + i, 12 * (i + 1) + 2 + correction + "");
			if (name.contains("D#" + i)) return name.replace("D#" + i, 12 * (i + 1) + 3 + correction + "");
			if (name.contains("Eb" + i)) return name.replace("Eb" + i, 12 * (i + 1) + 3 + correction + "");
			if (name.contains("E" + i)) return name.replace("E" + i, 12 * (i + 1) + 4 + correction + "");
			if (name.contains("F" + i)) return name.replace("F" + i, 12 * (i + 1) + 5 + correction + "");
			if (name.contains("F#" + i)) return name.replace("F#" + i, 12 * (i + 1) + 6 + correction + "");
			if (name.contains("Gb" + i)) return name.replace("Gb" + i, 12 * (i + 1) + 6 + correction + "");
			if (name.contains("G" + i)) return name.replace("G" + i, 12 * (i + 1) + 7 + correction + "");
			if (name.contains("G#" + i)) return name.replace("G#" + i, 12 * (i + 1) + 8 + correction + "");
			if (name.contains("Ab" + i)) return name.replace("Ab" + i, 12 * (i + 1) + 8 + correction + "");
			if (name.contains("A" + i)) return name.replace("A" + i, 12 * (i + 1) + 9 + correction + "");
			if (name.contains("A#" + i)) return name.replace("A#" + i, 12 * (i + 1) + 10 + correction + "");
			if (name.contains("Bb" + i)) return name.replace("Bb" + i, 12 * (i + 1) + 10 + correction + "");
			if (name.contains("B" + i)) return name.replace("B" + i, 12 * (i + 1) + 11 + correction + "");
		}
		return name;
	}

}
