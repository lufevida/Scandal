package language.tree;

import org.objectweb.asm.MethodVisitor;

import language.compiler.SymbolTable;
import language.compiler.Token;
import language.tree.expression.LambdaLitExpression;

public class LambdaLitDeclaration extends AssignmentDeclaration {
	
	public final LambdaLitExpression lambda;

	public LambdaLitDeclaration(Token firstToken, Token identToken, LambdaLitExpression lambda) {
		super(firstToken, identToken, lambda);
		this.lambda = lambda;
		isField = true;
	}

	@Override
	public void decorate(SymbolTable symtab) throws Exception {
		if (symtab.lookup(identToken.text) != null) throw new Exception("Redeclaration of: " + identToken.text);
		symtab.insert(identToken.text, this);
		lambda.decorate(symtab);
	}

	@Override
	public void generate(MethodVisitor mv, SymbolTable symtab) throws Exception {
		lambda.generate(mv, symtab);
		mv.visitFieldInsn(PUTSTATIC, symtab.className, identToken.text, getJvmType());
	}

}
