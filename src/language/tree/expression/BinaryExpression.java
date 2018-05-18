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
	
	@Override
	public void decorate(SymbolTable symtab) throws Exception {
		e0.decorate(symtab);
		e1.decorate(symtab);
		if ((e0.type == Types.INT || e0.type == Types.FLOAT) && (e1.type == Types.INT || e1.type == Types.FLOAT)) {
			switch(op.kind) {
			case MOD:
			case PLUS:
			case MINUS:
			case TIMES:
			case DIV: {
				if (e0.type == Types.INT && e1.type == Types.INT) type = Types.INT;
				else type = Types.FLOAT;
				break;
			}
			default: break;
			}
		}
		if ((e0.type == Types.INT || e0.type == Types.BOOL) && (e1.type == Types.INT || e1.type == Types.BOOL)) {
			switch(op.kind) {
			case AND:
			case OR: {
				type = Types.BOOL;
			} break;
			default: break;
			}
		}
		if ((e0.type == Types.INT || e0.type == Types.FLOAT || e0.type == Types.BOOL) && (e1.type == Types.INT || e1.type == Types.FLOAT || e1.type == Types.BOOL)) {
			switch(op.kind) {
			case LT:
			case LE:
			case GT:
			case GE:
			case EQUAL:
			case NOTEQUAL: {
				type = Types.BOOL;
			} break;
			default: break;
			}
		}
		if (e0 instanceof LambdaAppExpression) {
			LambdaAppExpression app = (LambdaAppExpression) e0;
			Declaration dec = app.lambda.declaration;
			if (dec instanceof ParamDeclaration) throw new Exception("Lambdas cannot be used before declared.");
		}
		if (e1 instanceof LambdaAppExpression) {
			LambdaAppExpression app = (LambdaAppExpression) e1;
			Declaration dec = app.lambda.declaration;
			if (dec instanceof ParamDeclaration) throw new Exception("Lambdas cannot be used before declared.");
		}
		if (type == null) throw new Exception("Invalid BinaryExpression");
	}

	@Override
	public void generate(MethodVisitor mv, SymbolTable symtab) throws Exception {
		e0.generate(mv, symtab);
		switch (op.kind) {
		case MOD: {
			if (type == Types.FLOAT) {
				if (e0.type == Types.INT) mv.visitInsn(I2F);
				e1.generate(mv, symtab);
				if (e1.type == Types.INT) mv.visitInsn(I2F);
				mv.visitInsn(FREM);
			}
			else {
				if (e0.type == Types.FLOAT) mv.visitInsn(F2I);
				e1.generate(mv, symtab);
				if (e1.type == Types.FLOAT) mv.visitInsn(F2I);
				mv.visitInsn(IREM);
			}
		} break;
		case PLUS: {
			if (type == Types.FLOAT) {
				if (e0.type == Types.INT) mv.visitInsn(I2F);
				e1.generate(mv, symtab);
				if (e1.type == Types.INT) mv.visitInsn(I2F);
				mv.visitInsn(FADD);
			}
			else {
				if (e0.type == Types.FLOAT) mv.visitInsn(F2I);
				e1.generate(mv, symtab);
				if (e1.type == Types.FLOAT) mv.visitInsn(F2I);
				mv.visitInsn(IADD);
			}
		} break;
		case MINUS: {
			if (type == Types.FLOAT) {
				if (e0.type == Types.INT) mv.visitInsn(I2F);
				e1.generate(mv, symtab);
				if (e1.type == Types.INT) mv.visitInsn(I2F);
				mv.visitInsn(FSUB);
			}
			else {
				if (e0.type == Types.FLOAT) mv.visitInsn(F2I);
				e1.generate(mv, symtab);
				if (e1.type == Types.FLOAT) mv.visitInsn(F2I);
				mv.visitInsn(ISUB);
			}
		} break;
		case TIMES: {
			if (type == Types.FLOAT) {
				if (e0.type == Types.INT) mv.visitInsn(I2F);
				e1.generate(mv, symtab);
				if (e1.type == Types.INT) mv.visitInsn(I2F);
				mv.visitInsn(FMUL);
			}
			else {
				if (e0.type == Types.FLOAT) mv.visitInsn(F2I);
				e1.generate(mv, symtab);
				if (e1.type == Types.FLOAT) mv.visitInsn(F2I);
				mv.visitInsn(IMUL);
			}
		} break;
		case DIV: {
			if (type == Types.FLOAT) {
				if (e0.type == Types.INT) mv.visitInsn(I2F);
				e1.generate(mv, symtab);
				if (e1.type == Types.INT) mv.visitInsn(I2F);
				mv.visitInsn(FDIV);
			}
			else {
				if (e0.type == Types.FLOAT) mv.visitInsn(F2I);
				e1.generate(mv, symtab);
				if (e1.type == Types.FLOAT) mv.visitInsn(F2I);
				mv.visitInsn(IDIV);
			}
		} break;
		case AND: {
			e1.generate(mv, symtab);
			mv.visitInsn(IAND);
		} break;
		case OR: {
			e1.generate(mv, symtab);
			mv.visitInsn(IOR);
		} break;
		default: {
			e1.generate(mv, symtab);
			Label l1 = new Label();
			switch (op.kind) {
			case LT: {
				if (e0.type == Types.FLOAT && e1.type == Types.FLOAT) {
					mv.visitInsn(FCMPG);
					mv.visitJumpInsn(IFGE, l1);
				}
				else mv.visitJumpInsn(IF_ICMPGE, l1);
			} break;
			case LE: {
				if (e0.type == Types.FLOAT && e1.type == Types.FLOAT) {
					mv.visitInsn(FCMPG);
					mv.visitJumpInsn(IFGT, l1);
				}
				else mv.visitJumpInsn(IF_ICMPGT, l1);
			} break;
			case GT: {
				if (e0.type == Types.FLOAT && e1.type == Types.FLOAT) {
					mv.visitInsn(FCMPL);
					mv.visitJumpInsn(IFLE, l1);
				}
				else mv.visitJumpInsn(IF_ICMPLE, l1);
			} break;
			case GE: {
				if (e0.type == Types.FLOAT && e1.type == Types.FLOAT) {
					mv.visitInsn(FCMPL);
					mv.visitJumpInsn(IFLT, l1);
				}
				else mv.visitJumpInsn(IF_ICMPLT, l1);
			} break;
			case EQUAL: {
				if (e0.type == Types.FLOAT && e1.type == Types.FLOAT) {
					mv.visitInsn(FCMPL);
					mv.visitJumpInsn(IFNE, l1);
				}
				else mv.visitJumpInsn(IF_ICMPNE, l1);
			} break;
			case NOTEQUAL: {
				if (e0.type == Types.FLOAT && e1.type == Types.FLOAT) {
					mv.visitInsn(FCMPL);
					mv.visitJumpInsn(IFEQ, l1);
				}
				else mv.visitJumpInsn(IF_ICMPEQ, l1);
			} break;
			default: break;
			}
			mv.visitInsn(ICONST_1);
			Label l2 = new Label();
			mv.visitJumpInsn(GOTO, l2);
			mv.visitLabel(l1);
			mv.visitInsn(ICONST_0);
			mv.visitLabel(l2);
		} break;
		}
	}

}
