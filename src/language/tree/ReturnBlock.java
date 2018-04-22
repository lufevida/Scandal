package language.tree;

import java.util.ArrayList;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

import language.compiler.SymbolTable;
import language.compiler.Token;
import language.tree.expression.Expression;

public class ReturnBlock extends Block {
	
	public final Expression returnExpression;
	public int paramCount = 0;
	public boolean resetCounter = true;

	public ReturnBlock(Token firstToken, ArrayList<Node> nodes, Expression returnExpression) {
		super(firstToken, nodes);
		this.returnExpression = returnExpression;
	}
	
	@Override
	public void decorate(SymbolTable symtab) throws Exception {
		for (Node node : nodes) {
			if (node.getClass() == ImportStatement.class) throw new Exception();
			node.decorate(symtab);
		}
		returnExpression.decorate(symtab);
	}
	
	@Override
	public void generate(MethodVisitor mv, SymbolTable symtab) throws Exception {
		returnExpression.isReturnExpression = true;
		Label blockStart = new Label();
		Label blockEnd = new Label();
		mv.visitLabel(blockStart);
		mv.visitLabel(blockEnd);
		int temp = symtab.slotCount;
		if (resetCounter) symtab.slotCount = 1 + paramCount;
		for (Node node : nodes) {
			if (node.getClass() == AssignmentDeclaration.class) {
				AssignmentDeclaration dec = (AssignmentDeclaration) node;
				dec.startLabel = blockStart;
				dec.endLabel = blockEnd;
				dec.expression.isReturnExpression = true;
			}
			node.generate(mv, symtab);
		}
		returnExpression.generate(mv, symtab);
		if (resetCounter) symtab.slotCount = temp;
	}

}
