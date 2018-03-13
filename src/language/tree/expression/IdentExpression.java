package language.tree.expression;

import org.objectweb.asm.MethodVisitor;

import language.compiler.SymbolTable;
import language.compiler.Token;
import language.tree.AssignmentDeclaration;
import language.tree.Declaration;
import language.tree.ParamDeclaration;

public class IdentExpression extends Expression {
	
	public Declaration declaration;
	public FuncLitExpression funcLit;

	public IdentExpression(Token firstToken) {
		super(firstToken);
	}
	
	@Override
	public void decorate(SymbolTable symtab) throws Exception {
		declaration = symtab.lookup(firstToken.text);
		if (declaration == null) throw new Exception("Variable must have been declared in some enclosing scope");
		if (declaration.isLambda()) {
			AssignmentDeclaration dec = (AssignmentDeclaration) declaration;
			funcLit = (FuncLitExpression) dec.expression;
		}
		type = declaration.type;
	}

	@Override
	public void generate(MethodVisitor mv, SymbolTable symtab) throws Exception {
		if (isReturnExpression) {
			if (declaration.getClass() == ParamDeclaration.class) {
				if (declaration.type == Types.FLOAT) mv.visitVarInsn(FLOAD, declaration.slotNumber);
				else if (declaration.type == Types.ARRAY) mv.visitVarInsn(ALOAD, declaration.slotNumber);
			}
			else if (declaration.getClass() == AssignmentDeclaration.class) {
				AssignmentDeclaration dec = (AssignmentDeclaration) declaration;
				if (dec.expression.isReturnExpression) {
					if (dec.expression.type == Types.FLOAT) mv.visitVarInsn(FLOAD, dec.slotNumber);
					else if (dec.expression.type == Types.ARRAY) mv.visitVarInsn(ALOAD, dec.slotNumber);
				}
				else dec.expression.generate(mv, symtab); // TODO: outliers
			}
		}
		else if (isLambda()) {
			mv.visitVarInsn(ALOAD, 0);
			mv.visitFieldInsn(GETFIELD, symtab.className, firstToken.text, symtab.lambdas.get(firstToken.text).getInvocation());
		}
		else if (type == Types.STRING || type == Types.ARRAY) mv.visitVarInsn(ALOAD, declaration.slotNumber);
		else if (type == Types.FLOAT) mv.visitVarInsn(FLOAD, declaration.slotNumber);
		else mv.visitVarInsn(ILOAD, declaration.slotNumber);
	}

}
