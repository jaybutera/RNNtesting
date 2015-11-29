import java.util.ArrayList;
import java.util.Random;

public class Population {
    public Population (int size) {
        pop = new ArrayList<Genome>(size);
        species = new ArrayList<Species>();
    }

    public void addGenome (Genome g) {
        pop.add(g);
        speciate(g);
    }

    public void nextGen () {
    }

    public void speciate (Genome g) {
        double g_compat;
        int i = 0;

        // Search for an appropriate species
        do {
            g_compat = species.get(i).compatibility(g);
        } while (g_compat > compatThresh && i < species.size());

        // Add genome to threshold matched species
        if (g_compat > compatThresh)
            species.get(i).add(g);
        // If no match exists, create a new species
        else
            species.add( new Species(g) );
    }

    private Genome crossover (Genome g1, Genome g2) {
        Genome child = new Genome();

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

    private Genome mutation(Genome g) {
        // Temporary

        return new Genome(0,0);
    }

    private ArrayList<Genome> pop;
    private ArrayList<Species> species;
    private double compatThresh;
}
