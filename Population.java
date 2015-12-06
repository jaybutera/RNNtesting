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
        pop = new ArrayList<>();
        this.inv_db = new Innovations();
        species = new ArrayList<Species>();

        this.f = f;
        this.dis_rate   = dis_rate;
        this.inter_rate = inter_rate;
        this.node_rate  = node_rate;
        this.link_rate  = link_rate;
        this.compatThresh= compat_thresh;

        this.inputs  = inputs;
        this.outputs = outputs;

        // Create an initial species for all genomes of first generation to reproduce in
        Genome g = new Genome(inputs, outputs, inv_db);
        pop.add( new Genome(inputs, outputs, inv_db) );
        g.fitness = f.simulate( new Network(g) );
        species.add( new Species(g, dis_rate, link_rate, node_rate, f, inv_db) );

        // Speciate all genomes in population
        for (int i = 1; i < size; i++) {
            pop.add( new Genome(inputs, outputs, inv_db) );
            g = pop.get(i);
            g.fitness = f.simulate( new Network(g) );
            species.get(0).add(g);
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
        //for ( Genome g : pop )
        for (int i = 0; i < pop.size(); i++) {
            Genome g = pop.get(i);
            //System.out.print("|" + g.size());
            g.fitness = f.simulate( new Network(g) );
            speciate(g);
        }
    }

    public void addGenome (Genome g) {
        pop.add(g);
        speciate(g);
    }

    public Population nextGen () {
        // Reset population
        pop.clear();

        // Accumulate genomes from species reproduction
        for ( Species s : species )
            pop.addAll( s.reproduce() );

        /*
        System.out.println("Node innovation num: " + inv_db.getNodeInvNum());
        System.out.println("Conn innovation num: " + inv_db.getConnInvNum());
        */

        /*
        for ( Genome g : pop ) {
            //System.out.println("Mutating next genome");
            mutate(g);
        }
        */

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
        if (species.isEmpty())
            species.add( new Species(g, dis_rate, link_rate, node_rate, f, inv_db) );

        else {
            double g_compat;
            int i = 0;

            // Search for an appropriate species
            do {
                g_compat = species.get(i).compatibility(g);
                i++;
            } while (g_compat > compatThresh && i < species.size());

            // Add genome to threshold matched species
            if (g_compat < compatThresh) {
                //System.out.println("Added to a species : " + g_compat);
                species.get(i-1).add(g);
            }
            // If no match exists, create a new species
            else {
                //System.out.println("New species : " + g_compat);
                //System.out.println("G1: \n" + g + "\nG2: " + species.get(i-1).getRep());
                species.add( new Species(g, dis_rate, link_rate, node_rate, f, inv_db) );
            }
        }
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

    // Temporary method for debugging
    public void printInvDB () {
        System.out.println(inv_db);
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
                cg = cg.flipGene();
        }
    }

    /***************/
    /*   PRIVATE   */
    /***************/

    private void perturbLinks (ArrayList<Node> input_layer,
                                  ArrayList<Node> output_layer,
                                  Genome g) {
        double weight_rate = .20;

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

        int anNum = 0;
        int acNum = 0;

        for (int i = 0; i < inp_size; i++) {
            inp = input_layer.get(i);

            for (int j = 0; j < out_size; j++) {
                out = output_layer.get(j);

                if ( r.nextDouble() < weight_rate ) {
                    // Chance to add a connection
                    if ( r.nextDouble() < link_rate ) {
                        //System.out.println("Call addConnection!");
                        acNum++;
                        g.addConnection(inp, out);
                    }
                    else if ( r.nextDouble() < node_rate ) {
                        //System.out.println("Call addNode!");
                        anNum++;
                        g.addNode(inp, out);
                    }
                }
            }
        }

        /*
        if (acNum > 30 || anNum > 30) {
            System.out.println("acNum: " + acNum);
            System.out.println("anNum: " + anNum);
            System.out.println("input_layer: " + inp_size);
            System.out.println("output_layer: " + out_size);
        }
        */
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
