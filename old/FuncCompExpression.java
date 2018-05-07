package language.tree.expression;

import java.util.ArrayList;

import org.objectweb.asm.MethodVisitor;

import language.compiler.SymbolTable;
import language.compiler.Token;
import language.tree.AssignmentDeclaration;
import language.tree.Node;

public class FuncCompExpression extends Expression {
	
	public final ArrayList<IdentExpression> idents;
	public final Expression param;
	protected ArrayList<FuncLitExpression> lambdas = new ArrayList<>();
	protected Types inputType;
	protected Types compositeType;
	public boolean isPartial;

	public FuncCompExpression(Token firstToken, ArrayList<IdentExpression> idents, Expression param) {
		super(firstToken);
		this.idents = idents;
		this.param = param;
	}
	
	@Override
	public void decorate(SymbolTable symtab) throws Exception {
		for (IdentExpression ident : idents) {
			AssignmentDeclaration dec = (AssignmentDeclaration) symtab.lookup(ident.firstToken.text);
			if (dec == null) throw new Exception("Missing declaration in line " + firstToken.lineNumber);
			FuncLitExpression lambda = (FuncLitExpression) dec.expression;
			if (lambda.isAbstract) throw new Exception("Composed functions can only take one parameter");
			lambdas.add(lambda);
		}
		for (int i = 0; i < lambdas.size() - 1; i++) {
			Types thisReturn = lambdas.get(i).returnType;
			Types nextInput = lambdas.get(i + 1).inputType;
			if (thisReturn != nextInput) throw new Exception("Type mismatch");
		}
		type = lambdas.get(lambdas.size() - 1).returnType;
		param.decorate(symtab);
		inputType = param.type;
		if (inputType != lambdas.get(0).inputType) throw new Exception("Type mismatch");
		compositeType = Node.getLambdaType(inputType, type);
		//isPartial = symtab.lookup(param.firstToken.text).getClass() == ParamDeclaration.class;
	}

	@Override
	public void generate(MethodVisitor mv, SymbolTable symtab) throws Exception {
		mv.visitVarInsn(ALOAD, 0);
		mv.visitFieldInsn(GETFIELD, symtab.className, idents.get(0).firstToken.text, lambdas.get(0).getInvocation());
		for (int i = 1; i < idents.size(); i++) {
			mv.visitVarInsn(ALOAD, 0);
			mv.visitFieldInsn(GETFIELD, symtab.className, idents.get(i).firstToken.text, lambdas.get(i).getInvocation());
			// TODO: incomplete implementation
			if (lambdas.get(i).returnExpression.type == Types.FLOAT)
				mv.visitMethodInsn(INVOKEINTERFACE, "language/interfaces/FloatFloat", "then", "(Llanguage/interfaces/FloatFloat;)Llanguage/interfaces/FloatFloat;", true);
			else if (lambdas.get(i).returnExpression.type == Types.ARRAY)
				mv.visitMethodInsn(INVOKEINTERFACE, "language/interfaces/ArrayArray", "then", "(Llanguage/interfaces/ArrayArray;)Llanguage/interfaces/ArrayArray;", true);
		}
		param.generate(mv, symtab);
		// TODO: incomplete implementation
		if (inputType == Types.FLOAT) mv.visitMethodInsn(INVOKEINTERFACE, "language/interfaces/FloatFloat", "apply", "(F)F", true);
		else if (inputType == Types.ARRAY) mv.visitMethodInsn(INVOKEINTERFACE, "language/interfaces/ArrayArray", "apply", "([F)[F", true);
	}

}
