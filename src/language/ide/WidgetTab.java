package language.ide;

import framework.generators.RealTimePerformer;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.io.File;

public abstract class WidgetTab extends FileTab {

	public final RealTimePerformer synth;
	private final VBox box = new VBox(20);

	public WidgetTab(String title, RealTimePerformer synth) {
		super(new File(title));
		this.synth = synth;
		synth.startFlow();
		box.setPadding(new Insets(20, 20, 20, 20));
		setContent(box);
		setOnClosed(e -> pause());
	}

	public void pause() {
		synth.stopFlow();
	}

	public void addSlider(String name, float min, float max, float init, String unit, ChangeListener<? super Number> cb) {
		Label label = new Label(name);
		label.setAlignment(Pos.CENTER_LEFT);
		Slider slider = new Slider(min, max, init);
		slider.valueProperty().addListener(cb);
		HBox.setHgrow(slider, Priority.ALWAYS);
		Label value = new Label();
		value.textProperty().bind(slider.valueProperty().asString("%.2f " + unit));
		value.setAlignment(Pos.CENTER_RIGHT);
		HBox hbox = new HBox(20);
		hbox.getChildren().addAll(label, slider, value);
		box.getChildren().add(hbox);
	}

}
