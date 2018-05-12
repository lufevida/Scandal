package framework.generators;

import java.io.PrintWriter;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

public class WaveFile {
	
	public final Path path;
	public final int numberOfChannels;
	public final int samplingRate;
	public final int bitDepth;
	private int dataStartIndex;
	private final int dataLength;
	private final float[] interleavedBuffer;
	
	public WaveFile(String name) throws Exception {
		this.path = FileSystems.getDefault().getPath(name);
		byte[] byteArray = Files.readAllBytes(path);
		this.numberOfChannels = (byteArray[22] & 0xff) | byteArray[23] << 8;
		if (numberOfChannels > 2) throw new Exception("Only stereo and mono files are supported.");
		this.samplingRate =
				(byteArray[24] & 0xff) |
				(byteArray[25] & 0xff) << 8 |
				(byteArray[26] & 0xff) << 16 |
				byteArray[27] << 24;
		this.bitDepth = (byteArray[34] & 0xff) | byteArray[35] << 8;
		if (bitDepth != 16 && bitDepth != 24) throw new Exception("Only 16 and 24-bit files are supported.");
		for (int i = 0; i < 100; i++) {
			boolean d = (char) byteArray[i] == 'd';
			boolean a1 = (char) byteArray[i + 1] == 'a';
			boolean t = (char) byteArray[i + 2] == 't';
			boolean a2 = (char) byteArray[i + 3] == 'a';
			if (d && a1 && t && a2) this.dataStartIndex = i + 8;	
		}
		if (dataStartIndex == 0) throw new Exception("Invalid wave file.");
		this.dataLength =
				(byteArray[dataStartIndex - 4] & 0xff) |
				(byteArray[dataStartIndex - 3] & 0xff) << 8 |
				(byteArray[dataStartIndex - 2] & 0xff) << 16 |
				byteArray[dataStartIndex - 1] << 24;
		// fill interleaved buffer
		interleavedBuffer = new float[dataLength / 2];
		if (bitDepth == 16) {
			for (int i = 0, j = dataStartIndex; j < dataStartIndex + dataLength; i++, j += 2) {
				// This is little endian.
				interleavedBuffer[i] = (byteArray[j] & 0xff) | byteArray[j + 1] << 8;
				interleavedBuffer[i] /= Short.MAX_VALUE;
			}
		}
		if (bitDepth == 24) {
			for (int i = 0, j = dataStartIndex; j < dataStartIndex + dataLength; i++, j += 3) {
				interleavedBuffer[i] = (byteArray[j] & 0xff) | (byteArray[j + 1] & 0xff) << 8 | byteArray[j + 2] << 16;
				interleavedBuffer[i] /= 0x7FFFFF;
			}
		}
	}
	
	public float[] getLeftChannel() {
		if (numberOfChannels == 1) return interleavedBuffer;
		float[] left = new float[interleavedBuffer.length / 2];
		for (int i = 0, j = 0; i < interleavedBuffer.length; i += 2, j++) {
			left[j] = interleavedBuffer[i];
		}
		return left;
	}
	
	public float[] getRightChannel() {
		if (numberOfChannels == 1) return interleavedBuffer;
		float[] right = new float[interleavedBuffer.length / 2];
		for (int i = 1, j = 0; i < interleavedBuffer.length - 1; i += 2, j++) {
			right[j] = interleavedBuffer[i];
		}
		return right;
	}
	
	public float[] getMonoSum() {
		if (numberOfChannels == 1) return interleavedBuffer;
		float[] sum = new float[interleavedBuffer.length / 2];
		for (int i = 1, j = 0; i < interleavedBuffer.length - 1; i += 2, j++) {
			sum[j] = (interleavedBuffer[i] + interleavedBuffer[i - 1]) * 0.5f;
		}
		return sum;
	}
	
	public float[] getNormalized() {
		float max = 0;
		float[] normal = new float[interleavedBuffer.length];
		for (int i = 0; i < interleavedBuffer.length; i++)
			if (interleavedBuffer[i] >= max) max = interleavedBuffer[i];
		for (int i = 0; i < normal.length; i++)
			normal[i] = interleavedBuffer[i] / (max + 0.1f);
		return normal;
	}

	public float[] get(int channels) {
		if (channels == numberOfChannels) return interleavedBuffer;
		else if (channels == 2) {
			float[] interleaved = new float[interleavedBuffer.length * 2];
			for (int i = 0; i < interleaved.length; i++) {
				interleaved[i] = interleavedBuffer[(int) (i * 0.5f)];
			}
			return interleaved;
		}
		else return getMonoSum();
	}
	
	public void exportText(String path, String arrayName, int samples) throws Exception {
		PrintWriter out = new PrintWriter(path);
		float[] array = getMonoSum();
		out.print("float " + arrayName + "[" + samples + "] = {");
		for (int i = 0; i < samples; i++) {
			if (i < array.length) out.print(array[i] + ", ");
			else out.print("0.0f, ");
		} 
		out.print("};");
		out.close();
	}
	
	public void printInfo() {
		System.out.println(
				"File name: " + path.getFileName() + "\n" +
				"Channel count: " + numberOfChannels + "\n" +
				"Sampling rate: " + samplingRate + "\n" +
				"Bit depth: " + bitDepth);
	}

}
