import java.util.ArrayList;
import java.util.Random;

public class Species {
    public Species (final Genome seed,
                    double dis_rate,
                    double link_rate,
                    double node_rate,
                    Fitness f,
                    Innovations inv_db) {
        genomes = new ArrayList<Genome>();
        genomes.add(seed);

        // Init mutation params
        this.dis_rate  = dis_rate;
        this.link_rate = link_rate;
        this.node_rate = node_rate;

        this.inv_db = inv_db;

        // Initialize species in/out node standard
        input_size  = seed.inputSize();
        output_size = seed.outputSize();

        // Initialize fitness
        this.f = f;

        // Initial genome becomes the rep
        representative = seed;

        // Initialize compatability parameters
        c1 = 1.0;
        c2 = 1.0;
        c3 = 1.0;
    }

    /*
    public Species (final Genome seed,
                    double c1,
                    double c2,
                    double c3) {
        genomes = new ArrayList<Genome>();
        genomes.add(seed);

        // Initialize species in/out node standard
        input_size  = seed.inputSize();
        output_size = seed.outputSize();

        // Initial genome becomes the rep
        representative = seed;

        // Initialize compatability parameters
        this.c1 = c1;
        this.c2 = c2;
        this.c3 = c3;
    }
    */

    public double compatibility (Genome g) {
        int N;

        /*
        if (g.connections.size() < 20 && representative.connections.size() < 20)
            N = 1;
            */

        // Set largest genome
        if (g.connections.size() > representative.connections.size())
            N = g.connections.size();
        else
            N = representative.connections.size();

        double x =   (c1 * g.getExcess(representative).size()) / N
                   + (c2 * g.getDisjoint(representative).size()) / N
                   +  c3 * g.weightDiff(representative);

        /*
        System.out.println("\nExcess size: " + g.getExcess(representative).size());
        System.out.println("Disjoint size: " + g.getDisjoint(representative).size());
        System.out.println("Weight diff: " + g.weightDiff(representative));
        */
        //System.out.println("\nCompatibility : " + x);
        return x;
    }

    public void add (Genome g) {
        genomes.add(g);
        //System.out.println("Genome added to species");
        //System.out.println("New species size: " + genomes.size());

        // Make genome the new rep if it has the highest fitness
        if (g.fitness < representative.fitness) {
            representative = g;
        }
    }

    public ArrayList<Genome> reproduce () {
        ArrayList<Genome> children = new ArrayList<Genome>();

        // TODO: Initial reproduction algorithm, use factorial formulation in
        // future.
        // Mate each adjacent genome
        for (int i = 1; i < genomes.size(); i++)
            children.add( crossover(genomes.get(i-1), genomes.get(i)) );
        //System.out.println("Children: " + children.size());

        // Add a final genome to keep same population size
        children.add( crossover(genomes.get(0), genomes.get(genomes.size()-1)) );

        // Replace pop with next generation
        genomes = children;

        return genomes;
    }

    private Genome updateRep () {
        for ( Genome g : genomes )
            if (g.fitness < representative.fitness)
                representative = g;

        return representative;
    }

    private Genome crossover (Genome g1, Genome g2) {
        Genome child = new Genome(input_size, output_size, inv_db);

        // Assign all matching connection genes
        ArrayList<ConnectionGene> matching = g1.getMatching(g2);
        child.addConnections(matching);

        // If parents have equal fitness, randomly match excess genes
        if (g1.fitness == g2.fitness) {
            Random r = new Random();

            // Get excess from both parents
            ArrayList<ConnectionGene> excess = g1.getExcess(g2);
            excess.addAll( g2.getExcess(g1) );

            // Get disjoint from both parents
            ArrayList<ConnectionGene> disjoint = g1.getDisjoint(g2);
            disjoint.addAll( g2.getDisjoint(g1) );

            // Randomly assign excess genes to child
            for ( ConnectionGene c : excess )
                if ( r.nextBoolean() )
                    child.addConnection(c);
            for ( ConnectionGene c : disjoint )
                if ( r.nextBoolean() )
                    child.addConnection(c);
        }

        // Otherwise child inherits excess/disjoint genes of most fit parent
        else if (g1.fitness > g2.fitness) {
            child.addConnections( g1.getExcess(g2) );
            child.addConnections( g1.getDisjoint(g2) );
        }
        else if (g1.fitness < g2.fitness) {
            child.addConnections( g2.getExcess(g1) );
            child.addConnections( g2.getDisjoint(g1) );
        }

        // TODO: Make fitness class an interface so it doesn't have to be passed
        // around so much

        // Apply mutations
        mutate(child);

        // Determine fitness of the child
        child.fitness = f.simulate( new Network(child) );

        return child;
    }

    public void mutate(Genome g) {
        Random r = new Random();

        double weight_val_rate = .70;

        // Mutations for input to hidden connections
        perturbLinks(g.input_nodes, g.hidden_nodes, g);

        // Mutations for hidden to hidden connections
        perturbLinks(g.hidden_nodes, g.hidden_nodes, g);

        // Mutations for hidden to output connections
        perturbLinks(g.hidden_nodes, g.output_nodes, g);

        // Mutations for input to output connections
        perturbLinks(g.input_nodes, g.output_nodes, g);

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

    // For debugging. Should take this out soon.
    public Genome getRep () {
        return representative;
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
                        g.addConnection(inp, out);
                    }
                    else if ( r.nextDouble() < node_rate ) {
                        g.addNode(inp, out);
                    }
                }
            }
        }
    }

    private ArrayList<Genome> genomes;
    private Genome representative;
    private Fitness f;
    private Innovations inv_db;

    public final int input_size;
    public final int output_size;

    // Compatibility parameters
    private double c1;
    private double c2;
    private double c3;

    // Mutation parameters
    double dis_rate;
    double link_rate;
    double node_rate;
}
