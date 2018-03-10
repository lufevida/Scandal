package framework.utilities;

import framework.generators.AudioFlow;
import framework.generators.RealTimePerformer;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public abstract class GenericView extends Application {

	public RealTimePerformer synth;
	public AudioFlow flow;
	public String title;
	public static VBox vbox = new VBox(20);
	
	@Override
	public abstract void init() throws Exception;

	@Override
	public void start(Stage stage) throws Exception {
		synth.startFlow();
		addParameters();
		vbox.setPadding(new Insets(20, 20, 20, 20));
		stage.setScene(new Scene(vbox));
		stage.setTitle(title);
		stage.setResizable(false);
		stage.show();
	}
	
	public abstract void addParameters();
	
	@Override
	public void stop() {
		synth.stopFlow();
	}
	
	public static void addSlider(String name, float min, float max, float init, String unit, ChangeListener<? super Number> cb) {
		Label label = new Label(name);
		label.setAlignment(Pos.CENTER_LEFT);
		label.setMinWidth(120);
		Slider slider = new Slider(min, max, init);
		slider.valueProperty().addListener(cb);
		slider.setMinWidth(240);
		Label value = new Label();
		value.textProperty().bind(slider.valueProperty().asString("%.2f " + unit));
		value.setAlignment(Pos.CENTER_RIGHT);
		value.setMinWidth(80);
		HBox hbox = new HBox(20);
		hbox.getChildren().addAll(label, slider, value);
		vbox.getChildren().addAll(hbox);
	}

}
