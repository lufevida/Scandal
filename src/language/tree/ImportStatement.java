package language.tree;

import static language.tree.Node.Types.STRING;

import org.objectweb.asm.MethodVisitor;

import language.compiler.SymbolTable;
import language.compiler.Token;
import language.tree.expression.Expression;

public class ImportStatement extends Statement {

	public ImportStatement(Token firstToken, Expression expression) {
		super(firstToken, expression);
	}
	
	@Override
	public void decorate(SymbolTable symtab) throws Exception {
		expression.decorate(symtab);
		if (expression.type != STRING) throw new Exception();
	}

	@Override
	public void generate(MethodVisitor mv, SymbolTable symtab) throws Exception {}

}
