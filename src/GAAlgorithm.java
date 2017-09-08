import java.util.Scanner;
import java.io.*;
import java.util.Date;
import java.util.InputMismatchException;
import java.util.Random;
import java.util.LinkedList;

public class GAAlgorithm {

	private static int popSize = 50;
	private static int maxGens = 150;
	private static int regularSize = 4;
	private static int convergenceLimit = 30;
	private static double xOverRate = 0.2;
	private static double mutationRate = 0.35;

	static Individual[] Population;

	static Individual bestIndividual;

	public static void main(String[] args) {
		GraphStructure GAGraph = new GraphStructure();
		while (true)
			menu(new Scanner(System.in), GAGraph);
	}

	public static void menu(Scanner myScanner, GraphStructure newGraph) {
		String fileInput;
		int input;
		System.out.println("[Genetic Algorithm]");
		System.out.println("1. Read Graph");
		System.out.println("2. Print Graph");
		System.out.println("3. Find Colouring (GA)");
		System.out.println("4. Find Colouring (nonGA)");
		System.out.println("5. Print Colouring");
		System.out.println("6. Print Chromons");
		System.out.println("7. Generate Random 4-Regular Graph");
		System.out.println("8. Run Tests");
		System.out.println("9. Settings");
		System.out.println("0. Quit");
		System.out.println("Please choose an option: ");
		try {
			input = myScanner.nextInt();
			switch (input) {
			case 1:
				System.out.println("Please input filename: ");
				fileInput = myScanner.next();
				readGraph(fileInput, newGraph);
				break;
			case 2:
				printGraph(newGraph);
				break;
			case 3:
				System.out
						.println("Run till max generations? \n 1.yes \n 2.no ");
				input = myScanner.nextInt();
				if (input == 1 || input == 2) {
					boolean maxGenEnabled = input == 1 ? true : false;
					findColouringGA(newGraph, maxGenEnabled, true);
				} else 
					throw new InputMismatchException();
				break;
			case 4:
				findColouringNonGA(newGraph, true);
				break;
			case 5:
				printColouring();
				break;
			case 6:
				printChromons();
				break;
			case 7:
				System.out.println("Please input graph size: ");
				input = myScanner.nextInt();
				generateRandomGraph(input, new GraphStructure(input));
				break;
			case 8:
				runAllTests(newGraph);
				break;
			case 9:
				while (setSettings(new Scanner(System.in)));
				break;
			case 0:
				System.exit(0);
				break;
			default:
				throw new InputMismatchException();
				//System.err.println("Invalid option. Try again.");
			}
		} catch (InputMismatchException e) {
			System.err.println("Invalid option or incorrect input. Try again.");
			// e.printStackTrace();
		}
	}

	/**
	 * Settings to change certain variables
	 * 
	 * @param myScanner
	 */
	public static boolean setSettings(Scanner myScanner) {
		int input;
		System.out.println("[Settings]");
		System.out.println("1. Set Population Size (current: " + popSize + ")");
		System.out.println("2. Set CrossOver Probability (current: " + xOverRate + ")");
		System.out.println("3. Set Mutation Probability (current: " + mutationRate + ")");
		System.out.println("4. Set Convergence Limit (current: " + convergenceLimit + ")");
		System.out.println("5. Set Max Generations (current: " + maxGens + ")");
		System.out.println("6. Exit Settings");
		System.out.println("Please choose an option: ");
		try {
			input = myScanner.nextInt();
			switch (input) {
			case 1:
				System.out.println("Choose population size (current: "
						+ popSize + "): "); 
				int psize = myScanner.nextInt();
				if (psize <= 1)
					throw new Exception();
				else
						popSize = psize;
				break;
			case 2:
				System.out.println("Choose xOver probability (current: "
						+ xOverRate + "): ");
				double xrate = myScanner.nextDouble();
				if (xrate >= 1 || xrate < 0)
					throw new Exception();
				else
					xOverRate = xrate;
				break;
			case 3:
				System.out.println("Choose mutation probability (current: "
						+ mutationRate + "): ");
				double mrate = myScanner.nextDouble();
				if (mrate >= 1 || mrate < 0)
					throw new Exception();
				else
					mutationRate = mrate;
				break;
			case 4:
				System.out.println("Choose convergence limit (current: "
						+ convergenceLimit + "): ");
				int climit = myScanner.nextInt();
				if (climit < 0)
					throw new Exception();
				else
					convergenceLimit = climit;
				break;
			case 5:
				System.out.println("Choose max generations (current: "
						+ maxGens + "): ");
				int mgens = myScanner.nextInt();
				if (mgens < 0)
					throw new Exception();
				else
					maxGens = mgens;
				break;
			case 6:
				return false;
			default:
				throw new Exception();
				//System.err.println("Invalid option. Try again.");
			}
		} catch (Exception e) {
			System.err
					.println("Invalid option or incorrect input. Try again.");
		} 
		return true;
	}

