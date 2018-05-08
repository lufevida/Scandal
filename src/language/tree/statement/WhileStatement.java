package language.tree.statement;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

import language.compiler.SymbolTable;
import language.compiler.Token;
import language.tree.Block;
import language.tree.expression.Expression;

public class WhileStatement extends Statement {

	public final Block block;

	public WhileStatement(Token firstToken, Expression expression, Block block) {
		super(firstToken, expression);
		this.block = block;
	}
	
	@Override
	public void decorate(SymbolTable symtab) throws Exception {
		expression.decorate(symtab);
		if (expression.type != Types.BOOL) throw new Exception("Invalid WhileStatement");
		block.decorate(symtab);
	}

	@Override
	public void generate(MethodVisitor mv, SymbolTable symtab) throws Exception {
		Label l1 = new Label();
		Label l2 = new Label();
		mv.visitLabel(l1);
		expression.generate(mv, symtab);
		mv.visitInsn(ICONST_1);
		mv.visitJumpInsn(IF_ICMPNE, l2);
		block.generate(mv, symtab);
		mv.visitJumpInsn(GOTO, l1);
		mv.visitLabel(l2);
	}

}
