package language.interfaces;

public interface FloatFloat {

	float apply(float x);

	default FloatFloat then(FloatFloat after) {
		return x -> after.apply(apply(x));
	}

}
