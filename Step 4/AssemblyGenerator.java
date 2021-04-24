import java.util.*;

public class AssemblyGenerator {
    private final ArrayList<String> asm = new ArrayList<>();
    private int regCtr = 0;

    public AssemblyGenerator() {}
    public void createAssembly(ScopeNode sn) {
        asm.add(";tiny code");
        asm.addAll(processTree(sn, new ArrayList<>()));
        asm.add("yolo :)");
        System.out.println();
        for(int i = 0; i < asm.size() / 2; i++) {
            System.out.println(asm.get(i));
        }
        asm.add("sys halt");
        System.out.println(asm.get(asm.size() - 1));
    }

    private ArrayList<String> processTree(ScopeNode sn, ArrayList<String> treeStrings) {
        for (int i = 0; i < sn.numChildren(); i++) {
            if (sn.getChild(i) instanceof BinaryNode) {
                treeStrings = processBinaryTree((BinaryNode)sn.getChild(i), treeStrings);
            }
            else {
                ArrayList<String> snStrings = processTree((ScopeNode)sn.getChild(i), treeStrings);
                treeStrings.addAll(snStrings);
            }
        }
        //Do things to make treeStrings ready to add to ir
        return treeStrings;
    }

    private ArrayList<String> processBinaryTree(BinaryNode bn, ArrayList<String> treeStrings) {
        if (bn.isLeaf()) {
            if (bn.getElement().equals("")) {
                return new ArrayList<>();
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
            if (assignVars.length <= 2) {
                if (type.equals("INT")) {
                    treeStrings.add("move " + assignVars[0] + " r" + regCtr);
                    treeStrings.add("addi " + assignVars[1] + " r" + regCtr);
                }
                else {
                    treeStrings.add("move " + assignVars[0] + " r" + regCtr);
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
            if (assignVars.length <= 2) {
                if (type.equals("INT")) {
                    treeStrings.add("move " + assignVars[0] + " r" + regCtr);
                    treeStrings.add("subi " + assignVars[1] + " r" + regCtr);
                }
                else {
                    treeStrings.add("move " + assignVars[0] + " r" + regCtr);
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
            if (assignVars.length <= 2) {
                if (type.equals("INT")) {
                    treeStrings.add("move " + assignVars[0] + " r" + regCtr);
                    treeStrings.add("muli " + assignVars[1] + " r" + regCtr);
                }
                else {
                    treeStrings.add("move " + assignVars[0] + " r" + regCtr);
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
            if (assignVars.length <= 2) {
                if (type.equals("INT")) {
                    treeStrings.add("move " + assignVars[0] + " r" + regCtr);
                    treeStrings.add("divi " + assignVars[1] + " r" + regCtr);
                }
                else {
                    treeStrings.add("move " + assignVars[0] + " r" + regCtr);
                    treeStrings.add("divr " + assignVars[1] + " r" + regCtr);
                }
            }
            regCtr++;
            return treeStrings;
        }
        // assignment
        if (bn.getElement().equals("assign_expr")) {
            ArrayList<String> oldStrings = new ArrayList<>(treeStrings);
            processBinaryTree(bn.getLeft(), treeStrings); //left should always have size() == 1
            ArrayList<String> right = new ArrayList<>(processBinaryTree(bn.getRight(), treeStrings));

            if (oldStrings.size() > 0) {
                right.subList(0, oldStrings.size()).clear();
            }


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

            String tempa = treeStrings.remove(treeStrings.size() - 1);
            String tempb = treeStrings.remove(treeStrings.size() - 1);

            if (assignVars.length <= 2) {
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
            processBinaryTree(bn.getLeft(), treeStrings);
            processBinaryTree(bn.getRight(), treeStrings);
            return treeStrings;
        }
        if (bn.getElement().equals("id_tail")) {
            processBinaryTree(bn.getLeft(), treeStrings);
            processBinaryTree(bn.getRight(), treeStrings);
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

            if (right.size() == 0) {
                String leftside = left.get(left.size() - 1);
                String[] lsplit = leftside.split(":");
                String type = lsplit[0];
                String var = lsplit[1];

                treeStrings.remove(treeStrings.size() - 1);

                if (type.equals("INT")) {
                    treeStrings.add("sys writei " + var);
                } else if (type.equals("FLOAT")) {
                    treeStrings.add("sys writer " + var);
                } else {
                    treeStrings.add("sys writes " + var);
                }

            }

            else {
                for (int i = 0; i < oldStrings.size(); i++) {
                    right.remove(0);
                    left.remove(0);
                }

                String[] assignVars = new String[right.size()];
                String[] varTypes = new String[right.size()];
                for (int i = 0; i < assignVars.length; i++) {
                    varTypes[i] = right.get(i).split(":")[0];
                    assignVars[i] = right.get(i).split(":")[1];
                }

                for (int i = 0; i < assignVars.length; i++) {
                    treeStrings.remove(treeStrings.size() - 1);
                }

                for (int i = 0; i < assignVars.length; i++) {
                    if (varTypes[i].equals("INT")) {
                        treeStrings.add("sys writei " + assignVars[i]);
                    } else if (varTypes[i].equals("FLOAT")) {
                        treeStrings.add("sys writer " + assignVars[i]);
                    } else {
                        treeStrings.add("sys writes " + assignVars[i]);
                    }
                }
            }


            return treeStrings;
        }
        return treeStrings;
    }
}
