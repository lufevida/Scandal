package language.compiler;

public class Dump implements Runnable {

	public static void main(String[] args) throws Exception {
		Compiler c = new Compiler("lib/Lambdas.scandal");
		c.compile();
		c.print();
		c.getInstance().run();
	}

	public void run() {}

}