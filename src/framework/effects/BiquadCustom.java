package framework.effects;

import framework.utilities.ComplexNumber;

public class BiquadCustom extends Biquad {

	private ComplexNumber pole;
	private ComplexNumber zero;

	public BiquadCustom(ComplexNumber pole) {
		this.pole = pole;
		this.zero = new ComplexNumber(1, 0).divideBy(pole);
		update(Float.NaN, Float.NaN);
	}

	public BiquadCustom(ComplexNumber pole, ComplexNumber zero) {
		this.pole = pole;
		this.zero = zero;
		update(Float.NaN, Float.NaN);
	}
	
	public void setPole(ComplexNumber pole) {
		this.pole = pole;
		update(Float.NaN, Float.NaN);
	}
	
	public void setZero(ComplexNumber zero) {
		this.zero = zero;
		update(Float.NaN, Float.NaN);
	}

	@Override
	public void update(float cutoff, float resonance) {
		float b0 = 1;
		float b1 = -2 * zero.getRealPart();
		float b2 = zero.getMagnitude() * zero.getMagnitude();
		float a1 = -2 * pole.getRealPart();
		float a2 = pole.getMagnitude() * pole.getMagnitude();
		setCoefficients(b0, b1, b2, 1, a1, a2);
	}

	public void plotMagnitudeResponse(int length) {
		plotMagnitudeResponse(length, Float.NaN, Float.NaN);
	}

}
