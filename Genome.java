import java.util.ArrayList;
import java.util.Random;
import java.util.stream.Collectors;

public class Genome {
    // These should be private
    public ArrayList<Node> input_nodes;           // Input neurons
    public ArrayList<Node> output_nodes;          // Output neurons
    public ArrayList<Node> hidden_nodes;          // Hidden neurons
    public ArrayList<Node> nodes;                 // All layers of nodes concatenated
    public ArrayList<ConnectionGene> connections; // Link genes between all layers (with respect to nodes)

    // Default constructor
    /*
    public Genome () {
        // Initialize empty lists
        connections  = new ArrayList<ConnectionGene>();
        nodes        = new ArrayList<Node>();
        input_nodes  = new ArrayList<Node>();
        hidden_nodes = new ArrayList<Node>();
        output_nodes = new ArrayList<Node>();
    }
    */

    public Genome (ArrayList<Node> inputs,
                   ArrayList<Node> hidden,
                   ArrayList<Node> outputs,
                   ArrayList<ConnectionGene> connections) {
        this.input_nodes  = inputs;
        this.hidden_nodes = hidden;
        this.output_nodes = outputs;
        this.connections  = connections;

        // Aggregate of all nodes in genome
        this.nodes = inputs;
        nodes.addAll(hidden);
        nodes.addAll(outputs);
    }

    // Randomly generate minimal genome (perceptron structure)
    public Genome (int inputs, int outputs) {
        // Initialize empty lists
        connections  = new ArrayList<ConnectionGene>();
        nodes        = new ArrayList<Node>();
        hidden_nodes = new ArrayList<Node>();

        // Initialize input neurons
        input_nodes  = new ArrayList<Node>();
        for (int i = 0; i < inputs; i++) {
            Node n = new Node(NodeType.INPUT, nodeNum());
            input_nodes.add(n);
            nodes.add(n);
        }

        // Initialize output neurons
        output_nodes = new ArrayList<Node>();
        for (int i = 0; i < outputs; i++) {
            Node n = new Node(NodeType.OUTPUT, nodeNum());
            output_nodes.add(n);
            nodes.add(n);
        }

        Random r = new Random();

        // Make at least one connection
        addConnection(input_nodes.get(r.nextInt(inputs)).id, output_nodes.get(r.nextInt(outputs)).id);

        // Chance to make each possible link between input and output nodes
        // with probability .5
        /*
        for (int i = 0; i < inputs; i++)
            for (int o = 0; o < outputs; o++)
                if ( new Random().nextBoolean() )
                    addConnection(input_nodes.get(i).id, output_nodes.get(o).id);
                    */
        int links = new Random().nextInt(inputs*outputs-1);
        for (int i = 0; i < links; i++)
            addConnection();
    }

    // Manually defined weight
    public void addConnection (Node n1, Node n2, double weight) {
        connections.add( new ConnectionGene(n1,
                                            n2,
                                            weight,
                                            innovationNum()) );
    }
    public void addConnection (int n1, int n2, double weight) {
        connections.add( new ConnectionGene(getNodeById(n1),
                                            getNodeById(n2),
                                            weight,
                                            innovationNum()) );
    }

    // Automatic random weight
    public ConnectionGene addConnection (Node n1, Node n2, Innovations inv_db) {
        double weight = new Random().nextDouble();

        ConnectionGene cg = new ConnectionGene(n1,
                                               n2,
                                               weight,
                                               0 ); // Innovation is modified by Innovations (inv_db)
        inv_db.addInnovation(cg);
        connections.add(cg);

        return cg;
    }
    public void addConnection (int n1, int n2) {
        connections.add( new ConnectionGene(getNodeById(n1),
                                            getNodeById(n2),
                                            new Random().nextDouble(),
                                            innovationNum()) );
    }

