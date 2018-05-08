package language.tree.statement;

import language.compiler.Token;
import language.tree.Node;
import language.tree.expression.Expression;

public abstract class Statement extends Node {
	
	public final Expression expression;
	
	public Statement(Token firstToken, Expression expression) {
		super(firstToken);
		this.expression = expression;
	}

}
