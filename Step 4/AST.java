import java.util.Arrays;
import java.util.Stack;

public class AST {

    //A lot of this code is based on my implementation of a Binary Expression Tree in COP3530
    //Nested Class
    private static class BinaryNode {
        //Data Fields
        private String element;
        private BinaryNode left;
        private BinaryNode right;

        //Constructors
        BinaryNode() {
            left = right = null;
        }

        BinaryNode(String element) {
            this.element = element;
            left = right = null;
        }

        BinaryNode(String element, BinaryNode left, BinaryNode right) {
            this.element = element;
            this.left = left;
            this.right = right;
        }

        //Methods
        public String getElement() {
            return element;
        }

        public BinaryNode getLeft() {
            return left;
        }

        public BinaryNode getRight() {
            return right;
        }

        public boolean isLeaf() {
            return (getLeft() == null && getRight() == null);
        }
    }

    //Data Fields
    BinaryNode expression;
    int flag;

    //Constructors
    AST() {
        expression = new BinaryNode();
    }

    AST(String expr, char mode) {
        this();
        if (mode == 'p') {
            if (!buildFromPostfix(expr)) {
                throw new IllegalStateException("Invalid notation: " + expr);
            }
        }

        else if (mode == 'i') {
            if (!buildFromInfix(expr)) {
                throw new IllegalStateException("Invalid notation: " + expr);
            }
        }

        else {
            throw new IllegalStateException("Invalid notation: " + expr);
        }
    }

    //Methods
    //Public interface methods
    public boolean buildFromPostfix(String postfix) {
        //Stage 1: Initiate all variables local to the entire method and clear the tree if there are existing nodes
        Stack<BinaryNode> nodeStack = new Stack<>();
        String[] postfixArray = postfix.split(" ");
        if (!isEmpty()) {
            makeEmpty(expression);
        }
        if (postfixArray[0].equals("")) {
            return true;
        }

        //Stage 2: Determine if the string is an operand or an operator and start constructing the tree appropriately for each string
        for (String s : postfixArray) {
            if (Arrays.asList("+", "-", "*", "/").contains(s)) {
                if (nodeStack.empty()) {
                    return false;
                }
                BinaryNode temp1 = nodeStack.pop();
                if (nodeStack.empty()) {
                    return false;
                }
                BinaryNode temp2 = nodeStack.pop();
                BinaryNode temp3 = new BinaryNode(s, temp2, temp1);
                nodeStack.push(temp3);
            }
            else {
                nodeStack.push(new BinaryNode(s));
            }
        }

        //Stage 3: Finish tree construction by popping from the stack for the last time and check whether or not the stack is empty
        if (!nodeStack.isEmpty()) {
            expression = nodeStack.pop();
            return nodeStack.isEmpty();
        }
        return true;
    }

    public boolean buildFromInfix(String infix) {
        //Stage 1: Initiate all variables local to the entire method and clear the tree if there are existing nodes
        Stack<BinaryNode> operators = new Stack<>();
        Stack<BinaryNode> operands = new Stack<>();
        int parentheses = 0;
        String[] infixArray = infix.split(" ");
        if (!isEmpty()) {
            makeEmpty(expression);
        }
        if (infixArray[0].equals("")) {
            return true;
        }

        //Stage 2: Determine if the string is an operand or an operator and start constructing the tree appropriately for each string
        for (String s : infixArray) {
            if (Arrays.asList("+", "-", "*", "/").contains(s)) {
                if (operands.isEmpty()) {
                    return false;
                }
                if (!operators.isEmpty()) {
                    if (Arrays.asList("*", "/").contains(operators.peek().getElement())) {
                        if (notBuildingInfix(operators, operands)) {
                            return false;
                        }
                    } else if (Arrays.asList("+", "-").contains(operators.peek().getElement()) && Arrays.asList("+", "-").contains(s)) {
                        if (notBuildingInfix(operators, operands)) {
                            return false;
                        }
                    }
                }
                operators.push(new BinaryNode(s));
            } else if (Arrays.asList("(", ")").contains(s)) {
                if (s.equals("(")) {
                    parentheses++;
                    operators.push(new BinaryNode(s));
                } else {
                    parentheses--;
                    if (parentheses < 0 || operands.isEmpty()) {
                        return false;
                    }
                    BinaryNode temp = operands.pop();
                    while (!operators.peek().getElement().equals("(")) {
                        expression = new BinaryNode(operators.pop().getElement(), operands.pop(), temp);
                        temp = expression;
                        operands.push(expression);
                    }
                    operators.pop();
                }
            } else {
                operands.push(new BinaryNode(s));
            }
        }

        //Stage 3: Before, while, and after finishing tree construction, perform final checks
        if (parentheses != 0) {
            return false;
        }
        while (!operators.isEmpty()) {
            if (notBuildingInfix(operators, operands)) {
                return false;
            }
        }
        if (!operands.isEmpty()) {
            expression = operands.pop();
            return operands.isEmpty();
        }
        return true;
    }

