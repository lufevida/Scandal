package language.tree.expression;

import java.util.ArrayList;

import org.objectweb.asm.MethodVisitor;

import language.compiler.SymbolTable;
import language.compiler.Token;
import language.tree.AssignmentDeclaration;
import language.tree.Declaration;
import language.tree.LambdaLitDeclaration;
import language.tree.ParamDeclaration;

public class LambdaAppExpression extends Expression {

	public final IdentExpression lambda;
	public final ArrayList<Expression> args;
	public final int count;
	public LambdaLitExpression lambdaLit;

	public LambdaAppExpression(Token firstToken, IdentExpression lambda, ArrayList<Expression> args) {
		super(firstToken);
		this.lambda = lambda;
		this.args = args;
		this.count = args.size();
		this.type = Types.LAMBDA;
	}

	@Override
	public void decorate(SymbolTable symtab) throws Exception {
		lambda.decorate(symtab);
		if (lambda.type != Types.LAMBDA) throw new Exception();
		Declaration dec = lambda.declaration;
		if (dec instanceof ParamDeclaration) {
			for (Expression arg : args) arg.decorate(symtab);
			return;
		}
		while (dec.getClass() != LambdaLitDeclaration.class) {
			Expression e = ((AssignmentDeclaration) dec).expression;
			if (e instanceof IdentExpression) {
				IdentExpression identExpression = (IdentExpression) e;
				dec = identExpression.declaration;
			}
			else if (e instanceof LambdaCompExpression) {
				LambdaCompExpression compExpression = (LambdaCompExpression) e;
				dec = compExpression.lambdas.get(compExpression.lambdas.size() - 1).declaration;
			}
			else {
				LambdaAppExpression appExpression = (LambdaAppExpression) e;
				for (int i = appExpression.args.size() - 1; i >= 0; i--)
					if (!args.contains(appExpression.args.get(i))) args.add(0, appExpression.args.get(i));
				dec = appExpression.lambda.declaration;
			}
		}
		lambdaLit = ((LambdaLitDeclaration) dec).lambda;
		for (int i = args.size() - count; i < args.size(); i++) {
			args.get(i).decorate(symtab);
			if (args.get(i).type != lambdaLit.params.get(i).type) throw new Exception("Type mismatch in line " + firstToken.lineNumber);
		}
		if (args.size() == lambdaLit.params.size()) this.type = lambdaLit.returnExpression.type;
	}

	@Override
	public void generate(MethodVisitor mv, SymbolTable symtab) throws Exception {
		lambda.generate(mv, symtab);
		generateArgs(mv, symtab);
	}
	
	public void generateArgs(MethodVisitor mv, SymbolTable symtab) throws Exception {
		for (int i = args.size() - count; i < args.size(); i++) {
			args.get(i).generate(mv, symtab);
			getValueOf(args.get(i).type, mv);
			mv.visitMethodInsn(INVOKEINTERFACE, "java/util/function/Function", "apply", "(Ljava/lang/Object;)Ljava/lang/Object;", true);
			if (lambda.declaration instanceof ParamDeclaration) getCheckCast(this.type, mv);
			else if (i == lambdaLit.params.size() - 1) getCheckCast(lambdaLit.returnExpression.type, mv);
			else getCheckCast(Types.LAMBDA, mv);
		}
		getTypeValue(this.type, mv);
	}

}
