package language.compiler;

public class Scandal {

	public static void main(String[] args) throws Exception {
		Compiler c = new Compiler("lib/Scandalous.scandal");
		c.compile();
		c.print();
		c.getInstance().run();
	}

}