package language.compiler;

import static language.compiler.Token.Kind.AND;
import static language.compiler.Token.Kind.ARROW;
import static language.compiler.Token.Kind.ASSIGN;
import static language.compiler.Token.Kind.COLON;
import static language.compiler.Token.Kind.COMMA;
import static language.compiler.Token.Kind.DIV;
import static language.compiler.Token.Kind.DOT;
import static language.compiler.Token.Kind.EOF;
import static language.compiler.Token.Kind.EQUAL;
import static language.compiler.Token.Kind.GE;
import static language.compiler.Token.Kind.GT;
import static language.compiler.Token.Kind.IDENT;
import static language.compiler.Token.Kind.KW_ARRAY;
import static language.compiler.Token.Kind.KW_FLOAT;
import static language.compiler.Token.Kind.KW_FUNC;
import static language.compiler.Token.Kind.KW_IF;
import static language.compiler.Token.Kind.KW_IMPORT;
import static language.compiler.Token.Kind.KW_PLAY;
import static language.compiler.Token.Kind.KW_PLOT;
import static language.compiler.Token.Kind.KW_PRINT;
import static language.compiler.Token.Kind.KW_RETURN;
import static language.compiler.Token.Kind.KW_WHILE;
import static language.compiler.Token.Kind.KW_WRITE;
import static language.compiler.Token.Kind.LBRACE;
import static language.compiler.Token.Kind.LBRACKET;
import static language.compiler.Token.Kind.LE;
import static language.compiler.Token.Kind.LPAREN;
import static language.compiler.Token.Kind.LT;
import static language.compiler.Token.Kind.MINUS;
import static language.compiler.Token.Kind.MOD;
import static language.compiler.Token.Kind.NOTEQUAL;
import static language.compiler.Token.Kind.OR;
import static language.compiler.Token.Kind.PLUS;
import static language.compiler.Token.Kind.RBRACE;
import static language.compiler.Token.Kind.RBRACKET;
import static language.compiler.Token.Kind.RPAREN;
import static language.compiler.Token.Kind.TIMES;

import java.util.ArrayList;

