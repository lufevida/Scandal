package language.tree;

import java.util.ArrayList;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

import language.compiler.Lambda;
import language.compiler.SymbolTable;
import language.compiler.Token;
import language.tree.expression.Expression;
import language.tree.expression.FuncAppExpression;
import language.tree.expression.FuncCompExpression;
import language.tree.expression.FuncLitBlock;
import language.tree.expression.IdentExpression;

public class Program extends Node {

	public final ArrayList<Declaration> declarations;
	public final ArrayList<Statement> statements;
	public byte[] bytecode;

	public Program(Token firstToken, ArrayList<Declaration> declarations, ArrayList<Statement> statements) {
		super(firstToken);
		this.declarations = declarations;
		this.statements = statements;
	}
	
	@Override
	public void decorate(SymbolTable symtab) throws Exception {
		for (Declaration declaration : declarations) declaration.decorate(symtab);
		for (Statement statement : statements) statement.decorate(symtab);
	}

	@Override
	public void generate(MethodVisitor mv, SymbolTable symtab) throws Exception {
		ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
		String classDesc = "L" + symtab.className + ";";
		cw.visit(V1_8, ACC_PUBLIC + ACC_SUPER, symtab.className, null, "javafx/application/Application", null);
		cw.visitSource(symtab.className, null);
		String sig = "java/lang/invoke/MethodHandles";
		cw.visitInnerClass(sig + "$Lookup", sig, "Lookup", ACC_PUBLIC + ACC_FINAL + ACC_STATIC);
		addMain(cw);
		addStart(cw, classDesc, this);
		addInit(cw, classDesc, this, symtab);
		addFields(cw, symtab);
		for (Lambda lambda : symtab.lambdas) addLambda(cw, lambda, symtab);
		cw.visitEnd();
		bytecode = cw.toByteArray();
	}
	
	private void addMain(ClassWriter cw) {
		MethodVisitor mv = cw.visitMethod(ACC_PUBLIC + ACC_STATIC, "main", "([Ljava/lang/String;)V", null, null);
		mv.visitCode();
		Label startLabel = new Label();
		Label endLabel = new Label();
		mv.visitLabel(startLabel);
		mv.visitVarInsn(ALOAD, 0);
		mv.visitMethodInsn(INVOKESTATIC, "javafx/application/Application", "launch", "([Ljava/lang/String;)V", false);
		mv.visitInsn(RETURN);
		mv.visitLabel(endLabel);
		mv.visitLocalVariable("args", "[Ljava/lang/String;", null, startLabel, endLabel, 0);
		mv.visitMaxs(0, 0);
		mv.visitEnd();
	}
	
	private void addInit(ClassWriter cw, String classDesc, Program program, SymbolTable symtab) throws Exception {
		MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
		mv.visitCode();
		Label startLabel = new Label();
		Label endLabel = new Label();
		mv.visitLabel(startLabel);
		mv.visitVarInsn(ALOAD, 0);
		mv.visitMethodInsn(INVOKESPECIAL, "javafx/application/Application", "<init>", "()V", false);
		for (Declaration dec : program.declarations) {
			dec.startLabel = startLabel;
			dec.endLabel = endLabel;
			dec.generate(mv, symtab);
		}
		for (Statement statement : program.statements) statement.generate(mv, symtab);
		mv.visitInsn(RETURN);
		mv.visitLabel(endLabel);
		mv.visitLocalVariable("this", classDesc, null, startLabel, endLabel, 0);
		mv.visitMaxs(0, 0);
		mv.visitEnd();
	}
	
