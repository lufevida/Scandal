package language.tree;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import language.compiler.SymbolTable;
import language.compiler.Token;

public abstract class Node implements Opcodes {
	
	public static enum Types {
		INT,
		FLOAT,
		BOOL,
		STRING,
		ARRAY,
		FILTER,
		WAVEFORM,
		FLOAT_FLOAT,
		FLOAT_ARRAY,
		ARRAY_FLOAT,
		ARRAY_ARRAY
	}

	public final Token firstToken;
	public Types type;
	public String jvmType;

	public Node(Token firstToken) {
		this.firstToken = firstToken;
		this.type = getType();
	}
	
	public boolean isLambda() {
		return
				type == Types.FLOAT_FLOAT ||
				type == Types.FLOAT_ARRAY ||
				type == Types.ARRAY_FLOAT ||
				type == Types.ARRAY_ARRAY;
	}

	public static Types getLambdaType(Types inputType, Types returnType) {
		if (inputType == Types.FLOAT && returnType == Types.FLOAT) return Types.FLOAT_FLOAT;
		if (inputType == Types.FLOAT && returnType == Types.ARRAY) return Types.FLOAT_ARRAY;
		if (inputType == Types.ARRAY && returnType == Types.FLOAT) return Types.ARRAY_FLOAT;
		if (inputType == Types.ARRAY && returnType == Types.ARRAY) return Types.ARRAY_ARRAY;
		return null;
	}
	
	public Types getType() {
		switch (firstToken.kind) {
		case KW_INT: return Types.INT;
		case KW_FLOAT: return Types.FLOAT;
		case KW_BOOL: return Types.BOOL;
		case KW_STRING: return Types.STRING;
		case KW_ARRAY: return Types.ARRAY;
		case KW_FILTER: return Types.FILTER;
		case KW_WAVEFORM: return Types.WAVEFORM;
		default: return null;
		}
	}
	
	public String getJvmType() {
		switch (firstToken.kind) {
		case KW_INT: return "I";
		case KW_FLOAT: return "F";
		case KW_BOOL: return "Z";
		case KW_STRING: return "Ljava/lang/String;";
		case KW_ARRAY: return "[F";
		case KW_FILTER: return "I";
		case KW_WAVEFORM: return "I";
		default: return null;
		}
	}

	public abstract void decorate(SymbolTable symtab) throws Exception;
	
	public abstract void generate(MethodVisitor mv, SymbolTable symtab) throws Exception;

}
