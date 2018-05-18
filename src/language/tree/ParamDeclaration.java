package language.tree;

import org.objectweb.asm.MethodVisitor;

import language.compiler.SymbolTable;
import language.compiler.Token;

public class ParamDeclaration extends Declaration {

	public ParamDeclaration(Token firstToken, Token identToken) {
		super(firstToken, identToken);
	}
	
	@Override
	public void decorate(SymbolTable symtab) throws Exception {
		if (symtab.topOfStackLookup(identToken.text) != null) throw new Exception();
		symtab.insert(identToken.text, this);
	}

	@Override
	public void generate(MethodVisitor mv, SymbolTable symtab) throws Exception {
		slotNumber = symtab.slotCount++;
	}

}
