import java.util.ArrayList;
import java.util.LinkedHashMap;

public class SymbolTable {
    private final String tableName;
    private final LinkedHashMap<String, ArrayList<String>> st = new LinkedHashMap<>();

    public SymbolTable(String tName) {
        this.tableName = tName;
    }

    public void insert(String name, String type, String value) {
        ArrayList<String> symbol = new ArrayList<>();
        symbol.add(name);
        symbol.add(type);
        symbol.add(value);
        st.put(name, symbol);
    }

    public boolean search(String tName) {
        return st.get(tName) != null;
    }

    public void print() {
        System.out.println("Symbol table " + tableName);
        for (ArrayList<String> symbol : st.values()) {
            String line = "name " + symbol.get(0) + " type " + symbol.get(1);
            if (symbol.get(2) != null) {
                line = line.concat(" value " + symbol.get(2));
            }
            System.out.println(line);
        }
        System.out.println();
    }
}
