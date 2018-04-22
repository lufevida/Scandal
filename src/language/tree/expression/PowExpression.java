package language.tree.expression;

import org.objectweb.asm.MethodVisitor;

import language.compiler.SymbolTable;
import language.compiler.Token;

public class PowExpression extends Expression {
	
	public final Expression base;
	public final Expression exponent;

	public PowExpression(Token firstToken, Expression base, Expression exponent) {
		super(firstToken);
		this.base = base;
		this.exponent = exponent;
		this.type = Types.FLOAT;
	}

	@Override
	public void decorate(SymbolTable symtab) throws Exception {
		base.decorate(symtab);
		if (base.type != Types.INT && base.type != Types.FLOAT) throw new Exception("Invalid PowExpression");
		exponent.decorate(symtab);
		if (exponent.type != Types.INT && exponent.type != Types.FLOAT) throw new Exception("Invalid PowExpression");
	}

	@Override
	public void generate(MethodVisitor mv, SymbolTable symtab) throws Exception {	
		base.generate(mv, symtab);
		if (base.type == Types.INT) mv.visitInsn(I2D);
		else mv.visitInsn(F2D);
		exponent.generate(mv, symtab);
		if (exponent.type == Types.INT) mv.visitInsn(I2D);
		else mv.visitInsn(F2D);
		mv.visitMethodInsn(INVOKESTATIC, "java/lang/Math", "pow", "(DD)D", false);		
		mv.visitInsn(D2F);
	}

}
