import java.util.ArrayList;
import java.util.Collections;

public class IRGenerator {
    private ScopeNode root;
    private ArrayList<String> ir = new ArrayList<>();
    private int tempCtr = 1;

    public IRGenerator() {}

    public ArrayList<String> createIR(ScopeNode sn) {
        root = sn;
        ir.add("IR code");
        ir.addAll(processTree(sn, new ArrayList<>()));
        System.out.println();
        for (String string: ir) {
            System.out.println(string);
        }
        return ir;
    }

    private ArrayList<String> processTree(ScopeNode sn, ArrayList<String> treeStrings) {
        for (int i = 0; i < sn.numChildren(); i++) {
            if (sn.getChild(i) instanceof BinaryNode) {
                ArrayList<String> bnStrings = processBinaryTree((BinaryNode)sn.getChild(i), treeStrings);
                //Do things to make bnStrings ready to add to treeStrings
                treeStrings.addAll(bnStrings);
            }
            else {
                String[] strings = sn.getChild(i).getElement().split(":");
                String scope = strings[1];
                treeStrings.add("LABEL " + scope);
                treeStrings.add("LINK");
                ArrayList<String> snStrings = processTree((ScopeNode)sn.getChild(i), treeStrings);
                //Do things to make snStrings ready to add to treeStrings
                treeStrings.add("RET");
                treeStrings.addAll(snStrings);
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
            if (!element.contains(":") && treeStrings.contains(element)) {
                String[] splitStr = element.split(" ");
                element = splitStr[0] + ":" + splitStr[1];
            }
            // element = type:name | INTLITERAL | FLOATLITERAL
            /*
            ArrayList<String> tmpStrings = new ArrayList<>();
            tmpStrings.add(element);
            tmpStrings.addAll(treeStrings);
            return tmpStrings;
            */
            treeStrings.add(element);
            return treeStrings;
        }
        // ADD operation
        if (bn.getElement().equals("+")) {
            ArrayList<String> oldStrings = new ArrayList<>(treeStrings);
            ArrayList<String> right = processBinaryTree(bn.getRight(), treeStrings);
            ArrayList<String> left = processBinaryTree(bn.getLeft(), treeStrings); //left should always have size() == 1
            for (String str: oldStrings) {
                right.remove(str);
                left.remove(str);
            }
            String[] leftside = left.get(0).split(":");
            if (right.size() == 1) {
                String[] rightside = right.get(0).split(":");
                if (rightside[0].equals("INT") && leftside[0].equals("INT")) {
                    treeStrings.add("ADDI " + leftside[1] + " " + rightside[1] + "$T" + tempCtr++);
                }
                else if (rightside[0].equals("FLOAT") && leftside[0].equals("FLOAT")) {
                    treeStrings.add("ADDF " + leftside[1] + " " + rightside[1] + "$T" + tempCtr++);
                }
                else {
                    System.out.println("Type mixing is not allowed in Little: " + leftside[0] + " " + rightside[0]);
                    System.exit(1);
                }
            }
            else {
                System.out.println("yolo :)");
            }
            return treeStrings;
        }
        if (bn.getElement().equals("-")) {
            ArrayList<String> oldStrings = new ArrayList<>(treeStrings);
            ArrayList<String> right = processBinaryTree(bn.getRight(), treeStrings);
            ArrayList<String> left = processBinaryTree(bn.getLeft(), treeStrings); //left should always have size() == 1
            for (String str: oldStrings) {
                right.remove(str);
                left.remove(str);
            }
            String[] leftside = left.get(0).split(":");
            if (right.size() == 1) {
                String[] rightside = right.get(0).split(":");
                if (rightside[0].equals("INT") && leftside[0].equals("INT")) {
                    treeStrings.add("SUBI " + leftside[1] + " " + rightside[1] + "$T" + tempCtr++);
                }
                else if (rightside[0].equals("FLOAT") && leftside[0].equals("FLOAT")) {
                    treeStrings.add("SUBF " + leftside[1] + " " + rightside[1] + "$T" + tempCtr++);
                }
                else {
                    System.out.println("Type mixing is not allowed in Little: " + leftside[0] + " " + rightside[0]);
                    System.exit(1);
                }
            }
            else {
                System.out.println("yolo :)");
            }
            return treeStrings;
        }
        if (bn.getElement().equals("*")) {
            ArrayList<String> oldStrings = new ArrayList<>(treeStrings);
            ArrayList<String> right = processBinaryTree(bn.getRight(), treeStrings);
            ArrayList<String> left = processBinaryTree(bn.getLeft(), treeStrings); //left should always have size() == 1
            for (String str: oldStrings) {
                right.remove(str);
                left.remove(str);
            }
            String[] leftside = left.get(0).split(":");
            if (right.size() == 1) {
                String[] rightside = right.get(0).split(":");
                if (rightside[0].equals("INT") && leftside[0].equals("INT")) {
                    treeStrings.add("MULI " + leftside[1] + " " + rightside[1] + "$T" + tempCtr++);
                }
                else if (rightside[0].equals("FLOAT") && leftside[0].equals("FLOAT")) {
                    treeStrings.add("MULF " + leftside[1] + " " + rightside[1] + "$T" + tempCtr++);
                }
                else {
                    System.out.println("Type mixing is not allowed in Little: " + leftside[0] + " " + rightside[0]);
                    System.exit(1);
                }
            }
            else {
                System.out.println("yolo :)");
            }
            return treeStrings;
        }
        if (bn.getElement().equals("/")) {
            ArrayList<String> oldStrings = new ArrayList<>(treeStrings);
            ArrayList<String> right = processBinaryTree(bn.getRight(), treeStrings);
            ArrayList<String> left = processBinaryTree(bn.getLeft(), treeStrings); //left should always have size() == 1
            for (String str: oldStrings) {
                right.remove(str);
                left.remove(str);
            }
            String[] leftside = left.get(0).split(":");
            if (right.size() == 1) {
                String[] rightside = right.get(0).split(":");
                if (rightside[0].equals("INT") && leftside[0].equals("INT")) {
                    treeStrings.add("DIVI " + leftside[1] + " " + rightside[1] + "$T" + tempCtr++);
                }
                else if (rightside[0].equals("FLOAT") && leftside[0].equals("FLOAT")) {
                    treeStrings.add("DIVF " + leftside[1] + " " + rightside[1] + "$T" + tempCtr++);
                }
                else {
                    System.out.println("Type mixing is not allowed in Little: " + leftside[0] + " " + rightside[0]);
                    System.exit(1);
                }
            }
            else {
                System.out.println("yolo :)");
            }
            return treeStrings;
        }
        // assignment
        if (bn.getElement().equals("assign_expr")) {
            ArrayList<String> right = processBinaryTree(bn.getRight(), treeStrings); // right.size() == 1
            ArrayList<String> left = processBinaryTree(bn.getLeft(), treeStrings); // left.size() == 1
            String[] leftside = left.get(0).split(":");
            if (leftside[0].equals("INT")) {
                treeStrings.add("STOREI " + "$T" + tempCtr + " " + leftside[1]);
            }
            else if (leftside[0].equals("FLOAT")) {
                treeStrings.add("STOREF " + "$T" + tempCtr + " " + leftside[1]);
            }
            return treeStrings;
        }
        if (bn.getElement().contains("STRING")) {
            ArrayList<String> right = processBinaryTree(bn.getRight(), treeStrings); // right.size() == 1
            ArrayList<String> left = processBinaryTree(bn.getLeft(), treeStrings); // left.size() == 1
        }
        return treeStrings;
    }
}
