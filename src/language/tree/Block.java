package language.tree;

import java.util.ArrayList;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

import language.compiler.SymbolTable;
import language.compiler.Token;

public class Block extends Node {

	public final ArrayList<Declaration> declarations;
	public final ArrayList<Statement> statements;

	public Block(Token firstToken, ArrayList<Declaration> declarations, ArrayList<Statement> statements) {
		super(firstToken);
		this.declarations = declarations;
		this.statements = statements;
	}
	
	@Override
	public void decorate(SymbolTable symtab) throws Exception {
		symtab.enterScope();
		for (Declaration declaration : declarations) declaration.decorate(symtab);
		for (Statement statement : statements) {
			if (statement.getClass() == ImportStatement.class) throw new Exception();
			statement.decorate(symtab);
		}
		symtab.leaveScope();
	}

	@Override
	public void generate(MethodVisitor mv, SymbolTable symtab) throws Exception {
		Label blockStart = new Label();
		Label blockEnd = new Label();
		mv.visitLabel(blockStart);
		mv.visitLabel(blockEnd);
		for (Declaration declaration : declarations) {
			declaration.startLabel = blockStart;
			declaration.endLabel = blockEnd;
			declaration.generate(mv, symtab);
		}
		for (Statement statement : statements) statement.generate(mv, symtab);
	}

}
