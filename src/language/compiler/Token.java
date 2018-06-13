package language.compiler;

public class Token {

	public final Kind kind;
	public final String text;
	public final int position;
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
		KW_PRINT("print"),
		KW_STRING("string"),
		KW_ARRAY("array"),
		KW_READ("read"),
		KW_PLOT("plot"),
		KW_PLAY("play"),
		KW_RECORD("record"),
		KW_WRITE("write"),
		KW_RETURN("return"),
		KW_SIZE("size"),
		KW_NEW("new"),
		KW_IMPORT("import"),
		KW_FLOOR("floor"),
		KW_PI("pi"),
		KW_COS("cos"),
		KW_POW("pow"),
		KW_LAMBDA("lambda"),
		KW_FIELD("field");

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
			throw new NumberFormatException("Illegal integer in line: " + lineNumber);
		}
	}

	public float getFloatValue() throws NumberFormatException {
		try {
			return Float.parseFloat(text);
		}
		catch (NumberFormatException exception) {
			throw new NumberFormatException("Illegal float in line: " + lineNumber);
		}
	}

	public boolean isDeclaration() {
		switch (this.kind) {
		case KW_FIELD:
		case KW_INT:
		case KW_BOOL:
		case KW_FLOAT:
		case KW_ARRAY:
		case KW_STRING:
		case KW_LAMBDA: return true;
		default: return false;
		}
	}

	public boolean isComparison() {
		switch (this.kind) {
		case LT:
		case LE:
		case GT:
		case GE:
		case EQUAL:
		case NOTEQUAL: return true;
		default: return false;
		}
	}

	public boolean isSummand() {
		switch (this.kind) {
		case PLUS:
		case MINUS:
		case OR: return true;
		default: return false;
		}
	}

	public boolean isFactor() {
		switch (this.kind) {
		case TIMES:
		case DIV:
		case MOD:
		case AND: return true;
		default: return false;
		}
	}

}
