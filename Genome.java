import java.util.ArrayList;
import java.util.Random;

public class Genome {
    // These should be private
    public ArrayList<Node> input_nodes;           // Input neurons
    public ArrayList<Node> output_nodes;          // Output neurons
    public ArrayList<Node> hidden_nodes;          // Hidden neurons
    public ArrayList<Node> nodes;                 // All layers of nodes concatenated
    public ArrayList<ConnectionGene> connections; // Link genes between all layers (with respect to nodes)

    // Default constructor
    public Genome () {
        // Initialize empty lists
        connections  = new ArrayList<ConnectionGene>();
        nodes        = new ArrayList<Node>();
        input_nodes  = new ArrayList<Node>();
        hidden_nodes = new ArrayList<Node>();
        output_nodes = new ArrayList<Node>();
    }

    public Genome (ArrayList<Node> inputs,
                   ArrayList<Node> hidden,
                   ArrayList<Node> outputs,
                   ArrayList<ConnectionGene> connections) {
        this.input_nodes  = inputs;
        this.hidden_nodes = hidden;
        this.output_nodes = outputs;

        // Aggregate of all nodes in genome
        this.nodes = inputs;
        nodes.addAll(hidden);
        nodes.addAll(outputs);

        this.connections  = connections;
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

        // Chance to make each possible link between input and output nodes
        // with probability .5
        for (int i = 0; i < inputs; i++)
            for (int o = 0; o < outputs; o++)
                if ( new Random().nextBoolean() )
                    addConnection(input_nodes.get(i).id, output_nodes.get(o).id);
    }

    // TODO: Do I need an overload with Node parameters?
    public void addConnection (int n1, int n2, double weight) {
        connections.add( new ConnectionGene(nodes.get(n1),
                                            nodes.get(n2),
                                            weight,
                                            innovationNum()) );
    }

    // Automatic random weight
    public void addConnection (int n1, int n2) {
        connections.add( new ConnectionGene(nodes.get(n1),
                                            nodes.get(n2),
                                            new Random().nextDouble(),
                                            innovationNum()) );
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

    public void addConnections(ArrayList<ConnectionGene> cs) {
        for (ConnectionGene c : cs)
            addConnection(c);
    }

    public void addNode (int n1, int n2) {
        /* * * * * */
        // Inputs  //
        /* * * * * */

        nodes.add( new Node(nodeNum()) );
        // Connect n1 to n
        addConnection(n1, nodes.indexOf(nodes.size()-1));
        // Connect n to n2
        addConnection(nodes.indexOf(nodes.size()-1), n2);
        // Disable connection from n1 to n2
        //connections.stream().filter(c -> c.in == nodes.get(n1) && c.out == nodes.get(n2)).map(
        for (int i = 0; i < connections.size(); i++)
            if (connections.get(i).in == nodes.get(n1) && connections.get(i).out == nodes.get(n2))
                connections.remove(connections.get(i));
    }

    public Double getWeight (int input_id, int output_id) {
        try {
            // Find the connection gene that holds given ids
            Double w = connections.stream()
                                  .filter(c -> c.in.id == input_id && c.out.id == output_id)
                                  .map(c -> c.weight)
                                  .findFirst()
                                  .get();
            return w;
        } catch (Exception e) {
            // If it doesn't exist, there is no connection
            return 0.0;
        }
    }

    public int hiddenSize () {
        return hidden_nodes.size();
    }

    public boolean contains (ConnectionGene c) {
        // Look for matching innovation number
        for ( ConnectionGene cg : connections )
            if (c.compareTo(cg) == 1)
                return true;
        return false;
    }

    public int numExcess (Genome g) {
        // Start at end of genome and look backward for matching gene
        int i = getSmallest(g).connections.size();
        int excess = 0;
        boolean done = false;

        while (!done) {
            if (this.connections.get(i) == g.connections.get(i))
                done = true;
            excess++;
            i--;
        }

        return excess;
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
        if (g.connections.size() < this.connections.size())
            return g;
        return this;
    }

}
