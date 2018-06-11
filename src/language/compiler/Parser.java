package language.compiler;

import static language.compiler.Token.Kind.*;

import java.util.ArrayList;

import language.compiler.Token.Kind;
import language.tree.*;
import language.tree.expression.*;
import language.tree.statement.*;

public class Parser {

	private final Scanner scanner;
	private Token token;

	public Parser(Scanner scanner) {
		this.scanner = scanner;
		token = scanner.nextToken();
	}

	private Token consume() throws Exception {
		Token temp = token;
		token = scanner.nextToken();
		return temp;
	}

	private Token match(Kind kind) throws Exception {
		if (token.kind == kind) return consume();
		throw new Exception("Saw " + token.kind + " expected " + kind + " in line " + token.lineNumber);
	}

	private Token matchEOF() throws Exception {
		if (token.kind == EOF) return token;
		throw new Exception("Expected EOF");
	}

	public Program parse() throws Exception {
		Token firstToken = token;
		ArrayList<Node> nodes = new ArrayList<>();
		while (token.kind != EOF) {
			if (token.isDeclaration()) nodes.add(assignmentDeclaration());
			else nodes.add(statement());
		}
		matchEOF();
		return new Program(firstToken, nodes);
	}

	public Block block() throws Exception {
		Token firstToken = match(LBRACE);
		ArrayList<Node> nodes = new ArrayList<>();
		while (token.kind != RBRACE) {
			if (token.isDeclaration()) nodes.add(assignmentDeclaration());
			else nodes.add(statement());
		}
		match(RBRACE);
		return new Block(firstToken, nodes);
	}
	
	public ReturnBlock lambdaBlock() throws Exception {
		Token firstToken = match(LBRACE);
		ArrayList<Node> nodes = new ArrayList<>();
		while (token.kind != KW_RETURN) {
			if (token.isDeclaration()) nodes.add(assignmentDeclaration());
			else nodes.add(statement());
		}
		match(KW_RETURN);
		Expression expression = expression();
		match(RBRACE);
		return new ReturnBlock(firstToken, nodes, expression);
	}

	public AssignmentDeclaration assignmentDeclaration() throws Exception {
		boolean isField = token.kind == KW_FIELD;
		if (isField) consume();
		Token firstToken = consume();
		Token identToken = match(IDENT);
		match(ASSIGN);
		Expression e = expression();
		if (e instanceof LambdaLitExpression)
			return new LambdaLitDeclaration(firstToken, identToken, (LambdaLitExpression) e);
		if (isField) return new FieldDeclaration(firstToken, identToken, e);
		return new AssignmentDeclaration(firstToken, identToken, e);
	}
	
	public ParamDeclaration paramDeclaration() throws Exception {
		Token firstToken = consume();
		Token identToken = match(IDENT);
		return new ParamDeclaration(firstToken, identToken);
	}

	public Statement statement() throws Exception {
		switch (token.kind) {
		case IDENT: return assignmentStatement();
		case KW_IMPORT: return importStatement();
		case KW_IF: return ifStatement();
		case KW_WHILE: return whileStatement();
		case KW_PRINT: return printStatement();
		case KW_PLOT: return plotStatement();
		case KW_PLAY: return playStatement();
		case KW_WRITE: return writeStatement();
		default: throw new Exception("Illegal statement in line: " + token.lineNumber);
		}
	}
	
	public ImportStatement importStatement() throws Exception {
		Token firstToken = match(KW_IMPORT);
		match(LPAREN);
		Expression expression = expression();
		match(RPAREN);
		return new ImportStatement(firstToken, expression);
	}

	public AssignmentStatement assignmentStatement() throws Exception {
		Token firstToken = match(IDENT);
		if (scanner.peek() == LBRACKET) {
			match(LBRACKET);
			Expression index = expression();
			match(RBRACKET);
			match(ASSIGN);
			Expression expression = expression();
			return new IndexedAssignmentStatement(firstToken, expression, index);
		}
		match(ASSIGN);
		return new AssignmentStatement(firstToken, expression());
	}

