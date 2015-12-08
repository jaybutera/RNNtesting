public class XOR extends Fitness {
    public Double simulate (Network n) {
        outs = new Double[outData.length][1];

        try {
            for (int i = 0; i < inData.length; i++) {
                outs[i] = n.step(inData[i]);
                //System.out.print(outs[i][0] + " : ");
            }
        }
        catch (Exception e) {
            System.out.println("Invalid network\n" + n);
        }

        Double fitness = 0.0;
        for (int i = 0; i < outs.length; i++)
            fitness += Math.abs(outs[i][0] - outData[i]);

        //System.out.println("Fitness - " + fitness);
        return Math.pow(fitness,2);
    }

    public Double[] getOuts () {
        Double[] x = new Double[outs.length];

        // Transpose outs
        for (int i = 0; i < outs.length; i++)
            x[i] = outs[i][0];

        return x;
    }

    private Double[][] outs; // Calculated output
    private Double[][] inData = {{0.,0.},
                                 {1.,0.},
                                 {0.,1.},
                                 {1.,1.}};
    /*
    private Double[][] inData = {{1.,1.,1.},
                                 {1.,1.,0.},
                                 {1.,0.,1.},
                                 {1.,0.,0.}};
                                 */
    private Double[] outData = {0.,1.,1.,0.};
}
