package language.tree;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import language.compiler.SymbolTable;
import language.compiler.Token;
import language.tree.expression.Expression;

public class IndexedAssignmentStatement extends AssignmentStatement implements Opcodes {
	
	public final Expression index;

	public IndexedAssignmentStatement(Token firstToken, Expression expression, Expression index) {
		super(firstToken, expression);
		this.index = index;
	}
	
	@Override
	public void decorate(SymbolTable symtab) throws Exception {
		declaration = symtab.lookup(firstToken.text);
		if (declaration == null) throw new Exception();
		if (declaration.type != Types.ARRAY) throw new Exception();
		index.decorate(symtab);
		if (index.type != Types.INT) throw new Exception();
		expression.decorate(symtab);
		if (expression.type != Types.FLOAT) throw new Exception();
	}
	
	@Override
	public void generate(MethodVisitor mv, SymbolTable symtab) throws Exception {
		mv.visitVarInsn(ALOAD, declaration.slotNumber);
		index.generate(mv, symtab);
		expression.generate(mv, symtab);
		mv.visitInsn(FASTORE);
	}

}
