package language.tree.expression;

import org.objectweb.asm.MethodVisitor;

import language.compiler.SymbolTable;
import language.compiler.Token;

public class OscillatorExpression extends Expression {
	
	public final Expression duration;
	public final Expression amplitude;
	public final Expression frequency;
	public final Expression shape;

	public OscillatorExpression(Token firstToken, Expression duration, Expression amplitude, Expression frequency, Expression shape) {
		super(firstToken);
		this.duration = duration;
		this.amplitude = amplitude;
		this.frequency = frequency;
		this.shape = shape;
		this.type = Types.ARRAY;
	}
	
	@Override
	public void decorate(SymbolTable symtab) throws Exception {
		duration.decorate(symtab);
		if (duration.type != Types.FLOAT && duration.type != Types.INT)
			throw new Exception("Invalid OscillatorExpression");
		amplitude.decorate(symtab);
		if (amplitude.type != Types.FLOAT && amplitude.type != Types.INT && amplitude.type != Types.ARRAY)
			throw new Exception("Invalid OscillatorExpression");
		frequency.decorate(symtab);
		if (frequency.type != Types.FLOAT && frequency.type != Types.INT && frequency.type != Types.ARRAY)
			throw new Exception("Invalid OscillatorExpression");
		shape.decorate(symtab);
		if (shape.type != Types.WAVEFORM) throw new Exception("Invalid OscillatorExpression");
	}

	@Override
	public void generate(MethodVisitor mv, SymbolTable symtab) throws Exception {
		mv.visitTypeInsn(NEW, "framework/utilities/WavetableOscillatorUtility");
		mv.visitInsn(DUP);
		mv.visitMethodInsn(INVOKESPECIAL, "framework/utilities/WavetableOscillatorUtility", "<init>", "()V", false);
		duration.generate(mv, symtab);
		if (duration.type == Types.FLOAT) mv.visitInsn(F2I);
		String type = "(I";
		amplitude.generate(mv, symtab);
		if (amplitude.type == Types.INT) mv.visitInsn(I2F);
		if (amplitude.type == Types.ARRAY) type += "[";
		type += "F";
		frequency.generate(mv, symtab);
		if (frequency.type == Types.INT) mv.visitInsn(I2F);
		if (frequency.type == Types.ARRAY) type += "[";
		type += "F";
		shape.generate(mv, symtab);
		type += "I)[F";
		mv.visitMethodInsn(INVOKEVIRTUAL, "framework/utilities/WavetableOscillatorUtility", "get", type, false);
	}

}
