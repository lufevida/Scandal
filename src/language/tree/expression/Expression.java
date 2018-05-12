package language.tree.expression;

import language.compiler.Token;
import language.tree.Node;

public abstract class Expression extends Node {
	
	public boolean isReturnExpression = false;

	public Expression(Token firstToken) {
		super(firstToken);
	}

}
