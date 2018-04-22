package language.tree.expression;

import org.objectweb.asm.MethodVisitor;

import language.compiler.SymbolTable;
import language.compiler.Token;

public class NewArrayExpression extends Expression {
	
	public final Expression size;

	public NewArrayExpression(Token firstToken, Expression size) {
		super(firstToken);
		this.size = size;
		this.type = Types.ARRAY;
	}
	
	@Override
	public void decorate(SymbolTable symtab) throws Exception {
		size.decorate(symtab);
		if (size.type != Types.INT && size.type != Types.FLOAT)
			throw new Exception("Invalid EmptyArrayExpression in line " + firstToken.lineNumber);
	}

	@Override
	public void generate(MethodVisitor mv, SymbolTable symtab) throws Exception {
		size.generate(mv, symtab);
		if (size.type == Types.FLOAT) mv.visitInsn(F2I);
		mv.visitIntInsn(NEWARRAY, T_FLOAT);
	}

}
