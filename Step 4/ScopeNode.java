import java.util.ArrayList;

public class ScopeNode extends AbstractNode {
    //Data Field
    private ArrayList<AbstractNode> children = new ArrayList<>();
    private ArrayList<String> parameters = null;

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
            if (this.parameters != null) {
                System.out.println("Child node of " + this.getElement() + this.parameters.toString() + ": Index " + i);
            }
            else {
                System.out.println("Child node of " + this.getElement() + ": Index " + i);
            }
            this.getChild(i).print();
        }
    }

    public void addChild(AbstractNode child) {
        this.children.add(child);
        //System.out.println("Added child to " + this.getElement() + " <----> "  + child.getElement());
    }

    public void addParameters(ArrayList<String> slist) { //remember this method when dealing with function parameters
        this.parameters = slist;
    }

    public int numChildren() {
        return children.size();
    }
}
