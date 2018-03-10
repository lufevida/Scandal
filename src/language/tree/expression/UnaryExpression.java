package language.tree.expression;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import language.compiler.SymbolTable;
import language.compiler.Token;

public class UnaryExpression extends Expression {
	
	public final Expression e;

	public UnaryExpression(Token firstToken, Expression expression) {
		super(firstToken);
		e = expression;
	}
	
	@Override
	public void decorate(SymbolTable symtab) throws Exception {
		e.decorate(symtab);
		if (e.type != Types.INT && e.type != Types.FLOAT && e.type != Types.BOOL)
			throw new Exception("Invalid UnaryExpression");
		type = e.type;
	}

	@Override
	public void generate(MethodVisitor mv, SymbolTable symtab) throws Exception {
		switch (type) {
		case INT: {
			e.generate(mv, symtab);
			mv.visitInsn(INEG);
		} break;
		case FLOAT: {
			e.generate(mv, symtab);
			mv.visitInsn(FNEG);
		} break;
		default: {
			Label l1 = new Label();
			Label l2 = new Label();
			e.generate(mv, symtab);
			mv.visitJumpInsn(IFEQ, l1);
			mv.visitInsn(ICONST_0);
			mv.visitJumpInsn(GOTO, l2);
			mv.visitLabel(l1);
			mv.visitFrame(Opcodes.F_APPEND, 2, new Object[] {"org/objectweb/asm/MethodVisitor", Opcodes.INTEGER}, 0, null);
			mv.visitInsn(ICONST_1);
			mv.visitLabel(l2);
			mv.visitFrame(Opcodes.F_SAME1, 0, null, 1, new Object[] {Opcodes.INTEGER});
		} break;
		}
	}

}
