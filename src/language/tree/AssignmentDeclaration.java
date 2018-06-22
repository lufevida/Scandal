package language.tree;

import org.objectweb.asm.MethodVisitor;

import language.compiler.SymbolTable;
import language.compiler.Token;
import language.tree.expression.Expression;
import language.tree.expression.LambdaAppExpression;

public class AssignmentDeclaration extends Declaration {

	public final Expression expression;

	public AssignmentDeclaration(Token firstToken, Token identToken, Expression expression) {
		super(firstToken, identToken);
		this.expression = expression;
	}
	
	@Override
	public void decorate(SymbolTable symtab) throws Exception {
		if (symtab.topOfStackLookup(identToken.text) != null)
			throw new Exception("Redeclaration in line: " + firstToken.lineNumber);
		symtab.insert(identToken.text, this);
		slotNumber = symtab.slotCount++;
		expression.decorate(symtab);
		if (expression instanceof LambdaAppExpression) {
			Declaration dec = ((LambdaAppExpression) expression).lambda.declaration;
			if (dec instanceof ParamDeclaration) expression.type = type;
		}
		if ((type == Types.INT || type == Types.FLOAT) && (expression.type != Types.INT && expression.type != Types.FLOAT))
			throw new Exception("Type mismatch in line: " + firstToken.lineNumber);
		else if ((type != Types.INT && type != Types.FLOAT) && expression.type != type)
			throw new Exception("Type mismatch in line: " + firstToken.lineNumber);		
	}

	@Override
	public void generate(MethodVisitor mv, SymbolTable symtab) throws Exception {
		expression.generate(mv, symtab);
		if (type == Types.INT && expression.type == Types.FLOAT) mv.visitInsn(F2I);
		else if (type == Types.FLOAT && expression.type == Types.INT) mv.visitInsn(I2F);
		switch (type) {
		case INT:
		case BOOL:
			mv.visitVarInsn(ISTORE, slotNumber);
			break;
		case FLOAT:
			mv.visitVarInsn(FSTORE, slotNumber);
			break;
		default:
			mv.visitVarInsn(ASTORE, slotNumber);
		}
	}

}
