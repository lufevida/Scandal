package language.tree;

import static language.tree.Node.Types.BOOL;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import language.compiler.SymbolTable;
import language.compiler.Token;
import language.tree.expression.Expression;

public class IfStatement extends Statement implements Opcodes {

	public final Block block;

	public IfStatement(Token firstToken, Expression expression, Block block) {
		super(firstToken, expression);
		this.block = block;
	}
	
	@Override
	public void decorate(SymbolTable symtab) throws Exception {
		expression.decorate(symtab);
		if (expression.type != BOOL) throw new Exception();
		block.decorate(symtab);
	}

	@Override
	public void generate(MethodVisitor mv, SymbolTable symtab) throws Exception {
		Label label = new Label();
		expression.generate(mv, symtab);
		mv.visitInsn(ICONST_1);
		mv.visitJumpInsn(IF_ICMPNE, label);
		block.generate(mv, symtab);
		mv.visitLabel(label);
	}

}
