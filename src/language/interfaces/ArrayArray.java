package language.interfaces;

public interface ArrayArray {
	
	float[] apply(float[] x);
	
	default ArrayArray then(ArrayArray after) {
		return x -> after.apply(apply(x));
	}

}
