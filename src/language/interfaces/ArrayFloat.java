package language.interfaces;

public interface ArrayFloat {
	
	float apply(float[] x);

	default ArrayFloat then(FloatFloat after) {
		return x -> after.apply(apply(x));
	}

}
