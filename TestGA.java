public class TestGA {
    public static void main (String[] args) {
        XOR f = new XOR();
        Population p = new Population(100, .2, .1, .3, .6, 1.1, 2, 1, f);

        // Run through 100 generations
        for (int i = 0; i < 15; i++) {
            System.out.println("Running generation " + i + "...");
            System.out.println("Number of species: " + p.getNumSpecies());
            p.nextGen();
            //System.out.println("Most fit genome:\n\n" + p.getMostFit());
            System.out.println("Top fitness: " + f.simulate( new Network(p.getMostFit()) ));
        }

        Genome g = p.getMostFit();
        System.out.println(g);
        Network n = new Network(g);
        //System.out.println(f.simulate(n));
        //testMutate(f);
        //testContains();
        //testAddNode();
        //testExcDis();
    }

    private void testMutate (XOR f) {
        Population p = new Population(1, .3, .6, .7, .9, .15, 2, 1, f);
        Innovations inv_db = new Innovations();

        p.addGenome( new Genome(3,2, inv_db) );
        System.out.println(p.getMostFit());
        p.mutate(p.getMostFit());
        System.out.println(p.getMostFit());
        System.out.println( new Network(p.getMostFit()) );
    }

    private static void testContains () {
        Innovations inv_db = new Innovations();
        Genome g = new Genome(3,2, inv_db);

        ConnectionGene cg = g.connections.get(0);
        System.out.println(g);
        ConnectionGene cg2 = new ConnectionGene(new Node(1), new Node(3), 4);
        System.out.println("Check genome for gene: " + cg + "\n-> " + g.contains(cg));
        System.out.println("\nCheck genome for gene: " + cg2 + "\n-> " + g.contains(cg2));
    }

    private static void testAddNode () {
        Innovations inv_db = new Innovations();
        Genome g = new Genome(3,2, inv_db);

        Node i = g.input_nodes.get(1);
        Node o = g.output_nodes.get(0);

        System.out.println(g);
        //g.addNode(i,o,inv_db);
        System.out.println(g);
    }

    private static void testExcDis () {
        XOR f = new XOR();
        Population p = new Population(2, .2, .1, .3, .6, 3.0, 3, 2, f);

        // Allow some mutations
        for (int i = 0; i < 8; i++)
            p.nextGen();

        Genome g1 = p.getGenome(0);
        Genome g2 = p.getGenome(1);

        System.out.println(g1);
        System.out.println(g2);

        System.out.println("Excess:");
        System.out.println("");
        System.out.println( g1.getExcess(g2) );
        System.out.println("\n");
        System.out.println("Disjoint:");
        System.out.println("");
        System.out.println( g1.getDisjoint(g2) );
        System.out.println("\n");
        System.out.println("Matching:");
        System.out.println("");
        System.out.println( g1.getMatching(g2) );
        System.out.println("\n");
    }
}
