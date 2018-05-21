package language.ide;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiSystem;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Mixer;

import javafx.geometry.Insets;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class SettingsBox extends VBox {
	
	private final int pad = 10;
	private final double left = 0.3;
	private final double right = 0.7;
	
	public SettingsBox() {
		super(10);
		setPadding(new Insets(pad, pad, pad, pad));
		getChildren().addAll(getControllers(), getDevices(), getVectors(), getRates(), getDepths());
	}

	private HBox getControllers() {		
		Label label = new Label("MIDI Controller");
		ComboBox<String> controllers = new ComboBox<>();
		MidiDevice.Info[] infos = MidiSystem.getMidiDeviceInfo();
		for (int i = 0; i < infos.length; i++) controllers.getItems().add(infos[i].getName());
		controllers.getSelectionModel().select(1);
		controllers.valueProperty().addListener((obs, old, val) -> {
			System.out.println(val);
		});
		HBox box = new HBox(pad);
		box.getChildren().addAll(label, controllers);
		box.widthProperty().addListener((obs, old, val) -> {
			label.setPrefWidth((double) val * left);
			controllers.setPrefWidth((double) val * right);
		});
		return box;
	}
	
	private HBox getDevices() {
		Label label = new Label("Audio Device");
		ComboBox<String> devices = new ComboBox<>();
		Mixer.Info[] mixers = AudioSystem.getMixerInfo();
		for (int i = 0; i < mixers.length; i++) devices.getItems().add(mixers[i].getName());
		devices.getSelectionModel().select(0);
		devices.valueProperty().addListener((obs, old, val) -> {
			System.out.println(val);
		});
		HBox box = new HBox(pad);
		box.getChildren().addAll(label, devices);
		box.widthProperty().addListener((obs, old, val) -> {
			label.setPrefWidth((double) val * left);
			devices.setPrefWidth((double) val * right);
		});
		return box;
	}
	
	private HBox getVectors() {
		Label label = new Label("Vector Size");
		ComboBox<Integer> vectors = new ComboBox<>();
		vectors.getItems().addAll(128, 256, 512, 1024, 2048, 4096);
		vectors.getSelectionModel().select(3);
		vectors.valueProperty().addListener((obs, old, val) -> {
			System.out.println(val);
		});
		HBox box = new HBox(pad);
		box.getChildren().addAll(label, vectors);
		box.widthProperty().addListener((obs, old, val) -> {
			label.setPrefWidth((double) val * left);
			vectors.setPrefWidth((double) val * right);
		});
		return box;
	}
	
	private HBox getRates() {
		Label label = new Label("Sampling Rate");
		ComboBox<Integer> rates = new ComboBox<>();
		rates.getItems().addAll(44100, 48000, 88200, 96000);
		rates.getSelectionModel().select(0);
		rates.valueProperty().addListener((obs, old, val) -> {
			System.out.println(val);
		});
		HBox box = new HBox(pad);
		box.getChildren().addAll(label, rates);
		box.widthProperty().addListener((obs, old, val) -> {
			label.setPrefWidth((double) val * left);
			rates.setPrefWidth((double) val * right);
		});
		return box;
	}
	
	private HBox getDepths() {
		Label label = new Label("Bit Depth");
		ComboBox<Integer> depths = new ComboBox<>();
		depths.getItems().addAll(16, 24);
		depths.getSelectionModel().select(0);
		depths.valueProperty().addListener((obs, old, val) -> {
			System.out.println(val);
		});
		HBox box = new HBox(pad);
		box.getChildren().addAll(label, depths);
		box.widthProperty().addListener((obs, old, val) -> {
			label.setPrefWidth((double) val * left);
			depths.setPrefWidth((double) val * right);
		});
		return box;
	}

}
