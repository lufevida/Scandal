package language.ide;

import java.io.File;

import javafx.scene.control.Button;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.text.Font;

public class MediaTab extends FileTab {

	private final String formatter;
	private final MediaPlayer player;
	private boolean mute = false;
	
	public MediaTab(File file) {
		super(file);
		formatter = file.getName() + " (%02d:%02d)";
		BorderPane pane = new BorderPane();
		pane.setTop(getBar());
		Media media = new Media(file.toURI().toString());
		player = new MediaPlayer(media);
		player.setAutoPlay(true);
		player.currentTimeProperty().addListener(obs -> setTime(player.getCurrentTime().toSeconds()));
		player.setOnReady(() -> setTime(player.getMedia().getDuration().toSeconds()));
		player.setOnEndOfMedia(this::rewind);
		MediaView mediaView = new MediaView(player);
		pane.setCenter(mediaView);
		setContent(pane);
		setOnClosed(e -> pause());
	}
	
	private ToolBar getBar() {
		ToolBar bar = new ToolBar();
		HBox spacer = new HBox();
		HBox.setHgrow(spacer, Priority.ALWAYS);
		bar.getItems().addAll(spacer, getMute(), getPlay(), getPause(), getRewind());
		return bar;
	}
	
	private Button getMute() {
		Button b = new Button(FontAwesome.VOLUME_UP.unicode);
		b.setFont(Font.font("FontAwesome"));
		b.setOnAction(e -> mute(b));
		return b;
	}
	
	private void mute(Button b) {
		mute = !mute;
		b.setText(mute ? FontAwesome.VOLUME_OFF.unicode : FontAwesome.VOLUME_UP.unicode);
		player.setVolume(mute ? 0 : 1);
	}
	
	private Button getPlay() {
		Button b = new Button(FontAwesome.PLAY.unicode);
		b.setFont(Font.font("FontAwesome"));
		b.setOnAction(e -> run());
		return b;
	}
	
	private Button getPause() {
		Button b = new Button(FontAwesome.PAUSE.unicode);
		b.setFont(Font.font("FontAwesome"));
		b.setOnAction(e -> pause());
		return b;
	}
	
	private Button getRewind() {
		Button b = new Button(FontAwesome.STEP_BACKWARD.unicode);
		b.setFont(Font.font("FontAwesome"));
		b.setOnAction(e -> rewind());
		return b;
	}
	
	private void rewind() { player.seek(player.getStartTime()); }
	
	private void setTime(Double time) {
		int secs = time.intValue();
		int mins = secs / 60;
		if (mins > 0) secs -= mins * 60;
		setText(String.format(formatter, mins, secs));
	}

	public void run() { player.play(); }

	public void pause() { player.pause(); }

}
