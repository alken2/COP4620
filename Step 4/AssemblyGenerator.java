import java.util.*;

public class AssemblyGenerator {
    private ScopeNode root;
    private LinkedHashMap<String, SymbolTable> st;
    private ArrayList<String> asm = new ArrayList<>();
    private Stack<String> scopeStack = new Stack<>();
    private int regCtr = 0;

    public AssemblyGenerator() {}

    public ArrayList<String> createAssembly(ScopeNode sn, LinkedHashMap<String, SymbolTable> table) {
        root = sn;
        st = table;
        asm.add(";tiny code");
        asm.addAll(processTree(sn, new ArrayList<>()));
        System.out.println();
        for (String string: asm) {
            System.out.println(string);
        }
        return asm;
    }

    private ArrayList<String> processTree(ScopeNode sn, ArrayList<String> treeStrings) {
        for (int i = 0; i < sn.numChildren(); i++) {
            if (sn.getChild(i) instanceof BinaryNode) {
                ArrayList<String> bnStrings = processBinaryTree((BinaryNode)sn.getChild(i), treeStrings);
                System.out.println(bnStrings.toString());
                treeStrings = bnStrings;
            }
            else {
                String[] strings = sn.getChild(i).getElement().split(":");
                int len = strings[1].length() - 1;
                String scope = strings[1].substring(0, len);
                scopeStack.push(scope);
                //treeStrings.add("label " + scope); //might comment out if it causes problems
                ArrayList<String> snStrings = processTree((ScopeNode)sn.getChild(i), treeStrings);
                //treeStrings.add("ret"); //might comment out if it causes problems
                treeStrings.addAll(snStrings);
                scopeStack.pop();
            }
        }
        //Do things to make treeStrings ready to add to ir
        return treeStrings;
    }

