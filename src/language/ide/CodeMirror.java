package language.ide;

import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

public final class CodeMirror {

	private static final String skeleton = getCode("jar/skeleton.html");
	private static final String style = getCode("jar/codemirror.css");
	private static final String complex = getCode("jar/codemirror.js");
	private static final String simple = getCode("jar/simplemode.js");
	private static final String syntax = getCode("jar/syntax.js");

	private static String getCode(String inPath) {
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
