package language.tree;

import org.objectweb.asm.Handle;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import language.compiler.SymbolTable;
import language.compiler.Token;
import language.tree.expression.LambdaLitExpression;

public class LambdaLitDeclaration extends Declaration {
	
	public final LambdaLitExpression lambda;
	public int lambdaSlot;
	public String lambdaSig;

	public LambdaLitDeclaration(Token firstToken, Token identToken, LambdaLitExpression lambda) {
		super(firstToken, identToken);
		this.lambda = lambda;
		this.lambda.dec = this;
		this.type = getType();
	}

	@Override
	public void decorate(SymbolTable symtab) throws Exception {
		if (symtab.topOfStackLookup(identToken.text) != null) throw new Exception("Redeclartion in line " + firstToken.lineNumber);
		lambda.decorate(symtab);
		symtab.insert(identToken.text, this);		
	}

	@Override
	public void generate(MethodVisitor mv, SymbolTable symtab) throws Exception {
		lambdaSlot = symtab.newLambdaCount;
		symtab.newLambdaCount += lambda.params.size();
		lambdaSig = "(" + lambda.params.get(0).getClassType() + ")";
		if (lambda.params.size() == 1) lambdaSig += lambda.block.returnExpression.getClassType();
		else lambdaSig += getJvmType();
		mv.visitInvokeDynamicInsn("apply", "()Ljava/util/function/Function;", getHandle(), getObjects(symtab, lambdaSlot, lambdaSig));
		mv.visitFieldInsn(PUTSTATIC, symtab.className, identToken.text, getJvmType());
	}
	
	public Handle getHandle() {
		String sig = "(Ljava/lang/invoke/MethodHandles$Lookup;";
		sig += "Ljava/lang/String;";
		sig += "Ljava/lang/invoke/MethodType;";
		sig += "Ljava/lang/invoke/MethodType;";
		sig += "Ljava/lang/invoke/MethodHandle;";
		sig += "Ljava/lang/invoke/MethodType;)";
		sig += "Ljava/lang/invoke/CallSite;";
		return new Handle(Opcodes.H_INVOKESTATIC, "java/lang/invoke/LambdaMetafactory", "metafactory", sig, false);
	}
	
	public Object[] getObjects(SymbolTable symtab, int slot, String sig) {
		Object[] objs = new Object[3];
		objs[0] = Type.getType("(Ljava/lang/Object;)Ljava/lang/Object;");
		objs[1] = new Handle(Opcodes.H_INVOKESTATIC, symtab.className, "lambda$" + slot, sig, false);
		objs[2] = Type.getType(sig);
		return objs;
	}

	public String getFullSig() {
		String sig = "";
		for (int i = 0; i < lambda.params.size(); i++)
			sig += "Ljava/util/function/Function<" + lambda.params.get(i).getClassType();
		sig += lambda.block.returnExpression.getClassType();
		for (int i = 0; i < lambda.params.size(); i++) sig += ">;";
		return sig;
	}

}
