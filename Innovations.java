import java.util.ArrayList;

public class Innovations {
    public Innovations () {
        connections = new ArrayList<ConnectionInv>();
        nodes = new ArrayList<NodeInv>();

        nextConnId = 0;
        nextNodeId = 0;
    }

    // Add node innovation
    public boolean addInnovation (ConnectionGene c1, ConnectionGene c2) {
        // If innovation is novel, add to database
        int inv_id = checkInnovation(c1, c2);
        if ( inv_id == -1) {
            c1.innovation = connGeneNum();
            c2.innovation = connGeneNum();
            nodes.add( new NodeInv(nodeInvNum(), c1, c2) );
        }
        else {
            // Assign connection ids to existing innovation ids
            NodeInv n = getNodeInvById(inv_id);
            c1.innovation = n.c_in.innovation;
            c2.innovation = n.c_out.innovation;

            return false;
        }

        // New innovation
        return true;
    }

    // Add connection innovation
    public boolean addInnovation (ConnectionGene c) {
        // If innovation is novel, add to database
        int inv_id = checkInnovation(c);
        if (inv_id == -1) {
            c.innovation = connGeneNum();
            connections.add( new ConnectionInv(nodeInvNum(), c) );
        }
        else {
            // Assign connection id to existing innovation id
            ConnectionInv ci = getConnectionInvById(inv_id);
            c.innovation = ci.c.innovation;

            return false;
        }

        // New innovation
        return true;
    }

    // Check for node innovation
    public int checkInnovation (ConnectionGene c_in, ConnectionGene c_out) {
        // Look for a node innovation with matching in and out node ids
        for ( NodeInv ni : nodes )
            if (c_in.in.id == ni.c_in.in.id && c_out.out.id == ni.c_out.out.id)
                return ni.id;

        // Innovation doesn't exist
        return -1;
    }

    // Check for connection innovation
    public int checkInnovation (ConnectionGene c) {
        // Look for a node innovation with matching in and out node ids
        for ( ConnectionInv ci : connections )
            if (c.in.id == ci.c.in.id && c.out.id == ci.c.out.id)
                return ci.id;

        // Innovation doesn't exist
        return -1;
    }

    // Check innovations for both connection (choice = 0) and node (choice = 1)
    // TODO: This is a terrible design, and definitely temporary.
    public int checkInnovation (Node c_in, Node c_out, int choice) {
        if (choice == 0) {
            // Look for a connection innovation with matching in and out node ids
            for ( ConnectionInv ci : connections )
                if (c_in.id == ci.c.in.id && c_out.id == ci.c.out.id)
                    return ci.id;
        }
        else if (choice == 1) {
            // Look for a node innovation with matching in and out node ids
            for ( NodeInv ni : nodes )
                if (c_in.id == ni.c_in.in.id && c_out.id == ni.c_out.out.id)
                    return ni.id;
        }

        // Innovation doesn't exist
        return -1;
    }

    /*
    // Get id of input connection for existing innovation
    public int getInId(int node_id) {
        for ( NodeInv ni : nodes )
            if (ni.id == node_id)
                return ni.id_in;

        // Node innovation doesn't exist
        return -1;
    }

    // Get id of output connection for existing innovation
    public int getOutId(int node_id) {
        for ( NodeInv ni : nodes )
            if (ni.id == node_id)
                return ni.id_out;

        // Node innovation doesn't exist
        return -1;
    }
    */

    private NodeInv getNodeInvById (int id) {
        for ( NodeInv n : nodes )
            if (n.id == id)
                return n;
        return null;
    }

    private ConnectionInv getConnectionInvById (int id) {
        for ( ConnectionInv c : connections )
            if (c.id == id)
                return c;
        return null;
    }

    // Global innovation number incrementers
    /************************************ */
    // TODO: These should be private
    private int nodeInvNum () {
        return nextNodeId++;
    }

    private int connInvNum () {
        return nextConnId++;
    }

    private int connGeneNum () {
        return connGeneId++;
    }
    /************************************ */

    private ArrayList<ConnectionInv> connections;
    private ArrayList<NodeInv> nodes;

    private int nextConnId;
    private int nextNodeId;
    private int connGeneId;

    /********************************/
    // Container innovation classes //
    /********************************/

    class ConnectionInv {
        public ConnectionInv (int id, ConnectionGene c) {
            this.c  = c;
            this.id = id;
        }

        final public ConnectionGene c;
        final public int id;
    }

    class NodeInv {
        public NodeInv (int id, ConnectionGene c1, ConnectionGene c2) {
            this.c_in   = c1;
            this.c_out  = c2;
            this.id     = id;
        }

        final public ConnectionGene c_in;
        final public ConnectionGene c_out;
        final public int id; // Node id
    }

    /********************************/
}
