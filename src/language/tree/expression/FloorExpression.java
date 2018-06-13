package language.tree.expression;

import org.objectweb.asm.MethodVisitor;

import language.compiler.SymbolTable;
import language.compiler.Token;

public class FloorExpression extends Expression {
	
	public final Expression param;

	public FloorExpression(Token firstToken, Expression param) {
		super(firstToken);
		this.param = param;
		this.type = Types.FLOAT;
	}
	
	@Override
	public void decorate(SymbolTable symtab) throws Exception {
		param.decorate(symtab);
		if (param.type != Types.INT && param.type != Types.FLOAT)
			throw new Exception("Invalid FloorExpression in line " + firstToken.lineNumber);
	}

	@Override
	public void generate(MethodVisitor mv, SymbolTable symtab) throws Exception {
		param.generate(mv, symtab);
		if (param.type == Types.INT) mv.visitInsn(I2D);
		else mv.visitInsn(F2D);
		mv.visitMethodInsn(INVOKESTATIC, "java/lang/Math", "floor", "(D)D", false);
		mv.visitInsn(D2F);
	}

}
