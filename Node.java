public class Node {
    public final NodeType type;
    public int id;

    // Allow no-id option in final product for external node testing
    /*
    public Node (NodeType t) {
        type = t;
    }
    */

    public Node (NodeType t) {
        this.id = -1; // Temporary
        type = t;
    }

    // Default to hidden type
    public Node () {
        this.id = -1; // Temporary
        type = NodeType.HIDDEN;
    }

    public Node (int id) {
        this.id = id;
        type = NodeType.HIDDEN;
    }
}
