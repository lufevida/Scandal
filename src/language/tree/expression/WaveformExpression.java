package language.tree.expression;

import org.objectweb.asm.MethodVisitor;

import language.compiler.SymbolTable;
import language.compiler.Token;

public class WaveformExpression extends Expression {

	public WaveformExpression(Token firstToken) {
		super(firstToken);
		this.type = Types.WAVEFORM;
	}
	
	@Override
	public void decorate(SymbolTable symtab) throws Exception {}

	@Override
	public void generate(MethodVisitor mv, SymbolTable symtab) throws Exception {
		mv.visitLdcInsn(getShape());
	}
	
	private int getShape() {
		switch (firstToken.kind) {
		case KW_COSINE: return 1;
		case KW_SAWTOOTH: return 2;
		case KW_SQUARE: return 3;
		case KW_TRIANGLE: return 4;
		case KW_NOISE: return 5;
		default: return 0;
		}
	}

}
