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
		this.type = getType();
	}
	
	public static Types getType(Token.Kind kind) {
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
	
	public Types getType() {
		return getType(firstToken.kind);
	}
	
	public String getJvmType() {
		switch (firstToken.kind) {
		case KW_INT: return "I";
		case KW_FLOAT: return "F";
		case KW_BOOL: return "Z";
		case KW_STRING: return "Ljava/lang/String;";
		case KW_ARRAY: return "[F";
		case KW_LAMBDA: return "Ljava/util/function/Function;";
		default: return null;
		}
	}
	
	public String getClassType() {
		switch (firstToken.kind) {
		case KW_INT: return "Ljava/lang/Integer;";
		case KW_FLOAT: return "Ljava/lang/Float;";
		case KW_STRING: return "Ljava/lang/String;";
		case KW_BOOL: return "Ljava/lang/Boolean;";
		case KW_ARRAY: return "[F";
		case KW_LAMBDA: return "Ljava/util/function/Function;";
		default: return null;
		}
	}

	public abstract void decorate(SymbolTable symtab) throws Exception;
	
	public abstract void generate(MethodVisitor mv, SymbolTable symtab) throws Exception;

}
