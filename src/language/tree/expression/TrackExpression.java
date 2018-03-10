package language.tree.expression;

import org.objectweb.asm.MethodVisitor;

import language.compiler.SymbolTable;
import language.compiler.Token;

public class TrackExpression extends Expression {
	
	public final Expression array;
	public final Expression start;
	public final Expression gain;
	public final Expression pan;

	public TrackExpression(Token firstToken, Expression array, Expression start, Expression gain, Expression pan) {
		super(firstToken);
		this.array = array;
		this.start = start;
		this.gain = gain;
		this.pan = pan;
		this.type = Types.ARRAY;
	}
	
	@Override
	public void decorate(SymbolTable symtab) throws Exception {
		array.decorate(symtab);
		if (array.type != Types.ARRAY) throw new Exception("Invalid TrackExpression");
		start.decorate(symtab);
		if (start.type != Types.INT && start.type != Types.FLOAT) throw new Exception("Invalid TrackExpression");
		gain.decorate(symtab);
		if (gain.type != Types.INT && gain.type != Types.FLOAT && gain.type != Types.ARRAY) throw new Exception("Invalid TrackExpression");
		pan.decorate(symtab);
		if (pan.type != Types.INT && pan.type != Types.FLOAT && pan.type != Types.ARRAY) throw new Exception("Invalid TrackExpression");
	}

	@Override
	public void generate(MethodVisitor mv, SymbolTable symtab) throws Exception {
		mv.visitTypeInsn(NEW, "framework/generators/AudioTrack");
		mv.visitInsn(DUP);
		array.generate(mv, symtab);
		String type = "([FI";
		start.generate(mv, symtab);
		if (start.type == Types.FLOAT) mv.visitInsn(F2I);
		gain.generate(mv, symtab);
		if (gain.type == Types.INT) mv.visitInsn(I2F);
		if (gain.type == Types.ARRAY) type += "[";
		type += "F";
		pan.generate(mv, symtab);
		if (pan.type == Types.INT) mv.visitInsn(I2F);
		if (pan.type == Types.ARRAY) type += "[";
		type += "F)V";
		mv.visitMethodInsn(INVOKESPECIAL, "framework/generators/AudioTrack", "<init>", type, false);
		mv.visitMethodInsn(INVOKEVIRTUAL, "framework/generators/AudioTrack", "getShiftedVector", "()[F", false);
	}

}
