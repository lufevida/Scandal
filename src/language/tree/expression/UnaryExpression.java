package language.tree.expression;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

import language.compiler.SymbolTable;
import language.compiler.Token;
import language.compiler.Token.Kind;

public class UnaryExpression extends Expression {
	
	public final Expression e;

	public UnaryExpression(Token firstToken, Expression expression) {
		super(firstToken);
		e = expression;
	}
	
	@Override
	public void decorate(SymbolTable symtab) throws Exception {
		e.decorate(symtab);
		if (firstToken.kind == Kind.MINUS && (e.type != Types.INT || e.type != Types.FLOAT))
			throw new Exception("Invalid UnaryExpression");
		else if (firstToken.kind == Kind.OR && e.type != Types.BOOL)
			throw new Exception("Invalid UnaryExpression");
		type = e.type;
	}

	@Override
	public void generate(MethodVisitor mv, SymbolTable symtab) throws Exception {
		e.generate(mv, symtab);
		switch (type) {
		case INT:
			mv.visitInsn(INEG);
			break;
		case FLOAT:
			mv.visitInsn(FNEG);
			break;
		default:
			Label l1 = new Label();
			Label l2 = new Label();
			mv.visitJumpInsn(IFEQ, l1);
			mv.visitInsn(ICONST_0);
			mv.visitJumpInsn(GOTO, l2);
			mv.visitLabel(l1);
			mv.visitInsn(ICONST_1);
			mv.visitLabel(l2);
		}
	}

}
