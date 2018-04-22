package language.tree;

import java.util.ArrayList;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

import language.compiler.SymbolTable;
import language.compiler.Token;

public class MethodStatement extends Statement {
	
	public final Token name;
	public final ArrayList<ParamDeclaration> decs;
	public final ReturnBlock block;

	public MethodStatement(Token firstToken, Token name, ArrayList<ParamDeclaration> decs, ReturnBlock block) {
		super(firstToken, block.returnExpression);
		this.name = name;
		this.decs = decs;
		this.block = block;
		this.block.paramCount = decs.size() - 1;
	}

	@Override
	public void decorate(SymbolTable symtab) throws Exception {
		if (symtab.methods.containsKey(name.text)) throw new Exception();
		symtab.methods.put(name.text, this);
		int count = 0;
		symtab.enterScope();
		for (ParamDeclaration dec : decs) {
			dec.decorate(symtab);
			dec.slotNumber = count++;
		}
		block.decorate(symtab);
		symtab.leaveScope();
	}
	
	@Override
	public void generate(MethodVisitor mv, SymbolTable symtab) throws Exception {}

	public void generate(ClassWriter cw, SymbolTable symtab) throws Exception {
		Label startLabel = new Label();
		Label endLabel = new Label();
		MethodVisitor mv = cw.visitMethod(ACC_STATIC, name.text, getSignature(), null, null);
		mv.visitCode();
		mv.visitLabel(startLabel);
		block.generate(mv, symtab);
		if (block.returnExpression.type == Types.INT) mv.visitInsn(IRETURN);
		if (block.returnExpression.type == Types.FLOAT) mv.visitInsn(FRETURN);
		else if (block.returnExpression.type == Types.ARRAY) mv.visitInsn(ARETURN);
		mv.visitLabel(endLabel);
		for (ParamDeclaration dec : decs)
			mv.visitLocalVariable(dec.identToken.text, dec.getJvmType(), null, startLabel, endLabel, dec.slotNumber);
		mv.visitMaxs(0, 0);
		mv.visitEnd();
	}
	
	public String getSignature() {
		String result = "(";
		for (ParamDeclaration dec : decs) result += dec.getJvmType();
		result += ")";
		return result + block.returnExpression.getJvmType();
	}

}
