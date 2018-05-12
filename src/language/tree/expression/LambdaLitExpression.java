package language.tree.expression;

import java.util.ArrayList;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Handle;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import language.compiler.SymbolTable;
import language.compiler.Token;
import language.tree.LambdaLitDeclaration;
import language.tree.ParamDeclaration;

public class LambdaLitExpression extends Expression {
	
	public final ArrayList<ParamDeclaration> params;
	public final Expression returnExpression;
	public LambdaLitDeclaration dec;

	public LambdaLitExpression(Token firstToken, ArrayList<ParamDeclaration> params, Expression returnExpression) {
		super(firstToken);
		this.params = params;
		this.returnExpression = returnExpression;
	}

	public void decorate(SymbolTable symtab) throws Exception {
		symtab.enterScope();
		for (int i = 0; i < params.size(); i++) {
			params.get(i).wrap = true;
			params.get(i).slotNumber = i;
			params.get(i).decorate(symtab);
		}
		returnExpression.decorate(symtab);
		symtab.leaveScope();
	}

	public void generate(MethodVisitor mv, SymbolTable symtab) throws Exception {}
	
	public void generate(ClassWriter cw, SymbolTable symtab) throws Exception {
		MethodVisitor mv;
		for (int i = 0; i < params.size(); i++) {
			mv = cw.visitMethod(ACC_PRIVATE + ACC_STATIC + ACC_SYNTHETIC, "lambda$" + (dec.lambdaSlot + i), getSig(i), null, null);
			mv.visitCode();
			if (i == params.size() - 1) {
				returnExpression.generate(mv, symtab);
				switch (returnExpression.type) {
				case INT:
					mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
					break;
				case BOOL:
					mv.visitMethodInsn(INVOKESTATIC, "java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;", false);
					break;
				case FLOAT:
					mv.visitMethodInsn(INVOKESTATIC, "java/lang/Float", "valueOf", "(F)Ljava/lang/Float;", false);
					break;
				default: break;
				}
			}
			else {
				for (int j = 0; j <= i; j++) mv.visitVarInsn(ALOAD, j);
				mv.visitInvokeDynamicInsn("apply", getSig(i), dec.getHandle(), getObjects(symtab, dec.lambdaSlot, i + 1));
			}
			mv.visitInsn(ARETURN);
			mv.visitMaxs(0, 0);
			mv.visitEnd();
		}
	}
	
	Object[] getObjects(SymbolTable symtab, int slot, int i) {
		Object[] objs = new Object[3];
		objs[0] = Type.getType("(Ljava/lang/Object;)Ljava/lang/Object;");
		objs[1] = new Handle(Opcodes.H_INVOKESTATIC, symtab.className, "lambda$" + (slot + i), getSig(i), false);
		objs[2] = Type.getType(getInOutSig(i));
		return objs;
	}
	
	String getSig(int i) {
		String sig = "(";
		for (int j = 0; j <= i; j++) sig += params.get(j).getClassType();
		if (i == params.size() - 1) return sig + ")" + returnExpression.getClassType();
		return sig + ")" + dec.getJvmType();
	}
	
	String getInOutSig(int i) {
		String sig = "(" + params.get(i).getClassType();
		if (i == params.size() - 1) return sig + ")" + returnExpression.getClassType();
		return sig + ")" + dec.getJvmType();
	}

}