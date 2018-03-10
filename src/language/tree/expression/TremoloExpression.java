package language.tree.expression;

import org.objectweb.asm.MethodVisitor;

import language.compiler.SymbolTable;
import language.compiler.Token;

public class TremoloExpression extends Expression {
	
	public final Expression array;
	public final Expression depth;
	public final Expression speed;
	public final Expression shape;

	public TremoloExpression(Token firstToken, Expression array, Expression depth, Expression speed, Expression shape) {
		super(firstToken);
		this.array = array;
		this.depth = depth;
		this.speed = speed;
		this.shape = shape;
		this.type = Types.ARRAY;
	}
	
	@Override
	public void decorate(SymbolTable symtab) throws Exception {
		array.decorate(symtab);
		if (array.type != Types.ARRAY) throw new Exception("Invalid TremoloExpression");
		depth.decorate(symtab);
		if (depth.type != Types.INT && depth.type != Types.FLOAT && depth.type != Types.ARRAY)
			throw new Exception("Invalid TremoloExpression");
		speed.decorate(symtab);
		if (speed.type != Types.INT && speed.type != Types.FLOAT && speed.type != Types.ARRAY)
			throw new Exception("Invalid TremoloExpression");
		shape.decorate(symtab);
		if (shape.type != Types.WAVEFORM) throw new Exception("Invalid TremoloExpression");
	}

	@Override
	public void generate(MethodVisitor mv, SymbolTable symtab) throws Exception {
		mv.visitTypeInsn(NEW, "framework/utilities/RingModulatorUtility");
		mv.visitInsn(DUP);
		mv.visitMethodInsn(INVOKESPECIAL, "framework/utilities/RingModulatorUtility", "<init>", "()V", false);
		array.generate(mv, symtab);
		String type = "([F";
		depth.generate(mv, symtab);
		if (depth.type == Types.INT) mv.visitInsn(I2F);
		if (depth.type == Types.ARRAY) type += "[";
		type += "F";
		speed.generate(mv, symtab);
		if (speed.type == Types.INT) mv.visitInsn(I2F);
		if (speed.type == Types.ARRAY) type += "[";
		type += "F";
		shape.generate(mv, symtab);
		type += "I)[F";
		mv.visitMethodInsn(INVOKEVIRTUAL, "framework/utilities/RingModulatorUtility", "process", type, false);
	}

}
