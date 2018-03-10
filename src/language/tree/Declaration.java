package language.tree;

import org.objectweb.asm.Label;

import language.compiler.Token;

public abstract class Declaration extends Node {
	
	public final Token identToken;
	public int slotNumber;
	public Types inputType;
	public Types returnType;
	public Label startLabel;
	public Label endLabel;
	
	public Declaration(Token firstToken, Token identToken) {
		super(firstToken);
		this.identToken = identToken;
		jvmType = super.getJvmType();
	}

}
