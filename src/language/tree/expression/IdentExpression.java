package language.tree.expression;

import org.objectweb.asm.MethodVisitor;

import language.compiler.SymbolTable;
import language.compiler.Token;
import language.tree.AssignmentDeclaration;
import language.tree.Declaration;
import language.tree.LambdaLitDeclaration;
import language.tree.ParamDeclaration;

public class IdentExpression extends Expression {
	
	public Declaration declaration;
	//public FuncLitExpression funcLit;

	public IdentExpression(Token firstToken) {
		super(firstToken);
	}
	
	@Override
	public void decorate(SymbolTable symtab) throws Exception {
		declaration = symtab.lookup(firstToken.text);
		if (declaration == null)
			throw new Exception("Missing declaration in line " + firstToken.lineNumber);
		type = declaration.type;
		/*if (isLambda()) {
			AssignmentDeclaration dec = (AssignmentDeclaration) declaration;
			funcLit = (FuncLitExpression) dec.expression;
		}*/
	}

	@Override
	public void generate(MethodVisitor mv, SymbolTable symtab) throws Exception {
		if (declaration instanceof ParamDeclaration) {
			ParamDeclaration dec = (ParamDeclaration) declaration;
			if (dec.wrap) {
				mv.visitVarInsn(ALOAD, dec.slotNumber);
				switch (dec.type) {
				case INT:
					mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Integer", "intValue", "()I", false);
					break;
				case FLOAT:
					mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Float", "floatValue", "()F", false);
					break;
				default: break;
				}
			}			
			else if (dec.expression != null) dec.expression.generate(mv, symtab); // this is a forced literal
			else if (dec.type == Types.INT) mv.visitVarInsn(ILOAD, dec.slotNumber);
			else if (dec.type == Types.FLOAT) mv.visitVarInsn(FLOAD, dec.slotNumber);
			else if (dec.type == Types.ARRAY) mv.visitVarInsn(ALOAD, dec.slotNumber);
		}
		else if (isReturnExpression) {
			AssignmentDeclaration dec = (AssignmentDeclaration) declaration;
			if (!dec.expression.isReturnExpression) dec.expression.generate(mv, symtab); // this is an outlier
			else if (dec.expression.type == Types.FLOAT) mv.visitVarInsn(FLOAD, dec.slotNumber);
			else if (dec.expression.type == Types.ARRAY) mv.visitVarInsn(ALOAD, dec.slotNumber);
		}
		/*else if (isLambda()) {
			mv.visitVarInsn(ALOAD, 0);
			mv.visitFieldInsn(GETFIELD, symtab.className, firstToken.text, funcLit.getInvocation());
		}*/
		else if (declaration instanceof LambdaLitDeclaration) mv.visitFieldInsn(GETSTATIC, symtab.className, firstToken.text, "Ljava/util/function/Function;");
		else if (type == Types.STRING || type == Types.ARRAY || type == Types.LAMBDA) mv.visitVarInsn(ALOAD, declaration.slotNumber);
		else if (type == Types.FLOAT) mv.visitVarInsn(FLOAD, declaration.slotNumber);
		else mv.visitVarInsn(ILOAD, declaration.slotNumber);
	}

}
