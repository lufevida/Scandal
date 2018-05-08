package language.interfaces;

public interface FloatArray {
	
	float[] apply(float x);

	default FloatArray then(ArrayArray after) {
		return x -> after.apply(apply(x));
	}

}
