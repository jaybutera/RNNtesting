import java.util.ArrayList;
import java.util.Optional;

public class Innovations {
    public Innovations () {
        connections = new ArrayList<ConnectionInv>();
        nodes       = new ArrayList<NodeInv>();

        nextConnId = 0;
        nextNodeId = 0;
    }

    public Innovations (ArrayList<Node> inputs, ArrayList<Node> outputs) {
        connections = new ArrayList<ConnectionInv>();
        nodes       = new ArrayList<NodeInv>();

        // Initialize database iterators to pass initial input/output nodes
        nextConnId = 0;
        nextNodeId = inputs.size() + outputs.size(); // Start iterator after input/output nodes

        // Add input/output nodes to database
        for ( Node n : inputs )
            addInnovation(n);
        for ( Node n : outputs )
            addInnovation(n);
    }

    // Add node innovation
    public boolean addInnovation (ConnectionGene c1, ConnectionGene c2, Node n) {
        // TODO: Add support for one connection parameter provided

        // If node is not from initialization (input/output nodes)
        // A.K.A. if it hasn't already been initialized
        if (n.id == -1) {
            int inv_id = checkInnovation(c1, c2, n);
            //System.out.println("Found a hidden node: " + inv_id);

            // If innovation is novel, add to database
            if ( inv_id == -1) {
                /*
                c1.innovation = connInvNum();
                c2.innovation = connInvNum();
                */

                // Add connections to database
                addInnovation(c1);
                addInnovation(c2);

                int inv = nodeInvNum();
                nodes.add( new NodeInv(inv, Optional.of(c1), Optional.of(c2)) );
                // Assign genome node a new id
                n.id = inv;
            }
            else {
                // Assign connection ids to existing innovation ids
                NodeInv ni = getNodeInvById(inv_id);

                /*
                System.out.println(c1);
                System.out.println(c1);
                */

                c1.innovation = ni.c_in.get().innovation;
                c2.innovation = ni.c_out.get().innovation;

                // Assign genome node the existing id
                n.id = inv_id;

                // Not a new innovation
                return false;
            }
        }
        else
            return false;

        // New innovation
        return true;
    }

    // TODO: Make public. Instead of optional parameters just overload
    // Assumes pre initialization of id in parameter node
    public boolean addInnovation (Node n) {
        // Innovation is novel
        if ( !checkInnovation(n) ) {
            int inv = nodeInvNum();

            // Add node to database
            nodes.add( new NodeInv(inv, Optional.empty(), Optional.empty()) );

            // Assign genome node a new id
            n.id = inv;

            // New innovation
            return true;
        }

        // Innovation exists
        return false;
    }

    // Add connection innovation
    public boolean addInnovation (ConnectionGene c) {
        int inv_id = checkInnovation(c);

        // If innovation is novel, add to database
        if (inv_id == -1) {
            int inv = connInvNum();
            c.innovation = inv;
            connections.add( new ConnectionInv(inv, c) );

            /// Stitch connection innovation to existing node(s)

            // In node
            /*
            NodeInv ni = getNodeInvById(c.in.id);
            if (ni != null)
                ni.c_in = Optional.of(c);
            // Out node
            ni = getNodeInvById(c.out.id);
            if (ni != null)
                ni.c_out = Optional.of(c);
                */

            ///
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
    public int checkInnovation (ConnectionGene c_in, ConnectionGene c_out, Node n) {
        // Look for a node innovation with matching in and out node ids
        for ( NodeInv ni : nodes )
            if ( ni.c_in.isPresent() && ni.c_out.isPresent() )
                if (c_in.in.id == ni.c_in.get().in.id && c_out.out.id == ni.c_out.get().out.id)
                    return ni.id;

        // Innovation doesn't exist
        return -1;
    }

    // TODO: Not consistent with other overloads. Probably a bad idea.
    // Check if node innovation by id exists
    public boolean checkInnovation (Node n) {
        // Look for existing node id
        for ( NodeInv ni : nodes )
            if (ni.id == n.id)
                return true;

        // Innovation doesn't exist
        return false;
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
    /*
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
    */

    public String toString () {
        String str = "";

        // Print connection invs
        str += "\nConnection Innovations\n"
               + "----------------------\n\n";
        for ( ConnectionInv ci : connections )
            str += ci.id + "\n" + ci.c + "\n";

        // Print node invs
        str += "\nNode Innovations\n"
               + "----------------\n\n";
        for ( NodeInv ni : nodes )
            str += ni.id + "\n" + ni.c_in + "--> " + ni.c_out + "\n";

        return str;
    }

    // For debugging
    public int getNodeInvNum () {
        return nextNodeId;
    }
    public int getConnInvNum () {
        return nextConnId;
    }

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

    /************************************ */

    private ArrayList<ConnectionInv> connections;
    private ArrayList<NodeInv> nodes;

    private int nextConnId;
    private int nextNodeId;

    // Numbers before this belong to input/output nodes
    private int start_connId;
    private int start_nodeId;

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
        // Optionals allow floating nodes or nodes with one connection
        public NodeInv (int id, Optional<ConnectionGene> c1, Optional<ConnectionGene> c2) {
            this.c_in   = c1;
            this.c_out  = c2;
            this.id     = id;
        }

        public Optional<ConnectionGene> c_in;
        public Optional<ConnectionGene> c_out;
        final public int id; // Node id
    }

    /********************************/
}
