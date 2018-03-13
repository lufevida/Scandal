package language.compiler;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.util.TraceClassVisitor;

import javafx.application.Application;
import language.tree.ImportStatement;
import language.tree.Program;
import language.tree.Statement;

public class Compiler {

	private final String className;
	private final SymbolTable symtab;
	private final Program program;
	private final ArrayList<String> imports = new ArrayList<>();
	private String code = "";
	private String temp = "";

	private class DynamicClassLoader extends ClassLoader {
		public DynamicClassLoader(ClassLoader parent) {
			super(parent);
		}

		public Class<? extends Application> define(String name, byte[] bytecode) {
			return super.defineClass(name, bytecode, 0, bytecode.length).asSubclass(Application.class);
		}
	}

	public Compiler(String inPath) throws Exception {
		link(inPath);
		className = getClassName(inPath);
		symtab = new SymbolTable(className);
		program = getProgram(code);
		program.decorate(symtab);
		program.generate(null, symtab);
	}

	private void link(String inPath) throws Exception {
		if (imports.contains(inPath)) return;
		imports.add(inPath);
		temp = getCode(inPath);
		code = temp + code; // depth-first
		Program program = getProgram(temp);
		for (Statement s : program.statements)
			if (s.getClass() == ImportStatement.class)
				link(((ImportStatement) s).expression.firstToken.text);
	}
	
	private String getCode(String inPath) throws Exception {
		Path path = FileSystems.getDefault().getPath(inPath);
		return new String(Files.readAllBytes(path));
	}
	
	private Program getProgram(String code) throws Exception {
		Scanner scanner = new Scanner(code);
		scanner.scan();
		Program program = new Parser(scanner).parse();
		return program;
	}
	
	private String getClassName(String inPath) {
		Path path = FileSystems.getDefault().getPath(inPath);
		int extension = path.getFileName().toString().lastIndexOf('.');
		return path.getFileName().toString().substring(0, extension);
	}

	public void run() throws Exception {
		//new DynamicClassLoader(getUrlClassLoader(userPath));
		DynamicClassLoader loader = new DynamicClassLoader(Thread.currentThread().getContextClassLoader());
		Application.launch(loader.define(className, program.bytecode), className);
	}

	public void save() throws Exception {
		OutputStream output = new FileOutputStream(className + ".class");
		output.write(program.bytecode);
		output.close();
	}
	
	public void print() {
		new ClassReader(program.bytecode).accept(new TraceClassVisitor(new PrintWriter(System.out)), ClassReader.SKIP_DEBUG);
	}

	public String getBytecode() {
		StringWriter out = new StringWriter();
		new ClassReader(program.bytecode).accept(new TraceClassVisitor(new PrintWriter(out)), ClassReader.SKIP_DEBUG);
		return out.toString();
	}
	
	/*public void getUrlClassLoader(String userClassPath) throws Exception {
		URLClassLoader urlClassLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();
	    Class<URLClassLoader> urlClass = URLClassLoader.class;
	    Method method = urlClass.getDeclaredMethod("addURL", new Class[]{URL.class});
	    method.setAccessible(true);
	    method.invoke(urlClassLoader, new Object[]{new File(userClassPath).toURI().toURL()});
	}*/

}
