import java.util.stream.Collectors;
import java.util.Arrays;

public class Network {
    public Network(Genome g) {
        // TODO: This entire constructor is so ugly and a terrible idea. Need to fix this.

        /*****************************/
        /* Initialize neuron vectors */
        /*****************************/

        /*
        inputNeurons = g.connections.stream()
                                    .filter(n -> n.in.type == NodeType.INPUT)
                                    .toArray(Double[]::new);
        */
        inputNeurons  = new Double[g.input_nodes.size()];
        outputNeurons = new Double[g.output_nodes.size()];
        hiddenNeurons = new Double[g.hiddenSize()];

        // Fill hidden and output neurons with hot vector 1
        Arrays.fill(hiddenNeurons, 1.0);
        Arrays.fill(outputNeurons, 1.0);



        /********************************/
        /* Initialize neuron id vectors */
        /********************************/

        // Temporary storage of ids of each type of neuron in genome
        // (used to create weight matrices)
        int[] input_neuron_ids  = new int[g.input_nodes.size()];
        int[] output_neuron_ids = new int[g.output_nodes.size()];
        int[] hidden_neuron_ids = new int[g.hiddenSize()];

        for (int i = 0; i < g.input_nodes.size(); i++)
            input_neuron_ids[i] = g.input_nodes.get(i).id;
        for (int i = 0; i < g.hidden_nodes.size(); i++)
            hidden_neuron_ids[i] = g.hidden_nodes.get(i).id;
        for (int i = 0; i < g.output_nodes.size(); i++)
            output_neuron_ids[i] = g.output_nodes.get(i).id;



        /******************************/
        /* Initialize weight matrices */
        /******************************/

        inputWeights  = new Double[g.hiddenSize()][g.input_nodes.size()];                       // N x K
        hiddenWeights = new Double[g.hiddenSize()][g.hiddenSize()];                             // N x N
        outputWeights = new Double[g.output_nodes.size()][g.input_nodes.size()+g.hiddenSize()]; // L x (N+K)

        // Input matrix
        for (int i = 0; i < hidden_neuron_ids.length; i++)
            for (int j = 0; j < input_neuron_ids.length; j++)
                inputWeights[i][j] = g.getWeight(input_neuron_ids[j],hidden_neuron_ids[i]);
        // Hidden matrix
        for (int i = 0; i < hidden_neuron_ids.length; i++)
            for (int j = 0; j < hidden_neuron_ids.length; j++)
                hiddenWeights[i][j] = g.getWeight(hidden_neuron_ids[j],hidden_neuron_ids[i]);
        // Output matrix
        int[] ih_neuron_ids = concat(input_neuron_ids, hidden_neuron_ids);
        for (int i = 0; i < output_neuron_ids.length; i++)
            for (int j = 0; j < (hidden_neuron_ids.length+input_neuron_ids.length); j++)
                outputWeights[i][j] = g.getWeight(ih_neuron_ids[j],output_neuron_ids[i]);

        System.out.println(inputWeights.length);
    }

    private Network (Double[][] inputWeights,
                     Double[][] hiddenWeights,
                     Double[][] outputWeights)
    {
        // Initialize neurons
        inputNeurons  = new Double[inputWeights[0].length]; // Kx1
        hiddenNeurons = new Double[hiddenWeights.length]; // Nx1
        outputNeurons = new Double[outputWeights.length]; // Lx1

        // Fil hidden and output neurons with hot vector 1
        Arrays.fill(hiddenNeurons, 1.0);
        Arrays.fill(outputNeurons, 1.0);

        // Initialize weights
        this.inputWeights  = new Double[inputWeights.length][inputWeights[0].length];   // NxK
        this.hiddenWeights = new Double[hiddenWeights.length][hiddenWeights.length];    // NxN
        this.outputWeights = new Double[outputWeights.length][outputWeights[0].length]; // Lx(K+N)
        //this.inptoOutWeights = new Double[outputWeights.length][inputWeights[0].length];  // LxK

        // Initialize input weights
        for (int i = 0; i < inputWeights.length; i++)
            for (int j = 0; j < inputWeights[0].length; j++)
                this.inputWeights[i][j] = inputWeights[i][j];
        // Initialize hidden weights
        for (int i = 0; i < hiddenWeights.length; i++)
            for (int j = 0; j < hiddenWeights[0].length; j++)
                this.hiddenWeights[i][j] = hiddenWeights[i][j];
        // Initialize output weights
        for (int i = 0; i < outputWeights.length; i++)
            for (int j = 0; j < outputWeights[0].length; j++)
                this.outputWeights[i][j] = outputWeights[i][j];
    }

    public Double[] step (Double[] inps) {
        // Temporary error handling
        if (inputNeurons.length != inps.length)
            return new Double[] {0.0};

        inputNeurons = Arrays.copyOf(inps, inps.length);

        hiddenNeurons = activate( addVecs(multWeightToVec(inputWeights, inputNeurons),
                                          multWeightToVec(hiddenWeights, hiddenNeurons)) );

        // Concatenate inputNeurons and hiddenNeurons
        outputNeurons = activate( multWeightToVec(outputWeights, concat(inputNeurons, hiddenNeurons)) );

        return outputNeurons;
    }

