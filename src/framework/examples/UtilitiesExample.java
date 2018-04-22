package framework.examples;

import framework.waveforms.WavetableResidual;
import javafx.application.Application;
import javafx.stage.Stage;

public class UtilitiesExample extends Application implements Runnable {

	@Override
	public void start(Stage stage) {}
	
	@Override
	public void run() {
		Application.launch();
	}
	
	public static void main(String[] args) throws Exception {
//		Settings.printInfo();
//		WaveFile lisa = new WaveFile("doc/monoLisa.wav");
//		lisa.printInfo();
//		lisa.plot(1000);
		WavetableResidual.getSharedInstance().plot(500, 1);
//		new AdditiveSquare().plot(512, 2);
//		new BreakpointFunction(512, new float[]{0, 0.5f, 0, 1, 0, 1, 0, 0.5f, 0}).plot();
//		new BiquadPeak().plotMagnitudeResponse(1000, 500, -4);
	}

}
