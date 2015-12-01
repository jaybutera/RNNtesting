import java.util.Random;

// Immutable
final public class ConnectionGene implements Comparable<ConnectionGene> {
    // All parameter inclusive
    public ConnectionGene (Node in, Node out, double weight, boolean enabled, int inv) {
        this.in      = in;
        this.out     = out;
        this.weight  = weight;
        this.enabled = enabled;
        innovation   = inv;
    }

    // Unspecified weight
    public ConnectionGene (Node in, Node out, boolean enabled, int inv) {
        // Randomly generate weight
        this.weight = new Random().nextDouble();

        this.in      = in;
        this.out     = out;
        this.enabled = enabled;
        innovation   = inv;
    }

    // Unspecified weight and default enable flag (true)
    public ConnectionGene (Node in, Node out, int inv) {
        // Randomly generate weight
        this.weight = new Random().nextDouble();

        this.in      = in;
        this.out     = out;
        this.enabled = true;
        innovation   = inv;
    }

    // Default enable flag (true)
    public ConnectionGene (Node in, Node out, double weight, int inv) {
        this.in      = in;
        this.out     = out;
        this.weight  = weight;
        this.enabled = true;
        innovation   = inv;
    }

    public ConnectionGene flipGene () {
        // Defensive copy
        return new ConnectionGene(in, out, weight, !enabled, innovation);
    }

    public String toString () {
        return "innovation: " + innovation + "\n" +
               "in:         " + in.id + " (" + in.type + ")\n" +
               "out:        " + out.id + " (" + out.type + ")\n" +
               "weight:     " + weight + "\n" +
               "enabled:    " + enabled + "\n";
    }

    public int compareTo (ConnectionGene c2) {
        return (c2.innovation == this.innovation) ? 1 : 0;
    }

    public final Node in;
    public final Node out;

    public double weight;
    public boolean enabled;
    public int innovation;
}
