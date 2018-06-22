package language.tree;

import org.objectweb.asm.MethodVisitor;

import language.compiler.SymbolTable;
import language.compiler.Token;
import language.tree.expression.Expression;

public class FieldDeclaration extends AssignmentDeclaration {

	public FieldDeclaration(Token firstToken, Token identToken, Expression expression) {
		super(firstToken, identToken, expression);
		isField = true;
	}

	public void decorate(SymbolTable symtab) throws Exception {
		if (symtab.scopeNumber > 0) throw new Exception("Invalid scope.");
		if (symtab.lookup(identToken.text) != null)
			throw new Exception("Redeclaration in line: " + firstToken.lineNumber);
		symtab.insert(identToken.text, this);
		expression.decorate(symtab);
		if ((type == Types.INT || type == Types.FLOAT) && (expression.type != Types.INT && expression.type != Types.FLOAT))
			throw new Exception("Type mismatch in line: " + firstToken.lineNumber);
		else if ((type != Types.INT && type != Types.FLOAT) && expression.type != type)
			throw new Exception("Type mismatch in line: " + firstToken.lineNumber);		
	}

	public void generate(MethodVisitor mv, SymbolTable symtab) throws Exception {
		expression.generate(mv, symtab);
		if (type == Types.INT && expression.type == Types.FLOAT) mv.visitInsn(F2I);
		else if (type == Types.FLOAT && expression.type == Types.INT) mv.visitInsn(I2F);
		mv.visitFieldInsn(PUTSTATIC, symtab.className, identToken.text, getJvmType());
	}

}