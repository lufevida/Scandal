package language.tree.expression;

import java.util.ArrayList;

import org.objectweb.asm.MethodVisitor;

import language.compiler.SymbolTable;
import language.compiler.Token;

public class ArrayLitExpression extends Expression {
	
	public final ArrayList<Float> floats;

	public ArrayLitExpression(Token firstToken, ArrayList<Float> floats) {
		super(firstToken);
		this.floats = floats;
		this.type = Types.ARRAY;
	}
	
	@Override
	public void decorate(SymbolTable symtab) throws Exception {}

	@Override
	public void generate(MethodVisitor mv, SymbolTable symtab) throws Exception {
		mv.visitLdcInsn(floats.size());
		mv.visitIntInsn(NEWARRAY, T_FLOAT);
		for (int i = 0; i < floats.size(); i++) {
			mv.visitInsn(DUP);
			mv.visitLdcInsn(i);
			mv.visitLdcInsn(floats.get(i));
			mv.visitInsn(FASTORE);
		}
	}

}
