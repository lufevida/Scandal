package language.compiler;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Stack;

import language.tree.Declaration;

public class SymbolTable {

	public final String className;
	private final Stack<Hashtable<String, Declaration>> table = new Stack<Hashtable<String, Declaration>>();
	public int scopeNumber = 0;
	public int slotCount = 1;
	public int lambdaCount = 0;
	public final ArrayList<Lambda> lambdas = new ArrayList<>();
	
	public Lambda lambdaWithName(String name) {
		for (Lambda l : lambdas) if (l.name.equals(name)) return l;
		return null;
	}

	public SymbolTable(String className) {
		this.className = className;
		table.push(new Hashtable<String, Declaration>()); // Initialize the zeroth scope
	}

	public void enterScope() {
		table.push(new Hashtable<String, Declaration>());
		scopeNumber++;
	}

	public void leaveScope() {
		if (!table.empty()) {
			table.pop();
			scopeNumber--;
		}
	}

	public void insert(String ident, Declaration Declaration){
		if (!table.empty()) table.peek().put(ident, Declaration);
	}

	public Declaration lookup(String ident) {
		// Last in, first out
		for (int i = table.size() - 1; i >= 0; i--) {
			Declaration info = table.elementAt(i).get(ident);
			if (info != null) return info;
		}
		return null;
	}

	public Declaration topOfStackLookup(String ident) {
		if (!table.empty()) return table.peek().get(ident);
		return null;
	}
	
	@Override
	public String toString() {
		String result = "\n";
		for (Hashtable<String, Declaration> elem : table) {
			for (String name : elem.keySet()) {
				result += name + ": ";
				result += elem.get(name).slotNumber + "\n";
			}
		}
		return result;
	}

}
