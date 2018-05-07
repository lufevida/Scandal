package language.tree;

import static language.compiler.Token.Kind.KW_FLOAT;

import org.objectweb.asm.MethodVisitor;

import language.compiler.SymbolTable;
import language.compiler.Token;
import language.tree.expression.Expression;

public class ParamDeclaration extends Declaration {
	
	public Expression expression;
	public boolean wrap = false;

	public ParamDeclaration(Token firstToken, Token returnToken, Token identToken) {
		super(firstToken, identToken);
		if (returnToken != null) {
			inputType = firstToken.kind == KW_FLOAT ? Types.FLOAT : Types.ARRAY;
			returnType = returnToken.kind == KW_FLOAT ? Types.FLOAT : Types.ARRAY;
			this.type = Node.getLambdaType(inputType, returnType);
		}
		else this.type = super.getType();
	}
	
	@Override
	public void decorate(SymbolTable symtab) throws Exception {
		Declaration testResult = symtab.topOfStackLookup(identToken.text);
		if (testResult != null) throw new Exception("Illegal redeclaration");
		symtab.insert(identToken.text, this);
	}

	@Override
	public void generate(MethodVisitor mv, SymbolTable symtab) throws Exception {
		slotNumber = symtab.slotCount++;
	}

}
