package language.tree.expression;

import org.objectweb.asm.MethodVisitor;

import language.compiler.SymbolTable;
import language.compiler.Token;
import language.tree.Declaration;
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
		if (declaration.isField) mv.visitFieldInsn(GETSTATIC, symtab.className, firstToken.text, declaration.getJvmType());
		else if (declaration instanceof ParamDeclaration) {
			ParamDeclaration dec = (ParamDeclaration) declaration;
			mv.visitVarInsn(ALOAD, dec.slotNumber);
			getTypeValue(dec.type, mv);
		}
		else switch (type) {
		case INT:
		case BOOL:
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
