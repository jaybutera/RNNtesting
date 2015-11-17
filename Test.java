import java.util.List;
import java.util.ArrayList;

public class Test {
    public static void main(String[] args) {
        /*
        Double[][] inputs = new Double[][] {{.23,.87,.46},
                                            {.51,.12,.30},
                                            {.72,.05,.55},
                                            {.66,.68,.19},
                                            {.90,.27,.78}};

        Double[][] hidden = new Double[][] {{.5,.646,.108,.914,.417},
                                            {.36,.216,.305,.434,.145},
                                            {.725,.214,.541,.014,.292},
                                            {.758,.242,.460,.929,.754},
                                            {.563,.882,.706,.153,.461}};

        Double[][] output = new Double[][] {{.150,.039,.158,.933,.628,.598,.044,.400},
                                            {.860,.199,.861,.512,.626,.430,.369,.626}};

        Network N = new Network.NetworkBuilder()
                               .inputs(inputs)
                               .outputs(output)
                               .hidden(hidden)
                               .createNetwork();

        */

        Genome g = new Genome(3,2);
        Network n = new Network(g);

        System.out.println(g);
        System.out.println(n);
        Double[] out = n.step(new Double[]{.68,.21,.89});
        System.out.println(out[0] + " - " + out[1]);
        out = n.step(new Double[]{.07,.72,.33});
        System.out.println(out[0] + " - " + out[1]);
    }
}
