package language.tree.expression;

import java.util.ArrayList;

import org.objectweb.asm.MethodVisitor;

import language.compiler.SymbolTable;
import language.compiler.Token;
import language.tree.Declaration;
import language.tree.LambdaAppDeclaration;
import language.tree.LambdaLitDeclaration;

public class LambdaAppExpression extends Expression {
	
	public final ArrayList<Expression> args;
	public final int count;
	public final IdentExpression lambda;
	public LambdaLitExpression lambdaLit;
	

	public LambdaAppExpression(Token firstToken, ArrayList<Expression> args, IdentExpression lambda) {
		super(firstToken);
		this.args = args;
		this.count = args.size();
		this.lambda = lambda;
		this.type = Types.LAMBDA;
	}

	@Override
	public void decorate(SymbolTable symtab) throws Exception {
		lambda.decorate(symtab);
		Declaration dec = lambda.declaration;
		while (dec.getClass() != LambdaLitDeclaration.class) {
			LambdaAppExpression appExpression = ((LambdaAppDeclaration) dec).lambda;
			for (int i = appExpression.args.size() - 1; i >= 0; i--)
				if (!args.contains(appExpression.args.get(i))) args.add(0, appExpression.args.get(i));
			dec = appExpression.lambda.declaration;
		}
		lambdaLit = ((LambdaLitDeclaration) dec).lambda;
		for (int i = args.size() - count; i < args.size(); i++) {
			args.get(i).decorate(symtab);
			if (args.get(i).type != lambdaLit.params.get(i).type) throw new Exception("Type mismatch in line " + firstToken.lineNumber);
		}
		if (args.size() == lambdaLit.params.size()) this.type = lambdaLit.block.returnExpression.type;
	}

	@Override
	public void generate(MethodVisitor mv, SymbolTable symtab) throws Exception {
		lambda.generate(mv, symtab);
		for (int i = args.size() - count; i < args.size(); i++) {
			args.get(i).generate(mv, symtab);
			switch (args.get(i).type) {
			case INT:
				mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
				break;
			case FLOAT:
				mv.visitMethodInsn(INVOKESTATIC, "java/lang/Float", "valueOf", "(F)Ljava/lang/Float;", false);
				break;
			default: break;
			}
			mv.visitMethodInsn(INVOKEINTERFACE, "java/util/function/Function", "apply", "(Ljava/lang/Object;)Ljava/lang/Object;", true);
			if (i == lambdaLit.params.size() - 1) switch (lambdaLit.block.returnExpression.type) {
			case INT:
				mv.visitTypeInsn(CHECKCAST, "java/lang/Integer");
				break;
			case FLOAT:
				mv.visitTypeInsn(CHECKCAST, "java/lang/Float");
				break;
			case STRING:
				mv.visitTypeInsn(CHECKCAST, "java/lang/String");
				break;
			default: break;
			}
			else mv.visitTypeInsn(CHECKCAST, "java/util/function/Function");
		}
		switch (this.type) {
		case INT:
			mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Integer", "intValue", "()I", false);
			break;
		case FLOAT:
			mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Float", "floatValue", "()F", false);
			break;
		default: break;
		}
	}

}
