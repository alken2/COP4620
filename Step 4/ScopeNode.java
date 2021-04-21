import java.util.ArrayList;

public class ScopeNode extends AbstractNode {
    //Data Field
    private ArrayList<AbstractNode> children = new ArrayList<>();

    //Constructors
    ScopeNode(String element) {
        super(element);
    }

    ScopeNode(String element, ArrayList<AbstractNode> children) {
        super(element);
        this.children = children;
    }

    //Methods
    protected String getElement() {
        return element;
    }

    public AbstractNode getChild(int index) {
        if (index < numChildren() && index >= 0) {
            return children.get(index);
        }
        else {
            return null;
        }
    }

    public void print() {
        System.out.println("Element: " + this.getElement());
        for (int i = 0; i < children.size(); i++) {
            System.out.println("Child node of " + this.getElement() + ": Index " + i);
            this.getChild(i).print();
        }
    }

    public void addChild(AbstractNode child) {
        this.children.add(child);
    }

    public int numChildren() {
        return children.size();
    }
}
