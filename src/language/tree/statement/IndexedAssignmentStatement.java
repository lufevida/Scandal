package language.tree.statement;

import org.objectweb.asm.MethodVisitor;

import language.compiler.SymbolTable;
import language.compiler.Token;
import language.tree.Declaration;
import language.tree.ParamDeclaration;
import language.tree.expression.Expression;
import language.tree.expression.LambdaAppExpression;

public class IndexedAssignmentStatement extends AssignmentStatement {
	
	public final Expression index;

	public IndexedAssignmentStatement(Token firstToken, Expression expression, Expression index) {
		super(firstToken, expression);
		this.index = index;
	}
	
	@Override
	public void decorate(SymbolTable symtab) throws Exception {
		declaration = symtab.lookup(firstToken.text);
		if (declaration == null || declaration.type != Types.ARRAY) throw new Exception();
		index.decorate(symtab);
		if (index.type != Types.INT && index.type != Types.FLOAT) throw new Exception();
		expression.decorate(symtab);
		if (expression instanceof LambdaAppExpression) {
			LambdaAppExpression app = (LambdaAppExpression) expression;
			Declaration dec = app.lambda.declaration;
			if (dec instanceof ParamDeclaration) expression.type = Types.FLOAT;
		}
		if (expression.type != Types.INT && expression.type != Types.FLOAT) throw new Exception();
	}
	
	@Override
	public void generate(MethodVisitor mv, SymbolTable symtab) throws Exception {
		mv.visitVarInsn(ALOAD, declaration.slotNumber);
		index.generate(mv, symtab);
		if (index.type == Types.FLOAT) mv.visitInsn(F2I);
		expression.generate(mv, symtab);
		if (expression.type == Types.INT) mv.visitInsn(I2F);
		mv.visitInsn(FASTORE);
	}

}
