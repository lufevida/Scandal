package language.tree.expression;

import org.objectweb.asm.MethodVisitor;

import language.compiler.SymbolTable;
import language.compiler.Token;

public class ArrayItemExpression extends Expression {

	public final IdentExpression array;
	public final Expression index;

	public ArrayItemExpression(Token firstToken, IdentExpression array, Expression index) {
		super(firstToken);
		this.array = array;
		this.index = index;
		this.type = Types.FLOAT;
	}

	@Override
	public void decorate(SymbolTable symtab) throws Exception {
		array.decorate(symtab);
		if (array.type != Types.ARRAY) throw new Exception("Type mismatch in line " + firstToken.lineNumber);
		index.decorate(symtab);
		if (index.type != Types.INT && index.type != Types.FLOAT) throw new Exception("Illegal array index in line " + firstToken.lineNumber);
	}

	@Override
	public void generate(MethodVisitor mv, SymbolTable symtab) throws Exception {
		array.generate(mv, symtab);
		index.generate(mv, symtab);
		if (index.type == Types.FLOAT) mv.visitInsn(F2I);
		mv.visitInsn(FALOAD);
	}

}
