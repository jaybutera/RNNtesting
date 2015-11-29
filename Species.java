import java.util.ArrayList;

public class Species {
    public Species (final Genome seed) {
        genomes = new ArrayList<Genome>();
        genomes.add(seed);

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

        // Initial genome becomes the rep
        representative = seed;

        // Initialize compatability parameters
        this.c1 = c1;
        this.c2 = c2;
        this.c3 = c3;
    }

    public double compatibility (Genome g) {
        int N;

        // Set largest genome
        if (g.connections.size() > representative.connections.size())
            N = g.connections.size();
        else
            N = representative.connections.size();

        return   (c1 * g.getExcess(representative).size()) / N
               + (c2 * g.getDisjoint(representative).size()) / N
               +  c3 * g.weightDiff(representative);
    }

    public void add (Genome g) {
        genomes.add(g);

        // Make genome the new rep if it has the highest fitness
        if (g.fitness > representative.fitness) {
            representative = g;
        }
    }

    private ArrayList<Genome> genomes;
    private Genome representative;

    // Compatibility parameters
    private double c1;
    private double c2;
    private double c3;
}
