import java.util.ArrayList;
import java.util.Collections;

public class IRGenerator {
    private ScopeNode root;
    private ArrayList<String> ir;
    private int tempCtr = 1;

    public ArrayList<String> createIR(ScopeNode sn) {
        root = sn;
        ir.add(";IR code");
        ir.add(";LABEL main");
        ir.add(";LINK");
        ir.addAll(processTree(sn));
        ir.add(";RET");
        return ir;
    }

    private ArrayList<String> processTree(ScopeNode sn) {
        ArrayList<String> treeStrings = new ArrayList<>();
        for (int i = 0; i < sn.numChildren() - 1; i++) {
            if (sn.getChild(i) instanceof BinaryNode) {
                ArrayList<String> bnStrings = processBinaryTree((BinaryNode)sn.getChild(i));
                //Do things to make bnStrings ready to add to treeStrings
                treeStrings.addAll(bnStrings);
            }
            else {
                ArrayList<String> snStrings = processTree((ScopeNode)sn.getChild(i));
                //Do things to make snStrings ready to add to treeStrings
                treeStrings.addAll(snStrings);
            }
        }
        //Do things to make treeStrings ready to add to ir
        return treeStrings;
    }

    private ArrayList<String> processBinaryTree(BinaryNode bn) {
        ArrayList<String> binaryTreeStrings = new ArrayList<>();
        if (bn.isLeaf()) {
            if (bn.getElement().equals("")) {
                return null;
            }
            // strArr[0] = name | INTLITERAL | FLOATLITERAL
            // strArr[1] = type |
            // strArr[2] = value |
            String[] strArr = bn.getElement().split(":");
            //Do things with strArr
            ArrayList<String> strings = new ArrayList<>();
            Collections.addAll(strings, strArr);
            return strings;
        }
        // 4-function operations
        if (bn.getElement().equals("+") || bn.getElement().equals("-") || bn.getElement().equals("*") || bn.getElement().equals("/")) {
            ArrayList<String> right = processBinaryTree(bn.getRight());
            ArrayList<String> left = processBinaryTree(bn.getLeft());
            // TBD
            return right; // Placeholder
        }
        // assignment
        if (bn.getElement().equals("assign_expr")) {
            ArrayList<String> right = processBinaryTree(bn.getRight());
            ArrayList<String> left = processBinaryTree(bn.getLeft());
            // TBD
            return right; // Placeholder
        }
        return binaryTreeStrings;
    }
}
