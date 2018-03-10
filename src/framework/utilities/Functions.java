package framework.utilities;

public class Functions {
	
	public static float rawTodBFS(float amplitude) {
		return 20 * (float) Math.log10(Math.abs(amplitude));
	}
	
	public static float dBFSToRaw(float amplitude, boolean negative) {
		return (float) Math.pow(10, amplitude * 0.05) * (negative ? -1 : 1);
	}
	
	public static float distance(float a, float b) {
		return (float) Math.abs(a - b);
	}
	
	public static void print(float[] array) {
		for (int i = 0; i < array.length; i++) System.out.println(array[i]);
	}
	
	public static float[] normalize(float[] buffer) {
		float[] result = new float[buffer.length];
		float max = 0;
		for (int i = 0; i < buffer.length; i++) if (buffer[i] >= max) max = buffer[i];
		for (int i = 0; i < buffer.length; i++) result[i] = (buffer[i] / max) * dBFSToRaw(-3, false);
		return result;
	}

}
