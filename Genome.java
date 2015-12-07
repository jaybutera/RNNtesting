import java.util.ArrayList;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.Optional;
import java.util.Comparator;

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
    public Genome (int inputs, int outputs, Innovations inv_db) {
        // Initialize empty lists
        connections  = new ArrayList<ConnectionGene>();
        nodes        = new ArrayList<Node>();
        hidden_nodes = new ArrayList<Node>();

        this.inv_db = inv_db;

        int inv_id = 0;

        // Initialize input neurons
        input_nodes  = new ArrayList<Node>();
        for (int i = 0; i < inputs; i++) {
            Node n = new Node(NodeType.INPUT, inv_id++);
            addNode(n, true);
        }

        // Initialize output neurons
        output_nodes = new ArrayList<Node>();
        for (int i = 0; i < outputs; i++) {
            Node n = new Node(NodeType.OUTPUT, inv_id++);
            addNode(n, true);
        }

        Random r = new Random();

        // Make at least one connection
        addConnection(input_nodes.get(r.nextInt(inputs)), output_nodes.get(r.nextInt(outputs)));

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

    // Copy constructor
    public Genome (Genome g) {
        connections  = new ArrayList<ConnectionGene>();
        nodes        = new ArrayList<Node>();
        hidden_nodes = new ArrayList<Node>();
        input_nodes  = new ArrayList<Node>();
        output_nodes = new ArrayList<Node>();

        for ( ConnectionGene cg : g.connections )
            connections.add(cg);
        for ( Node n : nodes )
            nodes.add(n);
        for ( Node n : g.hidden_nodes )
            hidden_nodes.add(n);
        for ( Node n : g.input_nodes )
            input_nodes.add(n);
        for ( Node n : g.output_nodes )
            output_nodes.add(n);

        this.inv_db = g.inv_db;
    }

    // TODO: Temporary method until I find a better way to handle crossovers
    public void flush () {
        connections.clear();
    }

    // Manually defined weight
    public ConnectionGene addConnection (Node n1, Node n2, double weight) {
        ConnectionGene cg = new ConnectionGene(n1,
                                               n2,
                                               weight,
                                               0);

        // If this is a new innovation, add connection
        if (inv_db.addInnovation(cg))
            connections.add(cg);

        // If it isn't new, add connection if it doesn't already exist in genome
        else if (!this.contains(cg))
            connections.add(cg);

        return cg;
    }
    /*
    public void addConnection (int n1, int n2, double weight) {
        connections.add( new ConnectionGene(getNodeById(n1),
                                            getNodeById(n2),
                                            weight,
                                            innovationNum()) );
    }
    */

    // Automatic random weight
    public ConnectionGene addConnection (Node n1, Node n2) {
        double weight = new Random().nextDouble();

        return addConnection (n1, n2, weight);
    }
    /*
    public void addConnection (int n1, int n2) {
        connections.add( new ConnectionGene(getNodeById(n1),
                                            getNodeById(n2),
                                            new Random().nextDouble(),
                                            innovationNum()) );
    }
    */

    // Automatic random weight at random point
    public ConnectionGene addConnection () {
        Random r = new Random();
        Node n1;
        Node n2;

        if ( !hidden_nodes.isEmpty() ) {
            // Prob of first node depends on size of input layer
            if (r.nextDouble() < input_nodes.size() / nodes.size()) {
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


        // TODO: Merge these if statements ^v
        if ( !hidden_nodes.isEmpty() ) {
            // Prob of second node depends on size of hidden layer
            if (r.nextDouble() < hidden_nodes.size() / nodes.size()) {
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
        // Otherwise connect to output
        else {
            if (output_nodes.size() == 1)
                n2 = output_nodes.get(0);
            else
                n2 = output_nodes.get(r.nextInt(output_nodes.size()));
        }

        return addConnection(n1, n2);
    }

    public void addConnection (ConnectionGene c, boolean db_enbl) {
        if (db_enbl) {
            addNode(c.in, db_enbl);
            addNode(c.out, db_enbl);
            addConnection(c.in, c.out);
        }
        else {
            connections.add(c);

            if (!nodes.contains(c.in))
                addNode(c.in, db_enbl);
            if (!nodes.contains(c.out))
                addNode(c.out, db_enbl);
        }
    }

    public void addConnections (ArrayList<ConnectionGene> cs, boolean db_enbl) {
        for (ConnectionGene c : cs)
            addConnection(c, db_enbl);
    }

    // Add node given two node ids
    /*
    public void addNode (int n1, int n2) {
        /* * * * * */
        // Inputs  //
        /* * * * * */

/*
        Node n = new Node( nodeNum() );
        nodes.add(n);
        hidden_nodes.add(n);

        // Connect n1 to n
        addConnection(getNodeById(n1), n);
        // Connect n to n2
        addConnection(n, getNodeById(n2));
        // Disable connection from n1 to n2
        for (int i = 0; i < connections.size(); i++)
            if (connections.get(i).in == getNodeById(n1) && connections.get(i).out == getNodeById(n2))
                connections.remove(connections.get(i));
    }
    */
    // Add node given two nodes
    public Node addNode (Node n1, Node n2) {
        Node n = new Node(0);

        // Connect n1 to n
        ConnectionGene c1 = addConnection(n1, n);
        // Connect n to n2
        ConnectionGene c2 = addConnection(n, n2);

        // If this is a new innovation, finish augmentation process
        if (inv_db.addInnovation(Optional.of(c1), Optional.of(c2), n)) {
            // Add to local genome database
            nodes.add(n);
            hidden_nodes.add(n);

            // Disable connection from n1 to n2
            connections.remove(getConnection(n1, n2));
            for (int i = 0; i < connections.size(); i++)
                if (connections.get(i).in == n1 && connections.get(i).out == n2)
                    connections.remove(connections.get(i));
        }
        // If it's not a new innovation, finish process if it doesn't already
        // exist in genome
        else {
            if ( getNodeById(n.id) == null ) {
                nodes.add(n);
                hidden_nodes.add(n);

                // Disable connection from n1 to n2
                connections.remove(getConnection(n1, n2));
                for (int i = 0; i < connections.size(); i++)
                    if (connections.get(i).in == n1 && connections.get(i).out == n2)
                        connections.remove(connections.get(i));
            }
        }

        return n;
    }

    public void addNode () {
        // Choose a random connection to augment
        ConnectionGene cg = connections.get(new Random().nextInt(connections.size()-1));

        addNode(cg.in, cg.out);
    }

    public void addNode (Node n, boolean db_enbl) {
        if (db_enbl) {
            if (inv_db.addInnovation(Optional.empty(), Optional.empty(), n)) {
                nodes.add(n);
                //System.out.println("New node: " + n.id);

                switch (n.type) {
                    case INPUT:
                        input_nodes.add(n);
                        break;
                    case HIDDEN:
                        hidden_nodes.add(n);
                        break;
                    case OUTPUT:
                        output_nodes.add(n);
                        break;
                }
            }

            else {
                nodes.add(n);

                switch (n.type) {
                    case INPUT:
                        input_nodes.add(n);
                        break;
                    case HIDDEN:
                        hidden_nodes.add(n);
                        break;
                    case OUTPUT:
                        output_nodes.add(n);
                        break;
                }
            }
        }
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

    public int inputSize () {
        return input_nodes.size();
    }

    public int outputSize () {
        return output_nodes.size();
    }

    public boolean contains (ConnectionGene c) {
        // Look for matching innovation number
        for ( ConnectionGene cg : connections )
            if (cg.in.id == c.in.id && cg.out.id == c.out.id) {
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

    // Returns any excess genes from THIS genome
    public ArrayList<ConnectionGene> getExcess (Genome g) {
        /*
        Genome small = getSmallest(g);

        // Get Largest gene id for both genomes
        Integer this_max_inv = small.connections.stream().map(s -> Integer.valueOf(s.innovation)).max(Comparator.naturalOrder()).get();
        Integer g_max_inv    = g.connections.stream().map(s -> Integer.valueOf(s.innovation)).max(Comparator.naturalOrder()).get();

        if (g_max_inv < this_max_inv)
            return this.connections.stream().filter(s -> s.innovation > g_max_inv).collect(Collectors.toCollection(ArrayList::new));
        */

        // Get largest innovation number in genome
        Integer this_max_inv = g.connections.stream().map(s -> Integer.valueOf(s.innovation)).max(Comparator.naturalOrder()).get();

        return this.connections.stream().filter(s -> s.innovation > this_max_inv).collect(Collectors.toCollection(ArrayList::new));
    }

    // Returns any disjoint genes from THIS genome
    public ArrayList<ConnectionGene> getDisjoint (Genome g) {
        // TODO : Could be optimized. Should store genes pools in sets for easy
        // disjoint evaluation

        /*
        Genome small = getSmallest(g);
        Integer max_inv = small.connections.stream().map(s -> Integer.valueOf(s.innovation)).max(Comparator.naturalOrder()).get();
        ArrayList<ConnectionGene> disjoint = new ArrayList<ConnectionGene>();

        if (small != this) {
            disjoint = small.connections.stream()
                                        .filter(s -> !this.contains(s))
                                        .collect(Collectors.toCollection(ArrayList::new));
            disjoint.addAll(this.connections.stream()
                                            .filter(s -> !small.contains(s) && s.innovation < max_inv)
                                            .collect(Collectors.toCollection(ArrayList::new)));
        }
        else if (small != g) {
            disjoint = small.connections.stream()
                                        .filter(s -> !g.contains(s))
                                        .collect(Collectors.toCollection(ArrayList::new));
            disjoint.addAll(g.connections.stream()
                                            .filter(s -> !small.contains(s) && s.innovation < max_inv)
                                            .collect(Collectors.toCollection(ArrayList::new)));
        }
        */

        return this.connections.stream()
                               .filter(s -> !g.contains(s))
                               .collect(Collectors.toCollection(ArrayList::new));
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

    public int size() {
        return nodes.size();
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

    private Innovations inv_db;

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
