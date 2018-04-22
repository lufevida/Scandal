package language.tree;

import java.util.ArrayList;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

import language.compiler.SymbolTable;
import language.compiler.Token;
import language.tree.expression.FuncLitExpression;

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
		//cw.visit(V1_8, ACC_PUBLIC + ACC_SUPER, symtab.className, null, "java/lang/Object", null);
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
		for (Node node : nodes) {
			if (node.getClass() == AssignmentDeclaration.class) {
				AssignmentDeclaration dec = (AssignmentDeclaration) node;
				dec.startLabel = startLabel;
				dec.endLabel = endLabel;
			}
			node.generate(mv, symtab);
			if (node.getClass() == MethodStatement.class) ((MethodStatement) node).generate(cw, symtab);
		}
		mv.visitInsn(RETURN);
		mv.visitLabel(endLabel);
		mv.visitLocalVariable("this", classDesc, null, startLabel, endLabel, 0);
		mv.visitMaxs(0, 0);
		mv.visitEnd();
	}
	
	private void addFields(ClassWriter cw, SymbolTable symtab) throws Exception {
		//String sig = "Ljava/util/function/Function;";
		FieldVisitor fv;
		for (Node node : nodes) if (node.getClass() == AssignmentDeclaration.class) {
			AssignmentDeclaration dec = (AssignmentDeclaration) node;
			if (dec.isLambda()) {
				FuncLitExpression lambda = (FuncLitExpression) dec.expression;
				if (!lambda.isAbstract) {
					//fv = cw.visitField(ACC_STATIC, "floatToArrayToArray", sig, "Ljava/util/function/Function<Ljava/lang/Integer;Ljava/util/function/Function<[F[F>;>;", null);
					//fv = cw.visitField(ACC_STATIC, "arrayToArray", sig, "Ljava/util/function/Function<[F[F>;", null);
					fv = cw.visitField(0, dec.identToken.text, lambda.getInvocation(), null, null);
					fv.visitEnd();
					lambda.generate(cw, symtab);
				}
			}
		}
	}

}
