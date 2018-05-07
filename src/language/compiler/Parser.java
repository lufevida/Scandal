package language.compiler;

import static language.compiler.Token.Kind.*;

import java.util.ArrayList;

import language.compiler.Token.Kind;
import language.tree.*;
import language.tree.expression.*;

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
		throw new Exception("Saw " + token.kind + " expected " + kind + " in line: " + token.lineNumber);
	}

	private Token matchEOF() throws Exception {
		if (token.kind == EOF) return token;
		throw new Exception("Expected EOF");
	}

	public Program parse() throws Exception {
		Token firstToken = token;
		ArrayList<Node> nodes = new ArrayList<>();
		while (token.kind != EOF) {
			if (token.kind == KW_LAMBDA) nodes.add(lambdaDeclaration());
			else if (isDeclarationKind(token.kind)) nodes.add(assignmentDeclaration());
			else nodes.add(statement());
		}
		matchEOF();
		return new Program(firstToken, nodes);
	}
	
	public LambdaBlock lambdaBlock() throws Exception {
		Token firstToken = match(LBRACE);
		ArrayList<Node> nodes = new ArrayList<>();
		while (token.kind != KW_RETURN) {
			if (isDeclarationKind(token.kind)) nodes.add(assignmentDeclaration());
			else nodes.add(statement());
		}
		match(KW_RETURN);
		Expression expression = expression();
		match(RBRACE);
		return new LambdaBlock(firstToken, nodes, expression);
	}

	public Block block() throws Exception {
		Token firstToken = match(LBRACE);
		ArrayList<Node> nodes = new ArrayList<>();
		while (token.kind != RBRACE) {
			if (isDeclarationKind(token.kind)) nodes.add(assignmentDeclaration());
			else nodes.add(statement());
		}
		match(RBRACE);
		return new Block(firstToken, nodes);
	}

	public ReturnBlock returnBlock() throws Exception {
		Token firstToken = match(LBRACE);
		ArrayList<Node> nodes = new ArrayList<>();
		while (token.kind != KW_RETURN) {
			if (isDeclarationKind(token.kind)) nodes.add(assignmentDeclaration());
			else nodes.add(statement());
		}
		match(KW_RETURN);
		Expression expression = expression();
		match(RBRACE);
		return new ReturnBlock(firstToken, nodes, expression);
	}

	public AssignmentDeclaration assignmentDeclaration() throws Exception {
		Token returnToken = null;
		Token firstToken = consume();
		boolean isLambda = token.kind == COLON;
		if (isLambda) {
			consume();
			if (token.kind != KW_FLOAT && token.kind != KW_ARRAY)
				throw new Exception("Illegal return declaration: " + token.lineNumberPosition);
			returnToken = consume();
		}
		Token identToken = match(IDENT);
		match(ASSIGN);
		Expression expression = expression();
		return new AssignmentDeclaration(firstToken, returnToken, identToken, expression);
	}
	
	public Declaration lambdaDeclaration() throws Exception {
		Token firstToken = match(KW_LAMBDA);
		Token identToken = match(IDENT);
		match(ASSIGN);
		if (isDeclarationKind(token.kind)) return new LambdaLitDeclaration(firstToken, identToken, lambdaLitExpression());
		//else if (token.kind == KW_APPLY) return new LambdaAppDeclaration(firstToken, identToken, lambdaAppExpression());
		else return new AssignmentDeclaration(firstToken, null, identToken, expression());
	}

	public LambdaLitExpression lambdaLitExpression() throws Exception {
		Token firstToken = token;
		ArrayList<ParamDeclaration> decs = new ArrayList<>();
		decs.add(paramDeclaration());
		match(ARROW);
		while (token.kind != LBRACE) {
			decs.add(paramDeclaration());
			match(ARROW);
		}
		return new LambdaLitExpression(firstToken, decs, lambdaBlock());
	}
	
	public LambdaAppExpression lambdaAppExpression() throws Exception {
		Token firstToken = match(KW_APPLY);
		ArrayList<Expression> args = new ArrayList<>();
		args.add(expression());
		while (token.kind != KW_TO) {
			match(COMMA);
			args.add(expression());
		}
		match(KW_TO);
		IdentExpression lambda = (IdentExpression) expression();
		return new LambdaAppExpression(firstToken, args, lambda);
	}
	
	public ParamDeclaration paramDeclaration() throws Exception {
		Token returnToken = null;
		Token firstToken = consume();
		boolean isLambda = token.kind == COLON;
		if (isLambda) {
			consume();
			if (token.kind != KW_FLOAT && token.kind != KW_ARRAY)
				throw new Exception("Illegal return declaration: " + token.lineNumberPosition);
			returnToken = consume();
		}
		Token identToken = match(IDENT);
		return new ParamDeclaration(firstToken, returnToken, identToken);
	}

	private boolean isDeclarationKind(Token.Kind kind) {
		switch (kind) {
		case KW_INT:
		case KW_FLOAT:
		case KW_BOOL:
		case KW_STRING:
		case KW_ARRAY:
			return true;
		default: return false;
		}
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
		case KW_FUNC: return methodStatement();
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
	
	public MethodStatement methodStatement() throws Exception {
		ArrayList<ParamDeclaration> decs = new ArrayList<>();
		Token firstToken = match(KW_FUNC);
		Token name = match(IDENT);
		match(LPAREN);
		decs.add(paramDeclaration());
		while (token.kind == COMMA) {
			match(COMMA);
			decs.add(paramDeclaration());
		}
		match(RPAREN);
		return new MethodStatement(firstToken, name, decs, returnBlock());
	}

	public Expression expression() throws Exception {
		Token firstToken = token;
		Expression e0;
		Token operator;
		Expression e1;
		e0 = term();
		while (
				token.kind == LT ||
				token.kind == LE ||
				token.kind == GT ||
				token.kind == GE ||
				token.kind == EQUAL ||
				token.kind == NOTEQUAL) {
			operator = token;
			consume();
			e1 = term();
			e0 = new BinaryExpression(firstToken, e0, operator, e1);
		}
		return e0;
	}

	private Expression term() throws Exception {
		Token firstToken = token;
		Expression e0;
		Token operator;
		Expression e1;
		e0 = summand();
		while (token.kind == PLUS || token.kind == MINUS || token.kind == OR) {
			operator = token;
			consume();
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
		while (token.kind == TIMES || token.kind == DIV || token.kind == MOD || token.kind == AND) {
			operator = token;
			consume();
			e1 = factor();
			e0 = new BinaryExpression(firstToken, e0, operator, e1);
		}
		return e0;
	}

	private Expression factor() throws Exception {
		Token firstToken = token;
		switch (token.kind) {
		case KW_INFO: return new InfoExpression(consume());
		case INT_LIT: return new IntLitExpression(consume());
		case FLOAT_LIT: return new FloatLitExpression(consume());
		case STRING_LIT: return new StringLitExpression(consume());
		case KW_PI: return new PiExpression(consume());
		case KW_FALSE:
		case KW_TRUE: return new BoolLitExpression(consume());
		case KW_APPLY: return lambdaAppExpression();
		case LPAREN: return parenthesizedExpression(consume());
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
		case IDENT: {
			consume();
			/*if (scanner.peek() == LPAREN) {
				match(LPAREN);
				ArrayList<Expression> params = new ArrayList<>();
				Expression param = expression();
				params.add(param);
				while (scanner.peek() == COMMA) {
					match(COMMA);
					Expression p = expression();
					params.add(p);
				}
				match(RPAREN);
				return new FuncAppExpression(firstToken, params);
			}*/
			/*if (scanner.peek() == DOT) {
				consume();
				ArrayList<IdentExpression> lambdas = new ArrayList<>();
				lambdas.add(new IdentExpression(firstToken));
				lambdas.add(new IdentExpression(token));
				consume();
				while (scanner.peek() == DOT) {
					consume();
					lambdas.add(new IdentExpression(token));
					consume();
				}
				match(LPAREN);
				Expression param = expression();
				match(RPAREN);
				return new FuncCompExpression(firstToken, lambdas, param);
			}*/
			if (scanner.peek() == LBRACKET) {
				consume();
				Expression index = expression();
				match(RBRACKET);
				return new ArrayItemExpression(firstToken, index);
			}
			if (scanner.peek() == KW_THEN) {
				ArrayList<IdentExpression> lambdas = new ArrayList<>();
				lambdas.add(new IdentExpression(firstToken));
				while (token.kind == KW_THEN) {
					consume();
					Token f = match(IDENT);
					lambdas.add(new IdentExpression(f));
				}
				return new LambdaCompExpression(firstToken, lambdas);
			}
			return new IdentExpression(firstToken);
		}
		/*case KW_ARRAY:
		case KW_FLOAT: {
			ArrayList<ParamDeclaration> decs = new ArrayList<>();
			ParamDeclaration declaration = paramDeclaration();
			decs.add(declaration);
			match(ARROW);
			while (token.kind == KW_FLOAT || token.kind == KW_ARRAY) {
				ParamDeclaration dec = paramDeclaration();
				decs.add(dec);
				match(ARROW);
			}
			if (scanner.peek() == LBRACE) {
				ReturnBlock returnBlock = returnBlock();
				return new FuncLitBlock(firstToken, decs, returnBlock);
			}
			Expression returnExpression = expression();
			if (returnExpression.getClass() == FuncAppExpression.class)
				return new FuncLitApp(firstToken, decs, (FuncAppExpression) returnExpression);
			else if (returnExpression.getClass() == FuncCompExpression.class)
				return new FuncLitComp(firstToken, decs, (FuncCompExpression) returnExpression);
			else return new FuncLitExpression(firstToken, decs, returnExpression);
		}*/
		default: throw new Exception("Illegal factor in line " + token.lineNumber);
		}
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
