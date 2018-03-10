package language.tree.expression;

import org.objectweb.asm.MethodVisitor;

import language.compiler.SymbolTable;
import language.compiler.Token;

public class IntLitExpression extends Expression {

	public IntLitExpression(Token firstToken) {
		super(firstToken);	
		this.type = Types.INT;
	}
	
	@Override
	public void decorate(SymbolTable symtab) throws Exception {}

	@Override
	public void generate(MethodVisitor mv, SymbolTable symtab) throws Exception {
		mv.visitLdcInsn(firstToken.getIntValue());
	}

}
