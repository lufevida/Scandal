package language.tree;

import language.compiler.Token;
import language.tree.expression.Expression;

public abstract class Statement extends Node {
	
	public final Expression expression;
	
	public Statement(Token firstToken, Expression expression) {
		super(firstToken);
		this.expression = expression;
	}

}
