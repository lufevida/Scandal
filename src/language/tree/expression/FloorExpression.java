package language.tree.expression;

import org.objectweb.asm.MethodVisitor;

import language.compiler.SymbolTable;
import language.compiler.Token;

public class FloorExpression extends Expression {
	
	public final Expression param;

	public FloorExpression(Token firstToken, Expression param) {
		super(firstToken);
		this.param = param;
		this.type = Types.INT;
	}
	
	@Override
	public void decorate(SymbolTable symtab) throws Exception {
		param.decorate(symtab);
		if (param.type != Types.INT && param.type != Types.FLOAT) throw new Exception("Invalid FloorExpression");
	}

	@Override
	public void generate(MethodVisitor mv, SymbolTable symtab) throws Exception {
		param.generate(mv, symtab);
		if (param.type == Types.FLOAT) mv.visitInsn(F2I);
	}

}
