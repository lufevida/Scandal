package language.compiler;

import static language.tree.Node.Types.BOOL;
import static language.tree.Node.Types.FLOAT;
import static language.tree.Node.Types.INT;
import static language.tree.Node.Types.LAMBDA;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import language.tree.AssignmentDeclaration;
import language.tree.Program;
import language.tree.expression.Expression;

public class TypeCheckerTests {
	
	@Test
	public void testLambdaLitDeclaration() throws Exception {
		String input = "lambda test = int i -> i";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		AssignmentDeclaration dec = parser.assignmentDeclaration();		
		dec.decorate(new SymbolTable("className"));
		assertEquals(LAMBDA, dec.expression.type);
	}
	
	@Test
	public void testBinaryExpression1() throws Exception {
		String input = "1 % 2 + 3 - 4 * 5 / 6";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		Expression expression = parser.expression();
		expression.decorate(null);
		assertEquals(INT, expression.type);
	}
	
	@Test
	public void testBinaryExpression2() throws Exception {
		String input = "1.1 + 2.2 - 3.3 * 4.4 / 5.5";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		Expression expression = parser.expression();
		expression.decorate(null);
		assertEquals(FLOAT, expression.type);
	}
	
	@Test
	public void testBinaryExpression3() throws Exception {
		String input = "1.1 + 2 + 3.3 - 4 - 5.5 * 6 * 7.7 / 8 / 9.9";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		Expression expression = parser.expression();
		expression.decorate(null);
		assertEquals(FLOAT, expression.type);
	}
	
	@Test
	public void testBinaryExpression4() throws Exception {
		String input = "1 + 2.2 + 3 - 4.4 - 5 * 6.6 * 7 / 8.8 / 9";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		Expression expression = parser.expression();
		expression.decorate(null);
		assertEquals(FLOAT, expression.type);
	}
	
	@Test
	public void testBinaryExpression5() throws Exception {
		String input = "(1 < 2.2) <= (3.3 > 4) >= (5 == 6) != (7.7 == 8.8) >= true > false";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		Expression expression = parser.expression();
		expression.decorate(null);
		assertEquals(BOOL, expression.type);
	}
	
	@Test
	public void testWaveFileExpression() throws Exception {
		String input = "array name = read(\"name.wav\", 1)";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		Program program = parser.parse();
		program.decorate(new SymbolTable("className"));
	}
	
	@Test
	public void testAssignmentDeclaration() throws Exception {
		String input = "int three = 3 float sum = three + 3.14";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		Program program = parser.parse();
		program.decorate(new SymbolTable("className"));
	}
	
	@Test
	public void testIfStatement() throws Exception {
		String input = "int three = 3 if (three < pi) { float sum = three + 3.14 }";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		Program program = parser.parse();
		program.decorate(new SymbolTable("className"));
	}

}
