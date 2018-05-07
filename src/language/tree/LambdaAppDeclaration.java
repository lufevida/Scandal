package language.tree;

import org.objectweb.asm.MethodVisitor;

import language.compiler.SymbolTable;
import language.compiler.Token;
import language.tree.expression.LambdaAppExpression;

public class LambdaAppDeclaration extends Declaration {
	
	public final LambdaAppExpression lambda;

	public LambdaAppDeclaration(Token firstToken, Token identToken, LambdaAppExpression lambda) {
		super(firstToken, identToken);
		this.lambda = lambda;
	}

	@Override
	public void decorate(SymbolTable symtab) throws Exception {
		if (symtab.topOfStackLookup(identToken.text) != null) throw new Exception();
		symtab.insert(identToken.text, this);
		lambda.decorate(symtab);
		if (lambda.type != type) throw new Exception();
	}

	@Override
	public void generate(MethodVisitor mv, SymbolTable symtab) throws Exception {
		slotNumber = symtab.slotCount++;
		lambda.generate(mv, symtab);
		mv.visitVarInsn(ASTORE, slotNumber);
	}

}
