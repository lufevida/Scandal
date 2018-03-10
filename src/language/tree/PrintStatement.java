package language.tree;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import language.compiler.SymbolTable;
import language.compiler.Token;
import language.tree.expression.Expression;

public class PrintStatement extends Statement implements Opcodes {

	public PrintStatement(Token firstToken, Expression expression) {
		super(firstToken, expression);
	}
	
	@Override
	public void decorate(SymbolTable symtab) throws Exception {
		expression.decorate(symtab);
		if (expression.type == Types.ARRAY) throw new Exception();
	}

	@Override
	public void generate(MethodVisitor mv, SymbolTable symtab) throws Exception {
		mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
		expression.generate(mv, symtab);
		mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(" + expression.getJvmType() + ")V", false);
	}

}
