package language.tree;

import java.util.ArrayList;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

import language.compiler.SymbolTable;
import language.compiler.Token;

public class Block extends Node {

	public final ArrayList<Node> nodes;

	public Block(Token firstToken, ArrayList<Node> nodes) {
		super(firstToken);
		this.nodes = nodes;
	}
	
	@Override
	public void decorate(SymbolTable symtab) throws Exception {
		symtab.enterScope();
		for (Node node : nodes) {
			if (node.getClass() == ImportStatement.class) throw new Exception();
			node.decorate(symtab);
		}
		symtab.leaveScope();
	}

	@Override
	public void generate(MethodVisitor mv, SymbolTable symtab) throws Exception {
		Label blockStart = new Label();
		Label blockEnd = new Label();
		mv.visitLabel(blockStart);
		mv.visitLabel(blockEnd);
		for (Node node : nodes) {
			if (node.getClass() == AssignmentDeclaration.class) {
				AssignmentDeclaration dec = (AssignmentDeclaration) node;
				dec.startLabel = blockStart;
				dec.endLabel = blockEnd;
			}
			node.generate(mv, symtab);
		}
	}

}
