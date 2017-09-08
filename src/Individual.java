public class Individual {

	private String colouring;
	private int chromonNo, chromonSize, generation;
	private double fitness;

	public Individual(String newColour) {
		colouring = newColour;
	}

	public double getFitness() {
		return fitness;
	}

	public int getChromonNo() {
		return chromonNo;
	}

	public int getChromonSize() {
		return chromonSize;
	}

	public int getGeneration() {
		return generation;
	}

	public String getColouring() {
		return colouring;
	}

	public void setColouring(String newColour) {
		colouring = newColour;
	}

	public void setChromonNo(int chromonCount) {
		chromonNo = chromonCount;
	}

	public void setChromonSize(int largestSize) {
		chromonSize = largestSize;
	}

	public void setFitness(double newFitness) {
		fitness = newFitness;
	}

	public void setGen(int gen) {
		generation = gen;
	}

	public String toString() {
		return "Number of Chromons: " + this.getChromonNo() + "\n"
				+ "Size of Largest Chromon: " + this.getChromonSize() + "\n"
				+ "Fitness Value: " + this.getFitness() + "\n"
				+ "Generation: " + this.getGeneration() + "\n";
	}
}
