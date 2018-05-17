package language.ide;

import java.io.File;

import javafx.scene.control.Tab;

public abstract class FileTab extends Tab {
	
	public final File file;
	
	public FileTab(File file) {
		super(file.getName());
		this.file = file;
	}
	
	public void add() {
		MainView.pane.getTabs().add(this);
		MainView.pane.getSelectionModel().select(MainView.pane.getTabs().size() - 1);
	}
	
	public void run() {}
	
	public void pause() {}
	
	public void save() {}
	
}
