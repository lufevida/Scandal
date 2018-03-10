package language.tree.expression;

import org.objectweb.asm.MethodVisitor;

import language.compiler.SymbolTable;
import language.compiler.Token;

public class BiquadExpression extends Expression {
	
	public final Expression array;
	public final Expression cutoff;
	public final Expression resonance;
	public final Expression method;

	public BiquadExpression(Token firstToken, Expression array, Expression cutoff, Expression resonance, Expression method) {
		super(firstToken);
		this.array = array;
		this.cutoff = cutoff;
		this.resonance = resonance;
		this.method = method;
		this.type = Types.ARRAY;
	}
	
	@Override
	public void decorate(SymbolTable symtab) throws Exception {
		array.decorate(symtab);
		if (array.type != Types.ARRAY) throw new Exception("Invalid BiquadExpression");
		cutoff.decorate(symtab);
		if (cutoff.type != Types.INT && cutoff.type != Types.FLOAT && cutoff.type != Types.ARRAY)
				throw new Exception("Invalid BiquadExpression");
		resonance.decorate(symtab);
		if (resonance.type != Types.INT && resonance.type != Types.FLOAT && resonance.type != Types.ARRAY)
				throw new Exception("Invalid GainExpression");
		method.decorate(symtab);
		if (method.type != Types.FILTER) throw new Exception("Invalid BiquadExpression");
	}

	@Override
	public void generate(MethodVisitor mv, SymbolTable symtab) throws Exception {
		mv.visitTypeInsn(NEW, "framework/utilities/BiquadUtility");
		mv.visitInsn(DUP);
		mv.visitMethodInsn(INVOKESPECIAL, "framework/utilities/BiquadUtility", "<init>", "()V", false);
		array.generate(mv, symtab);
		String type = "([F";
		cutoff.generate(mv, symtab);
		if (cutoff.type == Types.INT) mv.visitInsn(I2F);
		if (cutoff.type == Types.ARRAY) type += "[";
		type += "F";
		resonance.generate(mv, symtab);
		if (resonance.type == Types.INT) mv.visitInsn(I2F);
		if (resonance.type == Types.ARRAY) type += "[";
		type += "F";
		method.generate(mv, symtab);
		type += "I)[F";
		mv.visitMethodInsn(INVOKEVIRTUAL, "framework/utilities/BiquadUtility", "process", type, false);
	}

}
