package language.tree;

import org.objectweb.asm.MethodVisitor;

import language.compiler.SymbolTable;
import language.compiler.Token;
import language.tree.expression.Expression;

public class AssignmentStatement extends Statement {
	
	public Declaration declaration;

	public AssignmentStatement(Token identToken, Expression expression) {
		super(identToken, expression);
	}
	
	@Override
	public void decorate(SymbolTable symtab) throws Exception {
		declaration = symtab.lookup(firstToken.text);
		if (declaration == null) throw new Exception();
		expression.decorate(symtab);
		if (expression.type != declaration.type) throw new Exception();
	}

	@Override
	public void generate(MethodVisitor mv, SymbolTable symtab) throws Exception {
		expression.generate(mv, symtab);
		switch (expression.type) {
		case STRING:
		case ARRAY:
			mv.visitVarInsn(ASTORE, declaration.slotNumber);
			break;
		case FLOAT:
			mv.visitVarInsn(FSTORE, declaration.slotNumber);
			break;
		default:
			mv.visitVarInsn(ISTORE, declaration.slotNumber);
		}
	}

}
