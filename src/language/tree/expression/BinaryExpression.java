package language.tree.expression;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

import language.compiler.SymbolTable;
import language.compiler.Token;
import language.tree.Declaration;
import language.tree.ParamDeclaration;

public class BinaryExpression extends Expression {

	public final Expression e0;
	public final Token op;
	public final Expression e1;

	public BinaryExpression(Token firstToken, Expression e0, Token operator, Expression e1) {
		super(firstToken);
		this.e0 = e0;
		this.op = operator;
		this.e1 = e1;
	}

	public void decorate(SymbolTable symtab) throws Exception {
		e0.decorate(symtab);
		e1.decorate(symtab);
		if ((e0.type == Types.INT || e0.type == Types.FLOAT) && (e1.type == Types.INT || e1.type == Types.FLOAT)) {
			switch(op.kind) {
			case MOD:
			case PLUS:
			case MINUS:
			case TIMES:
			case DIV:
				if (e0.type == Types.INT && e1.type == Types.INT) type = Types.INT;
				else type = Types.FLOAT;
				break;
			default: break;
			}
		}
		if ((e0.type == Types.INT || e0.type == Types.FLOAT || e0.type == Types.BOOL) && (e1.type == Types.INT || e1.type == Types.FLOAT || e1.type == Types.BOOL)) {
			switch(op.kind) {
			case AND:
			case OR:
			case LT:
			case LE:
			case GT:
			case GE:
			case EQUAL:
			case NOTEQUAL:
				type = Types.BOOL;
				break;
			default: break;
			}
		}
		if (e0 instanceof LambdaAppExpression) {
			LambdaAppExpression app = (LambdaAppExpression) e0;
			Declaration dec = app.lambda.declaration;
			if (dec instanceof ParamDeclaration) throw new Exception();
		}
		if (e1 instanceof LambdaAppExpression) {
			LambdaAppExpression app = (LambdaAppExpression) e1;
			Declaration dec = app.lambda.declaration;
			if (dec instanceof ParamDeclaration) throw new Exception();
		}
		if (type == null) throw new Exception("Invalid BinaryExpression");
	}

	public void generate(MethodVisitor mv, SymbolTable symtab) throws Exception {
		e0.generate(mv, symtab);
		switch (op.kind) {
		case MOD:
			if (type == Types.FLOAT) {
				castToFloat(mv, symtab);
				mv.visitInsn(FREM);
			}
			else {
				e1.generate(mv, symtab);
				mv.visitInsn(IREM);
			}
			break;
		case PLUS:
			if (type == Types.FLOAT) {
				castToFloat(mv, symtab);
				mv.visitInsn(FADD);
			}
			else {
				e1.generate(mv, symtab);
				mv.visitInsn(IADD);
			}
			break;
		case MINUS:
			if (type == Types.FLOAT) {
				castToFloat(mv, symtab);
				mv.visitInsn(FSUB);
			}
			else {
				e1.generate(mv, symtab);
				mv.visitInsn(ISUB);
			}
			break;
		case TIMES:
			if (type == Types.FLOAT) {
				castToFloat(mv, symtab);
				mv.visitInsn(FMUL);
			}
			else {
				e1.generate(mv, symtab);
				mv.visitInsn(IMUL);
			}
			break;
		case DIV:
			if (type == Types.FLOAT) {
				castToFloat(mv, symtab);
				mv.visitInsn(FDIV);
			}
			else {
				e1.generate(mv, symtab);
				mv.visitInsn(IDIV);
			}
			break;
		case AND:
			if (e0.type == Types.FLOAT) mv.visitInsn(F2I);
			e1.generate(mv, symtab);
			if (e1.type == Types.FLOAT) mv.visitInsn(F2I);
			mv.visitInsn(IAND);
			break;
		case OR:
			if (e0.type == Types.FLOAT) mv.visitInsn(F2I);
			e1.generate(mv, symtab);
			if (e1.type == Types.FLOAT) mv.visitInsn(F2I);
			mv.visitInsn(IOR);
			break;
		default:
			castToFloat(mv, symtab);
			Label l1 = new Label();
			switch (op.kind) {
			case LT:
				mv.visitInsn(FCMPG);
				mv.visitJumpInsn(IFGE, l1);
				break;
			case LE:
				mv.visitInsn(FCMPG);
				mv.visitJumpInsn(IFGT, l1);
				break;
			case GT:
				mv.visitInsn(FCMPL);
				mv.visitJumpInsn(IFLE, l1);
				break;
			case GE:
				mv.visitInsn(FCMPL);
				mv.visitJumpInsn(IFLT, l1);
				break;
			case EQUAL:
				mv.visitInsn(FCMPL);
				mv.visitJumpInsn(IFNE, l1);
				break;
			case NOTEQUAL:
				mv.visitInsn(FCMPL);
				mv.visitJumpInsn(IFEQ, l1);
				break;
			default: break;
			}
			mv.visitInsn(ICONST_1);
			Label l2 = new Label();
			mv.visitJumpInsn(GOTO, l2);
			mv.visitLabel(l1);
			mv.visitInsn(ICONST_0);
			mv.visitLabel(l2);
			break;
		}
	}
	
	private void castToFloat(MethodVisitor mv, SymbolTable symtab) throws Exception {
		if (e0.type == Types.INT || e0.type == Types.BOOL) mv.visitInsn(I2F);
		e1.generate(mv, symtab);
		if (e1.type == Types.INT || e1.type == Types.BOOL) mv.visitInsn(I2F);
	}

}
