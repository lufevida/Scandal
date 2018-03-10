package language.tree.expression;

import java.util.ArrayList;

import org.objectweb.asm.MethodVisitor;

import language.compiler.Lambda;
import language.compiler.SymbolTable;
import language.compiler.Token;
import language.tree.AssignmentDeclaration;

public class FuncAppExpression extends Expression {
	
	public final ArrayList<Expression> params;

	public FuncAppExpression(Token firstToken, ArrayList<Expression> params) {
		super(firstToken);
		this.params = params;
	}
	
	@Override
	public void decorate(SymbolTable symtab) throws Exception {
		AssignmentDeclaration lambdaDec = (AssignmentDeclaration) symtab.lookup(firstToken.text);
		if (lambdaDec == null) throw new Exception("Function must have been declared in some enclosing scope");
		FuncLitExpression funcLit = (FuncLitExpression) lambdaDec.expression;
		for (int i = 0; i < params.size(); i++) {
			params.get(i).decorate(symtab);
			if (params.get(i).type != funcLit.params.get(i).type) throw new Exception("Type mismatch");
		}
		this.type = funcLit.returnType;
	}

	@Override
	public void generate(MethodVisitor mv, SymbolTable symtab) throws Exception {
		Lambda lambda = symtab.lambdaWithName(firstToken.text);
		if (lambda.isAbstract && lambda.slot < Integer.MAX_VALUE) throw new Exception("This feature is not yet supported");
		if (lambda.isLocal) mv.visitVarInsn(ALOAD, lambda.paramSlot);
		else {
			mv.visitVarInsn(ALOAD, 0);
			mv.visitFieldInsn(GETFIELD, symtab.className, lambda.name, lambda.expression.getInvocation());
		}
		params.get(0).generate(mv, symtab);
		mv.visitMethodInsn(INVOKEINTERFACE, lambda.expression.getInterface(), "apply", lambda.expression.getSignature(), true);
	}

}
