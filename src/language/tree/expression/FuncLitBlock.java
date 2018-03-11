package language.tree.expression;

import java.util.ArrayList;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

import language.compiler.SymbolTable;
import language.compiler.Token;
import language.tree.Node;
import language.tree.ReturnBlock;
import language.tree.UnassignedDeclaration;

public class FuncLitBlock extends FuncLitExpression {
	
	public final ReturnBlock returnBlock;

	public FuncLitBlock(Token firstToken, ArrayList<UnassignedDeclaration> params, ReturnBlock returnBlock) {
		super(firstToken, params, returnBlock.returnExpression);
		this.returnBlock = returnBlock;
	}

	@Override
	public void decorate(SymbolTable symtab) throws Exception {
		symtab.enterScope();
		for (UnassignedDeclaration param : params) param.decorate(symtab);
		inputType = params.get(0).type;
		returnBlock.decorate(symtab);
		returnType = returnBlock.returnExpression.type;		
		this.type = Node.getLambdaType(inputType, returnType);
		symtab.leaveScope();
	}
	
	@Override
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
		returnBlock.generate(mv, symtab);
		if (returnBlock.returnExpression.type == Types.FLOAT) mv.visitInsn(FRETURN);
		else if (returnBlock.returnExpression.type == Types.ARRAY) mv.visitInsn(ARETURN);
		mv.visitMaxs(0, 0);
		mv.visitEnd();
	}

}
