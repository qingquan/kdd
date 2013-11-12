package kddProject2;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.io.*;
import java.math.*;


import com.google.common.collect.Sets;

public class MinDiamSol{
	
	public static void main(String arg[]){
		//connect to database
		TestCon con =new TestCon();
		Connection dbCon = con.doConnection();
		if(dbCon != null)
			System.out.println("successfully connect to db");
		
		//Preprocess
		PreProcess myPreProcess = new PreProcess();		
		try{
			myPreProcess.getUserRepoDict(dbCon);
		}catch(SQLException ex){System.out.println(ex.getMessage());}
		try{
			myPreProcess.getTaskItemMapping(dbCon);
		}catch(SQLException ex){System.out.println(ex.getMessage());}
		
		ArrayList<String> users = myPreProcess.getUserList();
		ArrayList<String> repos = myPreProcess.getRepoList();
		Map<String, ArrayList> dictMap = myPreProcess.getUserRepoMap();
		Map<String, Map> relationMap = myPreProcess.getRelationWeight(users, dictMap);
		Map<String, ArrayList> itemMap = myPreProcess.getUserItemMap();
		Map<String, ArrayList> task_item_Map = myPreProcess.getTaskItemMap();
		
		Map<String, ArrayList> friendMap = myPreProcess.getUserFriendMap();

		//{user, capacity}
		Map<String, Double> capacityMap = new HashMap<String, Double>();
		for(String u: users){
			int numOfRepo =((dictMap.get(u)).size());
			double cap = Math.log(1+numOfRepo);
			capacityMap.put(u, cap);
		}
		
		
		ArrayList<String> itemsRequired = new ArrayList();
		ArrayList<String> Task = new ArrayList();
		Map<String, Float> group = new HashMap<String, Float>();
		ArrayList<String> groupMem = new ArrayList();
		ArrayList<String> groupMem2 = new ArrayList();
		ArrayList<String> groupMem3 = new ArrayList();
		int k;
		int hop = 2;
		/*for(String v: users){
			Task = dictMap.get(v);//Task 找v之前參加过的project 
			for(String t: Task){
				itemsRequired = task_item_Map.get(t);//这个Task需要的items
				k = itemsRequired.size();
				//begin
				groupMem = friendMap.get(v);
				for()
				//end
			}
									
		}*/

		
		String v = "crodjer";
		Task.add("django-social-auth");
		//System.out.println(Task.get(0));
		//System.out.println(task_item_Map);
		//找到指定的Task需要的所有languages(items)，放进itemsRequired里面
		for(String t: Task){
			itemsRequired.addAll(task_item_Map.get(t));//这个Task需要的items
		}
		k = itemsRequired.size();
		System.out.println(k);
		
		//friend_of_v是用户v的hop-1 uer的名单（这个放在exclude hop=2的用户之前做，因为get可能改变hashmap的值）
		ArrayList<String> friend_of_v = new ArrayList<String>();
		friend_of_v = friendMap.get(v);
		//System.out.print(friendMap.get("crodjer").size());
		//System.out.println(friend_of_v);
		
		//hop = 2 先找出hop=2以內的所有用戶，放进groupMem里面
		groupMem = friendMap.get(v);
		for (String gm1: groupMem){
			ArrayList groupMemTmp = friendMap.get(gm1);
			groupMem2.removeAll(groupMemTmp);//去掉重复元素
			groupMem2.addAll(groupMemTmp);
		}
		groupMem.removeAll(groupMem2);
		groupMem.addAll(groupMem2);
		groupMem.remove(v);
		System.out.println(groupMem);
		System.out.println(groupMem.size());
		
		//relation 存放用户v的relationMap（对于v，所有其他用户及其weight）,即{user, {user, relationweight}}后半部分
		Map<String, Float> relation = new HashMap<String, Float>();
		relation = relationMap.get(v);
		//relation_cost,存放w(u,v),用于Greedy算法选择最优的u
		Map<String, Float> relation_cost = new HashMap<String, Float>();
		float weight;
		for(String u: groupMem){
			float weight_uv;
			weight_uv = 100;
			for(String hop1: friend_of_v){
				if (u == hop1){					
					weight_uv = 100*(1-(relation.get(u)));
				}
			}
			relation_cost.put(u, weight_uv);							
		}		
		//System.out.println(relation_cost);
		
		//sort "relation_cost" by value
		List<Map.Entry<String, Float>> relationSorted =
		    new ArrayList<Map.Entry<String, Float>>(relation_cost.entrySet());
		Collections.sort(relationSorted, new Comparator<Map.Entry<String, Float>>() {   
		    public int compare(Map.Entry<String, Float> o1, Map.Entry<String, Float> o2) {      
		    	if ((o2.getValue() - o1.getValue())<0)  
		            return 1;  
		          else if((o2.getValue() - o1.getValue())==0)  
		            return 0;  
		          else   
		            return -1;  
		        //return (o1.getKey()).toString().compareTo(o2.getKey());
		    }
		}); 
		//System.out.println(relationSorted);

		//Greedy, find the final group
		//Map<String, ArrayList> finalGroup = new HashMap<String, ArrayList>();
		//ArrayList<Map> finalGroupMemberList = new ArrayList<Map>();
		//先把v加进他自己的finalGroupMemberList里面
		//Map<String, Double> finalGroupMembertmp = new HashMap<String, Double>();
		//String member1 = v;
		//Double capability1 = capacityMap.get(member1);
		//finalGroupMembertmp.put(member1, capability1);
		//finalGroupMemberList.add(finalGroupMembertmp);
		//System.out.println(finalGroupMemberList);
		
		//边集合。首先添加与v相关的边。
		ArrayList<Map> EdgeInMid = new ArrayList<Map>();
		ArrayList<String> itemsOfv = itemMap.get(v);
		for (String item_r : itemsRequired){
			for(String item_v: itemsOfv){
				if (item_r.equals(item_v)){
					Map<String,String> tmp = new HashMap<String, String>();
					tmp.put(item_r, v);
					EdgeInMid.add(tmp);
				}
			}
		}
		System.out.println(EdgeInMid);
		
		//按顺序一个个的把user加进来
		ArrayList<String> finalGroupMem = new ArrayList();
		finalGroupMem.add(v);
		for(int i=0; i<relationSorted.size();i++){
			
						
			//写个txt让Maxflow读一下	
			
	        FileWriter writer;
	        try {
	            writer = new FileWriter("files/maxflow.txt");
	            int num_node = finalGroupMem.size()+itemsRequired.size()+2;
				int num_edge = finalGroupMem.size()+itemsRequired.size()+EdgeInMid.size();
				String str="";
				str += num_node;
				str += " ";
				str += num_edge;
				str +="\r\n";				
	            writer.write(str);
	      
	            //Task 编号，从1到k,一共k个item
	            Map<String, Integer> itemMapMaxflow = new HashMap<String, Integer>();
	            int count = 1;
	            for(String item_r: itemsRequired){
	            	itemMapMaxflow.put(item_r, count);
	            	count++;
	            }
	            System.out.println(itemMapMaxflow);
	            //User 编号，从k+1到n-2
	            Map<String, Integer> userMapMaxflow = new HashMap<String, Integer>();
	            count = itemsRequired.size()+1;
	            for(String user_r: finalGroupMem){
	            	userMapMaxflow.put(user_r, count);
	            	count++;
	            }
	            System.out.println(userMapMaxflow);
	            //source到Task的点们      
	            String str1 = "";
	            for(String item_r: itemsRequired){
	            	str1 += "0 ";
	            	str1 += itemMapMaxflow.get(item_r);
	            	str1 += " 1";
	            	str1 += "\r\n";
	            }
	            writer.write(str1);
	            //Users到sink的点们   
	            String str2 = "";
	            for(String user_r: finalGroupMem){
	            	str2 += userMapMaxflow.get(user_r);
	            	str2 += " ";
	            	str2 += num_node-1;
	            	str2 += " ";
	            	double cap = Math.ceil(capacityMap.get(user_r));
	            	str2 += (int)cap;
	            	str2 += "\r\n";
	            }
	            writer.write(str2);
	            //Task 到Users的点们
	            String str3 ="";
	            for (String item_r: itemsRequired){
	            	for(String user_r: finalGroupMem){
	            		ArrayList<String> user_items = itemMap.get(user_r);
	            		for(String item_u: user_items){
	            			if(item_r.equals(item_u)){
	            				str3 += itemMapMaxflow.get(item_r);
	            				str3 += " ";
	            				str3 += userMapMaxflow.get(user_r);
	            				str3 += " 1";
	            				str3 += "\r\n";
	            				break;
	            			}
	            		}
	            		break;
	            	}	            		
	            }
	            writer.write(str3);	            
	            writer.flush();
	            writer.close();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }//txt done.
	        Maxflow mf = new Maxflow();
	        mf.runMF();
	        System.out.println("maxflow=" + mf.maxFlow);
	        if(k==mf.maxFlow)
	        	break;
	        
	        //不满足maxflow，加入新的user和它的capability
	        Map<String, Double> finalGroupMember = new HashMap<String, Double>();
			String member = relationSorted.get(i).getKey();
			Double capability = capacityMap.get(member);
			finalGroupMember.put(member, capability);
			//finalGroupMemberList.add(finalGroupMember);
			finalGroupMem.add(member);
			
			//此时maxflow中，Users和Task之间的边的集合
			ArrayList<String> itemsOfu = itemMap.get(member);
			for (String item_r: itemsRequired){
				for(String item_u: itemsOfu){
					if (item_r.equals(item_u)){
						Map<String,String> tmp = new HashMap<String, String>();
						tmp.put(item_r, member);
						EdgeInMid.add(tmp);
					}
				}
			}
			
	        
		}
		System.out.println(finalGroupMem);
		//finalGroup.put(v, finalGroupMemberList);
		/*System.out.println(relationSorted);
		System.out.println(relationSorted.get(1));
		System.out.println(relationSorted.get(1).getValue());
		System.out.println(finalGroupMemberList);
		System.out.println(finalGroupMemberList.get(1));*/

	}
	
	
}




/*
ArrayList<String> userList = new ArrayList<String>(); 
ArrayList<String> userCapability = new ArrayList<String>(); 
int hop = 1;

try {
	FileReader read = new FileReader("files/userRepos.txt");
	BufferedReader br = new BufferedReader(read);
	String row;
	while((row = br.readLine())!=null){
	    //System.out.println(row);
		String[] tmpuser = row.split(" ");
		userList.add(tmpuser[0]);
		double tmpcap = Double.valueOf(tmpuser[1]).doubleValue();;
		String cap = String.valueOf(Math.log(1+tmpcap));
		userCapability.add(cap);
		br.skip(1);
	}
} catch (FileNotFoundException e) {
	   e.printStackTrace();
} catch (IOException e){
	   e.printStackTrace();
}
System.out.println(userList);
System.out.println(userCapability);*/