	/**
	 * Generates a random colour
	 * 
	 * @return
	 */
	public static String generateColour() {
		Random generator = new Random();
		double probability = 0.5;
		return (generator.nextDouble() > probability ? "W" : "B");
	}

	/**
	 * Reads a graph and checks for any errors
	 * 
	 * @param fileInput
	 * @param newGraph
	 */
	public static boolean readGraph(String fileInput, GraphStructure newGraph) {
		try (Scanner inputStream = new Scanner(new FileInputStream(fileInput))) {
			int count = 1;
			int numVert = 0, numEdge = 0;
			while (inputStream.hasNextLine()) {
				switch (count) {
				// Insert amount of Vertices
				case 1:
					String noVert = inputStream.nextLine();
					numVert = Integer.parseInt(noVert);
					// checks for any vertices
					if (numVert < 1) {
						System.err
								.println("Invalid graph: There are no Vertices.");
						clearGraph(newGraph);
						return false;
					}
					newGraph = new GraphStructure(numVert);
					break;
				case 2:
					String noEdge = inputStream.nextLine();
					numEdge = Integer.parseInt(noEdge);
					// checks whether there is enough Edges
					if (numEdge < numVert / 2) {
						System.err
								.println("Invalid graph: Irregular Edge count.");
						clearGraph(newGraph);
						return false;
					}
					int checkEdge = 0;
					while (inputStream.hasNextLine()) {
						try {
							String[] jointEdge = inputStream.nextLine().split(
									" ");
							int vert1 = Integer.parseInt(jointEdge[0]);
							int vert2 = Integer.parseInt(jointEdge[1]);
							// checks for loops
							if (vert1 == vert2) {
								System.err
										.println("Invalid graph: Loop exists.");
								clearGraph(newGraph);
								return false;
							}

							if (hasDuplicateEdge(newGraph, vert1, vert2)) {
								System.err
										.println("Invalid graph: Duplicate Edges.");
								clearGraph(newGraph);
								return false;
							}

							// checks for incident Edges > 4
							if (isLargerThanRegular(newGraph, vert1, vert2)) {
								System.err.println("Invalid graph: not a "
										+ regularSize + "-Regular Graph.");
								clearGraph(newGraph);
								return false;
							} else {
								// adds edges into Graph Structure and
								// references each other
								Vertex[] verts = { newGraph.getVertex(vert1),
										newGraph.getVertex(vert2) };
								Edge tempEdge = new Edge(verts);
								newGraph.addEdge(tempEdge);
								newGraph.getVertex(vert1).getIncEdges().add(
										tempEdge);
								newGraph.getVertex(vert2).getIncEdges().add(
										tempEdge);
							}
						} catch (NumberFormatException e) {
							System.err.println("Incorrect format.");
							clearGraph(newGraph);
							return false;
						}
						checkEdge++;
					}
					// checks that edges = actual number of edges
					if (checkEdge != numEdge) {
						System.err
								.println("Invalid graph: inconsistent edge count.");
						clearGraph(newGraph);
						return false;
					}
					break;
				default:
					System.err.println("Incorrect format.");
					clearGraph(newGraph);
					return false;
				}
				count++;
			}

			if (!isRegularGraph(newGraph)) {
				System.err.println("Invalid graph: not a " + regularSize
						+ "-Regular Graph.");
				clearGraph(newGraph);
				return false;
			}

		} catch (FileNotFoundException e) {
			System.err.println("File not found or could not be opened.");
			return false;
		}
		return true;
	}

