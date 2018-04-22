package language.compiler;

import static org.junit.Assert.*;

import org.junit.Test;

import language.tree.AssignmentDeclaration;
import language.tree.AssignmentStatement;
import language.tree.Declaration;
import language.tree.IfStatement;
import language.tree.IndexedAssignmentStatement;
import language.tree.ParamDeclaration;
import language.tree.Program;
import language.tree.ReturnBlock;
import language.tree.Statement;
import language.tree.WhileStatement;
import language.tree.expression.ArrayItemExpression;
import language.tree.expression.ArraySizeExpression;
import language.tree.expression.BinaryExpression;
import language.tree.expression.BoolLitExpression;
import language.tree.expression.Expression;
import language.tree.expression.FloatLitExpression;
import language.tree.expression.FuncAppExpression;
import language.tree.expression.FuncCompExpression;
import language.tree.expression.FuncLitBlock;
import language.tree.expression.FuncLitExpression;
import language.tree.expression.IntLitExpression;
import language.tree.expression.ReadExpression;

import static language.compiler.Token.Kind.*;

public class ParserTests {

	@Test
	public void testDeclaration() throws Exception {
		String input = "int three";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		Program program = parser.parse();
		Declaration declaration = (Declaration) program.nodes.get(0);
		assertEquals(KW_INT, declaration.firstToken.kind);
		assertEquals(IDENT, declaration.identToken.kind);
	}
	
	@Test
	public void testAssignmentDeclaration() throws Exception {
		String input = "float pi = 3.14";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		Program program = parser.parse();
		AssignmentDeclaration declaration = (AssignmentDeclaration) program.nodes.get(0);
		assertEquals(KW_FLOAT, declaration.firstToken.kind);
		assertEquals(IDENT, declaration.identToken.kind);
		assertEquals(FloatLitExpression.class, declaration.expression.getClass());
	}
	
	@Test
	public void testAssignmentStatement() throws Exception {
		String input = "abc = 123";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		Program program = parser.parse();
		AssignmentStatement statement = (AssignmentStatement) program.nodes.get(0);
		assertEquals(IDENT, statement.firstToken.kind);
		assertEquals(IntLitExpression.class, statement.expression.getClass());
	}
	
	@Test
	public void testIfStatement() throws Exception {
		String input = "if (true) {}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		Program program = parser.parse();
		IfStatement statement = (IfStatement) program.nodes.get(0);
		assertEquals(KW_IF, statement.firstToken.kind);
		assertEquals(BoolLitExpression.class, statement.expression.getClass());
		assertNotNull(statement.block);
	}
	
	@Test
	public void testWhileStatement() throws Exception {
		String input = "while (2 + 2) {}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		Program program = parser.parse();
		WhileStatement statement = (WhileStatement) program.nodes.get(0);
		assertEquals(KW_WHILE, statement.firstToken.kind);
		assertEquals(BinaryExpression.class, statement.expression.getClass());
		assertNotNull(statement.block);
	}
	
	@Test
	public void testBinaryExpression() throws Exception {
		String input = "(1 * 2 / 3 % 4 & 5 + 6 - 7 | 8) < 9 <= 10 > 11.11 >= true == false != abc";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		Expression expression = parser.expression();
		assertEquals(BinaryExpression.class, expression.getClass());
	}
	
	@Test
	public void testProgram() throws Exception {
		String input = "int three \n"
				+ "three = 3 \n"
				+ "float pi = 3.1415 \n"
				+ "if (three < pi) { three = pi - 0.1415 } \n";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		new Parser(scanner).parse();
	}

	@Test
	public void testReadExpression() throws Exception {
		String input = " array sound = read(\"fileName.wav\", mono)";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		AssignmentDeclaration declaration = new Parser(scanner).assignmentDeclaration();
		AssignmentDeclaration ad = (AssignmentDeclaration) declaration;
		Expression e = ad.expression;
		assertEquals(ReadExpression.class, e.getClass());
	}
	
	@Test
	public void testReturnBlock() throws Exception {
		String input = "{ int three = 3 bool test play(\"lisa.wav\", mono) return 0 }";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		ReturnBlock block = new Parser(scanner).returnBlock();
		assertEquals(ReturnBlock.class, block.getClass());
	}
	
