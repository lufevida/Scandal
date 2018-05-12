package language.tree.expression;

import java.util.ArrayList;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;

import language.compiler.SymbolTable;
import language.compiler.Token;
import language.tree.LambdaBlock;
import language.tree.ParamDeclaration;

public class LambdaLitBlock extends LambdaLitExpression {
	
	public final LambdaBlock block;

	public LambdaLitBlock(Token firstToken, ArrayList<ParamDeclaration> params, LambdaBlock block) {
		super(firstToken, params, block.returnExpression);
		this.block = block;
		this.block.lambda = this;
	}

	public void decorate(SymbolTable symtab) throws Exception {
		symtab.enterScope();
		for (int i = 0; i < params.size(); i++) {
			params.get(i).wrap = true;
			params.get(i).slotNumber = i;
			params.get(i).decorate(symtab);
		}
		block.decorate(symtab);
		symtab.leaveScope();
	}

	public void generate(ClassWriter cw, SymbolTable symtab) throws Exception {
		MethodVisitor mv;
		for (int i = 0; i < params.size(); i++) {
			mv = cw.visitMethod(ACC_PRIVATE + ACC_STATIC + ACC_SYNTHETIC, "lambda$" + (dec.lambdaSlot + i), getSig(i), null, null);
			mv.visitCode();
			if (i == params.size() - 1) {
				block.generate(mv, symtab);
				switch (block.returnExpression.type) {
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
			}
			else {
				for (int j = 0; j <= i; j++) mv.visitVarInsn(ALOAD, j);
				mv.visitInvokeDynamicInsn("apply", getSig(i), dec.getHandle(), getObjects(symtab, dec.lambdaSlot, i + 1));
			}
			mv.visitInsn(ARETURN);
			mv.visitMaxs(0, 0);
			mv.visitEnd();
		}
	}

}
