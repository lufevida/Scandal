package framework.utilities;

public class ComplexNumber {

	private float x;
	private float y;

	public ComplexNumber(float x, float y) {
		this.x = x;
		this.y = y;
	}

	public float getRealPart() {
		return x;
	}

	public float getImaginaryPart() {
		return y;
	}

	public float getMagnitude() {
		return (float) Math.sqrt(x * x + y * y);
	}

	public ComplexNumber add(ComplexNumber summand) {
		return new ComplexNumber(x + summand.x, y + summand.y);
	}

	public ComplexNumber subtract(ComplexNumber summand) {
		return new ComplexNumber(x - summand.x, y - summand.y);
	}

	public ComplexNumber multiplyBy(ComplexNumber factor) {
		return new ComplexNumber(x * factor.x - y * factor.y, x * factor.y + y * factor.x);
	}

	public ComplexNumber divideBy(ComplexNumber factor) {
		float denominator = factor.getMagnitude();
		float realPart = (x * factor.x + y * factor.y) / denominator;
		float imaginaryPart = (y * factor.x + x * factor.y) / denominator;
		return new ComplexNumber(realPart, imaginaryPart);
	}

}
