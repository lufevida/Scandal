package language.compiler;

public class Scandal {

	public static void main(String[] args) throws Exception {
		Compiler c = new Compiler("lib/Lambdas.scandal");
		c.compile();
		c.getInstance().run();
	}

}