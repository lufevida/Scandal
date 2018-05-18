package language.tree.expression;

import org.objectweb.asm.MethodVisitor;

import language.compiler.Token;
import language.tree.Node;

public abstract class Expression extends Node {

	public Expression(Token firstToken) {
		super(firstToken);
	}
	
	public static void getTypeValue(Types inType, MethodVisitor mv) {
		switch (inType) {
		case INT:
			mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Integer", "intValue", "()I", false);
			return;
		case BOOL:
			mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Boolean", "booleanValue", "()Z", false);
			return;
		case FLOAT:
			mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Float", "floatValue", "()F", false);
			return;
		default: return;
		}
	}

	public static void getValueOf(Types inType, MethodVisitor mv) {
		switch (inType) {
		case INT:
			mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
			return;
		case BOOL:
			mv.visitMethodInsn(INVOKESTATIC, "java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;", false);
			return;
		case FLOAT:
			mv.visitMethodInsn(INVOKESTATIC, "java/lang/Float", "valueOf", "(F)Ljava/lang/Float;", false);
			return;
		default: return;
		}
	}
	
	public static void getCheckCast(Types inType, MethodVisitor mv) {
		switch (inType) {
		case INT:
			mv.visitTypeInsn(CHECKCAST, "java/lang/Integer");
			return;
		case BOOL:
			mv.visitTypeInsn(CHECKCAST, "java/lang/Boolean");
			return;
		case FLOAT:
			mv.visitTypeInsn(CHECKCAST, "java/lang/Float");
			return;
		case ARRAY:
			mv.visitTypeInsn(CHECKCAST, "[F");
			return;
		case STRING:
			mv.visitTypeInsn(CHECKCAST, "java/lang/String");
			return;
		case LAMBDA:
			mv.visitTypeInsn(CHECKCAST, "java/util/function/Function");
			return;
		default: return;
		}
	}

}
