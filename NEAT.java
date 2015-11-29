import java.util.Random;
import java.util.ArrayList;

public class NEAT extends GA {
    public NEAT (int    pop_size,
                 double dis_rate,
                 double inter_mating,
                 double node_rate,
                 double link_rate,
                 Fitness f) {
        super(pop_size, dis_rate, inter_mating, node_rate, link_rate, f);

        // Initialize population
        population = new Population(pop_size,
                                    dis_rate,
                                    inter_mating,
                                    node_rate,
                                    link_rate,
                                    f);
    }
}