import language.compiler.Token.Kind;
import language.tree.AssignmentDeclaration;
import language.tree.AssignmentStatement;
import language.tree.Block;
import language.tree.IfStatement;
import language.tree.ImportStatement;
import language.tree.IndexedAssignmentStatement;
import language.tree.MethodStatement;
import language.tree.Node;
import language.tree.ParamDeclaration;
import language.tree.PlayStatement;
import language.tree.PlotStatement;
import language.tree.PrintStatement;
import language.tree.Program;
import language.tree.ReturnBlock;
import language.tree.Statement;
import language.tree.WhileStatement;
import language.tree.WriteStatement;
import language.tree.expression.ArrayItemExpression;
import language.tree.expression.ArrayLitExpression;
import language.tree.expression.ArraySizeExpression;
import language.tree.expression.BinaryExpression;
import language.tree.expression.BoolLitExpression;
import language.tree.expression.CosExpression;
import language.tree.expression.Expression;
import language.tree.expression.FloatLitExpression;
import language.tree.expression.FloorExpression;
import language.tree.expression.FuncAppExpression;
import language.tree.expression.FuncCompExpression;
import language.tree.expression.FuncLitApp;
import language.tree.expression.FuncLitBlock;
import language.tree.expression.FuncLitComp;
import language.tree.expression.FuncLitExpression;
import language.tree.expression.IdentExpression;
import language.tree.expression.InfoExpression;
import language.tree.expression.IntLitExpression;
import language.tree.expression.NewArrayExpression;
import language.tree.expression.PiExpression;
import language.tree.expression.PowExpression;
import language.tree.expression.ReadExpression;
import language.tree.expression.RecordExpression;
import language.tree.expression.StringLitExpression;
import language.tree.expression.UnaryExpression;

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
		throw new Exception("Saw " + token.kind + " expected " + kind + " at " + token.lineNumberPosition);
	}

	private Token matchEOF() throws Exception {
		if (token.kind == EOF) return token;
		throw new Exception("Expected EOF");
	}

	public Program parse() throws Exception {
		Token firstToken = token;
		//ArrayList<AssignmentDeclaration> declarations = new ArrayList<>();
		//ArrayList<Statement> statements = new ArrayList<>();
		ArrayList<Node> nodes = new ArrayList<>();
		while (token.kind != EOF) {
			try {
				AssignmentDeclaration declaration = assignmentDeclaration();
				nodes.add(declaration);
			} catch (Exception declarationException) {
				try {
					Statement statement = statement();
					nodes.add(statement);
				} catch (Exception statementException) {
					throw new Exception("Illegal program in line " + token.lineNumber + ", pos. " + token.lineNumberPosition);
				}
			}
		}
		matchEOF();
		return new Program(firstToken, nodes);
	}

	public Block block() throws Exception {
		Token firstToken = token;
		ArrayList<Node> nodes = new ArrayList<>();
		match(LBRACE);
		while (token.kind != RBRACE) {
			try {
				AssignmentDeclaration declaration = assignmentDeclaration();
				nodes.add(declaration);
			} catch (Exception declarationException) {
				try {
					Statement statement = statement();
					nodes.add(statement);
				} catch (Exception statementException) {
					throw new Exception("Illegal block: " + token.lineNumberPosition);
				}
			}
		}
		match(RBRACE);
		return new Block(firstToken, nodes);
	}

	public ReturnBlock returnBlock() throws Exception {
		Token firstToken = token;
		ArrayList<Node> nodes = new ArrayList<>();
		match(LBRACE);
		while (token.kind != KW_RETURN) {
			try {
				AssignmentDeclaration declaration = assignmentDeclaration();
				nodes.add(declaration);
			} catch (Exception declarationException) {
				try {
					Statement statement = statement();
					nodes.add(statement);
				} catch (Exception statementException) {
					throw new Exception("Illegal return block: " + token.lineNumberPosition);
				}
			}
		}
		match(KW_RETURN);
		Expression expression = expression();
		match(RBRACE);
		return new ReturnBlock(firstToken, nodes, expression);
	}

	public AssignmentDeclaration assignmentDeclaration() throws Exception {
		if (!isDeclarationKind(token.kind)) throw new Exception("Illegal declaration: " + token.lineNumberPosition);
		Token returnToken = null;
		Token firstToken = token;
		consume();
		boolean isLambda = token.kind == COLON;
		if (isLambda) {
			consume();
			if (token.kind != KW_FLOAT && token.kind != KW_ARRAY)
				throw new Exception("Illegal return declaration: " + token.lineNumberPosition);
			returnToken = token;
			consume();
		}
		Token identToken = token;
		match(IDENT);
		match(ASSIGN);
		Expression expression = expression();
		return new AssignmentDeclaration(firstToken, returnToken, identToken, expression);
	}
	
	public ParamDeclaration unassignedDeclaration() throws Exception {
		if (!isDeclarationKind(token.kind)) throw new Exception("Illegal declaration: " + token.lineNumberPosition);
		Token returnToken = null;
		Token firstToken = token;
		consume();
		boolean isLambda = token.kind == COLON;
		if (isLambda) {
			consume();
			if (token.kind != KW_FLOAT && token.kind != KW_ARRAY)
				throw new Exception("Illegal return declaration: " + token.lineNumberPosition);
			returnToken = token;
			consume();
		}
		Token identToken = token;
		match(IDENT);
		return new ParamDeclaration(firstToken, returnToken, identToken);
	}

	private boolean isDeclarationKind(Token.Kind kind) {
		switch (kind) {
		case KW_INT:
		case KW_FLOAT:
		case KW_BOOL:
		case KW_STRING:
		case KW_ARRAY: return true;
		default: return false;
		}
	}

	public Statement statement() throws Exception {
		Statement statement;
		switch (token.kind) {
		case IDENT: {
			statement = assignmentStatement();
		} break;
		case KW_IMPORT: {
			statement = importStatement();
		} break;
		case KW_IF: {
			statement = ifStatement();
		} break;
		case KW_WHILE: {
			statement = whileStatement();
		} break;
		case KW_PRINT: {
			statement = printStatement();
		} break;
		case KW_PLOT: {
			statement = plotStatement();
		} break;
		case KW_PLAY: {
			statement = playStatement();
		} break;
		case KW_WRITE: {
			statement = writeStatement();
		} break;
		case KW_FUNC: {
			statement = methodStatement();
		} break;
		default: throw new Exception("Illegal statement: " + token.lineNumberPosition);
		}
		return statement;
	}
	
	public ImportStatement importStatement() throws Exception {
		Token firstToken = token;
		match(KW_IMPORT);
		match(LPAREN);
		Expression expression = expression();
		match(RPAREN);
		return new ImportStatement(firstToken, expression);
	}

	public AssignmentStatement assignmentStatement() throws Exception {
		Token firstToken = token;
		match(IDENT);
		if (scanner.peek() == LBRACKET) {
			match(LBRACKET);
			Expression index = expression();
			match(RBRACKET);
			match(ASSIGN);
			Expression expression = expression();
			return new IndexedAssignmentStatement(firstToken, expression, index);
		}
		match(ASSIGN);
		Expression expression = expression();
		return new AssignmentStatement(firstToken, expression);
	}

	public IfStatement ifStatement() throws Exception {
		Token firstToken = token;
		match(KW_IF);
		Expression expression = expression();
		Block block = block();
		return new IfStatement(firstToken, expression, block);
	}

	public WhileStatement whileStatement() throws Exception {
		Token firstToken = token;
		match(KW_WHILE);
		Expression expression = expression();
		Block block = block();
		return new WhileStatement(firstToken, expression, block);
	}

	public PrintStatement printStatement() throws Exception {
		Token firstToken = token;
		match(KW_PRINT);
		match(LPAREN);
		Expression expression = expression();
		match(RPAREN);
		return new PrintStatement(firstToken, expression);
	}

	public PlotStatement plotStatement() throws Exception {
		Token firstToken = token;
		match(KW_PLOT);
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
		Token firstToken = token;
		match(KW_PLAY);
		match(LPAREN);
		Expression array = expression();
		match(COMMA);
		Expression format = expression();
		match(RPAREN);
		return new PlayStatement(firstToken, array, format);
	}

	public WriteStatement writeStatement() throws Exception {
		Token firstToken = token;
		match(KW_WRITE);
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
		Token firstToken = token;
		ArrayList<ParamDeclaration> decs = new ArrayList<>();
		match(KW_FUNC);
		Token name = token;
		match(IDENT);
		match(LPAREN);
		decs.add(unassignedDeclaration());
		while (token.kind == COMMA) {
			match(COMMA);
			decs.add(unassignedDeclaration());
		}
		match(RPAREN);
		ReturnBlock block = returnBlock();
		return new MethodStatement(firstToken, name, decs, block);
	}

	public Expression expression() throws Exception {
		Token firstToken = token;
		Expression e0;
		Token operator;
		Expression e1;
		e0 = term();
		while (
				token.kind == LT |
				token.kind == LE |
				token.kind == GT |
				token.kind == GE |
				token.kind == EQUAL |
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
		while (token.kind == PLUS | token.kind == MINUS | token.kind == OR) {
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
		while (token.kind == TIMES | token.kind == DIV | token.kind == MOD | token.kind == AND) {
			operator = token;
			consume();
			e1 = factor();
			e0 = new BinaryExpression(firstToken, e0, operator, e1);
		}
		return e0;
	}

	private Expression factor() throws Exception {
		Expression expression;
		Token firstToken = token;
		switch (token.kind) {
		case MINUS:
		case NOT: {
			consume();
			Expression e = expression();
			expression = new UnaryExpression(firstToken, e);
		} break;
		case IDENT: {
			consume();
			if (scanner.peek() == LPAREN) {
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
				expression = new FuncAppExpression(firstToken, params);
			}
			else if (scanner.peek() == DOT) {
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
				expression = new FuncCompExpression(firstToken, lambdas, param);
			}
			else if (scanner.peek() == LBRACKET) {
				consume();
				Expression index = expression();
				match(RBRACKET);
				expression = new ArrayItemExpression(firstToken, index);
			}
			else expression = new IdentExpression(firstToken);
		} break;
		case KW_INFO: {
			consume();
			expression = new InfoExpression(firstToken);
		} break;
		case INT_LIT: {
			consume();
			expression = new IntLitExpression(firstToken);
		} break;
		case FLOAT_LIT: {
			consume();
			expression = new FloatLitExpression(firstToken);
		} break;
		case STRING_LIT: {
			consume();
			expression = new StringLitExpression(firstToken);
		} break;
		case LBRACKET: {
			consume();
			ArrayList<Expression> floats = new ArrayList<>();
			floats.add(expression());
			while (token.kind != RBRACKET) {
				match(COMMA);
				floats.add(expression());
			}
			match(RBRACKET);
			expression = new ArrayLitExpression(firstToken, floats);
		} break;
		case KW_READ: {
			consume();
			match(LPAREN);
			Expression fileName = expression();
			match(COMMA);
			Expression format = expression();
			match(RPAREN);
			expression = new ReadExpression(firstToken, fileName, format);
		} break;
		case KW_SIZE: {
			consume();
			match(LPAREN);
			Expression param = expression();
			match(RPAREN);
			expression = new ArraySizeExpression(firstToken, param);
		} break;
		case KW_NEW: {
			consume();
			match(LPAREN);
			Expression size = expression();
			match(RPAREN);
			expression = new NewArrayExpression(firstToken, size);
		} break;
		case KW_RECORD: {
			consume();
			match(LPAREN);
			Expression duration = expression();
			match(RPAREN);
			expression = new RecordExpression(firstToken, duration);
		} break;
		case KW_FLOOR: {
			consume();
			match(LPAREN);
			Expression param = expression();
			match(RPAREN);
			expression = new FloorExpression(firstToken, param);
		} break;
		case KW_FALSE:
		case KW_TRUE:
			consume();
			expression = new BoolLitExpression(firstToken);
			break;
		case LPAREN:
			consume();
			expression = expression();
			match(RPAREN);
			break;
		case KW_COS:
			expression = cosExpression();
			break;
		case KW_POW:
			expression = powExpression();
			break;
		case KW_PI:
			expression = piExpression();
			break;
		case KW_ARRAY:
		case KW_FLOAT: {
			ArrayList<ParamDeclaration> decs = new ArrayList<>();
			ParamDeclaration declaration = unassignedDeclaration();
			decs.add(declaration);
			match(ARROW);
			while (token.kind == KW_FLOAT || token.kind == KW_ARRAY) {
				ParamDeclaration dec = unassignedDeclaration();
				decs.add(dec);
				match(ARROW);
			}
			if (scanner.peek() == LBRACE) {
				ReturnBlock returnBlock = returnBlock();
				expression = new FuncLitBlock(firstToken, decs, returnBlock);
			}
			else {
				Expression returnExpression = expression();
				if (returnExpression.getClass() == FuncAppExpression.class)
					expression = new FuncLitApp(firstToken, decs, (FuncAppExpression) returnExpression);
				else if (returnExpression.getClass() == FuncCompExpression.class)
					expression = new FuncLitComp(firstToken, decs, (FuncCompExpression) returnExpression);
				else expression = new FuncLitExpression(firstToken, decs, returnExpression);
			}
		} break;
		default:
			throw new Exception("Illegal factor: " + token.lineNumberPosition);
		}
		return expression;
	}
	
	public CosExpression cosExpression() throws Exception {
		Token firstToken = token;
		consume();
		match(LPAREN);
		Expression phase = expression();
		match(RPAREN);
		return new CosExpression(firstToken, phase);
	}
	
	public PowExpression powExpression() throws Exception {
		Token firstToken = token;
		consume();
		match(LPAREN);
		Expression base = expression();
		match(COMMA);
		Expression exponent = expression();
		match(RPAREN);
		return new PowExpression(firstToken, base, exponent);
	}
	
	public PiExpression piExpression() throws Exception {
		Token firstToken = token;
		consume();
		return new PiExpression(firstToken);
	}

}
