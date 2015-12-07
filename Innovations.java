import java.util.ArrayList;
import java.util.Optional;

public class Innovations {
    public Innovations () {
        connections = new ArrayList<ConnectionInv>();
        nodes = new ArrayList<NodeInv>();

        nextConnId = 0;
        nextNodeId = 0;
    }

    public Innovations (int start_connId, int start_nodeId) {
        connections = new ArrayList<ConnectionInv>();
        nodes = new ArrayList<NodeInv>();

        nextConnId = start_connId;
        nextNodeId = start_nodeId;
    }

    // Add node innovation
    public boolean addInnovation (Optional<ConnectionGene> c1, Optional<ConnectionGene> c2, Node n) {
        // TODO: Add support for one connection parameter provided

        // Check if node is input/output
        /*
        if (n.type == NodeType.INPUT || n.type == NodeType.OUTPUT) {
            //System.out.println("Found an inp/out node");
            int inv = checkInnovation(n);

            // Innovation is novel
            if (inv == -1) {
                //inv = nodeInvNum();

                // Add node to database
                nodes.add( new NodeInv(n.id, Optional.empty(), Optional.empty()) );

                // Assign genome node a new id
                //n.id = inv;

                return true;
            }
            // TODO: Add case for if it's not novel
            return false;
        }
        */

        if (
        // If both connections are provided
        if (c1.isPresent() && c2.isPresent()) {
            int inv_id = checkInnovation(c1.get(), c2.get(), n);
            System.out.println("Found a hidden node: " + inv_id);

            // If innovation is novel, add to database
            if ( inv_id == -1) {
                /*
                c1.get().innovation = connInvNum();
                c2.get().innovation = connInvNum();
                */

                // Add connections to database
                addInnovation(c1.get());
                addInnovation(c2.get());

                int inv = nodeInvNum();
                nodes.add( new NodeInv(inv, c1, c2) );
                // Assign genome node a new id
                n.id = inv;
            }
            else {
                // Assign connection ids to existing innovation ids
                NodeInv ni = getNodeInvById(inv_id);

                System.out.println(c1.get());
                System.out.println(c1.get());

                c1.get().innovation = ni.c_in.get().innovation;
                c2.get().innovation = ni.c_out.get().innovation;

                // Assign genome node the existing id
                n.id = inv_id;

                return false;
            }

            // New innovation
            return true;
        }

        // If no connections are provided (assume later support for 1)
        else {
            System.out.println("Found a hidden node");
            // Check for pre-foating nodes
            int inv = checkInnovation(n);

            // Innovation is novel
            if (inv == -1) {
                inv = nodeInvNum();

                // Add node to database
                nodes.add( new NodeInv(inv, Optional.empty(), Optional.empty()) );

                // Assign genome node a new id
                n.id = inv;
            }
            // Innovation exists
            else {
                n.id = inv;
            }

            // New innovation
            return true;
        }
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
            NodeInv ni = getNodeInvById(c.in.id);
            if (ni != null)
                ni.c_in = Optional.of(c);
            // Out node
            ni = getNodeInvById(c.out.id);
            if (ni != null)
                ni.c_out = Optional.of(c);

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

        // Check for pre-floating nodes
        /*
        int id = checkInnovation(n);
        if (id != -1)
            return id;
        */

        // Innovation doesn't exist
        return -1;
    }
    // Check for node innovation
    public int checkInnovation (Node n) {
        // Look for existing node id
        for ( NodeInv ni : nodes )
            if (ni.id == n.id)
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
