package language.tree.expression;

import java.util.ArrayList;

import org.objectweb.asm.MethodVisitor;

import language.compiler.SymbolTable;
import language.compiler.Token;

public class LambdaCompExpression extends Expression {
	
	public final ArrayList<IdentExpression> lambdas;

	public LambdaCompExpression(Token firstToken, ArrayList<IdentExpression> lambdas) {
		super(firstToken);
		this.lambdas = lambdas;
		this.type = Types.LAMBDA;
	}

	@Override
	public void decorate(SymbolTable symtab) throws Exception {
		for (IdentExpression lambda : lambdas) lambda.decorate(symtab);
	}

	@Override
	public void generate(MethodVisitor mv, SymbolTable symtab) throws Exception {
		lambdas.get(0).generate(mv, symtab);
		for (int i = 1; i < lambdas.size(); i++) {
			lambdas.get(i).generate(mv, symtab);
			mv.visitMethodInsn(INVOKEINTERFACE, "java/util/function/Function", "andThen", "(Ljava/util/function/Function;)Ljava/util/function/Function;", true);
		}
	}

}
