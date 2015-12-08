TestGA
======

The block in the main method builds a population and runs it for n generations,
then provides analytics on the performance of the algorithm.

TestNN
======

Tests the neural network class on an arbitrary topology and data set. Results
are confirmed correct (will test XOR in future).

Explanation
===========

#### Genetic Algorithm (NEAT)
Population generates a list of perceptron genomes (no hidden nodes). The
population is divided into species based on compatibility of gene pools each
generation.

During a transition to the next generation, species reproduce and the mutated
offspring become the new population.

#### Neural Network
When a new genome is generated in a species or in the initialization of the
population, its fitness is computed by transforming the genetic encoding into a
recurrent neural network. The network computes based on the general model of an
RNN (2).

#### Innovation Database
The innovation database (Innovations.java) keeps track of all the mutation
structural perturbations that have occured in the population. If an
pre-existing mutation occurs, it will be assigned the same innovation id as
stored in the database. This protects topological similarities as shown in
paper (1).

---

(1) NEAT paper - http://nn.cs.utexas.edu/downloads/papers/stanley.ec02.pdf
(2) RNN - http://minds.jacobs-university.de/sites/default/files/uploads/papers/ESNTutorialRev.pdf
