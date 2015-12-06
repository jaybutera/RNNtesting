import java.util.ArrayList;
import java.util.Random;

public class Species {
    public Species (final Genome seed, Fitness f, Innovations inv_db) {
        genomes = new ArrayList<Genome>();
        genomes.add(seed);

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

        return children;
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

        // TODO: Make fitness class an interface so it doesn't have to be passed
        // around so much

        // Determine fitness of the child
        child.fitness = f.simulate( new Network(child) );

        return child;
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
}
