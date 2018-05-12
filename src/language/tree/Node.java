package language.tree;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import language.compiler.SymbolTable;
import language.compiler.Token;

public abstract class Node implements Opcodes {
	
	public static enum Types { INT, FLOAT, BOOL, STRING, ARRAY, LAMBDA }

	public final Token firstToken;
	public Types type;

	public Node(Token firstToken) {
		this.firstToken = firstToken;
		type = getType(firstToken.kind);
	}
	
	private static Types getType(Token.Kind kind) {
		switch (kind) {
		case KW_INT: return Types.INT;
		case KW_FLOAT: return Types.FLOAT;
		case KW_BOOL: return Types.BOOL;
		case KW_STRING: return Types.STRING;
		case KW_ARRAY: return Types.ARRAY;
		case KW_LAMBDA: return Types.LAMBDA;
		default: return null;
		}
	}

	public String getJvmType() {
		switch (type) {
		case INT: return "I";
		case FLOAT: return "F";
		case BOOL: return "Z";
		case STRING: return "Ljava/lang/String;";
		case ARRAY: return "[F";
		case LAMBDA: return "Ljava/util/function/Function;";
		default: return null;
		}
	}
	
	public String getClassType() {
		switch (type) {
		case INT: return "Ljava/lang/Integer;";
		case FLOAT: return "Ljava/lang/Float;";
		case BOOL: return "Ljava/lang/Boolean;";
		default: return getJvmType();
		}
	}

	public abstract void decorate(SymbolTable symtab) throws Exception;
	
	public abstract void generate(MethodVisitor mv, SymbolTable symtab) throws Exception;

}
