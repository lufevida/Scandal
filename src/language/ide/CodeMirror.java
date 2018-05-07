package language.ide;

import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

public final class CodeMirror {

	public static final String skeleton = getCode("src/language/ide/skeleton.html");
	public static final String style = getCode("src/language/ide/codemirror.css");
	public static final String complex = getCode("src/language/ide/codemirror.js");
	public static final String simple = getCode("src/language/ide/simple.js");
	public static final String syntax = getCode("src/language/ide/syntax.js");

	public static String getCode(String inPath) {
		Path path = FileSystems.getDefault().getPath(inPath);
		try { return new String(Files.readAllBytes(path)); }
		catch (Exception e) { return ""; }
	}
	
	public static String dump(String path) {
		return skeleton
				.replace("${code}", getCode(path))
				.replace("${style}", style)
				.replace("${complex}", complex)
				.replace("${simple}", simple)
				.replace("${syntax}", syntax);
	}

}
