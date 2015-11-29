public class TestGA {
    public static void main (String[] args) {
        Population p = new Population(100, .8, .1, .3, .4, new XOR());

        // Run through 100 generations
        for (int i = 0; i < 100; i++)
            p.nextGen();
    }
}
