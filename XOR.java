public class XOR extends Fitness {
    public Double simulate (Network n) {
        Double[][] outs = new Double[outData.length][1];

        try {
            for (int i = 0; i < inData.length; i++)
                outs[i] = n.step(inData[i]);
        }
        catch (Exception e) {
            System.out.println("Invalid network\n" + n);
        }

        Double sum = 0.0;
        for (int i = 0; i < outs.length; i++)
            sum += Math.abs(outs[i][0] - outData[i]);

        return sum;
    }

    private Double[][] inData = {{1.,1.},
                                 {1.,0.},
                                 {0.,1.},
                                 {0.,0.}};
    private Double[] outData = {0.,1.,1.,0.};
}
