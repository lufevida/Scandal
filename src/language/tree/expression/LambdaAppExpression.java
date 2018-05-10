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
				// Either should work, but check the last.
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
		if (args.size() == lambdaLit.params.size()) this.type = lambdaLit.block.returnExpression.type;
	}

	@Override
	public void generate(MethodVisitor mv, SymbolTable symtab) throws Exception {
		lambda.generate(mv, symtab);
		generateArgs(mv, symtab);
	}
	
	public void generateArgs(MethodVisitor mv, SymbolTable symtab) throws Exception {
		for (int i = args.size() - count; i < args.size(); i++) {
			args.get(i).generate(mv, symtab);
			switch (args.get(i).type) {
			case INT:
				mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
				break;
			case BOOL:
				mv.visitMethodInsn(INVOKESTATIC, "java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;", false);
				break;
			case FLOAT:
				mv.visitMethodInsn(INVOKESTATIC, "java/lang/Float", "valueOf", "(F)Ljava/lang/Float;", false);
				break;
			default: break;
			}
			mv.visitMethodInsn(INVOKEINTERFACE, "java/util/function/Function", "apply", "(Ljava/lang/Object;)Ljava/lang/Object;", true);
			if (lambdaLit != null && i == lambdaLit.params.size() - 1) switch (lambdaLit.block.returnExpression.type) {
			case INT:
				mv.visitTypeInsn(CHECKCAST, "java/lang/Integer");
				break;
			case BOOL:
				mv.visitTypeInsn(CHECKCAST, "java/lang/Boolean");
				break;
			case FLOAT:
				mv.visitTypeInsn(CHECKCAST, "java/lang/Float");
				break;
			case ARRAY:
				mv.visitTypeInsn(CHECKCAST, "[F");
				break;
			case STRING:
				mv.visitTypeInsn(CHECKCAST, "java/lang/String");
				break;
			default: break;
			}
			else if (lambdaLit != null) mv.visitTypeInsn(CHECKCAST, "java/util/function/Function");
			else if (lambda.declaration instanceof ParamDeclaration) {
				switch (this.type) {
				case INT:
					mv.visitTypeInsn(CHECKCAST, "java/lang/Integer");
					break;
				case BOOL:
					mv.visitTypeInsn(CHECKCAST, "java/lang/Boolean");
					break;
				case FLOAT:
					mv.visitTypeInsn(CHECKCAST, "java/lang/Float");
					break;
				case ARRAY:
					mv.visitTypeInsn(CHECKCAST, "[F");
					break;
				case STRING:
					mv.visitTypeInsn(CHECKCAST, "java/lang/String");
					break;
				default: break;
				}
			}
		}
		switch (this.type) {
		case INT:
			mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Integer", "intValue", "()I", false);
			break;
		case BOOL:
			mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Boolean", "booleanValue", "()Z", false);
			break;
		case FLOAT:
			mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Float", "floatValue", "()F", false);
			break;
		default: break;
		}
	}

}