	/**
	 * Prints the graph out
	 * 
	 * @param newGraph
	 */
	public static void printGraph(GraphStructure newGraph) {
		if (!(newGraph.getVerticeSize() > 0)) {
			System.err.println("No graph exists.");
			return;
		}
		for (int i = 0; i < newGraph.getVerticeSize(); i++) {
			String printLine = i + ": ";
			for (int j = 0; j < newGraph.getVertex(i).getIncEdges().size(); j++) {
				Vertex tempVertex = newGraph.getVertex(i).getIncEdges().get(j)
						.getVertices()[0].equals(newGraph.getVertex(i)) ? newGraph
						.getVertex(i).getIncEdges().get(j).getVertices()[1]
						: newGraph.getVertex(i).getIncEdges().get(j)
								.getVertices()[0];
				printLine += newGraph.getVertexIndex(tempVertex)
						+ (j + 1 == newGraph.getVertex(i).getIncEdges().size() ? ""
								: ", ");
			}
			System.out.println(printLine);
		}
	}

	/**
	 * Uses a Genetic algorithm on a graph to find the best "individual"
	 * 
	 * @param newGraph
	 */
	public static void findColouringGA(GraphStructure newGraph,
			boolean maxGenEnabled, boolean print) {
		if (!(newGraph.getVerticeSize() > 0)) {
			System.err.println("No graph exists.");
			return;
		}
		// initialises the best individual
		bestIndividual = null;
		Population = new Individual[popSize];
		// initialising population and evaluating each individual
		for (int i = 0; i < popSize; i++) {
			String colouring = "";
			for (int j = 0; j < newGraph.getVerticeSize(); j++) {
				colouring += generateColour();
			}
			Population[i] = new Individual(colouring);
			evalIndividual(newGraph, Population[i]);
			Population[i].setGen(1);
		}
		// finds the best individual in new population
		evalBestIndividual();
		// initialise convergence number
		int convergence = 0;
		/**
		 * FIXME: logic error in condition below
		 * confused?! is this running off maxGen or convergence limit? what if convergence limit is > than maxGen
		 * if maxGen is not enabled run to convergence limit?
		 * EDIT: FIXED???  17/06/17
		 */
		for (int i = 0; (!maxGenEnabled || (maxGenEnabled && i < maxGens))
				&& (convergence < convergenceLimit); i++) {
			mutation(newGraph);
			crossOver(newGraph, i + 2);
			if (!evalBestIndividual())
				convergence++;
			else
				convergence = 0;
			Population = generateNewPop();
		}
		if (print) {
			printColouring();
			printChromons();
		}
	}
/*
	public static boolean checkMaxGen(boolean enabled, boolean isNotMaxGen) {
		/*
		 * doesn't require check over maxGen limit so always returns true
		 * this allows convergence to reach its limit if it is allowed
		 *
		 *
		if (!enabled || (enabled && isNotMaxGen))
			return true;
		else
			return false;
	}
*/

	/**
	 * Fitness evaluation for individuals. A great fitness is above 5 (Perfect
	 * colouring is 10). formula: (no. of chromons + (1/ chromon size)) / N x
	 * 10.0
	 * 
	 * @param newGraph
	 * @param noChromons
	 * @param chromonSize
	 * @return
	 */
	public static double fitnessFunction(GraphStructure newGraph,
			double noChromons, double chromonSize) {
		double fitness = ((noChromons + (1 / chromonSize)) / newGraph
				.getVerticeSize()) * 10;
		return fitness > 10 ? 10 : fitness;
	}

	/**
	 * generates a new population
	 * 
	 * @param newGraph
	 * @param size
	 * @return
	 */
	public static Individual[] generateNewPop() {
		Random generator = new Random();
		Individual[] newPop = new Individual[popSize];
		double accumulatedFitness = 0;
		double[] roullete = new double[popSize];
		for (int i = 0; i < popSize; i++) {
			accumulatedFitness += Population[i].getFitness();
		}
		//distributing weight of individuals across population
		for (int i = 0; i < popSize; i++) {
			roullete[i] = Population[i].getFitness() / accumulatedFitness;
		}
		for (int i = 0; i < popSize - 1; i++) {
			//resets accumulated chance, or else individuals can easily get chosen early on
			double accChance = roullete[popSize - 1];
			/**
			 *FIXME: retarded roullette wheel simulation needs to be improved
			 *generating new population based on same size? duplicate individuals?
			 *however this allows for true random selection
			 *EDIT: FIXED??! 18/06/17
			 *RESERVOIR SAMPLING
			 */
			while (newPop[i] == null){
				for (int j = 0; j < roullete.length - 1; j++){
					accChance += roullete[j];
					if (generator.nextDouble() <= (roullete[j]/accChance)) {
						newPop[i] = Population[j];
					}
				}
			}
		}
		// guaranteed position for best individual in new population
		newPop[popSize - 1] = bestIndividual;
		return newPop;
	}

