package kddProject2;

import java.util.*;
import java.io.*;

public class Maxflow {
  int 		n;	    //number of nodes 
  public int 	maxFlow;	//maximum flow 
  int	source,sink;	//source node, sink node
  Node[]	v;	    //used to store Nodes 
  Edge[]	e;	    //used to store Edges
  

  public Maxflow (){
  }

  //this function read capacities from a file
  private void inputCapacity() throws IOException {
    BufferedReader br = new BufferedReader (
			   new FileReader ("files/maxflow.txt"));
    String line = br.readLine();
    StringTokenizer st = new StringTokenizer (line);
    //1st token is the # of nodes 6
    n = Integer.parseInt (st.nextToken());
    
    // first vertex is source and last vertex is sink=n-1.
    source = 0; sink = n-1;
    
    //2nd token is the # of edges 12
    int m = Integer.parseInt (st.nextToken());
    
    //create storage for Nodes and Edges
    v = new Node[n];
    e = new Edge[m];
    //System.out.println(n+" nodes and " + m + " edges.");
    
    //create Node objects for graph
    //numbering them as 0, 1, 2, ..., n-1
    for (int i = 0; i < n; i++)
        v[i] = new Node(i);   
    
    int i = 0;
    while ((line=br.readLine()) != null){
      //check input file
      if (i >= e.length){
        System.out.println ("# of lines is greater than # of edges in G, exit...");
        System.exit(1);
      }
      
      Edge edge = new Edge(i);
      st = new StringTokenizer (line);
      
      //first token is the start point
      String  start = st.nextToken();
      int sVal = Integer.parseInt (start);
      edge.start = sVal;
      
      //2nd token is the end point
      String  end = st.nextToken();
      int eVal = Integer.parseInt (end);
      edge.end = eVal;
      
      //3rd token is the capacity
      String  capacity = st.nextToken();
      edge.capacity = Integer.parseInt (capacity);
      
      //System.out.println(" edge: "+edge.start+" "+edge.end+" "+edge.capacity);
      
      edge.flow = 0;
      e[i] = edge;
      //map[sVal][eVal] = i;
        
      //now save edge information in nodes start and end
      v[sVal].fors.add(i);
      v[eVal].backs.add(i);
      
      i++;
      if (i == m) break; //i=12
    }
  }
  
