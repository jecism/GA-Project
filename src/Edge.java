
public class Edge {

	//pointers to vertices
	private Vertex[] Vertices;
	private String visited;
	
	public Edge(Vertex[] newVert) {
		Vertices = newVert;
		visited = "";
	}

	public String getVisited(){
		return visited;
	}
	
	public void setVisited(String input){
		visited = input;
	}
	
	public Vertex[] getVertices(){
		return Vertices.clone();
	}
}
