package kddProject2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class User {
	private String name;
    private ArrayList<String> repoList = new ArrayList<String>();//repos he/she committed
    private Map<String, Float> relationDict = new HashMap<String, Float>();//other users and relation weight	 
	
    public void setUserName(String myName){
		name = myName;
	}
	
	public String getUserName(){
		return name;
	}
	
	public void setRepoList(ArrayList<String> repos){
		repoList = repos;
	}
	
	public ArrayList<String> getRepoList(){
		return repoList;
	}
	
	public void setRelationDict(Map<String, Float> relation){
		relationDict = relation;
	}
	
	public Map<String, Float> getRelationDict(){
		return relationDict;
	}
}
