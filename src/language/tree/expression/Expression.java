package language.tree.expression;

import language.compiler.Token;
import language.tree.Node;

public abstract class Expression extends Node {
	
	public boolean isReturnExpression = false;

	public Expression(Token firstToken) {
		super(firstToken);
	}
	
	@Override
	public String getJvmType() {
		switch (this.type) {
		case INT: return "I";
		case FLOAT: return "F";
		case BOOL: return "Z";
		case STRING: return "Ljava/lang/String;";
		case ARRAY: return "[F";
		default: return null;
		}
	}

}
