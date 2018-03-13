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

	public ReturnBlock(Token firstToken, ArrayList<AssignmentDeclaration> declarations, ArrayList<Statement> statements, Expression returnExpression) {
		super(firstToken, declarations, statements);
		this.returnExpression = returnExpression;
	}
	
	@Override
	public void decorate(SymbolTable symtab) throws Exception {
		for (AssignmentDeclaration declaration : declarations) declaration.decorate(symtab);
		for (Statement statement : statements) {
			statement.decorate(symtab);
			if (statement.getClass() == ImportStatement.class)
				throw new Exception("Import statements are only allowed in the outmost scope");
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
		symtab.slotCount = 1 + paramCount;
		for (AssignmentDeclaration declaration : declarations) {
			if (declaration.getClass() == AssignmentDeclaration.class) {
				AssignmentDeclaration dec = (AssignmentDeclaration) declaration;
				dec.expression.isReturnExpression = true;
			}
			declaration.startLabel = blockStart;
			declaration.endLabel = blockEnd;
			declaration.generate(mv, symtab);
		}
		for (Statement statement : statements) statement.generate(mv, symtab);
		returnExpression.generate(mv, symtab);
		symtab.slotCount = temp;
	}

}
