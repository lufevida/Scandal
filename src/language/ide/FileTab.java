package language.ide;

import java.io.File;

import javafx.scene.control.Tab;

public abstract class FileTab extends Tab {
	
	public final File file;
	
	public FileTab(File file) {
		super(file.getName());
		this.file = file;
	}
	
	public abstract void save();
	
	public abstract void run();
	
	public abstract void pause();
	
}