	@Test
	public void testFuncLitReturnBlock() throws Exception {
		String input = "float:float test = float x -> { int three = 3 bool test play(\"lisa.wav\", mono) return 0.0 }";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		AssignmentDeclaration dec = new Parser(scanner).assignmentDeclaration();
		FuncLitBlock expr = (FuncLitBlock) dec.expression;
		assertEquals(expr.returnBlock.returnExpression, expr.returnExpression);
	}
	
	@Test
	public void testFuncLitAssignmentDeclaration() throws Exception {
		String input = "float:float sum = float a -> 22.0 + a";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		Program program = parser.parse();
		AssignmentDeclaration declaration = (AssignmentDeclaration) program.nodes.get(0);
		assertEquals(KW_FLOAT, declaration.firstToken.kind);
		assertEquals(IDENT, declaration.identToken.kind);
		assertEquals(FuncLitExpression.class, declaration.expression.getClass());
	}
	
	@Test
	public void testFuncApplication() throws Exception {
		String input = "abc(5.5)";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Expression exp = new Parser(scanner).expression();
		assertEquals(FuncAppExpression.class, exp.getClass());
	}
	
	@Test
	public void testFuncApplicationAssignment() throws Exception {
		String input = "int zeta = abc(3)";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		Program program = parser.parse();
		AssignmentDeclaration declaration = (AssignmentDeclaration) program.nodes.get(0);
		assertEquals(KW_INT, declaration.firstToken.kind);
		assertEquals(IDENT, declaration.identToken.kind);
		assertEquals(FuncAppExpression.class, declaration.expression.getClass());
	}
	
	@Test
	public void testFuncApplicationProgram() throws Exception {
		String input = "float:float lambda = float a -> a\n" + "float twelve = lambda(12.0)";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		new Parser(scanner).parse();
	}
	
	@Test
	public void testFuncComposition() throws Exception {
		String input = "abc.def.ghi(33, 12, 12)";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Expression exp = new Parser(scanner).expression();
		assertEquals(exp.getClass(), FuncCompExpression.class);
	}
	
	@Test
	public void testCurrying() throws Exception {
		String input = "float:float adder = float x -> float y -> x + y";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		AssignmentDeclaration dec = new Parser(scanner).assignmentDeclaration();
		FuncLitExpression expr = (FuncLitExpression) dec.expression;
		FuncLitExpression e = (FuncLitExpression) expr.returnExpression;
		assertEquals(e.returnExpression.getClass(), BinaryExpression.class);
	}
	
	@Test
	public void testPartialApplicationProgram() throws Exception {
		String input = "float:float add6 = float x -> adder(x, 6)";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		new Parser(scanner).parse();
	}
	
	@Test
	public void testArraySizeExpression() throws Exception {
		String input = "size(test)";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Expression e = new Parser(scanner).expression();
		assertEquals(e.getClass(), ArraySizeExpression.class);
	}
	
	@Test
	public void testArrayItemExpression() throws Exception {
		String input = "name[2]";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Expression e = new Parser(scanner).expression();
		assertEquals(e.getClass(), ArrayItemExpression.class);
	}
	
	@Test
	public void testIndexedAssigment() throws Exception {
		String input = "name[2] = 3.3";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Statement s = new Parser(scanner).statement();
		assertEquals(s.getClass(), IndexedAssignmentStatement.class);
	}
	
	@Test
	public void testReturnDeclaration() throws Exception {
		String input = "array:float lambda";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		ParamDeclaration d = new Parser(scanner).unassignedDeclaration();
		assertEquals(d.getClass(), ParamDeclaration.class);
	}
	
	@Test
	public void testReturnDeclarationFuncLit() throws Exception {
		String input = "float:float lambda = array:float x -> x";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		AssignmentDeclaration d = new Parser(scanner).assignmentDeclaration();
		assertEquals(d.getClass(), AssignmentDeclaration.class);
	}
	
	@Test
	public void testMethodStatement() throws Exception {
		String input = "func sum(int a, int b) { return a + b }";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		new Parser(scanner).methodStatement();
	}

}
