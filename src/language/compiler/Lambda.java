package language.compiler;

import language.tree.expression.FuncLitExpression;

public class Lambda {
	
	public final String name;
	public final int slot;
	public FuncLitExpression expression;
	public final boolean isAbstract;
	public boolean isLocal = false;
	public int paramSlot = 0;

	public Lambda(String name, int slot, FuncLitExpression expression, boolean isAbstract) {
		this.name = name;
		this.slot = slot;
		this.expression = expression;
		this.isAbstract = isAbstract;
	}

}
