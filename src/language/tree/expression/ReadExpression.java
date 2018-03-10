package language.tree.expression;

import org.objectweb.asm.MethodVisitor;

import language.compiler.SymbolTable;
import language.compiler.Token;

public class ReadExpression extends Expression {
	
	public final Expression fileName;
	public final Expression format;

	public ReadExpression(Token firstToken, Expression fileName, Expression format) {
		super(firstToken);
		this.fileName = fileName;
		this.format = format;
		this.type = Types.ARRAY;
	}
	
	@Override
	public void decorate(SymbolTable symtab) throws Exception {
		fileName.decorate(symtab);
		if (fileName.type != Types.STRING) throw new Exception();
		format.decorate(symtab);
		if (format.type != Types.INT) throw new Exception();
	}

	@Override
	public void generate(MethodVisitor mv, SymbolTable symtab) throws Exception {
		mv.visitTypeInsn(NEW, "framework/generators/WaveFile");
		mv.visitInsn(DUP);
		fileName.generate(mv, symtab);
		mv.visitMethodInsn(INVOKESPECIAL, "framework/generators/WaveFile", "<init>", "(Ljava/lang/String;)V", false);
		format.generate(mv, symtab);
		mv.visitMethodInsn(INVOKEVIRTUAL, "framework/generators/WaveFile", "get", "(I)[F", false);
	}

}
