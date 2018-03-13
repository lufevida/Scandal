package language.tree.expression;

import java.util.ArrayList;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import language.compiler.SymbolTable;
import language.compiler.Token;
import language.tree.Node;
import language.tree.ParamDeclaration;

public class FuncLitExpression extends Expression {
	
	public final boolean isAbstract;
	public final ArrayList<ParamDeclaration> params;
	public final Expression returnExpression;
	protected Types inputType;
	protected Types returnType;
	public boolean capturesSelf = false;
	public String classSig = "";
	public int lambdaSlot;

	public FuncLitExpression(Token firstToken, ArrayList<ParamDeclaration> params, Expression returnExpression) {
		super(firstToken);
		this.isAbstract = params.size() > 1;
		this.params = params;
		this.returnExpression = returnExpression;
	}
	
	@Override
	public void decorate(SymbolTable symtab) throws Exception {
		symtab.enterScope();
		for (ParamDeclaration param : params) param.decorate(symtab);
		inputType = params.get(0).type;
		returnExpression.decorate(symtab);
		returnType = returnExpression.type;
		this.type = Node.getLambdaType(inputType, returnType);
		symtab.leaveScope();
	}
	
	@Override
	public void generate(MethodVisitor mv, SymbolTable symtab) throws Exception {
		mv.visitVarInsn(ALOAD, 0);
		addInvocation(mv, symtab);
	}
	
	public void generate(ClassWriter cw, SymbolTable symtab) throws Exception {
		MethodVisitor mv = cw.visitMethod(ACC_PRIVATE + ACC_STATIC + ACC_SYNTHETIC, "lambda$" + lambdaSlot, getReturnSignature(), null, null);
		mv.visitCode();
		Label startLabel = new Label();
		Label endLabel = new Label();
		mv.visitLabel(startLabel);
		mv.visitLabel(endLabel);
		params.get(0).slotNumber = 0;
		if (capturesSelf) params.get(0).slotNumber = 1;
		mv.visitLocalVariable(params.get(0).identToken.text, params.get(0).getJvmType(), null, startLabel, endLabel, params.get(0).slotNumber);
		returnExpression.isReturnExpression = true;
		returnExpression.generate(mv, symtab);
		if (returnExpression.type == Types.FLOAT) mv.visitInsn(FRETURN);
		else if (returnExpression.type == Types.ARRAY) mv.visitInsn(ARETURN);
		mv.visitMaxs(0, 0);
		mv.visitEnd();
	}
	
	protected void addInvocation(MethodVisitor mv, SymbolTable symtab) {
		String i = getInputSignature();
		String s = getSignature();
		String returnSig = getReturnSignature();
		Handle h = new Handle(Opcodes.H_INVOKESTATIC, symtab.className, "lambda$" + lambdaSlot, returnSig, false);
		mv.visitInvokeDynamicInsn("apply", i, getMetafactoryHandle(), new Object[]{Type.getType(s), h, Type.getType(s)});
	}
	
	public String getInputSignature() {
		if (capturesSelf) return "(" + classSig + ")" + getInvocation();
		return "()" + getInvocation();
	}
	
	public String getReturnSignature() {
		if (capturesSelf) return getCaptureSignature();
		return getSignature();
	}
	
	public String getInvocation() {
		return "L" + getInterface() + ";";
	}
	
	public String getInterface() {
		switch (type) {
		case FLOAT_FLOAT:
			return "language/interfaces/FloatFloat";
		case FLOAT_ARRAY:
			return "language/interfaces/FloatArray";
		case ARRAY_FLOAT:
			return "language/interfaces/ArrayFloat";
		default:
			return "language/interfaces/ArrayArray";
		}
	}
	
	protected String getSignature() {
		switch (type) {
		case FLOAT_FLOAT:
			return "(F)F";
		case FLOAT_ARRAY:
			return "(F)[F";
		case ARRAY_FLOAT:
			return "([F)F";
		default:
			return "([F)[F";
		}
	}
	
	protected String getCaptureSignature() {
		switch (type) {
		case FLOAT_FLOAT:
			return "(" + classSig + "F)F";
		case FLOAT_ARRAY:
			return "(" + classSig + "F)[F";
		case ARRAY_FLOAT:
			return "(" + classSig + "[F)F";
		default:
			return "(" + classSig + "[F)[F";
		}
	}

	private Handle getMetafactoryHandle() {
		String signature = "(Ljava/lang/invoke/MethodHandles$Lookup;";
		signature += "Ljava/lang/String;";
		signature += "Ljava/lang/invoke/MethodType;";
		signature += "Ljava/lang/invoke/MethodType;";
		signature += "Ljava/lang/invoke/MethodHandle;";
		signature += "Ljava/lang/invoke/MethodType;)";
		signature += "Ljava/lang/invoke/CallSite;";
		return new Handle(Opcodes.H_INVOKESTATIC, "java/lang/invoke/LambdaMetafactory", "metafactory", signature, false);
	}

}