    public void printInfixExpression() {
        printInfixExpression(expression);
        System.out.println();
    }

    public void printPostfixExpression() {
        printPostfixExpression(expression);
        System.out.println();
    }

    public int size() {
        return size(expression);
    }

    public boolean isEmpty() {
        return size() == 0;
    }

    public int leafNodes() {
        return leafNodes(expression);
    }

    //Required private helper methods (recursive)
    private void printInfixExpression(BinaryNode n) {
        if (n == null) {
            return;
        }
        if (n.isLeaf()) {
            System.out.print(n.getElement() + " ");
        }
        else {
            System.out.print("( ");
            printInfixExpression(n.getLeft());
            System.out.print(n.getElement() + " ");
            printInfixExpression(n.getRight());
            System.out.print(") ");
        }
    }

    private void makeEmpty(BinaryNode t) {
        if (t == null) {
            return;
        }
        if (t.isLeaf()) {
            return;
        }
        makeEmpty(t.getLeft());
        makeEmpty(t.getRight());
        remove(t.getElement());
    }

    private void printPostfixExpression(BinaryNode n) {
        if (n == null) {
            return;
        }
        printPostfixExpression(n.getLeft());
        printPostfixExpression(n.getRight());
        System.out.print(n.getElement() + " ");
    }

    private int size(BinaryNode t) {
        if (t == null) {
            return 0;
        }
        if (t.isLeaf() && t.getElement() == null) {
            return 0;
        }
        return size(t.getLeft()) + size(t.getRight()) + 1;
    }

    private int leafNodes(BinaryNode t) {
        if (t == null) {
            return 0;
        }
        if (t.isLeaf() && t.getElement() == null) {
            return 0;
        }
        else if (t.isLeaf()) {
            return 1;
        }
        return leafNodes(t.getLeft()) + leafNodes(t.getRight());
    }

    //Custom private helper methods
    private boolean notBuildingInfix(Stack<BinaryNode> operators, Stack<BinaryNode> operands) {
        if (operands.isEmpty()) {
            return true;
        }
        BinaryNode temp1 = operands.pop();
        if (operands.isEmpty()) {
            return true;
        }
        BinaryNode temp2 = operands.pop();
        BinaryNode temp3 = new BinaryNode(operators.pop().getElement(), temp2, temp1);
        operands.push(temp3);
        return false;
    }

    //Credit for remove methods goes to: https://www.geeksforgeeks.org/binary-search-tree-set-2-delete/
    private void remove(String s) {
        flag = 0;
        expression = remove(expression, s);
    }

    private BinaryNode remove(BinaryNode root, String key) {
        if (root == null) {
            return null;
        }
        else if (!root.getElement().equals(key)) {
            root.left = remove(root.getLeft(), key);
        }
        else if (root.isLeaf()) {
            if (root.getElement() == null) {
                return root;
            }
            root = expression;
            for (int i = 0; i < flag; i++) {
                root = root.getRight();
            }
            flag++;
            root.right = remove(root.getRight(), key);
        }
        else {
            root = null;
        }
        return root;
    }
}
