package language.tree.expression;

import java.util.ArrayList;

import org.objectweb.asm.Handle;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import language.compiler.Lambda;
import language.compiler.SymbolTable;
import language.compiler.Token;
import language.tree.Node;
import language.tree.UnassignedDeclaration;

public class FuncLitExpression extends Expression {
	
	public final boolean isAbstract;
	public final ArrayList<UnassignedDeclaration> params;
	public final Expression returnExpression;
	protected Types inputType;
	protected Types returnType;
	public boolean capturesSelf = false;
	public String classSig = "";
	public int lambdaSlot;

	public FuncLitExpression(Token firstToken, ArrayList<UnassignedDeclaration> params, Expression returnExpression) {
		super(firstToken);
		this.isAbstract = params.size() > 1;
		this.params = params;
		this.returnExpression = returnExpression;
	}
	
	@Override
	public void decorate(SymbolTable symtab) throws Exception {
		symtab.enterScope();
		for (UnassignedDeclaration param : params) param.decorate(symtab);
		inputType = params.get(0).type;
		returnExpression.decorate(symtab);
		returnType = returnExpression.type;
		this.type = Node.getLambdaType(inputType, returnType);
		symtab.leaveScope();
	}
	
	@Override
	public void generate(MethodVisitor mv, SymbolTable symtab) throws Exception {
		//if (expression.capturesSelf)
		mv.visitVarInsn(ALOAD, 0);
		if (returnExpression.getClass() == FuncCompExpression.class) {
			for (IdentExpression id : ((FuncCompExpression) returnExpression).idents) {
				Lambda lambda = symtab.lambdaWithName(id.firstToken.text);
				mv.visitVarInsn(ALOAD, 0);
				mv.visitFieldInsn(GETFIELD, symtab.className, lambda.name, lambda.expression.getInvocation());
			}
		}
		else if (returnExpression.getClass() == FuncAppExpression.class) {
			Lambda lambda = symtab.lambdaWithName(returnExpression.firstToken.text);
			if (lambda.isAbstract) {
				FuncAppExpression app = (FuncAppExpression) returnExpression;
				for (Expression param : app.params)
					if (!param.firstToken.text.equals(this.params.get(0).identToken.text)) param.generate(mv, symtab);
			}
			else if (lambda.isLocal) {
				mv.visitVarInsn(ALOAD, 0);
				mv.visitFieldInsn(GETFIELD, symtab.className, lambda.name, lambda.expression.getInvocation());
			}
			else {
				// We are copying a field, so load self twice, change signature, and change internal variable counter
				mv.visitVarInsn(ALOAD, 0);
				classSig = "L" + symtab.className + ";";
				capturesSelf = true;
			}
		}
		addInvocation(mv, symtab);
	}
	
	protected void addInvocation(MethodVisitor mv, SymbolTable symtab) {
		String i = getInputSignature();
		String s = getSignature();
		String returnSig = getReturnSignature();
		Handle h = new Handle(Opcodes.H_INVOKESTATIC, symtab.className, "lambda$" + lambdaSlot, returnSig, false);
		mv.visitInvokeDynamicInsn("apply", i, getMetafactoryHandle(), new Object[]{Type.getType(s), h, Type.getType(s)});
	}
	
	public String getInputSignature() {
		if (returnExpression.getClass() == FuncCompExpression.class) {
			FuncCompExpression e = (FuncCompExpression) returnExpression;
			String sig = "(";
			for (int i = 0; i< e.idents.size(); i++) sig += getInvocation();
			sig += ")" + getInvocation();
			return sig;
		}
		if (returnExpression.getClass() == FuncAppExpression.class) {
			FuncAppExpression app = (FuncAppExpression) returnExpression;
			String input = "(";
			if (capturesSelf) input += classSig;
			for (int i = 1; i < app.params.size(); i++) {
				if (app.params.get(i).isLambda()) {
					IdentExpression id = (IdentExpression) app.params.get(i);
					input += id.funcLit.getInvocation();
				}
				else input += app.params.get(i).getJvmType();
			}
			input += ")";
			return input + getInvocation();
		}
		if (capturesSelf) return "(" + classSig + ")" + getInvocation();
		return "()" + getInvocation();
	}
	
	public String getReturnSignature() {
		if (returnExpression.getClass() == FuncCompExpression.class) {
			FuncCompExpression e = (FuncCompExpression) returnExpression;
			String sig = "(";
			for (int i = 0; i< e.idents.size(); i++) sig += getInvocation();
			sig += "F)F";
			return sig;
		}
		if (returnExpression.getClass() == FuncAppExpression.class) {
			FuncAppExpression app = (FuncAppExpression) returnExpression;
			String input = "(";
			if (capturesSelf) input += classSig;
			for (int i = 1; i < app.params.size(); i++) {
				if (app.params.get(i).isLambda()) {
					IdentExpression id = (IdentExpression) app.params.get(i);
					input += id.funcLit.getInvocation();
				}
				else input += app.params.get(i).getJvmType();
			}
			if (inputType == Types.ARRAY) input += "[";
			input += "F)";
			if (returnType == Types.ARRAY) input += "[";
			input += "F";
			return input;
		}
		if (capturesSelf) return getCaptureSignature();
		return getSignature();
	}
	
	public String getInterface() {
		switch (this.type) {
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
	
	public String getInvocation() {
		return "L" + getInterface() + ";";
	}

	public String getSignature() {
		switch (this.type) {
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
	
	public String getCaptureSignature() {
		switch (this.type) {
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
