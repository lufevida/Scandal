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
import static language.compiler.Token.Kind.FLOAT_LIT;
import static language.compiler.Token.Kind.GE;
import static language.compiler.Token.Kind.GT;
import static language.compiler.Token.Kind.IDENT;
import static language.compiler.Token.Kind.INT_LIT;
import static language.compiler.Token.Kind.KW_ARRAY;
import static language.compiler.Token.Kind.KW_FLOAT;
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
import language.tree.Declaration;
import language.tree.IfStatement;
import language.tree.ImportStatement;
import language.tree.IndexedAssignmentStatement;
import language.tree.PlayStatement;
import language.tree.PlotStatement;
import language.tree.PrintStatement;
import language.tree.Program;
import language.tree.ReturnBlock;
import language.tree.Statement;
import language.tree.UnassignedDeclaration;
import language.tree.WhileStatement;
import language.tree.WriteStatement;
import language.tree.expression.ArrayItemExpression;
import language.tree.expression.ArrayLitExpression;
import language.tree.expression.ArraySizeExpression;
import language.tree.expression.BinaryExpression;
import language.tree.expression.BiquadExpression;
import language.tree.expression.BoolLitExpression;
import language.tree.expression.Expression;
import language.tree.expression.FilterExpression;
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
import language.tree.expression.OscillatorExpression;
import language.tree.expression.PanExpression;
import language.tree.expression.ReadExpression;
import language.tree.expression.RecordExpression;
import language.tree.expression.StringLitExpression;
import language.tree.expression.TrackExpression;
import language.tree.expression.TremoloExpression;
import language.tree.expression.UnaryExpression;
import language.tree.expression.WaveformExpression;

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
		ArrayList<Declaration> declarations = new ArrayList<>();
		ArrayList<Statement> statements = new ArrayList<>();
		while (token.kind != EOF) {
			try {
				Declaration declaration = declaration();
				declarations.add(declaration);
			} catch (Exception declarationException) {
				try {
					Statement statement = statement();
					statements.add(statement);
				} catch (Exception statementException) {
					throw new Exception("Illegal program: " + token.lineNumberPosition);
				}
			}
		}
		matchEOF();
		return new Program(firstToken, declarations, statements);
	}

	public Block block() throws Exception {
		Token firstToken = token;
		ArrayList<Declaration> declarations = new ArrayList<>();
		ArrayList<Statement> statements = new ArrayList<>();
		match(LBRACE);
		while (token.kind != RBRACE) {
			try {
				Declaration declaration = declaration();
				declarations.add(declaration);
			} catch (Exception declarationException) {
				try {
					Statement statement = statement();
					statements.add(statement);
				} catch (Exception statementException) {
					throw new Exception("Illegal block: " + token.lineNumberPosition);
				}
			}
		}
		match(RBRACE);
		return new Block(firstToken, declarations, statements);
	}

	public ReturnBlock returnBlock() throws Exception {
		Token firstToken = token;
		ArrayList<Declaration> declarations = new ArrayList<>();
		ArrayList<Statement> statements = new ArrayList<>();
		match(LBRACE);
		while (token.kind != KW_RETURN) {
			try {
				Declaration declaration = declaration();
				declarations.add(declaration);
			} catch (Exception declarationException) {
				try {
					Statement statement = statement();
					statements.add(statement);
				} catch (Exception statementException) {
					throw new Exception("Illegal return block: " + token.lineNumberPosition);
				}
			}
		}
		match(KW_RETURN);
		Expression expression = expression();
		match(RBRACE);
		return new ReturnBlock(firstToken, declarations, statements, expression);
	}

	public Declaration declaration() throws Exception {
		if (!isDeclarationKind(token.kind))
			throw new Exception("Illegal declaration: " + token.lineNumberPosition);
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
		if (token.kind == ASSIGN) {
			match(ASSIGN);
			Expression expression = expression();
			return new AssignmentDeclaration(firstToken, returnToken, identToken, expression);
		}
		else return new UnassignedDeclaration(firstToken, returnToken, identToken);
	}
	
	private boolean isDeclarationKind(Token.Kind kind) {
		switch (kind) {
		case KW_INT:
		case KW_FLOAT:
		case KW_BOOL:
		case KW_STRING:
		case KW_FILTER:
		case KW_WAVEFORM:
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
			ArrayList<Float> floats = new ArrayList<>();
			while (token.kind != RBRACKET) {
				if (token.kind == INT_LIT || token.kind == FLOAT_LIT || token.kind == MINUS) {
					if (token.kind == INT_LIT) floats.add((float) token.getIntValue());
					else if (token.kind == FLOAT_LIT) floats.add(token.getFloatValue());
					else if (token.kind == MINUS) {
						consume();
						if (token.kind == INT_LIT) floats.add((float) -token.getIntValue());
						else if (token.kind == FLOAT_LIT) floats.add(-token.getFloatValue());
					}
					consume();
					try {
						match(COMMA);
					} catch (Exception e) {
						break;
					}
				} else {
					throw new Exception("Illegal factor: " + token.lineNumberPosition);
				}
			}
			expression = new ArrayLitExpression(firstToken, floats);
			match(RBRACKET);
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
		case KW_OSCILLATOR: {
			consume();
			match(LPAREN);
			Expression duration = expression();
			match(COMMA);
			Expression amplitude = expression();
			match(COMMA);
			Expression frequency = expression();
			match(COMMA);
			Expression shape = expression();
			match(RPAREN);
			expression = new OscillatorExpression(firstToken, duration, amplitude, frequency, shape);
		} break;
		case KW_BIQUAD: {
			consume();
			match(LPAREN);
			Expression array = expression();
			match(COMMA);
			Expression cutoff = expression();
			match(COMMA);
			Expression resonance = expression();
			match(COMMA);
			Expression method = expression();
			match(RPAREN);
			expression = new BiquadExpression(firstToken, array, cutoff, resonance, method);
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
		case KW_PAN: {
			consume();
			match(LPAREN);
			Expression array = expression();
			match(COMMA);
			Expression position = expression();
			match(RPAREN);
			expression = new PanExpression(firstToken, array, position);
		} break;
		case KW_TREMOLO: {
			consume();
			match(LPAREN);
			Expression array = expression();
			match(COMMA);
			Expression depth = expression();
			match(COMMA);
			Expression speed = expression();
			match(COMMA);
			Expression shape = expression();
			match(RPAREN);
			expression = new TremoloExpression(firstToken, array, depth, speed, shape);
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
		case KW_TRACK: {
			consume();
			match(LPAREN);
			Expression array = expression();
			match(COMMA);
			Expression start = expression();
			match(COMMA);
			Expression gain = expression();
			match(COMMA);
			Expression pan = expression();
			match(RPAREN);
			expression = new TrackExpression(firstToken, array, start, gain, pan);
		} break;
		case KW_ALLPASS:
		case KW_BANDPASS:
		case KW_BANDSTOP:
		case KW_LOWPASS:
		case KW_HIPASS:
		case KW_LOWSHELF:
		case KW_HISHELF:
		case KW_PEAKING: {
			consume();
			expression = new FilterExpression(firstToken);
		} break;
		case KW_COSINE:
		case KW_SAWTOOTH:
		case KW_SQUARE:
		case KW_TRIANGLE:
		case KW_NOISE: {
			consume();
			expression = new WaveformExpression(firstToken);
		} break;
		case KW_FALSE:
		case KW_TRUE: {
			consume();
			expression = new BoolLitExpression(firstToken);
		} break;
		case LPAREN: {
			consume();
			expression = expression();
			match(RPAREN);
		} break;
		case KW_ARRAY:
		case KW_FLOAT: {
			ArrayList<UnassignedDeclaration> decs = new ArrayList<>();
			UnassignedDeclaration declaration = (UnassignedDeclaration) declaration();
			decs.add(declaration);
			match(ARROW);
			while (token.kind == KW_FLOAT || token.kind == KW_ARRAY) {
				UnassignedDeclaration dec = (UnassignedDeclaration) declaration();
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

}
