package language.tree.statement;

import org.objectweb.asm.MethodVisitor;

import language.compiler.SymbolTable;
import language.compiler.Token;
import language.tree.expression.Expression;

public class WriteStatement extends Statement {
	
	public final Expression name;
	public final Expression format;

	public WriteStatement(Token firstToken, Expression array, Expression name, Expression format) {
		super(firstToken, array);
		this.name = name;
		this.format = format;
	}
	
	@Override
	public void decorate(SymbolTable symtab) throws Exception {
		expression.decorate(symtab);
		if (expression.type != Types.ARRAY) throw new Exception();
		name.decorate(symtab);
		if (name.type != Types.STRING) throw new Exception();
		format.decorate(symtab);
		if (format.type != Types.INT) throw new Exception();
	}

	@Override
	public void generate(MethodVisitor mv, SymbolTable symtab) throws Exception {
		mv.visitTypeInsn(NEW, "framework/generators/AudioTask");
		mv.visitInsn(DUP);
		mv.visitMethodInsn(INVOKESPECIAL, "framework/generators/AudioTask", "<init>", "()V", false);
		expression.generate(mv, symtab);
		name.generate(mv, symtab);
		format.generate(mv, symtab);
		mv.visitMethodInsn(INVOKEVIRTUAL, "framework/generators/AudioTask", "export", "([FLjava/lang/String;I)V", false);
	}

}
