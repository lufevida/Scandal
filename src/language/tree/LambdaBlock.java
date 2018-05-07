package language.tree;

import java.util.ArrayList;

import org.objectweb.asm.MethodVisitor;

import language.compiler.SymbolTable;
import language.compiler.Token;
import language.tree.expression.Expression;
import language.tree.expression.LambdaLitExpression;

public class LambdaBlock extends Block {
	
	public final Expression returnExpression;
	public LambdaLitExpression lambda;

	public LambdaBlock(Token firstToken, ArrayList<Node> nodes, Expression returnExpression) {
		super(firstToken, nodes);
		this.returnExpression = returnExpression;
	}
	
	public void decorate(SymbolTable symtab) throws Exception {
		for (Node node : nodes) {
			if (node.getClass() == ImportStatement.class) throw new Exception();
			node.decorate(symtab);
		}
		returnExpression.decorate(symtab);
	}
	
	public void generate(MethodVisitor mv, SymbolTable symtab) throws Exception {
		int temp = symtab.slotCount;
		symtab.slotCount = lambda.params.size();
		for (Node node : nodes) node.generate(mv, symtab);
		returnExpression.generate(mv, symtab);
		symtab.slotCount = temp;
	}

}
