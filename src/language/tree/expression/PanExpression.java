package language.tree.expression;

import org.objectweb.asm.MethodVisitor;

import language.compiler.SymbolTable;
import language.compiler.Token;

public class PanExpression extends Expression {
	
	public final Expression array;
	public final Expression position;

	public PanExpression(Token firstToken, Expression array, Expression position) {
		super(firstToken);
		this.array = array;
		this.position = position;
		this.type = Types.ARRAY;
	}
	
	@Override
	public void decorate(SymbolTable symtab) throws Exception {
		array.decorate(symtab);
		if (array.type != Types.ARRAY) throw new Exception("Invalid PanExpression");
		position.decorate(symtab);
		if (array.type != Types.INT && array.type != Types.FLOAT && array.type != Types.ARRAY)
			throw new Exception("Invalid PanExpression");
	}

	@Override
	public void generate(MethodVisitor mv, SymbolTable symtab) throws Exception {
		mv.visitTypeInsn(NEW, "framework/generators/StereoPanner");
		mv.visitInsn(DUP);
		mv.visitMethodInsn(INVOKESPECIAL, "framework/generators/StereoPanner", "<init>", "()V", false);
		array.generate(mv, symtab);
		String type = "([F";
		position.generate(mv, symtab);
		if (position.type == Types.INT) mv.visitInsn(I2F);
		if (position.type == Types.ARRAY) type += "[";
		type += "F)[F";
		mv.visitMethodInsn(INVOKEVIRTUAL, "framework/generators/StereoPanner", "process", type, false);
	}

}