  //this function looks for an augmenting path if any.
  //An augmenting path contains only forward edges with flow below capacity
  //and/or backward edges with a flow greater than 0
  private int[] findAugPath(){
    //used to mark a node as visited if set as 1
    int[] visited = new int[v.length];
    
    //start from source, walk to the sink
    Queue<Integer> que = new LinkedList<Integer>();
    
    //start from source, do Breadth-First-Search for augmenting paths
    que.add(source);
    visited[source] = 1;
    int current = -1;
    
    while (!que.isEmpty()){
      current = que.remove();
      
      //check if we reach sink, if yes, Bingo! 
      if (current == sink)
	    break;

      //get current's edges
      Vector<Integer> fors = v[current].fors;
      Vector<Integer> backs = v[current].backs;

      //select a forward edge if it is not saturated
      for (int i = 0; i < fors.size(); i++){
        Integer edge_obj = (Integer)fors.elementAt(i);
        int edge = edge_obj.intValue();
        
        //skip those saturated
        if (e[edge].capacity == e[edge].flow)
          continue;
          
        //at this point, edge.capacity > edge.flow
	    if (visited[e[edge].end] == 0){//not yet visited
	      que.add(e[edge].end);
	      visited[e[edge].end] = 1;
	      v[e[edge].end].pNode = current;
	      v[e[edge].end].pEdge = edge;
	      e[edge].direct = 1;
	    }
      }
    
      //select a backward edge if its flow > 0
      for (int i = 0; i < backs.size(); i++){
        Integer edge_obj = (Integer)backs.elementAt(i);
        int edge = edge_obj.intValue();
        
        //skip this edge if its flow is 0
        if (e[edge].flow == 0)
          continue;
          
        //at this point, its flow must be >0        
	    if (visited[e[edge].start] == 0){//not yet visited
	      que.add(e[edge].start);
	      e[edge].direct = -1;
	      visited[e[edge].start] = 1;
	      v[e[edge].start].pNode = current;
	      v[e[edge].start].pEdge = edge;
	    }
      }
    }

    //check if we find an augmenting path, if yes, do more work
    //if not return null
    if (current != sink)
      return null;
    
    //store augmenting path found, in the worst case contain all nodes 
    int[] revPath = new int[v.length];//path reversed
    int delta = Integer.MAX_VALUE; //augmenting value on this path
    
    //now walk back along this path to update flow
    int i = 0;//index of path[]
    current = sink;
    int pEdge, diff;
    while (current != source){
      revPath[i] = current;
      pEdge = v[current].pEdge;
      
      if (e[pEdge].direct == 1){//forward
        //error check
        if (e[pEdge].end != current){
          System.out.println ("Edge trace error, exit1...");
          System.exit(1);
        }
        
        //calculate possible augmenting value on this edge
        diff = e[pEdge].capacity - e[pEdge].flow;
        if (diff > 0 && diff < delta)
          delta = diff;
        
        //go to next stop  
        current = e[pEdge].start;
      }
      else if (e[pEdge].direct == -1){
        //error check
        if (e[pEdge].start != current){
          System.out.println ("Edge trace error, exit2...");
          System.exit(1);
        }
        
        //possible augmenting value on this edge is its flow
        //i.e. the maximum amount of flow we could push back
      	if (e[pEdge].flow > 0 && e[pEdge].flow < delta)
      	  delta = e[pEdge].flow;
        
        //go to next stop       	  
      	current = e[pEdge].end;
      }	
      
      i++;//advance the index
    }
    revPath[i] = source;//don't forget the last one --- the source

    //now walk backward again to update flow on the way
    int[] path = new int[i+1];//we have i+1 nodes on this path
    int k;
    for (int j = 0; j < i; j++){
      k = v[revPath[j]].pEdge; 
      path[i-j] = revPath[j];//save in forward order
      
      if (e[k].direct == 1){//forwards
        e[k].flow += delta;
        e[k].direct = 0;//set back to default
      }
      else if (e[k].direct == -1){//backwards
        e[k].flow -= delta;
        e[k].direct = 0;//set back to default
      }
    }
    
    //output the augmenting path found
    System.out.println("\nAugmenting path with delta = " + delta);
    int m = 0;
    for (; m < path.length - 1; m++)
      System.out.print(path[m] + " --> ");
    System.out.println(path[m]);
    
    //update maxFlow
    maxFlow += delta;

    return path;
  }
  
  
  //look for the cut vertices R or R-bar whichever smaller
  //start to walk from source and save nodes visited until 
  //we can't go further (when all forward edges are saturated
  //and all backward edges have a flow of 0.
  private int[] cutVertices(){
    int k = 0, current;
    int[] temp = new int[v.length];
    
    //used to mark a node as visited if set as 1
    int[] visited = new int[v.length];
    
    Queue<Integer> que = new LinkedList<Integer>();
    que.add (source);
    visited[source] = 1;
    
    while (!que.isEmpty()){
      current = que.remove();
      temp[k] = current;
      k++;
      
      //get current's edges
      Vector<Integer> fors = v[current].fors;
      Vector<Integer> backs = v[current].backs;

      //select a forward edge if it is not saturated
      for (int i = 0; i < fors.size(); i++){
        Integer edge_obj = (Integer)fors.elementAt(i);
        int edge = edge_obj.intValue();
        
        //skip those saturated
        if (e[edge].capacity == e[edge].flow)
          continue;
          
        //at this point, edge.capacity > edge.flow
	    if (visited[e[edge].end] == 0){//not yet visited
	      que.add(e[edge].end);
	      visited[e[edge].end] = 1;
	    }
      }
    
      //select a backward edge if its flow > 0
      for (int i = 0; i < backs.size(); i++){
        Integer edge_obj = (Integer)backs.elementAt(i);
        int edge = edge_obj.intValue();
        
        //skip this edge if its flow is 0
        if (e[edge].flow == 0)
          continue;
          
        //at this point, its flow must be >0        
	    if (visited[e[edge].start] == 0){//not yet visited
	      que.add(e[edge].start);
	      visited[e[edge].start] = 1;
	    }
      }
    }
    
    //find out which is smaller, R or R-bar
    if(k <= v.length / 2){//R is smaller
      int[] R = new int[k];
      for (int i = 0; i < k; i++)
        R[i] = temp[i];
      return R;
    }
    else {//R_bar is smaller
      int[] R_bar = new int[v.length - k];
      int m = 0;
      for (int i = 0; i < n; i++){
        boolean flag = false;
        for (int j = 0; j < temp.length; j++)
          if (i == temp[j])
            flag = true;
            
        if (flag == false){
          R_bar[m] = i;
          m++;
        }
      }
      
      return R_bar;
    }
    
  }
  
  //public access to start the algorithm
  public void runMF (){
    try {
      inputCapacity();
    }
    catch (IOException e){
      System.out.println ("Read file error, exit....");
      System.exit(1);
    }
    
    //looking for augmenting paths and compute max flow
    while (findAugPath() != null)
      ;	//do nothing
    
    
    //get all cut vertices and output
    int[] vertices = cutVertices();
    //System.out.print("\nVertices in cut set A:  ");
    //for (int i = 0; i < vertices.length; i++)
    //  System.out.print (vertices[i]+", ");
   // System.out.println ();    
  }
  
  
  public static void main (String arg[]){
  	Maxflow mf = new Maxflow();
  	mf.runMF();
  	System.out.println ("Maximum flow = " + mf.maxFlow);
  }
}
