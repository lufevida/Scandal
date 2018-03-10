package language.tree.expression;

import org.objectweb.asm.MethodVisitor;

import language.compiler.SymbolTable;
import language.compiler.Token;

public class RecordExpression extends Expression {
	
	public final Expression duration;

	public RecordExpression(Token firstToken, Expression duration) {
		super(firstToken);
		this.duration = duration;
		this.type = Types.ARRAY;
	}
	
	@Override
	public void decorate(SymbolTable symtab) throws Exception {
		duration.decorate(symtab);
		if (duration.type != Types.INT && duration.type != Types.FLOAT) throw new Exception("Invalid RecordExpression");
	}

	@Override
	public void generate(MethodVisitor mv, SymbolTable symtab) throws Exception {
		mv.visitTypeInsn(NEW, "framework/generators/AudioTask");
		mv.visitInsn(DUP);
		mv.visitMethodInsn(INVOKESPECIAL, "framework/generators/AudioTask", "<init>", "()V", false);
		duration.generate(mv, symtab);
		if (duration.type == Types.FLOAT) mv.visitInsn(F2I);
		mv.visitMethodInsn(INVOKEVIRTUAL, "framework/generators/AudioTask", "record", "(I)[F", false);
	}

}
