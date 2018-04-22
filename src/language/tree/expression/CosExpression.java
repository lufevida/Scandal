package language.tree.expression;

import org.objectweb.asm.MethodVisitor;

import language.compiler.SymbolTable;
import language.compiler.Token;

public class CosExpression extends Expression {
	
	public final Expression phase;

	public CosExpression(Token firstToken, Expression phase) {
		super(firstToken);
		this.phase = phase;
		this.type = Types.FLOAT;
	}

	@Override
	public void decorate(SymbolTable symtab) throws Exception {
		phase.decorate(symtab);
		if (phase.type != Types.INT && phase.type != Types.FLOAT) throw new Exception("Invalid CosExpression");
	}

	@Override
	public void generate(MethodVisitor mv, SymbolTable symtab) throws Exception {
		phase.generate(mv, symtab);
		if (phase.type == Types.INT) mv.visitInsn(I2D);
		else mv.visitInsn(F2D);
		mv.visitMethodInsn(INVOKESTATIC, "java/lang/Math", "cos", "(D)D", false);
		mv.visitInsn(D2F);
	}

}
