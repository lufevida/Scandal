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

	public IdentExpression(Token firstToken) {
		super(firstToken);
	}
	
	@Override
	public void decorate(SymbolTable symtab) throws Exception {
		declaration = symtab.lookup(firstToken.text);
		if (declaration == null)
			throw new Exception("Missing declaration of: " + firstToken.text);
		type = declaration.type;
	}

	@Override
	public void generate(MethodVisitor mv, SymbolTable symtab) throws Exception {
		if (declaration instanceof LambdaLitDeclaration) mv.visitFieldInsn(GETSTATIC, symtab.className, firstToken.text, "Ljava/util/function/Function;");
		else if (declaration instanceof ParamDeclaration) {
			ParamDeclaration dec = (ParamDeclaration) declaration;
			if (dec.wrap) {
				mv.visitVarInsn(ALOAD, dec.slotNumber);
				switch (dec.type) {
				case INT:
					mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Integer", "intValue", "()I", false);
					return;
				case FLOAT:
					mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Float", "floatValue", "()F", false);
					return;
				default: return;
				}
			}
			else switch (dec.type) {
			case INT:
				mv.visitVarInsn(ILOAD, dec.slotNumber);
				return;
			case FLOAT:
				mv.visitVarInsn(FLOAD, dec.slotNumber);
				return;
			default:
				mv.visitVarInsn(ALOAD, dec.slotNumber);
				return;	
			}
		}
		else if (isReturnExpression) {
			AssignmentDeclaration dec = (AssignmentDeclaration) declaration;
			if (!dec.expression.isReturnExpression) dec.expression.generate(mv, symtab); // this is an outlier
			else if (dec.expression.type == Types.FLOAT) mv.visitVarInsn(FLOAD, dec.slotNumber);
			else if (dec.expression.type == Types.ARRAY) mv.visitVarInsn(ALOAD, dec.slotNumber);
		}
		else switch (type) {
		case INT:
			mv.visitVarInsn(ILOAD, declaration.slotNumber);
			return;
		case FLOAT:
			mv.visitVarInsn(FLOAD, declaration.slotNumber);
			return;
		default:
			mv.visitVarInsn(ALOAD, declaration.slotNumber);
			return;	
		}
	}

}
