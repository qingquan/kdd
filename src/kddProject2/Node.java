package kddProject2;

import java.util.*;

class Node {
	int 	name;	//node ID, started from 0 to n-1
	Vector<Integer> 	preds;	//predecessors (String)
	Vector<Integer>		neibs;	//neighbors (String)
	Vector<Integer> 	backs;	//backward edges -node is end vertex (Integer)
	Vector<Integer> 	fors;	//forward edges -node is start vertex (Integer)
	int	pNode;  // previous node on the augmenting path
	int	pEdge;  //from which edge this node comes on the augmenting path
	
	public Node (int id){
	  name  = id;
	  backs = new Vector<Integer> ();
	  fors = new Vector<Integer> ();
	  pNode = -1;
	  pEdge = -1;
	} 
}
