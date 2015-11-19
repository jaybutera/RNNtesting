import java.util.Random;
import java.util.ArrayList;

public class NEAT extends GA {
    public NEAT (int    pop_size,
                 double dis_rate,
                 double inter_mating,
                 double node_rate,
                 double link_rate) {
        super(pop_size, dis_rate, inter_mating, node_rate, link_rate);

        // Initialize population
        population = new ArrayList<Genome>(pop_size);
    }

    protected Genome crossover (Genome g1, Genome g2) {
        Genome child = new Genome();

        // Assign all matching connection genes
        ArrayList<ConnectionGene> matching = getMatching(g1, g2);
        child.addConnections(matching);

        int g1_fit = fitness(g1);
        int g2_fit = fitness(g2);

        // If parents have equal fitness, randomly match excess genes
        if (g1_fit == g2_fit) {
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
        else if (g1_fit > g2_fit) {
            ArrayList<ConnectionGene> excess_g1 = new ArrayList<ConnectionGene>(g1.connections);
            excess_g1.removeAll(matching);

            child.addConnections(excess_g1);
        }
        else if (g1_fit < g2_fit) {
            ArrayList<ConnectionGene> excess_g2 = new ArrayList<ConnectionGene>(g2.connections);
            excess_g2.removeAll(matching);

            child.addConnections(excess_g2);
        }

        return child;
    }

    // TODO: make this abstract and make sub classes for specific applications
    // that use the NEAT strategy
    protected int fitness (Genome g) {
        // Fitness for XOR

        // Temporary
        return 0;
    }

    protected Genome mutation(Genome g) {
        // Temporary
        return new Genome(0,0);
    }

    /*
    public class NEATbuilder extends GAbuilder {
        public createGA GA
    }
    */
}
