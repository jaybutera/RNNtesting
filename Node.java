public class Node {
    public final NodeType type;
    public final int id;

    // Allow no-id option in final product for external node testing
    /*
    public Node (NodeType t) {
        type = t;
    }
    */

    public Node (NodeType t, int id) {
        this.id = id;
        type = t;
    }

    // Default to hidden type
    /*
    public Node () {
        type = NodeType.HIDDEN;
    }
    */

    public Node (int id) {
        this.id = id;
        type = NodeType.HIDDEN;
    }
}
