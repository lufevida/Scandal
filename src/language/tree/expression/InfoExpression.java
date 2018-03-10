package language.tree.expression;

import org.objectweb.asm.MethodVisitor;

import framework.utilities.Settings;
import language.compiler.SymbolTable;
import language.compiler.Token;

public class InfoExpression extends Expression {

	public InfoExpression(Token firstToken) {
		super(firstToken);
		this.type = Types.STRING;
	}
	
	@Override
	public void decorate(SymbolTable symtab) throws Exception {}

	@Override
	public void generate(MethodVisitor mv, SymbolTable symtab) throws Exception {
		mv.visitLdcInsn(Settings.getInfo());
	}

}
