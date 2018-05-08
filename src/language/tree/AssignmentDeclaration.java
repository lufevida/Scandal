package language.tree;

import org.objectweb.asm.MethodVisitor;

import language.compiler.SymbolTable;
import language.compiler.Token;
import language.tree.expression.Expression;

public class AssignmentDeclaration extends Declaration {

	public final Expression expression;

	public AssignmentDeclaration(Token firstToken, Token identToken, Expression expression) {
		super(firstToken, identToken);
		this.expression = expression;
	}
	
	@Override
	public void decorate(SymbolTable symtab) throws Exception {
		if (symtab.topOfStackLookup(identToken.text) != null) throw new Exception();
		symtab.insert(identToken.text, this);
		expression.decorate(symtab);
		if (expression.type != type) throw new Exception("Type mismatch in line " + firstToken.lineNumber);
	}

	@Override
	public void generate(MethodVisitor mv, SymbolTable symtab) throws Exception {
		slotNumber = symtab.slotCount++;
		expression.generate(mv, symtab);
		switch (expression.type) {
		case INT:
		case BOOL:
			mv.visitVarInsn(ISTORE, slotNumber);
			break;
		case FLOAT:
			mv.visitVarInsn(FSTORE, slotNumber);
			break;
		default:
			mv.visitVarInsn(ASTORE, slotNumber);
		}
	}

}
