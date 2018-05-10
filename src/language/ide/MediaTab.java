package language.ide;

import java.io.File;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class MediaTab extends FileTab {
	
	public final MediaPlayer mediaPlayer;

	public MediaTab(File file) {
		super(file);
		Media media = new Media(file.toURI().toString());
		this.mediaPlayer = new MediaPlayer(media);
		mediaPlayer.setAutoPlay(true);
		MediaControl mediaControl = new MediaControl(mediaPlayer);
		setContent(mediaControl);
		setOnClosed(e -> pause());
	}

	public void save() {}

	public void run() {
		mediaPlayer.play();
	}

	public void pause() {
		mediaPlayer.pause();
	}

}
