public class TestGA {
    public static void main (String[] args) {
        XOR f = new XOR();
        Population p = new Population(100, .8, .1, .3, .4, .15, 2, 1, f);

        // Run through 100 generations
        for (int i = 0; i < 10; i++) {
            System.out.println("Running generation " + i + "...");
            p.nextGen();
            System.out.println(p.species.size());
            //System.out.println(p.getMostFit());
        }

        Network n = new Network(p.getMostFit());
        System.out.println(n);
        System.out.println(f.simulate(n));
        //testMutate(f);
        //testContains();
    }

    private void testMutate (XOR f) {
        Population p = new Population(1, .3, .6, .7, .9, .15, 2, 1, f);

        p.addGenome( new Genome(3,2) );
        System.out.println(p.getMostFit());
        p.mutate(p.getMostFit());
        System.out.println(p.getMostFit());
        System.out.println( new Network(p.getMostFit()) );
    }

    private static void testContains () {
        Genome g = new Genome(3,2);
        ConnectionGene cg = g.connections.get(0);
        System.out.println(g);
        ConnectionGene cg2 = new ConnectionGene(new Node(1), new Node(3), 4);
        System.out.println("Check genome for gene: " + cg + "\n-> " + g.contains(cg));
        System.out.println("\nCheck genome for gene: " + cg2 + "\n-> " + g.contains(cg2));
    }
}
