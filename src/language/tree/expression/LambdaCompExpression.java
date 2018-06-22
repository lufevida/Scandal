package language.tree.expression;

import java.util.ArrayList;

import org.objectweb.asm.MethodVisitor;

import language.compiler.SymbolTable;
import language.compiler.Token;

public class LambdaCompExpression extends Expression {
	
	// TODO: accept lambda app expressions
	public final ArrayList<IdentExpression> lambdas;
	public final LambdaAppExpression lambdaApp;

	public LambdaCompExpression(Token firstToken, ArrayList<IdentExpression> lambdas, LambdaAppExpression lambdaApp) {
		super(firstToken);
		this.lambdas = lambdas;
		this.lambdaApp = lambdaApp;
		if (lambdaApp == null) this.type = Types.LAMBDA;
	}

	@Override
	public void decorate(SymbolTable symtab) throws Exception {
		// TODO: get input and output types and type-check
		// TODO: handle parameter declarations
		for (IdentExpression lambda : lambdas) {
			lambda.decorate(symtab);
			if (lambda.type != Types.LAMBDA) throw new Exception();
		}
		if (lambdaApp != null) {
			lambdaApp.decorate(symtab);
			this.type = lambdaApp.type;
		}
	}

	@Override
	public void generate(MethodVisitor mv, SymbolTable symtab) throws Exception {
		lambdas.get(0).generate(mv, symtab);
		for (int i = 1; i < lambdas.size(); i++) {
			lambdas.get(i).generate(mv, symtab);
			mv.visitMethodInsn(INVOKEINTERFACE, "java/util/function/Function", "andThen", "(Ljava/util/function/Function;)Ljava/util/function/Function;", true);
		}
		if (lambdaApp != null) lambdaApp.generateArgs(mv, symtab);
	}

}