	public IfStatement ifStatement() throws Exception {
		Token firstToken = match(KW_IF);
		Expression expression = expression();
		return new IfStatement(firstToken, expression, block());
	}

	public WhileStatement whileStatement() throws Exception {
		Token firstToken = match(KW_WHILE);
		Expression expression = expression();
		return new WhileStatement(firstToken, expression, block());
	}

	public PrintStatement printStatement() throws Exception {
		Token firstToken = match(KW_PRINT);
		match(LPAREN);
		Expression expression = expression();
		match(RPAREN);
		return new PrintStatement(firstToken, expression);
	}

	public PlotStatement plotStatement() throws Exception {
		Token firstToken = match(KW_PLOT);
		match(LPAREN);
		Expression title = expression();
		match(COMMA);
		Expression array = expression();
		match(COMMA);
		Expression points = expression();
		match(RPAREN);
		return new PlotStatement(firstToken, title, array, points);
	}

	public PlayStatement playStatement() throws Exception {
		Token firstToken = match(KW_PLAY);
		match(LPAREN);
		Expression array = expression();
		match(COMMA);
		Expression format = expression();
		match(RPAREN);
		return new PlayStatement(firstToken, array, format);
	}

	public WriteStatement writeStatement() throws Exception {
		Token firstToken = match(KW_WRITE);
		match(LPAREN);
		Expression array = expression();
		match(COMMA);
		Expression name = expression();
		match(COMMA);
		Expression format = expression();
		match(RPAREN);
		return new WriteStatement(firstToken, array, name, format);
	}

	public Expression expression() throws Exception {
		Token firstToken = token;
		Expression e0;
		Token operator;
		Expression e1;
		e0 = comparison();
		while (token.isComparison()) {
			operator = consume();
			e1 = comparison();
			e0 = new BinaryExpression(firstToken, e0, operator, e1);
		}
		return e0;
	}

	private Expression comparison() throws Exception {
		Token firstToken = token;
		Expression e0;
		Token operator;
		Expression e1;
		e0 = summand();
		while (token.isSummand()) {
			operator = consume();
			e1 = summand();
			e0 = new BinaryExpression(firstToken, e0, operator, e1);
		}
		return e0;
	}

	private Expression summand() throws Exception {
		Token firstToken = token;
		Expression e0;
		Token operator;
		Expression e1;
		e0 = factor();
		while (token.isFactor()) {
			operator = consume();
			e1 = factor();
			e0 = new BinaryExpression(firstToken, e0, operator, e1);
		}
		return e0;
	}

	private Expression factor() throws Exception {
		if (token.isDeclaration()) return lambdaLitExpression(token);
		switch (token.kind) {
		case LPAREN: return parenthesizedExpression(consume());
		case INT_LIT: return new IntLitExpression(consume());
		case FLOAT_LIT: return new FloatLitExpression(consume());
		case STRING_LIT: return new StringLitExpression(consume());
		case KW_PI: return new PiExpression(consume());
		case KW_FALSE:
		case KW_TRUE: return new BoolLitExpression(consume());
		case KW_COS: return cosExpression(consume());
		case KW_POW: return powExpression(consume());
		case KW_NEW: return newArrayExpression(consume());
		case KW_RECORD: return recordExpression(consume());
		case KW_FLOOR: return floorExpression(consume());
		case LBRACKET: return arrayLitExpression(consume());
		case KW_READ: return readExpression(consume());
		case KW_SIZE: return arraySizeExpression(consume());
		case MINUS:
		case NOT: return unaryExpression(consume());
		case IDENT:
			Token firstToken = consume();
			switch (token.kind) {
			case LBRACKET: return arrayItemExpression(firstToken);
			case LPAREN: return lambdaAppExpression(firstToken);
			case DOT: return lambdaCompExpression(firstToken);
			default: return new IdentExpression(firstToken);
			}
		default: throw new Exception("Illegal factor in line " + token.lineNumber + "\n");
		}
	}
	
