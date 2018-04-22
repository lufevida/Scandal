package language.tree.expression;

import java.util.ArrayList;

import org.objectweb.asm.MethodVisitor;

import language.compiler.SymbolTable;
import language.compiler.Token;

public class ArrayLitExpression extends Expression {
	
	public final ArrayList<Expression> floats;

	public ArrayLitExpression(Token firstToken, ArrayList<Expression> floats) {
		super(firstToken);
		this.floats = floats;
		this.type = Types.ARRAY;
	}
	
	@Override
	public void decorate(SymbolTable symtab) throws Exception {
		for (Expression e : floats) {
			e.decorate(symtab);
			if (e.type != Types.FLOAT && e.type != Types.INT) throw new Exception();
		}
	}

	@Override
	public void generate(MethodVisitor mv, SymbolTable symtab) throws Exception {
		mv.visitLdcInsn(floats.size());
		mv.visitIntInsn(NEWARRAY, T_FLOAT);
		for (int i = 0; i < floats.size(); i++) {
			mv.visitInsn(DUP);
			mv.visitLdcInsn(i);
			floats.get(i).generate(mv, symtab);
			if (floats.get(i).type == Types.INT) mv.visitInsn(I2F);
			mv.visitInsn(FASTORE);
		}
	}

}
