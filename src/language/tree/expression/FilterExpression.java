package language.tree.expression;

import org.objectweb.asm.MethodVisitor;

import language.compiler.SymbolTable;
import language.compiler.Token;

public class FilterExpression extends Expression {

	public FilterExpression(Token firstToken) {
		super(firstToken);
		this.type = Types.FILTER;
	}
	
	@Override
	public void decorate(SymbolTable symtab) throws Exception {}

	@Override
	public void generate(MethodVisitor mv, SymbolTable symtab) throws Exception {
		mv.visitLdcInsn(getMethod());
	}
	
	private int getMethod() {
		switch (firstToken.kind) {
		case KW_ALLPASS: return 1;
		case KW_BANDPASS: return 2;
		case KW_BANDSTOP: return 3;
		case KW_LOWPASS: return 4;
		case KW_HIPASS: return 5;
		case KW_LOWSHELF: return 6;
		case KW_HISHELF: return 7;
		case KW_PEAKING: return 8;
		default: return 0;
		}
	}

}