	private void addStart(ClassWriter cw, String classDesc, Program program) throws Exception {
		MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "start", "(Ljavafx/stage/Stage;)V", null, null);
		mv.visitCode();
		Label startLabel = new Label();
		Label endLabel = new Label();
		mv.visitLabel(startLabel);
		mv.visitInsn(RETURN);
		mv.visitLabel(endLabel);
		mv.visitLocalVariable("this", classDesc, null, startLabel, endLabel, 0);
		mv.visitLocalVariable("stage", "Ljavafx/stage/Stage;", null, startLabel, endLabel, 1);
		mv.visitMaxs(0, 0);
		mv.visitEnd();
	}
	
	private void addFields(ClassWriter cw, SymbolTable symtab) {
		FieldVisitor fv;
		for (Lambda lambda : symtab.lambdas) if (!lambda.isAbstract) {
			fv = cw.visitField(0, lambda.name, lambda.expression.getInvocation(), null, null);
			fv.visitEnd();
		}
	}
	
	private void addLambda(ClassWriter cw, Lambda lambda, SymbolTable symtab) throws Exception {
		if (lambda.isAbstract) return;
		if (lambda.expression.getClass() == FuncLitBlock.class) {
			addLambdaWithBlock(cw, lambda, symtab);
			return;
		}
		String returnSig = lambda.expression.getReturnSignature();
		MethodVisitor mv = cw.visitMethod(ACC_PRIVATE + ACC_STATIC + ACC_SYNTHETIC, "lambda$" + lambda.slot, returnSig, null, null);
		mv.visitCode();
		Label startLabel = new Label();
		Label endLabel = new Label();
		mv.visitLabel(startLabel);
		mv.visitLabel(endLabel);
		UnassignedDeclaration param = lambda.expression.params.get(0);
		Expression returnExpression = lambda.expression.returnExpression;
		if (returnExpression.getClass() == FuncCompExpression.class) {
			FuncCompExpression comp = (FuncCompExpression) returnExpression;
			comp.isReturnExpression = true;
			mv.visitLocalVariable(param.identToken.text, param.getJvmType(), null, startLabel, endLabel, comp.idents.size());
			mv.visitVarInsn(ALOAD, 0);
			for (int i = 1; i < comp.idents.size(); i++) {
				mv.visitVarInsn(ALOAD, i);
				// TODO: incomplete implementation
				mv.visitMethodInsn(INVOKEINTERFACE, "language/interfaces/FloatFloat", "then", "(Llanguage/interfaces/FloatFloat;)Llanguage/interfaces/FloatFloat;", true);
			}
			mv.visitVarInsn(FLOAD, comp.idents.size());
			mv.visitMethodInsn(INVOKEINTERFACE, "language/interfaces/FloatFloat", "apply", "(F)F", true);
			mv.visitInsn(FRETURN);
		}
		else if (returnExpression.getClass() == FuncAppExpression.class) {
			FuncAppExpression app = (FuncAppExpression) returnExpression;
			for (Expression arg : app.params)
				if (arg.firstToken.text.equals(param.identToken.text) && app.params.indexOf(arg) != 0) throw new Exception("This feature is not yet supported");
			if (lambda.expression.capturesSelf) param.slotNumber = 1;
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
			if (!lambda.expression.capturesSelf) param.slotNumber = paramCount++;
			mv.visitLocalVariable(param.identToken.text, param.getJvmType(), null, startLabel, endLabel, param.slotNumber);
			if (app.type == Types.FLOAT) mv.visitInsn(FRETURN);
			else if (app.type == Types.ARRAY) mv.visitInsn(ARETURN);
		}
		else {
			param.slotNumber = 0;
			if (lambda.expression.capturesSelf) param.slotNumber = 1;
			mv.visitLocalVariable(param.identToken.text, param.getJvmType(), null, startLabel, endLabel, param.slotNumber);
			returnExpression.isReturnExpression = true;
			returnExpression.generate(mv, symtab);
			if (returnExpression.type == Types.FLOAT) mv.visitInsn(FRETURN);
			else if (returnExpression.type == Types.ARRAY) mv.visitInsn(ARETURN);
		}
		mv.visitMaxs(0, 0);
		mv.visitEnd();
	}
	
	private void addLambdaWithBlock(ClassWriter cw, Lambda lambda, SymbolTable symtab) throws Exception {
		String returnSig = lambda.expression.getReturnSignature();
		MethodVisitor mv = cw.visitMethod(ACC_PRIVATE + ACC_STATIC + ACC_SYNTHETIC, "lambda$" + lambda.slot, returnSig, null, null);
		mv.visitCode();
		Label startLabel = new Label();
		Label endLabel = new Label();
		mv.visitLabel(startLabel);
		mv.visitLabel(endLabel);
		UnassignedDeclaration param = lambda.expression.params.get(0);
		ReturnBlock returnBlock = ((FuncLitBlock) lambda.expression).returnBlock;
		param.slotNumber = 0;
		if (lambda.expression.capturesSelf) param.slotNumber = 1;
		mv.visitLocalVariable(param.identToken.text, param.getJvmType(), null, startLabel, endLabel, param.slotNumber);
		returnBlock.generate(mv, symtab);
		if (returnBlock.returnExpression.type == Types.FLOAT) mv.visitInsn(FRETURN);
		else if (returnBlock.returnExpression.type == Types.ARRAY) mv.visitInsn(ARETURN);
		mv.visitMaxs(0, 0);
		mv.visitEnd();
	}

}
