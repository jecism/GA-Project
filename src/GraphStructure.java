import java.util.LinkedList;


/**
 * Graph structure is an adjacency list 
 * @author JeC
 *
 */
public class GraphStructure {

	private static LinkedList<Edge> Edges;
	private static LinkedList<Vertex> Vertices;
	
	public GraphStructure(){
		Edges = new LinkedList<Edge>();
		Vertices = new LinkedList<Vertex>();
	}
	
	public GraphStructure(int noVerts){
		Vertices = new LinkedList<Vertex>();
		for (int i = 0; i < noVerts; i++) {
			this.addVertex();
		}
		Edges = new LinkedList<Edge>();
	}
	
	public LinkedList<Edge> getEdges() {
		return Edges;
	}
	public LinkedList<Vertex> getVertices() {
		return Vertices;
	}
	
	public boolean addEdge(Edge newEdge){
		return Edges.add(newEdge);
	}
	
	public boolean addVertex(){
		return Vertices.add(new Vertex(new LinkedList<Edge>()));
	}
	
	public Vertex getVertex(int index){
		return Vertices.get(index);
	}
	
	public Edge getEdge(int index){
		return Edges.get(index);
	}
	
	public int getVerticeSize(){
		return Vertices.size();
	}
	
	public int getEdgeSize(){
		return Edges.size();
	}
	
	public int getVertexIndex(Vertex tempVertex){
		return Vertices.indexOf(tempVertex);
	}
	
	public int getEdgeIndex(Edge tempEdge){
		return Edges.indexOf(tempEdge);
	}
	
	public void clearVisits(){
		for (int i = 0; i < getVerticeSize(); i++) {
			getVertex(i).setVisited("UNEXPLORED");
		}
		for (int i = 0; i < getEdgeSize(); i++) {
			getEdge(i).setVisited("UNEXPLORED");
		}
	}
	
}
