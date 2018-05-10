package language.ide;

import java.io.File;
import java.io.FileWriter;

import javafx.scene.web.WebView;
import language.compiler.Compiler;

public class ScandalTab extends FileTab {

	public final Compiler compiler;
	public final WebView view = new WebView();
	
	public ScandalTab(File file) {
		super(file);
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
