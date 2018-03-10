package language.tree.expression;

import org.objectweb.asm.MethodVisitor;

import language.compiler.SymbolTable;
import language.compiler.Token;

public class FloatLitExpression extends Expression {

	public FloatLitExpression(Token firstToken) {
		super(firstToken);
		this.type = Types.FLOAT;
	}
	
	@Override
	public void decorate(SymbolTable symtab) throws Exception {}

	@Override
	public void generate(MethodVisitor mv, SymbolTable symtab) throws Exception {
		mv.visitLdcInsn(firstToken.getFloatValue());
	}

}
