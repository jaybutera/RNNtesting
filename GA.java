import java.util.ArrayList;

public abstract class GA {
    public abstract class GAbuilder {
        protected double pop_size;
        protected double dis_rate;
        protected double inter_mating_rate;
        protected double node_rate;
        protected double link_rate;

        public GAbuilder disableRate (double dis_rate) {
            this.dis_rate = dis_rate;
            return this;
        }

        public GAbuilder interspeciesMatingRate (double inter) {
            inter_mating_rate = inter;
            return this;
        }

        public GAbuilder node_rate (double node_rate) {
            this.node_rate = node_rate;
            return this;
        }

        public GAbuilder linkRate (double link_rate) {
            this.link_rate = link_rate;
            return this;
        }

        abstract public GA createGA ();
    }

    /*********************/
    /***** protected *****/
    /*********************/

    protected ArrayList<Genome> population;
    protected ArrayList<ConnectionGene> generation_innovs;

    protected double pop_size     = 100.0;
    protected double dis_rate;
    protected double inter_mating_rate;
    protected double node_rate;
    protected double link_rate;

    protected GA (int    pop_size,
                  double dis_rate,
                  double inter,
                  double node_rate,
                  double link_rate)
    {
        this.pop_size          = pop_size;
        this.dis_rate          = dis_rate;
        this.inter_mating_rate = inter;
        this.node_rate         = node_rate;
        this.link_rate         = link_rate;
    }

    protected ArrayList<ConnectionGene> getMatching (Genome g1, Genome g2) {
        // Find all matching innovation numbers
        ArrayList<ConnectionGene> matching = new ArrayList<ConnectionGene>();

        // TODO: Optimize by iterating through the smallest genome
        for ( ConnectionGene c1 : g1.connections )
            if ( g2.contains(c1) )
                matching.add(c1);

        return matching;
    }

    abstract protected Genome crossover (Genome g1, Genome g2);
    abstract protected int fitness (Genome g);
    abstract protected Genome mutation (Genome g);
}
