import java.util.ArrayList;
import java.util.Random;

public class Population {
    public Population (int size,
                       double dis_rate,
                       double inter_rate,
                       double node_rate,
                       double link_rate,
                       Fitness f) {
        pop = new ArrayList<Genome>(size);
        species = new ArrayList<Species>();
        gen_mutations = new ArrayList<ConnectionGene>();
        this.f = f;

        // Speciate all genomes in population
        for ( Genome g : pop )
            speciate(g);
    }

    private Population (ArrayList<Genome> pop,
                        double dis_rate,
                        double inter_rate,
                        double node_rate,
                        double link_rate,
                        Fitness f) {
        this.pop = pop;
        species = new ArrayList<Species>();
        gen_mutations = new ArrayList<ConnectionGene>();
        this.f = f;

        // Speciate all genomes in population
        for ( Genome g : pop )
            speciate(g);
    }

    public void addGenome (Genome g) {
        pop.add(g);
        speciate(g);
    }

    public Population nextGen () {
        Genome parent1 = getMostFit();
        pop.remove(pop.indexOf(parent1));
        Genome parent2 = getMostFit();

        Genome child = crossover(parent1, parent2);

        ArrayList<Genome> new_pop = new ArrayList<Genome>(pop.size()+1);

        for ( Genome g : new_pop )
            mutate(g);

        return new Population(new_pop,
                              dis_rate,
                              inter_rate,
                              node_rate,
                              link_rate,
                              f);
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

    /***************/
    /*   PRIVATE   */
    /***************/

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

    private Genome getMostFit () {
        Genome top = pop.get(0);

        for ( Genome g : pop )
            if (g.fitness > top.fitness)
                top = g;

        return top;
    }

    private void mutate(Genome g) {
        double weight_rate  = .80;
        double weight_val_rate = .10;

        Random r = new Random();

        for (int i = 0; i < g.connections.size(); i++) {
            if ( r.nextDouble() > weight_rate ) {
                if ( r.nextDouble() > link_rate )
                    g.addConnection();
                if ( r.nextDouble() > node_rate )
                    g.addNode();
                if ( r.nextDouble() > weight_val_rate )
                    g.connections.get(i).weight = r.nextDouble();
            }

            if ( r.nextDouble() > dis_rate )
                g.connections.get(i).enabled = !g.connections.get(i).enabled;
        }
    }

    // Mutation parameters
    private double dis_rate;
    private double inter_rate;
    private double node_rate;
    private double link_rate;

    private ArrayList<Genome> pop;
    private ArrayList<Species> species;
    private double compatThresh;
    private ArrayList<ConnectionGene> gen_mutations;
    private Fitness f;
}
