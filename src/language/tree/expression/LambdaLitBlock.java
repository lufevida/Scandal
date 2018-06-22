package language.tree.expression;

import java.util.ArrayList;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;

import language.compiler.SymbolTable;
import language.compiler.Token;
import language.tree.ParamDeclaration;
import language.tree.ReturnBlock;

public class LambdaLitBlock extends LambdaLitExpression {
	
	public final ReturnBlock block;

	public LambdaLitBlock(Token firstToken, ArrayList<ParamDeclaration> params, ReturnBlock block) {
		super(firstToken, params, block.returnExpression);
		this.block = block;
		this.type = Types.LAMBDA;
	}

	public void decorate(SymbolTable symtab) throws Exception {
		symtab.enterScope();
		for (int i = 0; i < params.size(); i++) {
			params.get(i).slotNumber = i;
			params.get(i).decorate(symtab);
		}
		int temp = symtab.slotCount;
		symtab.slotCount = params.size();
		block.decorate(symtab);
		symtab.slotCount = temp;
		symtab.leaveScope();
		lambdaSlot = symtab.lambdaCount;
		symtab.lambdaCount += params.size();
	}

	public void generate(ClassWriter cw, SymbolTable symtab) throws Exception {
		MethodVisitor mv;
		for (int i = 0; i < params.size(); i++) {
			mv = cw.visitMethod(ACC_PRIVATE + ACC_STATIC + ACC_SYNTHETIC, "lambda$" + (lambdaSlot + i), getSig(i), null, null);
			if (i == params.size() - 1) {
				block.generate(mv, symtab);
				getValueOf(block.returnExpression.type, mv);
			}
			else {
				for (int j = 0; j <= i; j++) mv.visitVarInsn(ALOAD, j);
				mv.visitInvokeDynamicInsn("apply", getSig(i), getHandle(), getObjects(symtab, lambdaSlot, i + 1));
			}
			mv.visitInsn(ARETURN);
			mv.visitMaxs(0, 0);
		}
	}

}
