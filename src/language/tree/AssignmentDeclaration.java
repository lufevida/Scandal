package language.tree;

import static language.compiler.Token.Kind.KW_FLOAT;

import org.objectweb.asm.MethodVisitor;

import language.compiler.Lambda;
import language.compiler.SymbolTable;
import language.compiler.Token;
import language.tree.expression.Expression;
import language.tree.expression.FuncLitExpression;

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
		Declaration testResult = symtab.topOfStackLookup(identToken.text);
		if (testResult != null) throw new Exception();
		expression.decorate(symtab);
		if (expression.type != type) throw new Exception();
		symtab.insert(identToken.text, this);
	}

	@Override
	public void generate(MethodVisitor mv, SymbolTable symtab) throws Exception {
		slotNumber = symtab.slotCount++;
		if (isLambda()) {
			FuncLitExpression funcLit = (FuncLitExpression) expression;
			if (funcLit.isAbstract) {
				funcLit.lambdaSlot = symtab.lambdaCount++;
				symtab.lambdas.add(new Lambda(identToken.text, funcLit.lambdaSlot, funcLit, true));
				for (UnassignedDeclaration param : funcLit.params)
					if (param.isLambda())
						symtab.lambdas.add(new Lambda(param.identToken.text, Integer.MAX_VALUE, null, true));
				return;
			}
			funcLit.lambdaSlot = symtab.lambdaCount++;
			symtab.lambdas.add(new Lambda(identToken.text, funcLit.lambdaSlot, funcLit, false));
			jvmType = funcLit.getInvocation();
			funcLit.generate(mv, symtab);
			mv.visitFieldInsn(PUTFIELD, symtab.className, identToken.text, jvmType);
			return;
		}
		expression.generate(mv, symtab);
		switch (expression.type) {
		case STRING:
		case ARRAY:
			mv.visitVarInsn(ASTORE, slotNumber);
			break;
		case FLOAT:
			mv.visitVarInsn(FSTORE, slotNumber);
			break;
		default:
			mv.visitVarInsn(ISTORE, slotNumber);
		}
		mv.visitLocalVariable(identToken.text, jvmType, null, startLabel, endLabel, slotNumber);
	}

}
