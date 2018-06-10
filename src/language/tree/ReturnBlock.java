package language.tree;

import java.util.ArrayList;

import org.objectweb.asm.MethodVisitor;

import language.compiler.SymbolTable;
import language.compiler.Token;
import language.tree.expression.Expression;
import language.tree.expression.LambdaLitBlock;

public class ReturnBlock extends Block {
	
	public final Expression returnExpression;
	public LambdaLitBlock lambda;

	public ReturnBlock(Token firstToken, ArrayList<Node> nodes, Expression returnExpression) {
		super(firstToken, nodes);
		this.returnExpression = returnExpression;
	}
	
	public void decorate(SymbolTable symtab) throws Exception {
		int temp = symtab.slotCount;
		symtab.slotCount = lambda.params.size();
		for (Node node : nodes) {
			if (node instanceof Declaration && node.type == Types.LAMBDA)
				throw new Exception("Cannot declare lambda inside lambda.");
			else node.decorate(symtab);
		}
		returnExpression.decorate(symtab);
		if (returnExpression.type == Types.LAMBDA)
			throw new Exception("Must fix a type in line: " + returnExpression.firstToken.lineNumber);
		symtab.slotCount = temp;
	}
	
	public void generate(MethodVisitor mv, SymbolTable symtab) throws Exception {
		for (Node node : nodes) node.generate(mv, symtab);
		returnExpression.generate(mv, symtab);
	}

}