	/**
	 * Initial method of evaluating an individual in the population
	 * 
	 * @param newGraph
	 * @param individual
	 * @param print
	 */
	public static void evalIndividual(GraphStructure newGraph,
			Individual individual) {
		newGraph.clearVisits();
		int countChromon = 0;
		int chromonSize = 0;
		// will go through each Vertex that is unexplored
		for (int j = 0; j < newGraph.getVerticeSize(); j++) {
			if (newGraph.getVertex(j).getVisited().equals("UNEXPLORED")) {
				int temp = evalIndividual(newGraph.getVertex(j), newGraph, 1,
						individual);
				// if temp is larger than current chromon size, store it into
				// variable
				chromonSize = temp > chromonSize ? temp : chromonSize;
				// keeps check on how many chromons there are
				countChromon++;
			}
		}
		double fitnessVal = fitnessFunction(newGraph, countChromon, chromonSize);
		// stores data of individual
		individual.setChromonNo(countChromon);
		individual.setChromonSize(chromonSize);
		individual.setFitness(fitnessVal);
	}

	/**
	 * Recursive method of evaluating an individual
	 * 
	 * @param input
	 * @param newGraph
	 * @param chromonSize
	 * @param individual
	 * @return
	 */
	public static int evalIndividual(Vertex input, GraphStructure newGraph,
			int chromonSize, Individual individual) {
		input.setVisited("VISITED");
		for (int i = 0; i < input.getIncEdges().size(); i++) {
			Edge tempEdge = input.getIncEdges().get(i);
			if (tempEdge.getVisited().equals("UNEXPLORED")) {
				for (int j = 0; j < 2; j++) {
					Vertex tempVertex = tempEdge.getVertices()[j];
					if (!tempVertex.equals(input)) {
						if (tempVertex.getVisited().equals("UNEXPLORED")) {
							tempEdge.setVisited("DISCOVERY");
							if (individual.getColouring().charAt(
									newGraph.getVertexIndex(tempVertex)) == (individual
									.getColouring().charAt(newGraph
									.getVertexIndex(input)))) {
								chromonSize += evalIndividual(tempVertex,
										newGraph, 1, individual);
							}
						}
					}
				}
			}
		}
		return chromonSize;
	}

	/**
	 * Finds best individual in a population
	 * 
	 * @return
	 */
	public static boolean evalBestIndividual() {
		boolean updated = false;
		for (int i = 0; i < popSize; i++) {
			if (evalBestIndividual(Population[i]))
				updated = true;
		}
		return updated;
	}

	/**
	 * Compares two individuals
	 * 
	 * @param individual
	 * @return
	 */
	public static boolean evalBestIndividual(Individual individual) {
		if (bestIndividual == null) {
			bestIndividual = individual;
			return true;
		}
		if (individual.getFitness() > bestIndividual.getFitness()) {
			bestIndividual = individual;
			return true;
		}
		return false;
	}

	/**
	 * Initial method for mutation, checks whether mutation is needed
	 * 
	 * @param newGraph
	 */
	public static void mutation(GraphStructure newGraph) {
		for (int ind = 0; ind < popSize; ind++) {
			Random generator = new Random();
			double probMutation = generator.nextDouble();
			if (probMutation < mutationRate) {
				mutate(newGraph, Population[ind]);
			}
		}
	}

	/**
	 * Method which mutates an individual
	 * 
	 * @param newGraph
	 * @param ind
	 */
	public static void mutate(GraphStructure newGraph, Individual ind) {
		Random generator = new Random();
		int temp = generator.nextInt(newGraph.getVerticeSize());
		flip(ind, temp);
		newGraph.clearVisits();
		smooth(newGraph.getVertex(temp), newGraph, ind);
	}

