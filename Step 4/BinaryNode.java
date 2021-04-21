public class BinaryNode extends AbstractNode {
    //Data Fields
    private final BinaryNode left;
    private final BinaryNode right;

    //Constructors
    BinaryNode(String element) {
        super(element);
        left = right = null;
    }

    BinaryNode(String element, BinaryNode left, BinaryNode right) {
        super(element);
        this.left = left;
        this.right = right;
    }

    //Methods
    public String getElement() {
        return element;
    }

    protected void print() {
        System.out.println("Element: " + this.getElement());
        if (!this.isLeaf()) {
            System.out.println("Left Node of " + this.getElement());
            assert this.left != null;
            print(this.left);
            System.out.println("Right Node of " + this.getElement());
            assert this.right != null;
            print(this.right);
        }
    }

    public void print(BinaryNode bn) {
        System.out.println("Element: " + bn.getElement());
        if (!bn.isLeaf()) {
            System.out.println("Left Node of " + bn.getElement());
            assert bn.left != null;
            print(bn.left);
            System.out.println("Right Node of " + bn.getElement());
            assert bn.right != null;
            print(bn.right);
        }
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
