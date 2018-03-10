package language.tree.expression;

import java.util.ArrayList;

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
	public void generate(MethodVisitor mv, SymbolTable symtab) throws Exception {
		//if (expression.capturesSelf)
		mv.visitVarInsn(ALOAD, 0);
		addInvocation(mv, symtab);
	}

}
