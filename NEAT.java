public class NEAT extends GA {
    Genome crossover (Genome g1, Genome g2) {
    }

    /*
    public NEAT (int pop_size,
                 double dis_rate,
                 double inter_mating,
                 double node_rate,
                 double link_rate)
    {
        this.pop_size = pop_size;
        this.dis_rate = dis_rate;
        this.inter_mating = inter_mating;
        this.node_rate = node_rate;
        this.link_rate = link_rate;
    }
    */

    public NEAT (int    pop_size,
                 double dis_rate,
                 double inter_mating,
                 double node_rate,
                 double link_rate) {
        super();

        // Initialize population
        population = new ArrayList<Genome>(pop_size);
    }

    /*
    public class NEATbuilder extends GAbuilder {
        public createGA GA
    }
    */
}
