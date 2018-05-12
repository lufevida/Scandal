package language.tree.statement;

import org.objectweb.asm.MethodVisitor;

import language.compiler.SymbolTable;
import language.compiler.Token;
import language.tree.Declaration;
import language.tree.ParamDeclaration;
import language.tree.expression.Expression;
import language.tree.expression.LambdaAppExpression;

public class AssignmentStatement extends Statement {
	
	public Declaration declaration;

	public AssignmentStatement(Token identToken, Expression expression) {
		super(identToken, expression);
	}
	
	@Override
	public void decorate(SymbolTable symtab) throws Exception {
		declaration = symtab.lookup(firstToken.text);
		if (declaration == null) throw new Exception();
		expression.decorate(symtab);
		if (expression instanceof LambdaAppExpression) {
			LambdaAppExpression app = (LambdaAppExpression) expression;
			Declaration dec = app.lambda.declaration;
			if (dec instanceof ParamDeclaration) expression.type = declaration.type;
		}
		if (expression.type != declaration.type) throw new Exception();
	}

	@Override
	public void generate(MethodVisitor mv, SymbolTable symtab) throws Exception {
		expression.generate(mv, symtab);
		if (declaration instanceof ParamDeclaration) {
			switch (expression.type) {
			case INT:
				mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
				break;
			case BOOL:
				mv.visitMethodInsn(INVOKESTATIC, "java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;", false);
				break;
			case FLOAT:
				mv.visitMethodInsn(INVOKESTATIC, "java/lang/Float", "valueOf", "(F)Ljava/lang/Float;", false);
				break;
			default: break;
			}
			mv.visitVarInsn(ASTORE, declaration.slotNumber);
		}
		else switch (expression.type) {
		case INT:
		case BOOL:
			mv.visitVarInsn(ISTORE, declaration.slotNumber);
			break;
		case FLOAT:
			mv.visitVarInsn(FSTORE, declaration.slotNumber);
			break;
		default:
			mv.visitVarInsn(ASTORE, declaration.slotNumber);
		}
	}

}
