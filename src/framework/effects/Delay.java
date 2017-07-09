package framework.effects;

import framework.utilities.Settings;

public class Delay implements EffectsProcessor {

	public float[] process(float[] buffer, int time, float feedback, float mix) {
		int samples = time * Settings.samplingRate / 1000; // ms to samples
		if (samples <= 0 || samples >= buffer.length) return buffer; // sanity check
		float[] processedBuffer = new float[buffer.length];
		float[] circularBuffer = new float[buffer.length];
		int readIndex = 0;
		int writeIndex = (int) samples;
		for (int i = 0; i < buffer.length; i++) {
			processedBuffer[i] = mix * circularBuffer[readIndex] + (1 - mix) * buffer[i];
			circularBuffer[writeIndex] = buffer[i] + circularBuffer[readIndex] * feedback;
			readIndex++;
			if (readIndex >= circularBuffer.length) readIndex -= circularBuffer.length;
			writeIndex++;
			if (writeIndex >= circularBuffer.length) writeIndex -= circularBuffer.length;
		}
		return processedBuffer;
	}

	public float[] process(float[] buffer, int time, float[] feedbacks, float mix) {
		int samples = time * Settings.samplingRate / 1000;
		if (samples <= 0 || samples >= buffer.length) return buffer;
		float[] processedBuffer = new float[buffer.length];
		float[] circularBuffer = new float[buffer.length];
		int readIndex = 0;
		int writeIndex = (int) samples;
		float feedbackIndex = 0;
		float feedbackIncrement = (float) feedbacks.length / buffer.length;
		for (int i = 0; i < buffer.length; i++) {
			processedBuffer[i] = mix * circularBuffer[readIndex] + (1 - mix) * buffer[i];
			circularBuffer[writeIndex] = buffer[i] + circularBuffer[readIndex] * feedbacks[(int) feedbackIndex];
			feedbackIndex += feedbackIncrement;
			readIndex++;
			if (readIndex >= circularBuffer.length) readIndex -= circularBuffer.length;
			writeIndex++;
			if (writeIndex >= circularBuffer.length) writeIndex -= circularBuffer.length;
		}
		return processedBuffer;
	}

	public float[] process(float[] buffer, int time, float feedback, float[] mixes) {
		int samples = time * Settings.samplingRate / 1000; // ms to samples
		if (samples <= 0 || samples >= buffer.length) return buffer; // sanity check
		float[] processedBuffer = new float[buffer.length];
		float[] circularBuffer = new float[buffer.length];
		int readIndex = 0;
		int writeIndex = (int) samples;
		float mixIndex = 0;
		float mixIncrement = (float) mixes.length / buffer.length;
		for (int i = 0; i < buffer.length; i++) {
			processedBuffer[i] = mixes[(int) mixIndex] * circularBuffer[readIndex] + (1 - mixes[(int) mixIndex]) * buffer[i];
			circularBuffer[writeIndex] = buffer[i] + circularBuffer[readIndex] * feedback;
			mixIndex += mixIncrement;
			readIndex++;
			if (readIndex >= circularBuffer.length) readIndex -= circularBuffer.length;
			writeIndex++;
			if (writeIndex >= circularBuffer.length) writeIndex -= circularBuffer.length;
		}
		return processedBuffer;
	}

	public float[] process(float[] buffer, int time, float[] feedbacks, float[] mixes) {
		int samples = time * Settings.samplingRate / 1000; // ms to samples
		if (samples <= 0 || samples >= buffer.length) return buffer; // sanity check
		float[] processedBuffer = new float[buffer.length];
		float[] circularBuffer = new float[buffer.length];
		int readIndex = 0;
		int writeIndex = (int) samples;
		float feedbackIndex = 0;
		float feedbackIncrement = (float) feedbacks.length / buffer.length;
		float mixIndex = 0;
		float mixIncrement = (float) mixes.length / buffer.length;
		for (int i = 0; i < buffer.length; i++) {
			processedBuffer[i] = mixes[(int) mixIndex] * circularBuffer[readIndex] + (1 - mixes[(int) mixIndex]) * buffer[i];
			circularBuffer[writeIndex] = buffer[i] + circularBuffer[readIndex] * feedbacks[(int) feedbackIndex];
			feedbackIndex += feedbackIncrement;
			mixIndex += mixIncrement;
			readIndex++;
			if (readIndex >= circularBuffer.length) readIndex -= circularBuffer.length;
			writeIndex++;
			if (writeIndex >= circularBuffer.length) writeIndex -= circularBuffer.length;
		}
		return processedBuffer;
	}

}
