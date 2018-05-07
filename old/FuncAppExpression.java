package language.tree.expression;

import java.util.ArrayList;

import org.objectweb.asm.MethodVisitor;

import language.compiler.SymbolTable;
import language.compiler.Token;
import language.tree.AssignmentDeclaration;
import language.tree.Declaration;
import language.tree.MethodStatement;
import language.tree.ParamDeclaration;

public class FuncAppExpression extends Expression {
	
	public final ArrayList<Expression> params;
	public FuncLitExpression funcLit;
	public boolean isLocal;
	public int paramSlot;

	public FuncAppExpression(Token firstToken, ArrayList<Expression> params) {
		super(firstToken);
		this.params = params;
	}
	
	@Override
	public void decorate(SymbolTable symtab) throws Exception {
		if (symtab.methods.containsKey(firstToken.text)) {
			MethodStatement s = symtab.methods.get(firstToken.text);
			for (Expression param: params) param.decorate(symtab);
			this.type = s.block.returnExpression.type;
			return;
		}
		Declaration lambdaDec = symtab.lookup(firstToken.text);
		if (lambdaDec == null) throw new Exception("Missing declaration in line " + firstToken.lineNumber);
		if (lambdaDec.getClass() == ParamDeclaration.class) {			
			for (int i = 0; i < params.size(); i++) params.get(i).decorate(symtab);
			isLocal = true;
			type = lambdaDec.returnType;
			paramSlot = lambdaDec.slotNumber;
			return;
		}
		funcLit = (FuncLitExpression) ((AssignmentDeclaration) lambdaDec).expression;
		for (int i = 0; i < params.size(); i++) {
			params.get(i).decorate(symtab);
			if (params.get(i).type != funcLit.params.get(i).type) throw new Exception("Type mismatch in line " + firstToken.lineNumber);
		}
		if (funcLit.isAbstract) {
			for (int i = 1; i < funcLit.params.size(); i++) {
				if (funcLit.params.get(i).isLambda()) {
					AssignmentDeclaration lookup = (AssignmentDeclaration) symtab.lookup(params.get(i).firstToken.text);
					symtab.lambdaParams.replace(funcLit.params.get(i).identToken.text, lookup);
				}
			}
		}
		this.type = funcLit.returnType;
	}

	@Override
	public void generate(MethodVisitor mv, SymbolTable symtab) throws Exception {
		if (symtab.methods.containsKey(firstToken.text)) {
			MethodStatement s = symtab.methods.get(firstToken.text);
			for (Expression param: params) param.generate(mv, symtab);
			mv.visitMethodInsn(INVOKESTATIC, symtab.className, s.name.text, s.getSignature(), false);
			return;
		}
		if (funcLit == null) funcLit = (FuncLitExpression) symtab.lambdaParams.get(firstToken.text).expression;
		if (funcLit.isAbstract) {
			for (int i = 0; i < params.size(); i++) funcLit.params.get(i).expression = params.get(i);
			if (funcLit.getClass() == FuncLitBlock.class) {
				FuncLitBlock funcLitBLock = (FuncLitBlock) funcLit;
				funcLitBLock.returnBlock.resetCounter = false;
				funcLitBLock.returnBlock.generate(mv, symtab);
			}
			else {
				funcLit.returnExpression.isReturnExpression = true;
				funcLit.returnExpression.generate(mv, symtab);
			}
			return;
		}
		if (isLocal) mv.visitVarInsn(ALOAD, paramSlot);
		else {
			mv.visitVarInsn(ALOAD, 0);
			mv.visitFieldInsn(GETFIELD, symtab.className, firstToken.text, funcLit.getInvocation());
		}
		params.get(0).generate(mv, symtab);
		mv.visitMethodInsn(INVOKEINTERFACE, funcLit.getInterface(), "apply", funcLit.getSignature(), true);
	}

}
