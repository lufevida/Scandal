package language.tree;

import java.util.ArrayList;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

import language.compiler.Lambda;
import language.compiler.SymbolTable;
import language.compiler.Token;

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
	
	private void addFields(ClassWriter cw, SymbolTable symtab) throws Exception {
		FieldVisitor fv;
		for (Lambda lambda : symtab.lambdas) if (!lambda.isAbstract) {
			fv = cw.visitField(0, lambda.name, lambda.expression.getInvocation(), null, null);
			fv.visitEnd();
			lambda.expression.generate(cw, symtab);
		}
	}

}
