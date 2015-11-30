public class TestGA {
    public static void main (String[] args) {
        XOR f = new XOR();
        Population p = new Population(100, .8, .1, .3, .4, 2, 1, f);

        // Run through 100 generations
        for (int i = 0; i < 100; i++) {
            System.out.println("Running generation " + i + "...");
            p.nextGen();
        }

        Network n = new Network(p.getMostFit());
        System.out.println(n);
        System.out.println(f.simulate(n));
    }
}
