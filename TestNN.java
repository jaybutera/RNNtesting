import java.util.List;
import java.util.ArrayList;

public class TestNN {
    public static void main(String[] args) {
        /************************
         * MANUAL INITIALIZATION
         ************************/
        Double[][] inputs = new Double[][] {{.0,.87,.46},
                                            {.51,.0,.30},
                                            {.72,.0,.0},
                                            {.0,.68,.0},
                                            {.90,.27,.78}};

        Double[][] hidden = new Double[][] {{.000,.646,.000,.000,.417},
                                            {.000,.216,.000,.434,.000},
                                            {.725,.214,.000,.000,.000},
                                            {.000,.000,.460,.000,.754},
                                            {.000,.000,.706,.153,.000}};

        Double[][] output = new Double[][] {{.150,.039,.000,.933,.000,.000,.044,.400},
                                            {.860,.199,.861,.000,.000,.430,.369,.000}};

        Network n1 = new Network.NetworkBuilder()
                               .inputs(inputs)
                               .outputs(output)
                               .hidden(hidden)
                               .createNetwork();

        // Print results

        System.out.println(n1);

        Double[] out = n1.step(new Double[]{.68,.21,.89});
        System.out.println(out[0] + " - " + out[1]);
        out = n1.step(new Double[]{.07,.72,.33});
        System.out.println(out[0] + " - " + out[1]);

        System.out.println("\n-------------------------------------\n\n");

        /**********************
         * AUTO INITIALIZATION
         **********************/

        Genome g = new Genome(3,2);
        Network n2 = new Network(g);

        // Print results

        System.out.println(g);
        System.out.println(n2);

        out = n2.step(new Double[]{.68,.21,.89});
        System.out.println(out[0] + " - " + out[1]);
        out = n2.step(new Double[]{.07,.72,.33});
        System.out.println(out[0] + " - " + out[1]);
    }

    public static void testXOR () {
        Double[][] inputs = new Double[][] {{.0,.87,.46},
                                            {.90,.27,.78}};

        Double[][] hidden = new Double[][] {{.000,.646,.000,.000,.417},
                                            {.000,.216,.000,.434,.000},
                                            {.725,.214,.000,.000,.000},
                                            {.000,.000,.460,.000,.754},
                                            {.000,.000,.706,.153,.000}};

        Double[][] output = new Double[][] {{.150,.039,.000,.933,.000,.000,.044,.400},
                                            {.860,.199,.861,.000,.000,.430,.369,.000}};

        Network n1 = new Network.NetworkBuilder()
                               .inputs(inputs)
                               .outputs(output)
                               .hidden(hidden)
                               .createNetwork();

        // Print results

        System.out.println(n1);

        Double[] out = n1.step(new Double[]{.68,.21,.89});
        System.out.println(out[0] + " - " + out[1]);
        out = n1.step(new Double[]{.07,.72,.33});
        System.out.println(out[0] + " - " + out[1]);
    }
}
