package language.tree;

import org.objectweb.asm.MethodVisitor;

import language.compiler.SymbolTable;
import language.compiler.Token;
import language.tree.expression.Expression;

public class FieldDeclaration extends AssignmentDeclaration {

	public FieldDeclaration(Token firstToken, Token identToken, Expression expression) {
		super(firstToken, identToken, expression);
	}

	@Override
	public void decorate(SymbolTable symtab) throws Exception {
		if (symtab.topOfStackLookup(identToken.text) != null)
			throw new Exception("Redeclaration in line: " + firstToken.lineNumber);
		symtab.insert(identToken.text, this);
		expression.decorate(symtab);
		if (expression.type != type) throw new Exception("Type mismatch in line: " + firstToken.lineNumber);
	}

	@Override
	public void generate(MethodVisitor mv, SymbolTable symtab) throws Exception {
		expression.generate(mv, symtab);
		mv.visitFieldInsn(PUTSTATIC, symtab.className, identToken.text, getJvmType());
	}

}