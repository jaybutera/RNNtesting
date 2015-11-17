import java.util.ArrayList;
import java.util.Random;

public class Genome {
    // These should be private
    public ArrayList<Node> input_nodes;           // Input neurons
    public ArrayList<Node> output_nodes;          // Output neurons
    public ArrayList<Node> hidden_nodes;          // Hidden neurons
    public ArrayList<Node> nodes;                 // All layers of nodes concatenated
    public ArrayList<ConnectionGene> connections; // Link genes between all layers (with respect to nodes)

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
            input_nodes.add(new Node(NodeType.INPUT, nodeNum()));
            nodes.add(new Node(NodeType.INPUT, nodeNum()));
        }

        // Initialize output neurons
        output_nodes = new ArrayList<Node>();
        for (int i = 0; i < outputs; i++) {
            output_nodes.add( new Node(NodeType.OUTPUT, nodeNum()) );
            nodes.add( new Node(NodeType.INPUT, nodeNum()) );
        }

        // Chance to make each possible link between input and output nodes
        // with probability .5
        for (int i = 0; i < inputs; i++)
            for (int o = 0; o < outputs; o++)
                if ( new Random().nextBoolean() )
                    addConnection(i, o);
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
        str += "\n\nnConnections:\n";
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
}
