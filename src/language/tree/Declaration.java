package language.tree;

import language.compiler.Token;

public abstract class Declaration extends Node {
	
	public final Token identToken;
	public int slotNumber;
	public Types inputType;
	public Types returnType;
	public boolean isField = false;
	
	public Declaration(Token firstToken, Token identToken) {
		super(firstToken);
		this.identToken = identToken;
	}

}
