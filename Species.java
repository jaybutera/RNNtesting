import java.util.ArrayList;

public class Species {
    public Species (final Genome seed) {
        genomes = new ArrayList<Genome>();
        genomes.add(seed);

        // Initial genome becomes the rep
        representative = seed;
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
}
