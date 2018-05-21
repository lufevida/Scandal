package language.ide;

import java.io.File;
import java.io.FileWriter;

import javafx.scene.web.WebView;
import language.compiler.Compiler;

public class CodeMirrorTab extends FileTab {

	private final Compiler compiler;
	private final WebView view = new WebView();
	
	public CodeMirrorTab(File file) {
		super(file);
		compiler = new Compiler(file.getPath());
		view.getEngine().loadContent(CodeMirror.dump(file.getPath()));
		setContent(view);
		setOnClosed(e -> save());
	}

	public void save() {
		try {
			FileWriter writer = new FileWriter(file);
			writer.write((String) view.getEngine().executeScript("editor.getValue();"));
			writer.close();
		}
		catch (Exception e) {
			MainView.console.appendText(e.getMessage());
			MainView.console.appendText("\n");
		}
	}
	
	public void run() {
		try {
			compiler.compile();
			compiler.getInstance().run();
		}
		catch (Exception e) {
			MainView.console.appendText(e.getMessage());
			MainView.console.appendText("\n");
		}
	}

}
