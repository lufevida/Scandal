package language.tree.expression;

import org.objectweb.asm.MethodVisitor;

import language.compiler.SymbolTable;
import language.compiler.Token;

public class StringLitExpression extends Expression {

	public StringLitExpression(Token firstToken) {
		super(firstToken);
		this.type = Types.STRING;
	}
	
	@Override
	public void decorate(SymbolTable symtab) throws Exception {}

	@Override
	public void generate(MethodVisitor mv, SymbolTable symtab) throws Exception {
		mv.visitLdcInsn(firstToken.text);
	}

}