    // Automatic random weight at random point
    public void addConnection () {
        Random r = new Random();
        Node n1;
        Node n2;

        if ( !hidden_nodes.isEmpty() ) {
            // Prob of first node depends on size of input layer
            if (r.nextInt() < input_nodes.size() / nodes.size()) {
                if (input_nodes.size() == 1)
                    n1 = input_nodes.get(0);
                else
                    n1 = input_nodes.get(r.nextInt(input_nodes.size()-1));
            }
            else {
                if (hidden_nodes.size() == 1)
                    n1 = hidden_nodes.get(0);
                else
                    n1 = hidden_nodes.get(r.nextInt(hidden_nodes.size()-1));
            }
        }
        else { // If there are no hidden nodes, default input
            if (input_nodes.size() == 1)
                n1 = input_nodes.get(0);
            else
                n1 = input_nodes.get(r.nextInt(input_nodes.size()-1));
        }


        if ( !hidden_nodes.isEmpty() ) {
            // Prob of second node depends on size of hidden layer
            if (r.nextInt() < hidden_nodes.size() / nodes.size()) {
                if (hidden_nodes.size() == 1)
                    n2 = hidden_nodes.get(0);
                else
                    n2 = hidden_nodes.get(r.nextInt(hidden_nodes.size()-1));
            }
            else {
                if (output_nodes.size() == 1)
                    n2 = output_nodes.get(0);
                else
                    n2 = output_nodes.get(r.nextInt(output_nodes.size()-1));
            }
        }
        else {
            if (output_nodes.size() == 1)
                n2 = output_nodes.get(0);
            else
                n2 = output_nodes.get(r.nextInt(output_nodes.size()));
        }

        ConnectionGene cg = new ConnectionGene(n1,
                                               n2,
                                               new Random().nextDouble(),
                                               innovationNum());

        // Need to change to try again as long as it is still possible to make
        // a new connection (not fully connected)
        if (!this.contains(cg))
            connections.add(cg);
        /*
        connections.add( new ConnectionGene(n1,
                                            n2,
                                            new Random().nextDouble(),
                                            innovationNum()) );
        */
    }

    public void addConnection (ConnectionGene c) {
        // Add nodes if they don't exist
        if (!nodes.contains(c.in))
            nodes.add(c.in);
        if (!nodes.contains(c.out))
            nodes.add(c.out);

        // Add input node to organized node lists
        switch (c.in.type) {
            case INPUT:
                input_nodes.add(c.in);
                break;
            case HIDDEN:
                hidden_nodes.add(c.in);
                break;
            case OUTPUT:
                output_nodes.add(c.in);
                break;
        }

        // Add output node to organized node lists
        switch (c.out.type) {
            case INPUT:
                input_nodes.add(c.out);
                break;
            case HIDDEN:
                hidden_nodes.add(c.out);
                break;
            case OUTPUT:
                output_nodes.add(c.out);
                break;
        }

        // Add connection
        connections.add(c);
    }

    public void addConnections (ArrayList<ConnectionGene> cs) {
        for (ConnectionGene c : cs)
            addConnection(c);
    }

    // Add node given two node ids
    public void addNode (int n1, int n2, Innovations inv_db) {
        /* * * * * */
        // Inputs  //
        /* * * * * */

        Node n = new Node( nodeNum() );
        nodes.add(n);
        hidden_nodes.add(n);

        // Connect n1 to n
        addConnection(getNodeById(n1), n, inv_db);
        // Connect n to n2
        addConnection(n, getNodeById(n2), inv_db);
        // Disable connection from n1 to n2
        for (int i = 0; i < connections.size(); i++)
            if (connections.get(i).in == getNodeById(n1) && connections.get(i).out == getNodeById(n2))
                connections.remove(connections.get(i));
    }
    // Add node given two nodes
    public Node addNode (Node n1, Node n2, Innovations inv_db) {
        /* * * * * */
        // Inputs  //
        /* * * * * */

        Node n = new Node(0);

        // Connect n1 to n
        ConnectionGene c1 = addConnection(n1, n, inv_db);
        // Connect n to n2
        ConnectionGene c2 = addConnection(n, n2, inv_db);
        // Disable connection from n1 to n2
        for (int i = 0; i < connections.size(); i++)
            if (connections.get(i).in == n1 && connections.get(i).out == n2)
                connections.remove(connections.get(i));

        // Add to innovation database and update node id
        inv_db.addInnovation(c1, c2);
        // Add to local genome database
        nodes.add(n);
        hidden_nodes.add(n);

        return n;
    }

    public void addNode (Innovations inv_db) {
        /* * * * * */
        // Inputs  //
        /* * * * * */

        // Choose a random connection to augment
        ConnectionGene cg = connections.get(new Random().nextInt(connections.size()-1));

        addNode(cg.in, cg.out, inv_db);
    }

    public Double getWeight (int input_id, int output_id) {
        try {
            // Find the connection gene that holds given ids
            Double w = connections.stream()
                                  .filter(c -> c.in.id == input_id && c.out.id == output_id)
                                  .map(c -> c.weight)
                                  .findFirst()
                                  .get();
            //System.out.println("Found weight from node [" + input_id + "] to [" + output_id + "] - " + w);
            return w;
        } catch (Exception e) {
            // If it doesn't exist, there is no connection
            //System.out.println("Couldn't find weight from node [" + input_id + "] to [" + output_id + "]");
            return 0.0;
        }
    }