	/**
	 * Initial crossover method which defines whether crossover is possible in a
	 * population
	 * 
	 * @param newGraph
	 */
	public static void crossOver(GraphStructure newGraph, int gen) {
		int parentA = 0;
		int parentB = 0;
		int numParents = 0;
		Random generator = new Random();
		for (int indNo = 0; indNo < popSize; indNo++) {
			double probXOver = generator.nextDouble();
			if (probXOver < xOverRate) {
				
				if (numParents == 0) {
					parentA = indNo;
				} else {
					parentB = indNo;
				}
				numParents++;
				if (numParents == 2) {
					int randReplace = generator.nextInt(100);
					randReplace = randReplace > 50 ? parentA : parentB;
					Population[randReplace] = crossOver(newGraph,
							Population[parentA], Population[parentB], gen);
					numParents = 0;
				} else if (numParents == 1 && indNo == (popSize - 1)) {
					Population[parentA] = crossOver(newGraph,
							Population[parentA], bestIndividual, gen);
					numParents = 0;
				}
			}
		}
	}

	/**
	 * Crossover method
	 * 
	 * @param newGraph
	 * @param parentA
	 * @param parentB
	 */
	public static Individual crossOver(GraphStructure newGraph,
			Individual parentA, Individual parentB, int gen) {
		double sameColoured = 0;
		// Compares two parents for same coloured vertices
		for (int i = 0; i < newGraph.getVerticeSize(); i++) {
			if (parentA.getColouring().charAt(i) == parentB.getColouring()
					.charAt(i)) {
				sameColoured++;
			}
		}
		/*
		 * FIXME: logic condition?
		 * EDIT: FIXED? 18/06/17
		 * if there is less than 40% of same colours flip parent
		 */
		if (sameColoured / newGraph.getVerticeSize() < 0.40) {
			Random generator = new Random();
			int randParent = generator.nextInt(100);
			for (int i = 0; i < newGraph.getVerticeSize(); i++) {
				if (randParent > 50)
					flip(parentA, i);
				else
					flip(parentB, i);
			}
		}
		Individual child = new Individual("");
		// marking smooth points for smoothing function to run on
		int[] smoothPoints = new int[newGraph.getVerticeSize()];
		
		// Making a child
		for (int i = 0; i < newGraph.getVerticeSize(); i++) {
			if (parentA.getColouring().charAt(i) == parentB.getColouring()
					.charAt(i)) {
				child.setColouring(child.getColouring()
						+ parentA.getColouring().charAt(i));
			} else {
				child.setColouring(child.getColouring() + generateColour());
				smoothPoints[i] = 1;
			}
		}

		newGraph.clearVisits();
		// smoothing out the child
		for (int i = 0; i < smoothPoints.length; i++) {
			if (smoothPoints[i] == 1) {
				smooth(newGraph.getVertex(i), newGraph, child);
			}
		}

		evalIndividual(newGraph, child);
		// child[genIndex] = "" + gen;
		child.setGen(gen);
		return child;
	}

	/**
	 * Smoothing function which goes through the incident edges
	 * 
	 * @param input
	 * @param newGraph
	 * @param ind
	 */
	public static void smooth(Vertex input, GraphStructure newGraph,
			Individual ind) {
		input.setVisited("VISITED");
		for (int i = 0; i < input.getIncEdges().size(); i++) {
			Edge tempEdge = input.getIncEdges().get(i);
			if (tempEdge.getVisited().equals("UNEXPLORED")) {
				for (int j = 0; j < 2; j++) {
					Vertex tempVertex = tempEdge.getVertices()[j];
					if (!tempVertex.equals(input)) {
						if (tempVertex.getVisited().equals("UNEXPLORED")) {
							tempEdge.setVisited("DISCOVERY");
							smoothing(tempVertex, newGraph, ind);
						}
					}
				}
			}
		}
	}

	/**
	 * Second part of smoothing function which checks the neighbours, before
	 * deciding to flip the vertex
	 * 
	 * @param inputVertex
	 * @param inputEdge
	 * @param newGraph
	 * @param ind
	 */
	public static void smoothing(Vertex inputVertex, GraphStructure newGraph,
			Individual ind) {
		int coChromaticNeighbor = 0, nonChromaticNeighbor = 0;
		for (int i = 0; i < inputVertex.getIncEdges().size(); i++) {
			for (int j = 0; j < 2; j++) {
				Vertex neighborVertex = inputVertex.getIncEdges().get(i)
						.getVertices()[j];
				if (!neighborVertex.equals(inputVertex)) {
					if (ind.getColouring().charAt(
							newGraph.getVertexIndex(inputVertex)) == ind
							.getColouring().charAt(
									newGraph.getVertexIndex(neighborVertex))) {
						coChromaticNeighbor++;
					} else {
						nonChromaticNeighbor++;
					}
				}
			}
		}
		if (coChromaticNeighbor > nonChromaticNeighbor) {
			flip(ind, newGraph.getVertexIndex(inputVertex));
			smooth(inputVertex, newGraph, ind);
		}
	}

