package language.tree;

import static language.compiler.Token.Kind.KW_FLOAT;

import org.objectweb.asm.MethodVisitor;

import language.compiler.SymbolTable;
import language.compiler.Token;
import language.tree.expression.Expression;

public class AssignmentDeclaration extends Declaration {

	public final Expression expression;

	public AssignmentDeclaration(Token firstToken, Token returnToken, Token identToken, Expression expression) {
		super(firstToken, identToken);
		this.expression = expression;
		if (returnToken != null) {
			inputType = firstToken.kind == KW_FLOAT ? Types.FLOAT : Types.ARRAY;
			returnType = returnToken.kind == KW_FLOAT ? Types.FLOAT : Types.ARRAY;
			type = Node.getLambdaType(inputType, returnType);
		}
		else type = getType();
	}
	
	@Override
	public void decorate(SymbolTable symtab) throws Exception {
		if (symtab.topOfStackLookup(identToken.text) != null) throw new Exception();
		symtab.insert(identToken.text, this);
		expression.decorate(symtab);
		if (expression.type != type) throw new Exception();
	}

	@Override
	public void generate(MethodVisitor mv, SymbolTable symtab) throws Exception {
		slotNumber = symtab.slotCount++;
		/*if (isLambda()) {
			FuncLitExpression funcLit = (FuncLitExpression) expression;
			funcLit.lambdaSlot = symtab.lambdaCount++;
			if (funcLit.isAbstract) return;
			funcLit.generate(mv, symtab);
			mv.visitFieldInsn(PUTFIELD, symtab.className, identToken.text, funcLit.getInvocation());
			return;
		}*/
		expression.generate(mv, symtab);
		switch (expression.type) {
		case STRING:
		case ARRAY:
		case LAMBDA:
			mv.visitVarInsn(ASTORE, slotNumber);
			break;
		case FLOAT:
			mv.visitVarInsn(FSTORE, slotNumber);
			break;
		default:
			mv.visitVarInsn(ISTORE, slotNumber);
		}
		//mv.visitLocalVariable(identToken.text, jvmType, null, startLabel, endLabel, slotNumber);
	}

}
