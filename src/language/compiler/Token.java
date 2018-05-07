package language.compiler;

public class Token {

	public final Kind kind;
	public final String text;
	public final int position; // position in input array
	public final int length;
	public final int lineNumber;
	public final int lineNumberPosition;

	public static enum Kind {
		EOF("eof"),
		IDENT(""),
		INT_LIT(""),
		FLOAT_LIT(""),
		STRING_LIT(""),
		// Single-character
		PLUS("+"),
		TIMES("*"),
		DIV("/"),
		MOD("%"),
		AND("&"),
		OR("|"),
		COMMA(","),
		DOT("."),
		LPAREN("("),
		RPAREN(")"),
		LBRACE("{"), 
		RBRACE("}"),
		LBRACKET("["), 
		RBRACKET("]"),
		COLON(":"),
		// Single-character that precedes multiple-character
		ASSIGN("="),
		LT("<"),
		GT(">"),
		NOT("!"),
		MINUS("-"),
		// Multiple-character
		EQUAL("=="),
		LE("<="),
		GE(">="),
		NOTEQUAL("!="),
		ARROW("->"),
		// Keywords
		KW_INT("int"),
		KW_FLOAT("float"),
		KW_BOOL("bool"),
		KW_TRUE("true"),
		KW_FALSE("false"),
		KW_IF("if"),
		KW_WHILE("while"),
		KW_FUNC("func"),
		KW_PRINT("print"),
		KW_STRING("string"),
		KW_ARRAY("array"),
		KW_READ("read"),
		KW_PLOT("plot"),
		KW_PLAY("play"),
		KW_RECORD("record"),
		KW_WRITE("write"),
		KW_INFO("info"),
		KW_RETURN("return"),
		KW_SIZE("size"),
		KW_NEW("new"),
		KW_IMPORT("import"),
		KW_FLOOR("floor"),
		KW_PI("pi"),
		KW_COS("cos"),
		KW_POW("pow"),
		KW_LAMBDA("lambda"),
		KW_APPLY("apply"),
		KW_TO("to"),
		KW_THEN("then");

		final String text;

		Kind(String text) {
			this.text = text;
		}
	}

	public Token(Kind kind, int position, int length, int lineNumber, int lineNumberPosition) {
		this.kind = kind;
		this.text = kind.text;
		this.position = position;
		this.length = length;
		this.lineNumber = lineNumber;
		this.lineNumberPosition = lineNumberPosition;
	}

	public Token(Kind kind, String text, int position, int length, int lineNumber, int lineNumberPosition) {
		this.kind = kind;
		this.text = text;
		this.position = position;
		this.length = length;
		this.lineNumber = lineNumber;
		this.lineNumberPosition = lineNumberPosition;
	}

	public int getIntValue() throws NumberFormatException {
		try {
			return Integer.parseInt(text);
		} catch (NumberFormatException exception) {
			throw new NumberFormatException("The token does not contain an integer.");
		}
	}

	public float getFloatValue() throws NumberFormatException {
		try {
			return Float.parseFloat(text);
		} catch (NumberFormatException exception) {
			throw new NumberFormatException("The token does not contain a float.");
		}
	}

}
