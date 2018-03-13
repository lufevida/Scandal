package language.tree.expression;

import java.util.ArrayList;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

import language.compiler.SymbolTable;
import language.compiler.Token;
import language.tree.ParamDeclaration;

public class FuncLitComp extends FuncLitExpression {
	
	public final FuncCompExpression comp;

	public FuncLitComp(Token firstToken, ArrayList<ParamDeclaration> params, FuncCompExpression comp) {
		super(firstToken, params, comp);
		this.comp = comp;
	}
	
	@Override
	public void generate(MethodVisitor mv, SymbolTable symtab) throws Exception {
		mv.visitVarInsn(ALOAD, 0);
		for (IdentExpression id : comp.idents) {
			mv.visitVarInsn(ALOAD, 0);
			mv.visitFieldInsn(GETFIELD, symtab.className, id.firstToken.text, symtab.lambdas.get(id.firstToken.text).getInvocation());
		}
		addInvocation(mv, symtab);
	}
	
	@Override
	public void generate(ClassWriter cw, SymbolTable symtab) throws Exception {
		MethodVisitor mv = cw.visitMethod(ACC_PRIVATE + ACC_STATIC + ACC_SYNTHETIC, "lambda$" + lambdaSlot, getReturnSignature(), null, null);
		mv.visitCode();
		Label startLabel = new Label();
		Label endLabel = new Label();
		mv.visitLabel(startLabel);
		mv.visitLabel(endLabel);
		comp.isReturnExpression = true;
		mv.visitLocalVariable(params.get(0).identToken.text, params.get(0).getJvmType(), null, startLabel, endLabel, comp.idents.size());
		mv.visitVarInsn(ALOAD, 0);
		for (int i = 1; i < comp.idents.size(); i++) {
			mv.visitVarInsn(ALOAD, i);
			mv.visitMethodInsn(INVOKEINTERFACE, "language/interfaces/FloatFloat", "then", "(Llanguage/interfaces/FloatFloat;)Llanguage/interfaces/FloatFloat;", true);
		}
		mv.visitVarInsn(FLOAD, comp.idents.size());
		mv.visitMethodInsn(INVOKEINTERFACE, "language/interfaces/FloatFloat", "apply", "(F)F", true);
		mv.visitInsn(FRETURN);
		mv.visitMaxs(0, 0);
		mv.visitEnd();
	}
	
	@Override
	public String getInputSignature() {
		String sig = "(";
		for (int i = 0; i< comp.idents.size(); i++) sig += getInvocation();
		sig += ")" + getInvocation();
		return sig;
	}
	
	@Override
	public String getReturnSignature() {
		String sig = "(";
		for (int i = 0; i< comp.idents.size(); i++) sig += getInvocation();
		sig += "F)F";
		return sig;
	}

}
