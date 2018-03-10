package language.tree.expression;

import org.objectweb.asm.MethodVisitor;

import language.compiler.SymbolTable;
import language.compiler.Token;

public class ArraySizeExpression extends Expression {
	
	public final Expression param;

	public ArraySizeExpression(Token firstToken, Expression param) {
		super(firstToken);
		this.param = param;
		this.type = Types.INT;
	}
	
	@Override
	public void decorate(SymbolTable symtab) throws Exception {
		param.decorate(symtab);
		if (param.type != Types.ARRAY) throw new Exception("Invalid ArraySizeExpression");
	}

	@Override
	public void generate(MethodVisitor mv, SymbolTable symtab) throws Exception {
		param.generate(mv, symtab);
		mv.visitInsn(ARRAYLENGTH);
	}

}
