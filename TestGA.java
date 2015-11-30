public class TestGA {
    public static void main (String[] args) {
        XOR f = new XOR();
        //Population p = new Population(100, .8, .1, .3, .4, 2, 1, f);

        // Run through 100 generations
        /*
        for (int i = 0; i < 100; i++) {
            System.out.println("Running generation " + i + "...");
            p.nextGen();
        }

        Network n = new Network(p.getMostFit());
        System.out.println(n);
        System.out.println(f.simulate(n));
        */

        Population p = new Population(1, .8, .6, .7, .9, 2, 1, f);

        p.addGenome( new Genome(3,2) );
        System.out.println(p.getMostFit());
        p.mutate(p.getMostFit());
        System.out.println(p.getMostFit());
    }
}
