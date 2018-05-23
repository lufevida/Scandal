package language.ide;

import java.io.File;
import java.io.FileWriter;
import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;
import org.reactfx.Subscription;

import language.compiler.Compiler;

public class ScandalTab extends FileTab {

	private static final String[] KEYWORDS = new String[] {
			"if", "while", "return", "import", "write", "play", "plot", "print", "true", "false", "pi", "floor",
			"cos", "pow", "size", "new", "read", "record", "int", "float", "bool", "string", "array", "lambda", "field"};
	private static final String KEYWORD = "?<KEYWORD>\\b(" + String.join("|", KEYWORDS) + ")\\b";
	private static final String STRING = "?<STRING>\"([^\"\\\\]|\\\\.)*\"";
	private static final String COMMENT = "?<COMMENT>//[^\n]*" + "|" + "/\\*(.|\\R)*?\\*/";
	private static final String[] PATTERNS = new String[] {KEYWORD, STRING, COMMENT};
	private static final Pattern PATTERN = Pattern.compile("(" + String.join(")|(", PATTERNS) + ")");
	
	private final CodeArea codeArea = new CodeArea();
	private final Compiler compiler;
	private Subscription cleanup;
	
	public ScandalTab(File file) {
		super(file);
		compiler = new Compiler(file.getPath());
		cleanup = codeArea.multiPlainChanges()
				.successionEnds(Duration.ofMillis(200))
				.subscribe(ignore -> codeArea.setStyleSpans(0, computeHighlighting(codeArea.getText())));
		codeArea.replaceText(0, 0, Compiler.getCode(file.getPath()));
		codeArea.setParagraphGraphicFactory(LineNumberFactory.get(codeArea));
		codeArea.getStylesheets().add(getClass().getResource("keywords.css").toExternalForm());
		setContent(new VirtualizedScrollPane<>(codeArea));
	}
	
	private static StyleSpans<Collection<String>> computeHighlighting(String text) {
		int end = 0;
		Matcher matcher = PATTERN.matcher(text);
		StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();
		while(matcher.find()) {
			String styleClass =
			matcher.group("KEYWORD") != null ? "keyword" :
			matcher.group("STRING") != null ? "string" :
			matcher.group("COMMENT") != null ? "comment" : null;
			spansBuilder.add(Collections.emptyList(), matcher.start() - end);
			spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
			end = matcher.end();
		}
		spansBuilder.add(Collections.emptyList(), text.length() - end);
		return spansBuilder.create();
	}
	
	public void pause() { cleanup.unsubscribe(); }
	
	public void save() {
		try {
			FileWriter writer = new FileWriter(file);
			writer.write(codeArea.getContent().getText());
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
