package language.tree.expression;

import org.objectweb.asm.MethodVisitor;

import language.compiler.SymbolTable;
import language.compiler.Token;

public class BoolLitExpression extends Expression {

	public BoolLitExpression(Token firstToken) {
		super(firstToken);
		this.type = Types.BOOL;
	}
	
	@Override
	public void decorate(SymbolTable symtab) throws Exception {}

	@Override
	public void generate(MethodVisitor mv, SymbolTable symtab) throws Exception {
		mv.visitInsn(firstToken.text.equals("true") ? ICONST_1 : ICONST_0);
	}

}