	/**
	 * Flip method for flipping a vertex colour
	 * 
	 * @param ind
	 * @param vertInd
	 */
	public static void flip(Individual ind, int vertInd) {
		char[] tempColours = ind.getColouring().toCharArray();
		tempColours[vertInd] = tempColours[vertInd] == 'W' ? 'B' : 'W';
		String tempC = "";
		for (char c : tempColours) {
			tempC += c;
		}
		ind.setColouring(tempC);
	}

	/**
	 * Prints out the colouring of the best individual last stored
	 */
	public static void printColouring() {
		if (bestIndividual != null) {
			for (int i = 0; i < bestIndividual.getColouring().length(); i++) {
				System.out.println(i + ": "
						+ bestIndividual.getColouring().charAt(i));
			}
		} else {
			System.err
					.println("Cannot print as no colouring algorithm was performed.");
		}
	}

	/**
	 * Prints out chromon information of the last stored best individual
	 * 
	 * @param newGraph
	 */
	public static void printChromons() {
		if (bestIndividual != null) {
			System.out.println("[Best Individual] \n" + bestIndividual.toString());
		} else {
			System.err
					.println("Cannot print as no colouring algorithm was performed.");
		}
	}

	/**
	 * Clears the graph
	 * 
	 * @param newGraph
	 * @return
	 */
	public static GraphStructure clearGraph(GraphStructure newGraph) {
		return new GraphStructure();
	}

