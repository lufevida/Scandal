package language.tree.expression;

import java.util.ArrayList;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

import language.compiler.Lambda;
import language.compiler.SymbolTable;
import language.compiler.Token;
import language.tree.UnassignedDeclaration;

public class FuncLitApp extends FuncLitExpression {
	
	public final FuncAppExpression app;

	public FuncLitApp(Token firstToken, ArrayList<UnassignedDeclaration> params, FuncAppExpression app) {
		super(firstToken, params, app);
		this.app = app;
	}
	
	@Override
	public void generate(MethodVisitor mv, SymbolTable symtab) throws Exception {
		mv.visitVarInsn(ALOAD, 0);
		Lambda lambda = symtab.lambdaWithName(returnExpression.firstToken.text);
		if (lambda.isAbstract) {
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
		for (Expression arg : app.params)
			if (arg.firstToken.text.equals(params.get(0).identToken.text) && app.params.indexOf(arg) != 0) throw new Exception("This feature is not yet supported");
		if (capturesSelf) params.get(0).slotNumber = 1;
		int paramCount = 0;
		Lambda l = symtab.lambdaWithName(app.firstToken.text);
		if (l.isAbstract) {
			l.expression.returnExpression.isReturnExpression = true;
			for (int i = 1; i < l.expression.params.size(); i++) {
				UnassignedDeclaration parameter = l.expression.params.get(i);
				parameter.slotNumber = paramCount++;
				if (parameter.isLambda()) {
					IdentExpression id = (IdentExpression) app.params.get(parameter.slotNumber + 1);
					Lambda ell = symtab.lambdaWithName(parameter.identToken.text);
					ell.expression = symtab.lambdaWithName(id.firstToken.text).expression;
					ell.isLocal = true;
					ell.paramSlot = parameter.slotNumber;
				}
			}
			l.expression.params.get(0).slotNumber = paramCount;
			if (l.expression.getClass() == FuncLitBlock.class) {
				((FuncLitBlock) l.expression).returnBlock.paramCount = paramCount;
				((FuncLitBlock) l.expression).returnBlock.generate(mv, symtab);
			}
			else l.expression.returnExpression.generate(mv, symtab);
		}
		else {
			app.isReturnExpression = true;
			app.generate(mv, symtab);
		}
		if (!capturesSelf) params.get(0).slotNumber = paramCount++;
		mv.visitLocalVariable(params.get(0).identToken.text, params.get(0).getJvmType(), null, startLabel, endLabel, params.get(0).slotNumber);
		if (app.type == Types.FLOAT) mv.visitInsn(FRETURN);
		else if (app.type == Types.ARRAY) mv.visitInsn(ARETURN);
		mv.visitMaxs(0, 0);
		mv.visitEnd();
	}
	
	@Override
	public String getInputSignature() {
		String input = getBaseSig();
		input += ")";
		return input + getInvocation();
	}
	
	@Override
	public String getReturnSignature() {
		String input = getBaseSig();
		if (inputType == Types.ARRAY) input += "[";
		input += "F)";
		if (returnType == Types.ARRAY) input += "[";
		input += "F";
		return input;
	}
	
	private String getBaseSig() {
		String input = "(";
		if (capturesSelf) input += classSig;
		for (int i = 1; i < app.params.size(); i++) {
			if (app.params.get(i).isLambda()) {
				IdentExpression id = (IdentExpression) app.params.get(i);
				input += id.funcLit.getInvocation();
			}
			else input += app.params.get(i).getJvmType();
		}
		return input;
	}

}
