package language.tree.statement;

import org.objectweb.asm.MethodVisitor;

import javafx.application.Platform;
import language.compiler.SymbolTable;
import language.compiler.Token;
import language.tree.expression.Expression;

public class PlayStatement extends Statement {
	
	public final Expression channels;

	public PlayStatement(Token firstToken, Expression expression, Expression channels) {
		super(firstToken, expression);
		this.channels = channels;
	}
	
	@Override
	public void decorate(SymbolTable symtab) throws Exception {
		expression.decorate(symtab);
		if (expression.type != Types.ARRAY) throw new Exception();
		channels.decorate(symtab);
		if (channels.type != Types.INT) throw new Exception();
	}

	@Override
	public void generate(MethodVisitor mv, SymbolTable symtab) throws Exception {
		if (!Platform.isFxApplicationThread()) {
			mv.visitTypeInsn(NEW, "framework/generators/AudioTask");
			mv.visitInsn(DUP);
			mv.visitMethodInsn(INVOKESPECIAL, "framework/generators/AudioTask", "<init>", "()V", false);
			expression.generate(mv, symtab);
			channels.generate(mv, symtab);
			mv.visitMethodInsn(INVOKEVIRTUAL, "framework/generators/AudioTask", "play", "([FI)V", false);
			return;
		}
		mv.visitTypeInsn(NEW, "language/ide/WaveTab");
		mv.visitLdcInsn(symtab.className);
		expression.generate(mv, symtab);
		channels.generate(mv, symtab);
		mv.visitMethodInsn(INVOKESPECIAL, "language/ide/WaveTab", "<init>", "(Ljava/lang/String;[FI)V", false);		
	}

}
