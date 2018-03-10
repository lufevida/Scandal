package language.tree.expression;

import org.objectweb.asm.MethodVisitor;

import language.compiler.SymbolTable;
import language.compiler.Token;
import language.tree.Declaration;

public class ArrayItemExpression extends Expression {
	
	public final Expression index;
	public Declaration arrayDec;

	public ArrayItemExpression(Token firstToken, Expression index) {
		super(firstToken);
		this.index = index;
		this.type = Types.FLOAT;
	}
	
	@Override
	public void decorate(SymbolTable symtab) throws Exception {
		arrayDec = symtab.lookup(firstToken.text);
		if (arrayDec == null) throw new Exception("Array must have been declared in some enclosing scope");
		if (arrayDec.type != Types.ARRAY) throw new Exception("Type mismatch");
		index.decorate(symtab);
		if (index.type != Types.INT) throw new Exception("Array index must be an integer");
	}

	@Override
	public void generate(MethodVisitor mv, SymbolTable symtab) throws Exception {
		mv.visitVarInsn(ALOAD, arrayDec.slotNumber);
		index.generate(mv, symtab);
		mv.visitInsn(FALOAD);
	}

}