    private ArrayList<String> processBinaryTree(BinaryNode bn, ArrayList<String> treeStrings) {
        if (bn.isLeaf()) {
            if (bn.getElement().equals("")) {
                return null;
            }
            // element = name type value | INTLITERAL | FLOATLITERAL
            String element = bn.getElement();
            String[] splitStr = element.split(" ");
            if (splitStr.length > 1) {
                if (treeStrings.contains("var " + splitStr[1]) || treeStrings.contains("str " + splitStr[1] + " " + splitStr[2])) {
                    element = splitStr[0] + ":" + splitStr[1];
                }
                else if (splitStr[0].equals("STRING")) {
                    element = "str " + splitStr[1] + " " + splitStr[2];
                }
                else {
                    element = "var " + splitStr[1];
                }
            }
            else {
                element = splitStr[0];
            }
            // element = "var " + name | INTLITERAL | FLOATLITERAL
            treeStrings.add(element);
            return treeStrings;
        }
        // ADD operation
        if (bn.getElement().equals("+")) {
            ArrayList<String> oldStrings = new ArrayList<>(treeStrings);
            ArrayList<String> left = new ArrayList<>(processBinaryTree(bn.getLeft(), treeStrings)); //left should always have size() == 1
            ArrayList<String> right = new ArrayList<>(processBinaryTree(bn.getRight(), treeStrings));

            for (int i = 0; i < oldStrings.size(); i++) {
                right.remove(0);
                left.remove(0);
            }

            String leftside = left.get(0);
            String[] lsplit = leftside.split(":");
            String type = lsplit[0];

            String[] assignVars = new String[right.size()];
            for (int i = 0; i < assignVars.length; i++) {
                assignVars[i] = right.get(i).split(":")[1];
            }
            System.out.println("+ type: " + type);
            if (assignVars.length > 2) {
                System.out.println("+ assignVars: " + assignVars[0] + " " + assignVars[1] + " " + assignVars[2]);
            }
            else {
                System.out.println("+ assignVars: " + assignVars[0] + " " + assignVars[1]);
            }
            if (assignVars.length > 2) {
                //TBD
            }
            else {
                if (type.equals("INT")) {
                    treeStrings.add("move " + assignVars[0] + " r" + regCtr);
                    treeStrings.add("addi " + assignVars[1] + " r" + regCtr);
                }
                else {
                    treeStrings.add("move " + assignVars[1] + " r" + regCtr);
                    treeStrings.add("addr " + assignVars[1] + " r" + regCtr);
                }
            }
            regCtr++;
            return treeStrings;
        }
        if (bn.getElement().equals("-")) {
            ArrayList<String> oldStrings = new ArrayList<>(treeStrings);
            ArrayList<String> left = new ArrayList<>(processBinaryTree(bn.getLeft(), treeStrings)); //left should always have size() == 1
            ArrayList<String> right = new ArrayList<>(processBinaryTree(bn.getRight(), treeStrings));

            for (int i = 0; i < oldStrings.size(); i++) {
                right.remove(0);
                left.remove(0);
            }

            String leftside = left.get(0);
            String[] lsplit = leftside.split(":");
            String type = lsplit[0];

            String[] assignVars = new String[right.size()];
            for (int i = 0; i < assignVars.length; i++) {
                assignVars[i] = right.get(i).split(":")[1];
            }
            System.out.println("- type: " + type);
            if (assignVars.length > 2) {
                System.out.println("- assignVars: " + assignVars[0] + " " + assignVars[1] + " " + assignVars[2]);
            }
            else {
                System.out.println("- assignVars: " + assignVars[0] + " " + assignVars[1]);
            }
            if (assignVars.length > 2) {
                //TBD
            }
            else {
                if (type.equals("INT")) {
                    treeStrings.add("move " + assignVars[0] + " r" + regCtr);
                    treeStrings.add("subi " + assignVars[1] + " r" + regCtr);
                }
                else {
                    treeStrings.add("move " + assignVars[1] + " r" + regCtr);
                    treeStrings.add("subr " + assignVars[1] + " r" + regCtr);
                }
            }
            regCtr++;
            return treeStrings;
        }
        if (bn.getElement().equals("*")) {
            ArrayList<String> oldStrings = new ArrayList<>(treeStrings);
            ArrayList<String> left = new ArrayList<>(processBinaryTree(bn.getLeft(), treeStrings)); //left should always have size() == 1
            ArrayList<String> right = new ArrayList<>(processBinaryTree(bn.getRight(), treeStrings));

            for (int i = 0; i < oldStrings.size(); i++) {
                right.remove(0);
                left.remove(0);
            }

            String leftside = left.get(0);
            String[] lsplit = leftside.split(":");
            String type = lsplit[0];

            String[] assignVars = new String[right.size()];
            for (int i = 0; i < assignVars.length; i++) {
                assignVars[i] = right.get(i).split(":")[1];
            }
            System.out.println("* type: " + type);
            if (assignVars.length > 2) {
                System.out.println("* assignVars: " + assignVars[0] + " " + assignVars[1] + " " + assignVars[2]);
            }
            else {
                System.out.println("* assignVars: " + assignVars[0] + " " + assignVars[1]);
            }
            if (assignVars.length > 2) {
                //TBD
            }
            else {
                if (type.equals("INT")) {
                    treeStrings.add("move " + assignVars[0] + " r" + regCtr);
                    treeStrings.add("muli " + assignVars[1] + " r" + regCtr);
                }
                else {
                    treeStrings.add("move " + assignVars[1] + " r" + regCtr);
                    treeStrings.add("mulr " + assignVars[1] + " r" + regCtr);
                }
            }
            regCtr++;
            return treeStrings;
        }
        if (bn.getElement().equals("/")) {
            ArrayList<String> oldStrings = new ArrayList<>(treeStrings);
            ArrayList<String> left = new ArrayList<>(processBinaryTree(bn.getLeft(), treeStrings)); //left should always have size() == 1
            ArrayList<String> right = new ArrayList<>(processBinaryTree(bn.getRight(), treeStrings));

            for (int i = 0; i < oldStrings.size(); i++) {
                right.remove(0);
                left.remove(0);
            }

            String leftside = left.get(0);
            String[] lsplit = leftside.split(":");
            String type = lsplit[0];

            String[] assignVars = new String[right.size()];
            for (int i = 0; i < assignVars.length; i++) {
                assignVars[i] = right.get(i).split(":")[1];
            }
            System.out.println("/ type: " + type);
            if (assignVars.length > 2) {
                System.out.println("/ assignVars: " + assignVars[0] + " " + assignVars[1] + " " + assignVars[2]);
            }
            else {
                System.out.println("/ assignVars: " + assignVars[0] + " " + assignVars[1]);
            }
            if (assignVars.length > 2) {
                //TBD
            }
            else {
                if (type.equals("INT")) {
                    treeStrings.add("move " + assignVars[0] + " r" + regCtr);
                    treeStrings.add("divi " + assignVars[1] + " r" + regCtr);
                }
                else {
                    treeStrings.add("move " + assignVars[1] + " r" + regCtr);
                    treeStrings.add("divr " + assignVars[1] + " r" + regCtr);
                }
            }
            regCtr++;
            return treeStrings;
        }
        // assignment
        if (bn.getElement().equals("assign_expr")) {
            ArrayList<String> oldStrings = new ArrayList<>(treeStrings);
            ArrayList<String> left = new ArrayList<>(processBinaryTree(bn.getLeft(), treeStrings)); //left should always have size() == 1
            ArrayList<String> right = new ArrayList<>(processBinaryTree(bn.getRight(), treeStrings));

            for (int i = 0; i < oldStrings.size(); i++) {
                right.remove(0);
                left.remove(0);
            }

            String leftside = left.get(0);
            String[] lsplit = leftside.split(":");
            String type = lsplit[0];
            boolean flag = false;

            String[] assignVars = new String[right.size()];
            for (int i = 0; i < assignVars.length; i++) {
                if (right.get(i).split(":").length > 1) {
                    assignVars[i] = right.get(i).split(":")[1];
                }
                else {
                    flag = true;
                    break;
                }
            }
            System.out.println("type: " + type);
            if (assignVars.length > 2) {
                System.out.println("assignVars: " + assignVars[0] + " " + assignVars[1] + " " + assignVars[2]);
            }
            else {
                System.out.println("assignVars: " + assignVars[0] + " " + assignVars[1]);
            }

            String tempa = treeStrings.remove(treeStrings.size() - 1);
            String tempb = treeStrings.remove(treeStrings.size() - 1);

            if (assignVars.length > 2) {
                //TBD
            }
            else {
                treeStrings.add("move " + assignVars[1] + " " + "r" + regCtr);
                treeStrings.add("move r" + regCtr + " " + assignVars[0]);
            }
            if (flag) {
                treeStrings.remove(treeStrings.size() - 1);
                treeStrings.remove(treeStrings.size() - 1);
                treeStrings.remove(treeStrings.size() - 1);
                treeStrings.add(tempb);
                treeStrings.add(tempa);
                treeStrings.add("move r" + --regCtr + " " + assignVars[0]);
            }

            regCtr++;
            return treeStrings;
        }
        /* Probably not be needed because nodes that contain "STRING" as elements are leaves
        if (bn.getElement().contains("STRING")) {
            ArrayList<String> right = processBinaryTree(bn.getRight(), treeStrings); // right.size() == 1
            ArrayList<String> left = processBinaryTree(bn.getLeft(), treeStrings); // left.size() == 1
        }
        */
        if (bn.getElement().equals("var_decl")) {
            ArrayList<String> left = processBinaryTree(bn.getLeft(), treeStrings);
            ArrayList<String> right = processBinaryTree(bn.getRight(), treeStrings);
            return treeStrings;
        }
        if (bn.getElement().equals("id_tail")) {
            ArrayList<String> left = processBinaryTree(bn.getLeft(), treeStrings);
            ArrayList<String> right = processBinaryTree(bn.getRight(), treeStrings);
            return treeStrings;
        }
        if (bn.getElement().equals("READ")) {
            ArrayList<String> oldStrings = new ArrayList<>(treeStrings);
            ArrayList<String> left = new ArrayList<>(processBinaryTree(bn.getLeft(), treeStrings)); //left should always have size() == 1
            ArrayList<String> right = new ArrayList<>(processBinaryTree(bn.getRight(), treeStrings));

            for (int i = 0; i < oldStrings.size(); i++) {
                right.remove(0);
                left.remove(0);
            }

            String leftside = left.get(0);
            String[] lsplit = leftside.split(":");
            String type = lsplit[0];

            String[] assignVars = new String[right.size()];
            for (int i = 0; i < assignVars.length; i++) {
                assignVars[i] = right.get(i).split(":")[1];
            }
            System.out.println("READ type: " + type);
            if (assignVars.length > 2) {
                System.out.println("READ assignVars: " + assignVars[0] + " " + assignVars[1] + " " + assignVars[2]);
            }
            else {
                System.out.println("READ assignVars: " + assignVars[0] + " " + assignVars[1]);
            }

            treeStrings.remove(treeStrings.size() - 1);
            treeStrings.remove(treeStrings.size() - 1);

            for (String var: assignVars) {
                if (type.equals("INT")) {
                    treeStrings.add("sys readi " + var);
                }
                else {
                    treeStrings.add("sys readr " + var);
                }
            }

            return treeStrings;
        }
        if (bn.getElement().equals("WRITE")) {
            ArrayList<String> oldStrings = new ArrayList<>(treeStrings);
            ArrayList<String> left = new ArrayList<>(processBinaryTree(bn.getLeft(), treeStrings)); //left should always have size() == 1
            ArrayList<String> right = new ArrayList<>(processBinaryTree(bn.getRight(), treeStrings));

            for (int i = 0; i < oldStrings.size(); i++) {
                right.remove(0);
                left.remove(0);
            }

            String leftside = left.get(0);
            String[] lsplit = leftside.split(":");
            String type = lsplit[0];

            System.out.println(right.toString());
            System.out.println("^^^^^^^^^^^^^^^^^^");

            String[] assignVars = new String[right.size()];
            String[] varTypes = new String[right.size()];
            for (int i = 0; i < assignVars.length; i++) {
                varTypes[i] = right.get(i).split(":")[0];
                assignVars[i] = right.get(i).split(":")[1];
            }

            System.out.println("WRITE assignVars: " + Arrays.toString(varTypes));
            System.out.println("WRITE assignVars: " + Arrays.toString(assignVars));
            System.out.println("^^^^^^^^^^^^^^^^^^");

            for (int i = 0; i < assignVars.length; i++) {
                treeStrings.remove(treeStrings.size() - 1);
            }

            for (int i = 0; i < assignVars.length; i++) {
                if (varTypes[i].equals("INT")) {
                    treeStrings.add("sys writei " + assignVars[i]);
                }
                else if (varTypes[i].equals("FLOAT")) {
                    treeStrings.add("sys writer " + assignVars[i]);
                }
                else {
                    treeStrings.add("sys writes " + assignVars[i]);
                }
            }


            return treeStrings;
        }
        return treeStrings;
    }
}
