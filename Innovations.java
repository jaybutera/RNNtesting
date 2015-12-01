public class Innovations {
    public Innovations () {
        connections = new ArrayList<ConnectionInv>();
        nodes = new ArrayLIst<NodeInv>();

        nextConnId = 0;
        nextNodeId = 0;
    }

    // Add node innovation
    public boolean addInnovation (ConnectionGene c1, ConnectionGene c2) {
        // If innovation is novel, add to database
        if (checkInnovation(c1, c2) == -1)
            nodes.add( new NodeInv(NodeInvNum(), c1, NodeInvNum(), c2) );
        else
            return 0;
        return 1;
    }

    // Add connection innovation
    public boolean addInnovation (ConnectionGene c) {
        // If innovation is novel, add to database
        if (checkInnovation(c) == -1)
            connections.add( new NodeInv(NodeInvNum(), c1, NodeInvNum(), c2) );
        else
            return 0;
        return 1;
    }

    // Check for node innovation
    public int checkInnovation (ConnectionGene c_in, ConnectionGene c_out) {
        // Look for a node innovation with matching in and out node ids
        for ( NodeInv ni : nodes )
            if (c_in.in.id == ni.c_in.in.id && c_out.out.id == ni.c_out.out.id)
                return 
    }


    // Global innovation number incrementers
    /************************************ */
    private int NodeInvNum () {
        return nextNodeId++;
    }

    private int ConnInvNum () {
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
        public NodeInv (int id1, ConnectionGene c1, int id2, ConnectionGene c2) {
            this.c_in   = c1;
            this.c_out  = c2;
            this.id_in  = id1;
            this.id_out = id2;
        }

        final public ConnectionGene c_in;
        final public ConnectionGene c_out;
        /*
        final public int id_in;
        final public int id_out;
        */
    }

    /********************************/
}
