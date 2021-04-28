public abstract class AbstractNode {
    //Data Field
    protected final String element;

    //Constructor
    protected AbstractNode(String element) {
        this.element = element;
    }

    //Methods
    protected abstract String getElement();

    protected abstract AbstractNode getChild(int index);

    protected abstract void print();
}