	public static GraphStructure generateRandomGraph(int noVertices,
			GraphStructure newGraph) {

		try {
			PrintWriter output = new PrintWriter(new FileWriter("test.txt"));

			while (!isRegularGraph(newGraph)) {

				newGraph = new GraphStructure(noVertices);
				output.println(noVertices);
				output.println(2 * noVertices);
				LinkedList<Integer> possibleVPts = new LinkedList<Integer>();

				for (int i = 0; i < noVertices; i++) {
					for (int j = 0; j < regularSize; j++) {
						possibleVPts.add(new Integer(i));
					}
				}

				while (isSuitable(possibleVPts, newGraph)) {
					Random generator = new Random();
					int randVal1 = generator.nextInt(possibleVPts.size());
					int randVal2 = generator.nextInt(possibleVPts.size());

					int vert1 = possibleVPts.get(randVal1);
					int vert2 = possibleVPts.get(randVal2);

					if ((randVal1 != randVal2) && (vert1 != vert2)
							&& (!hasDuplicateEdge(newGraph, vert1, vert2))
							&& (!isLargerThanRegular(newGraph, vert1, vert2))) {

						possibleVPts.remove((Integer) vert1);
						possibleVPts.remove((Integer) vert2);

						output.println(vert1 + " " + vert2);

						Vertex[] verts = { newGraph.getVertex(vert1),
								newGraph.getVertex(vert2) };

						Edge tempEdge = new Edge(verts);

						newGraph.addEdge(tempEdge);

						newGraph.getVertex(vert1).getIncEdges().add(tempEdge);
						newGraph.getVertex(vert2).getIncEdges().add(tempEdge);
					}
				}
			}
			output.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return newGraph;
	}

	public static boolean isSuitable(LinkedList<Integer> possibleVPts,
			GraphStructure newGraph) {
		boolean checkSuit = false;
		if (possibleVPts.size() <= regularSize && possibleVPts.size() > 0) {
			for (Integer i : possibleVPts) {
				int checkCount = 0;
				for (Integer j : possibleVPts) {
					if ((int) i == (int) j) {
						checkCount++;
					}
					if ((double) checkCount > (double) (possibleVPts.size() / 2)) {
						return false;
					}
				}
			}
			for (Integer i : possibleVPts) {
				for (Integer j : possibleVPts) {
					if (i != j && !hasDuplicateEdge(newGraph, i, j)
							&& !isLargerThanRegular(newGraph, i, j))
						checkSuit = true;
				}
			}
		} else if (possibleVPts.size() == 0) {
			return false;
		} else {
			return true;
		}
		return checkSuit;
	}

	public static boolean isRegularGraph(GraphStructure newGraph) {
		for (Vertex v : newGraph.getVertices()) {
			if (v.getIncEdges().size() != regularSize) {
				return false;
			}
		}
		return true;
	}

	public static boolean isLargerThanRegular(GraphStructure newGraph,
			int vert1, int vert2) {
		return newGraph.getVertex(vert1).getIncEdges().size() + 1 > regularSize
				|| newGraph.getVertex(vert2).getIncEdges().size() + 1 > regularSize;
	}

	public static boolean hasDuplicateEdge(GraphStructure newGraph, int vert1,
			int vert2) {
		if (newGraph.getEdgeSize() > 0) {
			for (Edge e : newGraph.getEdges()) {
				Vertex[] check = e.getVertices();
				if ((check[0].equals(newGraph.getVertex(vert1)) && check[1]
						.equals(newGraph.getVertex(vert2)))
						|| (check[0].equals(newGraph.getVertex(vert2)) && check[1]
								.equals(newGraph.getVertex(vert1)))) {
					return true;
				}
			}
		}
		return false;
	}

	public static void runTest(int n, GraphStructure newGraph, boolean isGA) {
		long worstTime = 0, totalTime = 0, maxMaxChromonSize = 0;
		double avgMaxChromonSize = 0, maxAvgChromonSize = 0, avgAvgChromonSize = 0;
		int loops = 10;
		for (int i = 0; i < loops; i++) {

			newGraph = generateRandomGraph(n, new GraphStructure(n));

			Date recordTime = new Date();
			long startTime = recordTime.getTime();
			if(isGA)
				findColouringGA(newGraph, true, false);
			else
				findColouringNonGA(newGraph, false);
			// printChromons();
			Date recordEndTime = new Date();
			long endTime = recordEndTime.getTime();

			avgMaxChromonSize += bestIndividual.getChromonSize();
			avgAvgChromonSize += (double) n
					/ (double) bestIndividual.getChromonNo();

			if (bestIndividual.getChromonSize() > maxMaxChromonSize)
				maxMaxChromonSize = bestIndividual.getChromonSize();

			if ((double) n / (double) bestIndividual.getChromonNo() > maxAvgChromonSize) {
				maxAvgChromonSize = (double) n
						/ (double) bestIndividual.getChromonNo();
			}

			totalTime += (endTime - startTime);

			if (i == 0) {
				worstTime = endTime - startTime;
			} else if ((endTime - startTime) > worstTime) {
				worstTime = endTime - startTime;
			}
		}
		
		avgMaxChromonSize /= (double) loops;
		avgAvgChromonSize /= (double) loops;
		System.out.println("Average Time(milliseconds): " + totalTime / loops);
		System.out.println("Worst Time(milliseconds): " + worstTime);
		System.out.println("Max max chromon size: " + maxMaxChromonSize);
		System.out.println("Average max chromon size: " + avgMaxChromonSize);
		System.out.println("Max average chromon size: " + maxAvgChromonSize);
		System.out.println("Average average chromon size: " + avgAvgChromonSize
				+ "\n");
	}

	public static void runAllTests(GraphStructure newGraph) {
		int[] sizeN = { 20, 50, 100, 200, 300, 400, 500 };
		for (int n : sizeN) {
			System.out.println("Test run on GA of size: " + n);
			runTest(n, newGraph, true);
			System.out.println("Test run on nonGA of size: " + n);
			runTest(n, newGraph, false);
		}
	}

	public static void findColouringNonGA(GraphStructure newGraph, boolean print) {
		if (!(newGraph.getVerticeSize() > 0)) {
			System.err.println("No graph exists.");
			return;
		}
		String colouring = "";
		for (int i = 0; i < newGraph.getVerticeSize(); i++) {
			colouring += generateColour();
		}
		bestIndividual = new Individual(colouring);
		for (Vertex v : newGraph.getVertices()) {
			newGraph.clearVisits();
			smooth(v, newGraph, bestIndividual);
		}
		evalIndividual(newGraph, bestIndividual);
		if(print){
			printColouring();
			printChromons();
		}
	}

}
