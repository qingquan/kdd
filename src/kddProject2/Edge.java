package kddProject2;

class Edge {
	int 	name;	//edge ID, started from 0 to n-1
	int	start;	//start vertex of this edge 
	int	end;	//end vertex of this edge
	int	direct;	//forwards (+1) or backwards (-1) on augmenting path
                        //if 0 then not part of augmenting path
	int	capacity;	// capacity 
	int	flow;		// current flow 
	
	public Edge (int id){
	  name = id;
	  start = -1;
	  end = -1;
	  direct = 0; //default is neither
	  capacity = 0;
	  flow = 0;
	}
	
	//for error check only
	public String toString (){
	  return name + ": s=" + start + " e=" + end + " d=" + direct;
	}
}
