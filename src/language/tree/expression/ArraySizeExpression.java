package language.tree.expression;

import org.objectweb.asm.MethodVisitor;

import language.compiler.SymbolTable;
import language.compiler.Token;

public class ArraySizeExpression extends Expression {
	
	public final Expression array;

	public ArraySizeExpression(Token firstToken, Expression param) {
		super(firstToken);
		this.array = param;
		this.type = Types.INT;
	}
	
	@Override
	public void decorate(SymbolTable symtab) throws Exception {
		array.decorate(symtab);
		if (array.type != Types.ARRAY) throw new Exception("Invalid ArraySizeExpression");
	}

	@Override
	public void generate(MethodVisitor mv, SymbolTable symtab) throws Exception {
		array.generate(mv, symtab);
		mv.visitInsn(ARRAYLENGTH);
	}

}
