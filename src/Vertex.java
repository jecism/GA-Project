import java.util.LinkedList;


public class Vertex {
	
	private LinkedList<Edge> incidentEdges;
	private String visited;
	
	public Vertex(LinkedList<Edge> incEdge) {
		incidentEdges = incEdge;
		visited = "";
	}
	
	public LinkedList<Edge> getIncEdges() {
		return incidentEdges;
	}
	
	public String getVisited() {
		return visited;
	}
	
	public void setVisited(String input){
		visited = input;
	}
}
