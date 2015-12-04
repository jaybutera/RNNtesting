import java.util.ArrayList;
import java.util.Random;

public class Population {
    public Population (int size,
                       double dis_rate,
                       double inter_rate,
                       double node_rate,
                       double link_rate,
                       double compat_thresh,
                       int inputs,
                       int outputs,
                       Fitness f) {
        // Initialize population
        pop = new ArrayList<Genome>();
        this.inv_db = new Innovations();

        for (int i = 0; i < size; i++)
            pop.add( new Genome(inputs, outputs) );

        species = new ArrayList<Species>();
        gen_mutations = new ArrayList<ConnectionGene>();
        this.f = f;
        this.dis_rate   = dis_rate;
        this.inter_rate = inter_rate;
        this.node_rate  = node_rate;
        this.link_rate  = link_rate;
        this.compatThresh= compat_thresh;

        this.inputs  = inputs;
        this.outputs = outputs;

        // Speciate all genomes in population
        for ( Genome g : pop ) {
            g.fitness = f.simulate( new Network(g) );
            speciate(g);
        }
    }

    private Population (ArrayList<Genome> pop,
                        double dis_rate,
                        double inter_rate,
                        double node_rate,
                        double link_rate,
                        double compat_thresh,
                        Innovations inv_db,
                        Fitness f) {
        this.pop = pop;
        this.inv_db = inv_db;
        species = new ArrayList<Species>();
        gen_mutations = new ArrayList<ConnectionGene>();
        this.f = f;
        this.dis_rate   = dis_rate;
        this.inter_rate = inter_rate;
        this.node_rate  = node_rate;
        this.link_rate  = link_rate;
        this.compatThresh= compat_thresh;

        // Speciate all genomes in population
        //System.out.println("Calculating fitness and speciating...");
        //for ( Genome g : pop ) {
        for (int i = 0; i < pop.size(); i++) {
            Genome g = pop.get(i);
            if (g.size() > 100)
                pop.remove(g);
            //System.out.print("|" + g.size());
            g.fitness = f.simulate( new Network(g) );
            speciate(g);
        }
        //System.out.println("");

        //System.out.println("Top organism:\n" + getMostFit());
    }

    public void addGenome (Genome g) {
        pop.add(g);
        speciate(g);
    }

    public Population nextGen () {
        Genome parent1 = getMostFit();

        int temp_ind = pop.indexOf(parent1);
        Genome temp = pop.remove(temp_ind);

        Genome parent2 = getMostFit();

        // Return first genome to population
        pop.add(temp_ind, temp);

        Genome child = crossover(parent1, parent2);

        /*
        ArrayList<Genome> temp_pop = pop;
        pop.clear();
        */

        // Initialize new population
        //ArrayList<Genome> new_pop = new ArrayList<Genome>();

        //for (int i = 0; i < 100; i++)
            //pop.add( new Genome(inputs, outputs) );
        //***

        for ( Genome g : pop ) {
            //System.out.println("Mutating next genome");
            mutate(g);
        }

        return new Population(pop,
                              dis_rate,
                              inter_rate,
                              node_rate,
                              link_rate,
                              compatThresh,
                              inv_db,
                              f);
    }

    public void speciate (Genome g) {
        double g_compat;
        int i = 0;

        if (species.isEmpty())
            species.add( new Species(g) );

        // Search for an appropriate species
        do {
            g_compat = species.get(i).compatibility(g);
            i++;
        } while (g_compat > compatThresh && i < species.size());

        if (g_compat > compatThresh)
            System.out.println("New species : " + g_compat);
        // Add genome to threshold matched species
        if (g_compat < compatThresh)
            species.get(i-1).add(g);
        // If no match exists, create a new species
        else
            species.add( new Species(g) );
    }

    /***************/
    /*   PRIVATE   */
    /***************/

