package kddProject2;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.io.*;

import com.google.common.collect.Sets;

public class PreProcess {
	private int limitNum = 80;
	private ArrayList<String> userList = new ArrayList<String>();
	private Map<String, ArrayList> dictMap = new HashMap<String, ArrayList>();
//	public void printContributor(Connection con) throws SQLException{
//		Statement stmt = null;
//		String query = "SELECT * FROM contributors LIMIT 10";
//		stmt = con.createStatement();
//		ResultSet rs = stmt.executeQuery(query);
//		
//		while (rs.next()) {
//			String loginName = rs.getString(1); // or rs.getString("NAME");
//			String repoName= rs.getString(2);
//			String contributorName = rs.getString(3);
//			int score = rs.getInt(4);
//			System.out.println(" login name : "+loginName);
//			System.out.println(" repo name : "+repoName);
//			System.out.println(" contributor : "+contributorName);
//			System.out.println(" score : "+score);
//		}
//	}
	
//	String query = 
//			SELECT * FROM
//			    (SELECT repo_name, contributor_login 
//			     FROM contributors LIMIT 80) t 
//			WHERE contributor_login="klaussilveira";
	
	
	public Map<String, ArrayList> getUserRepoDict(Connection con) throws SQLException{
		Statement stmt = null;
		String queryUser = "SELECT DISTINCT contributor_login FROM " + 
							    "(SELECT repo_name, contributor_login "+
						        "FROM contributors LIMIT " + limitNum + " ) t ";
		
		stmt = con.createStatement();
		ResultSet rs = stmt.executeQuery(queryUser);
		
		//construct user list
//		ArrayList<String> users = new ArrayList<String>();
		while (rs.next()) {
			String userName = rs.getString(1); // or rs.getString("NAME");
			userList.add(userName);
//			System.out.println(" user name : "+userName);
		}
		//construct dict <user, <repo1,repo2>>
//		Map<String, ArrayList> dictMap = new HashMap<String, ArrayList>();
		for(String s: userList){
			ArrayList<String> repoList = new ArrayList<String>();
			String query = 
					"SELECT repo_name FROM " + 
					    "(SELECT repo_name, contributor_login "+
				        "FROM contributors LIMIT " + limitNum + " ) t " + 
				    "WHERE contributor_login= '" + s + "'";
			ResultSet rsRepo= stmt.executeQuery(query);
			while(rsRepo.next()){
				String repoName = rsRepo.getString(1);
				repoList.add(repoName);
//				System.out.println("user name "+s+" repo "+repoName);
			}
			dictMap.put(s, repoList);
		}
		//check the dict is correct
//	    Iterator it = dictMap.entrySet().iterator();
//	    while (it.hasNext()) {
//	        Map.Entry pairs = (Map.Entry)it.next();
//	        System.out.println(pairs.getKey() + " = " + pairs.getValue());
//	    }
	    return dictMap;
	}

	public void getRelationWeight(ArrayList<String> users, Map<String, ArrayList> dictMap){
//	    Iterator it = dictMap.entrySet().iterator();
//	    while (it.hasNext()) {
//	        Map.Entry pairs = (Map.Entry)it.next();
//	        System.out.println(pairs.getKey() + " = " + pairs.getValue());
//	    }
	    Map<String, Map> relationMap = new HashMap<String, Map>();
	    for(String s: users){
	    	ArrayList repoList1 = dictMap.get(s);
	    	Set<String> repoSet1 = new HashSet<String>(repoList1);
	    	int numRepo1 = repoList1.size();
	    	
			Map<String, Float> relation = new HashMap<String, Float>();

	    	for(String os: users){
	    		if(!os.equals(s)){
	    			ArrayList repoList2 = dictMap.get(os);
	    			int numRepo2 = repoList2.size();
	    			
	    			Set<String> repoSet2 = new HashSet<String>(repoList2);
	    			int numIntersect = Sets.intersection(repoSet1, repoSet2).size();
	    			
	    			float weight = numIntersect/(numRepo1+numRepo2);
	    			relation.put(os, weight);
	    		}
	    	}
	    	
	    	relationMap.put(s, relation);
	    }
		
	}
//	public void printCityByCountry(String country) throws SQLException{
//		Statement stmt = null;
//		String query = " SELECT * FROM CITY WHERE country='"+country+"'";
//		stmt = connection.createStatement();
//		ResultSet rs = stmt.executeQuery(query);
//		while (rs.next()) {
//			String name = rs.getString(1); // or rs.getString("NAME");
//			String coun= rs.getString(2);
//			String province = rs.getString(3);
//			int population = rs.getInt(4);
//			System.out.println(" Name : "+name);
//			System.out.println(" Country : "+coun);
//			System.out.println(" Province : "+province);
//			System.out.println(" Population : "+population);
//		}
//		stmt.close();
//	}
//
//	public void updateCityPopulation(String cityName,String province, String population)throws SQLException
//	{
//		Statement stmt = null;
//		stmt = connection.createStatement();
//		String sql = "UPDATE CITY SET population='"+ population
//		+"' WHERE NAME='"+cityName +"' AND
//		PROVINCE='"+ province +"'";
//		stmt.executeUpdate(sql);
//		stmt.close();
//	}
	
	public static void main(String arg[]){
		TestCon con =new TestCon();
		Connection dbCon = con.doConnection();
		if(dbCon != null)
			System.out.println("successfully connect to db");
		
		PreProcess myPreProcess = new PreProcess();
		try{
			myPreProcess.getUserRepoDict(dbCon);
		}catch(SQLException ex){System.out.println(ex.getMessage());}
//		System.out.println("Connection : " +con.doConnection());
//		try{
//			con.printCounr yByCapital("Paris");
//			con.printCityByCountry("D");
//			con.updateCityPopulation("Munich","Bayern","3000");
//		}catch(SQLException ex){System.out.println(ex.getMessage());}
	}

}
