package language.tree.statement;

import org.objectweb.asm.MethodVisitor;

import javafx.application.Platform;
import language.compiler.SymbolTable;
import language.compiler.Token;
import language.tree.expression.Expression;

public class PrintStatement extends Statement {

	public PrintStatement(Token firstToken, Expression expression) {
		super(firstToken, expression);
	}
	
	@Override
	public void decorate(SymbolTable symtab) throws Exception {
		expression.decorate(symtab);
		if (expression.type == Types.ARRAY || expression.type == Types.LAMBDA) throw new Exception();
	}

	@Override
	public void generate(MethodVisitor mv, SymbolTable symtab) throws Exception {
		if (!Platform.isFxApplicationThread()) {
			mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
			expression.generate(mv, symtab);
			mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(" + expression.getJvmType() + ")V", false);
			return;
		}
		mv.visitFieldInsn(GETSTATIC, "language/ide/MainView", "console", "Ljavafx/scene/control/TextArea;");
		mv.visitInsn(DUP);
		expression.generate(mv, symtab);
		if (expression.type != Types.STRING)
			mv.visitMethodInsn(INVOKESTATIC, "java/lang/String", "valueOf", "(" + expression.getJvmType() + ")Ljava/lang/String;", false);
		mv.visitMethodInsn(INVOKEVIRTUAL, "javafx/scene/control/TextArea", "appendText", "(Ljava/lang/String;)V", false);
		mv.visitLdcInsn("\n");
		mv.visitMethodInsn(INVOKEVIRTUAL, "javafx/scene/control/TextArea", "appendText", "(Ljava/lang/String;)V", false);
	}

}
