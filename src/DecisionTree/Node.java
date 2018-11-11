package DecisionTree; /**
 * Created by olanre on 2018-10-22.
 * Generic Java Tree Class
 * Credit to https://www.javagists.com/java-tree-data-structure
 */
import java.util.ArrayList;

public class Node {
    private ArrayList<Node> children = new ArrayList<Node>();
    private Node parent = null;
    private DecisionNode data = null;



    private String branch;

    public Node(DecisionNode data) {
        this.data = data;
    }

    public Node() {

    }


    public Node(DecisionNode data, Node parent) {
        this.data = data;
        this.setParent(parent);
    }

    public ArrayList<Node> getChildren() {
        return children;
    }

    public void setParent(Node parent) {
        parent.addChild(this);
        this.parent = parent;
    }

    public void addChild(DecisionNode data) {
        Node child = new Node(data);
        child.setParent(this);
        this.children.add(child);
    }

    public void addChild(Node child) {
        child.setParent(this);
        this.children.add(child);
    }

    public DecisionNode getData() {
        return this.data;
    }

    public void setData(DecisionNode data) {
        this.data = data;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public boolean isRoot() {
        return (this.parent == null);
    }

    public boolean isLeaf() {
        return this.children.size() == 0;
    }

    public void removeParent() {
        this.parent = null;
    }
}