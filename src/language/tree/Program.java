package language.tree;

import java.util.ArrayList;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;

import language.compiler.SymbolTable;
import language.compiler.Token;

public class Program extends Node {

	public final ArrayList<Node> nodes;
	public byte[] bytecode;

	public Program(Token firstToken, ArrayList<Node> nodes) {
		super(firstToken);
		this.nodes = nodes;
	}
	
	@Override
	public void decorate(SymbolTable symtab) throws Exception {
		for (Node node : nodes) node.decorate(symtab);
	}

	@Override
	public void generate(MethodVisitor mv, SymbolTable symtab) throws Exception {
		ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
		cw.visit(V1_8, ACC_PUBLIC + ACC_SUPER, symtab.className, null, "java/lang/Object", new String[]{"java/lang/Runnable"});
		addInit(cw, symtab);
		addFields(cw, symtab);
		addRun(cw, symtab);
		addMain(cw, symtab);
		bytecode = cw.toByteArray();
	}
	
	private void addInit(ClassWriter cw, SymbolTable symtab) throws Exception {
		MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
		mv.visitVarInsn(ALOAD, 0);
		mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
		for (Node node : nodes) if (node instanceof LambdaLitDeclaration) node.generate(mv, symtab);
		mv.visitInsn(RETURN);
		mv.visitMaxs(0, 0);
	}
	
	private void addFields(ClassWriter cw, SymbolTable symtab) throws Exception {
		for (Node node : nodes) {
			if (node instanceof LambdaLitDeclaration) {
				LambdaLitDeclaration dec = (LambdaLitDeclaration) node;
				cw.visitField(ACC_FINAL + ACC_STATIC, dec.identToken.text, dec.getJvmType(), null, null);
				dec.lambda.generate(cw, symtab);
			}
			if (node instanceof FieldDeclaration) {
				FieldDeclaration dec = (FieldDeclaration) node;
				cw.visitField(ACC_STATIC, dec.identToken.text, dec.getJvmType(), null, null);
			}
		}
	}
	
	private void addRun(ClassWriter cw, SymbolTable symtab) throws Exception {
		MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "run", "()V", null, null);
		for (Node node : nodes) if (node.getClass() != LambdaLitDeclaration.class) node.generate(mv, symtab);
		mv.visitInsn(RETURN);
		mv.visitMaxs(0, 0);
	}

	private void addMain(ClassWriter cw, SymbolTable symtab) {
		MethodVisitor mv = cw.visitMethod(ACC_PUBLIC + ACC_STATIC, "main", "([Ljava/lang/String;)V", null, null);
		mv.visitTypeInsn(NEW, symtab.className);
		mv.visitInsn(DUP);
		mv.visitMethodInsn(INVOKESPECIAL, symtab.className, "<init>", "()V", false);
		mv.visitMethodInsn(INVOKEVIRTUAL, symtab.className, "run", "()V", false);
		mv.visitInsn(RETURN);
		mv.visitMaxs(0, 0);
	}

}
