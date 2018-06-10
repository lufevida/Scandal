package language.tree.statement;

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
		if (symtab.scopeNumber > 0) throw new Exception();
		expression.decorate(symtab);
		if (expression.type != Types.STRING) throw new Exception();
	}

	@Override
	public void generate(MethodVisitor mv, SymbolTable symtab) throws Exception {}

}
