package language.interfaces;

public interface MatrixArray {
	
	float[] apply(float[][] x);

	default MatrixArray then(ArrayArray after) {
		return x -> after.apply(apply(x));
	}

}
