public class BinaryNode {
    //Data Fields
    private String element;
    private BinaryNode left;
    private BinaryNode right;

    //Constructors
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