	public LambdaLitExpression lambdaLitExpression(Token firstToken) throws Exception {
		ArrayList<ParamDeclaration> decs = new ArrayList<>();
		decs.add(paramDeclaration());
		match(ARROW);
		while (token.isDeclaration()) {
			decs.add(paramDeclaration());
			match(ARROW);
		}
		if (token.kind == LBRACE) return new LambdaLitBlock(firstToken, decs, lambdaBlock());
		return new LambdaLitExpression(firstToken, decs, expression());
	}
	
	public LambdaAppExpression lambdaAppExpression(Token firstToken) throws Exception {
		IdentExpression lambda = new IdentExpression(firstToken);
		match(LPAREN);
		ArrayList<Expression> args = new ArrayList<>();
		args.add(expression());
		while (token.kind != RPAREN) {
			match(COMMA);
			args.add(expression());
		}
		match(RPAREN);
		return new LambdaAppExpression(firstToken, lambda, args);
	}
	
	public LambdaCompExpression lambdaCompExpression(Token firstToken) throws Exception {
		ArrayList<IdentExpression> lambdas = new ArrayList<>();
		LambdaAppExpression lambdaApp = null;
		lambdas.add(new IdentExpression(firstToken));
		Token next = null;
		while (token.kind == DOT) {
			consume();
			next = match(IDENT);
			lambdas.add(new IdentExpression(next));
		}
		if (token.kind == LPAREN) lambdaApp = lambdaAppExpression(next);
		return new LambdaCompExpression(firstToken, lambdas, lambdaApp);
	}
	
	public ArrayItemExpression arrayItemExpression(Token firstToken) throws Exception {
		match(LBRACKET);
		Expression index = expression();
		match(RBRACKET);
		return new ArrayItemExpression(firstToken, new IdentExpression(firstToken), index);
	}
	
	public ArrayLitExpression arrayLitExpression(Token firstToken) throws Exception {
		ArrayList<Expression> floats = new ArrayList<>();
		floats.add(expression());
		while (token.kind != RBRACKET) {
			match(COMMA);
			floats.add(expression());
		}
		match(RBRACKET);
		return new ArrayLitExpression(firstToken, floats);
	}
	
	public ReadExpression readExpression(Token firstToken) throws Exception {
		match(LPAREN);
		Expression fileName = expression();
		match(COMMA);
		Expression format = expression();
		match(RPAREN);
		return new ReadExpression(firstToken, fileName, format);
	}
	
	public ArraySizeExpression arraySizeExpression(Token firstToken) throws Exception {
		match(LPAREN);
		Expression param = expression();
		match(RPAREN);
		return new ArraySizeExpression(firstToken, param);
	}
	
	public NewArrayExpression newArrayExpression(Token firstToken) throws Exception {
		match(LPAREN);
		Expression size = expression();
		match(RPAREN);
		return new NewArrayExpression(firstToken, size);
	}
	
	public RecordExpression recordExpression(Token firstToken) throws Exception {
		match(LPAREN);
		Expression duration = expression();
		match(RPAREN);
		return new RecordExpression(firstToken, duration);
	}
	
	public FloorExpression floorExpression(Token firstToken) throws Exception {
		match(LPAREN);
		Expression param = expression();
		match(RPAREN);
		return new FloorExpression(firstToken, param);
	}
	
	public Expression parenthesizedExpression(Token firstToken) throws Exception {
		Expression e = expression();
		match(RPAREN);
		return e;
	}
	
	public UnaryExpression unaryExpression(Token firstToken) throws Exception {
		return new UnaryExpression(firstToken, expression());
	}
	
	public CosExpression cosExpression(Token firstToken) throws Exception {
		match(LPAREN);
		Expression phase = expression();
		match(RPAREN);
		return new CosExpression(firstToken, phase);
	}
	
	public PowExpression powExpression(Token firstToken) throws Exception {
		match(LPAREN);
		Expression base = expression();
		match(COMMA);
		Expression exponent = expression();
		match(RPAREN);
		return new PowExpression(firstToken, base, exponent);
	}

}
