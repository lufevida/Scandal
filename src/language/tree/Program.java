package language.tree;

import java.util.ArrayList;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;

import language.compiler.SymbolTable;
import language.compiler.Token;
import language.tree.statement.MethodStatement;

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
		String classDesc = "L" + symtab.className + ";";
		cw.visit(V1_8, ACC_PUBLIC + ACC_SUPER, symtab.className, null, "java/lang/Object", new String[]{"java/lang/Runnable"});
		cw.visitSource(symtab.className, null);
		String sig = "java/lang/invoke/MethodHandles";
		cw.visitInnerClass(sig + "$Lookup", sig, "Lookup", ACC_PUBLIC + ACC_FINAL + ACC_STATIC);
		addInit(cw, classDesc, symtab);
		addMain(cw, symtab);
		addRun(cw, classDesc, symtab);
		addFields(cw, symtab);
		cw.visitEnd();
		bytecode = cw.toByteArray();
	}
	
	private void addInit(ClassWriter cw, String classDesc, SymbolTable symtab) throws Exception {
		MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
		mv.visitCode();
		mv.visitVarInsn(ALOAD, 0);
		mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
		mv.visitInsn(RETURN);
		mv.visitMaxs(0, 0);
		mv.visitEnd();
	}
	
	private void addMain(ClassWriter cw, SymbolTable symtab) {		
		MethodVisitor mv = cw.visitMethod(ACC_PUBLIC + ACC_STATIC, "main", "([Ljava/lang/String;)V", null, null);
		mv.visitCode();
		mv.visitTypeInsn(NEW, symtab.className);
		mv.visitInsn(DUP);
		mv.visitMethodInsn(INVOKESPECIAL, symtab.className, "<init>", "()V", false);
		mv.visitMethodInsn(INVOKEVIRTUAL, symtab.className, "run", "()V", false);
		mv.visitInsn(RETURN);
		mv.visitMaxs(0, 0);
		mv.visitEnd();
	}
	
	private void addRun(ClassWriter cw, String classDesc, SymbolTable symtab) throws Exception {
		MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "run", "()V", null, null);
		mv.visitCode();
		for (Node node : nodes) node.generate(mv, symtab);
		mv.visitInsn(RETURN);
		mv.visitMaxs(0, 0);
		mv.visitEnd();
	}
	
	private void addFields(ClassWriter cw, SymbolTable symtab) throws Exception {
		FieldVisitor fv;
		for (Node node : nodes) {
			if (node instanceof LambdaLitDeclaration) {
				LambdaLitDeclaration dec = (LambdaLitDeclaration) node;
				fv = cw.visitField(ACC_STATIC, dec.identToken.text, dec.getJvmType(), dec.getFullSig(), null);
				fv.visitEnd();
				dec.lambda.generate(cw, symtab);
			}
			else if (node instanceof MethodStatement) ((MethodStatement) node).generate(cw, symtab);
		}
	}

}
