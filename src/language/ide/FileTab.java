package language.ide;

import java.io.File;
import java.io.FileWriter;

import javafx.scene.control.Tab;
import javafx.scene.web.WebView;
import language.compiler.Compiler;

public class FileTab extends Tab {

	public final File file;
	public final Compiler compiler;
	public final WebView view = new WebView();
	public String code;
	
	public FileTab(File file) {
		super(file.getName());
		this.file = file;
		compiler = new Compiler(file.getPath());
		view.getEngine().loadContent(CodeMirror.dump(file.getPath()));
		setContent(view);
		setOnClosed(e -> save());
	}

	public String snapshot() {
		return (String) view.getEngine().executeScript("editor.getValue();");
	}
	
	public void save() {
		try {
			FileWriter writer = new FileWriter(file);
			writer.write(snapshot());
			writer.close();
		}
		catch (Exception e) { e.printStackTrace(); }
	}
	
	public void run() {
		try {
			compiler.compile();
			compiler.getInstance().run();
		}
		catch (Exception e) { e.printStackTrace(); }
	}
	
	public void pause() {
		// TODO use audio flows instead
	}

}