    private Genome crossover (Genome g1, Genome g2) {
        Genome child = new Genome(inputs, outputs);

        // Assign all matching connection genes
        ArrayList<ConnectionGene> matching = g1.getMatching(g2);
        child.addConnections(matching);

        // If parents have equal fitness, randomly match excess genes
        if (g1.fitness == g2.fitness) {
            // TODO: Could be more efficient
            // Derive all unmatching genes (excess and disjoint)
            ArrayList<ConnectionGene> excess_g1 = new ArrayList<ConnectionGene>(g1.connections);
            excess_g1.removeAll(matching);
            ArrayList<ConnectionGene> excess_g2 = new ArrayList<ConnectionGene>(g2.connections);
            excess_g2.removeAll(matching);

            Random r = new Random();

            // Randomly assign excess genes to child
            for ( ConnectionGene c : excess_g1 )
                if ( r.nextBoolean() )
                    child.addConnection(c);
            for ( ConnectionGene c : excess_g2 )
                if ( r.nextBoolean() )
                    child.addConnection(c);
        }

        // Otherwise child inherits excess genes of most fit parent
        else if (g1.fitness > g2.fitness) {
            ArrayList<ConnectionGene> excess_g1 = new ArrayList<ConnectionGene>(g1.connections);
            excess_g1.removeAll(matching);

            child.addConnections(excess_g1);
        }
        else if (g1.fitness < g2.fitness) {
            ArrayList<ConnectionGene> excess_g2 = new ArrayList<ConnectionGene>(g2.connections);
            excess_g2.removeAll(matching);

            child.addConnections(excess_g2);
        }

        return child;
    }

    public Genome getMostFit () {
        Genome top = pop.get(0);

        for ( Genome g : pop ) {
            if (g.fitness < top.fitness)
                top = g;
        }

        return top;
    }

    public int getNumSpecies () {
        return species.size();
    }

    // Get genome in population by index
    public Genome getGenome (int i) {
        return pop.get(i);
    }

    public void mutate(Genome g) {
        Random r = new Random();

        double weight_val_rate = .70;
        //System.out.println("Mutating...");

        //System.out.println("Starting perturbation...");
        // Mutations for input to hidden connections
        perturbLinks(g.input_nodes, g.hidden_nodes, g);
        //System.out.println("1");

        // Mutations for hidden to hidden connections
        perturbLinks(g.hidden_nodes, g.hidden_nodes, g);
        //System.out.println("2");

        // Mutations for hidden to output connections
        perturbLinks(g.hidden_nodes, g.output_nodes, g);
        //System.out.println("3");

        // Mutations for input to output connections
        perturbLinks(g.input_nodes, g.output_nodes, g);
        //System.out.println("4");

        //System.out.println("Perturbation done!");

        // Mutate existing connections
        for ( ConnectionGene cg : g.connections ) {
            // Chance to change weight
            if ( r.nextDouble() < weight_val_rate )
                cg.weight = r.nextDouble();

            // Chance to enable or disable (flip) connection gene
            if ( r.nextDouble() < dis_rate )
                cg.enabled = !cg.enabled;
        }
    }

    private void perturbLinks (ArrayList<Node> input_layer,
                                  ArrayList<Node> output_layer,
                                  Genome g) {
        double weight_rate  = .20;

        Random r = new Random();

        Node inp;
        Node out;

        /*
        for ( Node inp : input_layer ) {
            for ( Node out : output_layer ) {
                */
        /*
        System.out.println("inp size: " + input_layer.size());
        System.out.println("out size: " + output_layer.size());
        System.out.println("");
        */

        // Predefinition avoids run away size changes in for loops
        int inp_size = input_layer.size();
        int out_size = output_layer.size();

        for (int i = 0; i < inp_size; i++) {
            inp = input_layer.get(i);

            for (int j = 0; j < out_size; j++) {
                out = output_layer.get(j);

                if ( r.nextDouble() < weight_rate ) {
                    // Chance to add a connection
                    if ( r.nextDouble() < link_rate ) {
                        //System.out.println("Call addConnection!");
                        g.addConnection(inp, out, inv_db);
                    }
                    else if ( r.nextDouble() < node_rate ) {
                        //System.out.println("Call addNode!");
                        g.addNode(inp, out, inv_db);
                    }
                }
            }
        }
    }

    // Mutation parameters
    private double dis_rate;
    private double inter_rate;
    private double node_rate;
    private double link_rate;
    private double compatThresh;

    // Number of interface nodes in NN
    private int inputs;
    private int outputs;

    private ArrayList<Genome> pop;
    private ArrayList<Species> species;
    private ArrayList<ConnectionGene> gen_mutations;
    private Fitness f;
    private Innovations inv_db;
}
