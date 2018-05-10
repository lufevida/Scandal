package language.tree.statement;

import org.objectweb.asm.MethodVisitor;

import javafx.application.Platform;
import language.compiler.SymbolTable;
import language.compiler.Token;
import language.tree.expression.Expression;

public class PlotStatement extends Statement {
	
	public final Expression array;
	public final Expression points;

	public PlotStatement(Token firstToken, Expression title, Expression array, Expression points) {
		super(firstToken, title);
		this.array = array;
		this.points = points;
	}
	
	@Override
	public void decorate(SymbolTable symtab) throws Exception {
		expression.decorate(symtab);
		if (expression.type != Types.STRING) throw new Exception();
		array.decorate(symtab);
		if (array.type != Types.ARRAY) throw new Exception();
		points.decorate(symtab);
		if (points.type != Types.INT && points.type != Types.FLOAT) throw new Exception();
	}

	@Override
	public void generate(MethodVisitor mv, SymbolTable symtab) throws Exception {
		if (Platform.isFxApplicationThread()) {
			mv.visitTypeInsn(NEW, "framework/utilities/PlotUtility");
			expression.generate(mv, symtab);
			array.generate(mv, symtab);
			points.generate(mv, symtab);
			if (points.type == Types.FLOAT) mv.visitInsn(F2I);
			mv.visitMethodInsn(INVOKESPECIAL, "framework/utilities/PlotUtility", "<init>", "(Ljava/lang/String;[FI)V", false);
		}
	}

}