    // Network builder class
    public static class NetworkBuilder {
        private Double[][] inputs;
        private Double[][] hidden;
        private Double[][] outputs;

        public NetworkBuilder inputs (Double[][] inputs) {
            this.inputs = new Double[inputs.length][inputs[0].length];

            for (int i = 0; i < inputs.length; i++)
                for (int j = 0; j < inputs[0].length; j++)
                    this.inputs[i][j] = inputs[i][j];

            return this;
        }

        public NetworkBuilder outputs (Double[][] outputs) {
            this.outputs = new Double[outputs.length][outputs[0].length];

            for (int i = 0; i < outputs.length; i++)
                for (int j = 0; j < outputs[0].length; j++)
                    this.outputs[i][j] = outputs[i][j];

            return this;
        }

        public NetworkBuilder hidden (Double[][] hidden) {
            this.hidden = new Double[hidden.length][hidden[0].length];

            for (int i = 0; i < hidden.length; i++)
                for (int j = 0; j < hidden[0].length; j++)
                    this.hidden[i][j] = hidden[i][j];

            return this;
        }

        public Network createNetwork () {
            return new Network (inputs, hidden, outputs);
        }
    }

    public String toString () {
        /*
        String[][] inputWeights  = new Double[this.hiddenNeurons.length][this.inputNeurons.length];   // NxK
        String[][] hiddenWeights = new Double[this.hidden.length][this.hiddenNeurons.length];    // NxN
        String[][] outputWeights = new Double[this.outputWeights.length][this.outputWeights[0].length]; // Lx(K+N)
        */
        String mats = "";

        mats += "\nInput adjacency matrix\n\n";
        for (int i = 0; i < this.hiddenNeurons.length; i++) {
            for (int j = 0; j < this.inputNeurons.length; j++)
                mats += String.valueOf(inputWeights[i][j]) + " ";
            mats += "\n";
        }
        mats += "\nHidden adjacency matrix\n\n";
        for (int i = 0; i < this.hiddenNeurons.length; i++) {
            for (int j = 0; j < this.hiddenNeurons.length; j++)
                mats += String.valueOf(hiddenWeights[i][j]) + " ";
            mats += "\n";
        }
        mats += "\nOutput adjacency matrix\n\n";
        for (int i = 0; i < this.outputNeurons.length; i++) {
            for (int j = 0; j < this.hiddenNeurons.length+this.inputNeurons.length; j++)
                mats += String.valueOf(outputWeights[i][j]) + " ";
            mats += "\n";
        }

        return mats;
    }

    private Double[] multWeightToVec (Double[][] mat, Double[] vec) {
        Double sum;

        Double[] out_vec = new Double[mat.length];

        // Temporary error checker
        try {
        if (mat[0].length != vec.length) {
            Arrays.fill(out_vec, 0.0);
            return out_vec;
        }
        } catch (ArrayIndexOutOfBoundsException e) {
            //System.out.println("Warning: No hidden nodes in network");
            Arrays.fill(outputNeurons, 0.0);
            return out_vec;
        }

        for (int i = 0; i < mat.length; i++) {
            sum = 0.0;
            for (int j = 0; j < mat[0].length; j++)
                sum += mat[i][j] * vec[j];
            out_vec[i] = sum;
        }

        return out_vec;
    }

    private Double[] addVecs (Double[] x1, Double[] x2) {
        // Temporary error checker
        if (x1.length != x2.length)
            return new Double[] {0.0};

        Double[] vec = new Double[x1.length];

        for (int i = 0; i < x1.length; i++)
            vec[i] = x1[i] + x2[i];

        return vec;
    }

    private Double[] concat (Double[] x1, Double[] x2) {
        Double[] vec = new Double[x1.length + x2.length];

        int i = 0;
        for (; i < x1.length; i++)
            vec[i] = x1[i];
        for (int j = 0; j < x2.length; i++, j++)
            vec[i] = x2[j];

        return vec;
    }

    private int[] concat (int[] x1, int[] x2) {
        int[] vec = new int[x1.length + x2.length];

        int i = 0;
        for (; i < x1.length; i++)
            vec[i] = x1[i];
        for (int j = 0; j < x2.length; i++, j++)
            vec[i] = x2[j];

        return vec;
    }

    private Double[] activate (Double[] inp) {
        Double[] vec = new Double[inp.length];

        // Compute sigmoid
        for (int i = 0; i < inp.length; i++)
            vec[i] = 1 / (1 + Math.exp(-inp[i]));

        return vec;
    }

    private Double[] inputNeurons;
    private Double[] hiddenNeurons;
    private Double[] outputNeurons;

    private Double[][] inputWeights;
    private Double[][] hiddenWeights;
    private Double[][] outputWeights;
}
