package language.tree;

import java.util.ArrayList;

import org.objectweb.asm.MethodVisitor;

import language.compiler.SymbolTable;
import language.compiler.Token;
import language.tree.expression.Expression;

public class ReturnBlock extends Block {
	
	public final Expression returnExpression;

	public ReturnBlock(Token firstToken, ArrayList<Node> nodes, Expression returnExpression) {
		super(firstToken, nodes);
		this.returnExpression = returnExpression;
	}
	
	public void decorate(SymbolTable symtab) throws Exception {
		for (Node node : nodes) node.decorate(symtab);
		returnExpression.decorate(symtab);
		if (returnExpression.type == Types.LAMBDA)
			throw new Exception("Cannot return lambda in line: " + firstToken.lineNumber);
	}
	
	public void generate(MethodVisitor mv, SymbolTable symtab) throws Exception {
		for (Node node : nodes) node.generate(mv, symtab);
		returnExpression.generate(mv, symtab);
	}

}