    public double weightDiff (Genome g) {
        ArrayList<ConnectionGene> m = this.getMatching(g);
        // Debugging
        if (m.size() == 0) {
            /*
            System.out.println("No matching genes between genomes:");
            System.out.println(g);
            System.out.println(this);
            */

            return 0.0;
        }

        double avg = 0.0;

        for ( ConnectionGene c : m )
            avg += c.weight;

        return avg / m.size();
    }

    public int hiddenSize () {
        return hidden_nodes.size();
    }

    public boolean contains (ConnectionGene c) {
        // Look for matching innovation number
        for ( ConnectionGene cg : connections )
            if (cg.in == c.in && cg.out == c.out) {
                //System.out.println("Matching gene found!");
                return true;
            }
        return false;
    }
    /*
    public boolean contains (ConnectionGene c) {
        // Look for matching innovation number
        for ( ConnectionGene cg : connections )
            if (c.compareTo(cg) == 1)
                return true;
        return false;
    }
    */

    public ArrayList<ConnectionGene> getExcess (Genome g) {
        // Start at end of genome and look backward for matching gene
        Genome small = getSmallest(g);
        // Get last gene's id
        int max_inv = small.connections.get(small.connections.size()-1).innovation;
        //int max_inv = small.connections.get(0).innovation;

        if (small != this)
            return this.connections.stream().filter(s -> s.innovation > max_inv).collect(Collectors.toCollection(ArrayList::new));
        return g.connections.stream().filter(s -> s.innovation > max_inv).collect(Collectors.toCollection(ArrayList::new));
    }

    public ArrayList<ConnectionGene> getDisjoint (Genome g) {
        Genome small = getSmallest(g);
        int max_inv = small.connections.get(small.connections.size()-1).innovation;
        ArrayList<ConnectionGene> disjoint = new ArrayList<ConnectionGene>();

        if (small != this) {
            disjoint = small.connections.stream()
                                        .filter(s -> !this.contains(s))
                                        .collect(Collectors.toCollection(ArrayList::new));
            disjoint.addAll(this.connections.stream()
                                            .filter(s -> !this.contains(s) && s.innovation < max_inv)
                                            .collect(Collectors.toCollection(ArrayList::new)));
        }
        else if (small != g) {
            disjoint = small.connections.stream()
                                        .filter(s -> !g.contains(s))
                                        .collect(Collectors.toCollection(ArrayList::new));
            disjoint.addAll(g.connections.stream()
                                            .filter(s -> !g.contains(s) && s.innovation < max_inv)
                                            .collect(Collectors.toCollection(ArrayList::new)));
        }

        return disjoint;
    }

    public ArrayList<ConnectionGene> getMatching (Genome g) {
        // Find all matching innovation numbers
        // TODO: Optimize by iterating through the smallest genome
        /*
        return this.connections.stream()
                               .filter(c -> g.contains(c))
                               .collect(Collectors.toCollection(ArrayList::new));
                               */
        ArrayList<ConnectionGene> matching = new ArrayList<ConnectionGene>();
        for ( ConnectionGene c : this.connections )
            if ( g.contains(c) )
                matching.add(c);

        return matching;
    }

    public ConnectionGene getConnection (Node in, Node out) {
        for ( ConnectionGene cg : connections )
            if (cg.in.id == in.id && cg.out.id == out.id)
                return cg;
        return null;
    }

    // Display phenotype of genome
    public String toString() {
        String str = "";

        str += "Genome\n-------\n";
        str += "Input Node IDs:\n";
        for (Node n : input_nodes)
            str += n.id + " ";
        str += "\nHidden Node IDs:\n";
        for (Node n : hidden_nodes)
            str += n.id + " ";
        str += "\nOutput Node IDs:\n";
        for (Node n : output_nodes)
            str += n.id + " ";
        str += "\n\nConnections:\n\n";
        for (ConnectionGene g : connections)
            str += g + "\n";

        return str;
    }

    public double fitness;

    /*** Innovation number ***/
    private int innovationNum () {
        return inv_num++;
    }
    private int inv_num = 0;
    /*************************/

    /*** Node number ***/
    private int nodeNum () {
        return node_num++;
    }
    private int node_num = 0;
    /*******************/

    private Genome getSmallest (Genome g) {
        //System.out.println(g.connections.size() + "\n");
        //System.out.println(this.connections.size());
        if (g.connections.size() < this.connections.size())
            return g;
        return this;
    }

    private Node getNodeById(int id) {
        for ( Node n : nodes )
            if (n.id == id)
                return n;
        return null;
    }
}
