import java.util.ArrayList;

public class Population {
    public Population (int size) {
        pop = new ArrayList<Genome>(size);
    }

    public void addGenome (Genome g) {
        pop.add(g);
    }

    private ArrayList<Genome> pop;
}
