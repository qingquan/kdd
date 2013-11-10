package kddProject2;

	import java.sql.Connection;
	import java.sql.DriverManager;
	import java.sql.ResultSet;
	import java.sql.SQLException;
	import java.sql.Statement;

	public class TestCon{
		Connection connection = null;
//		String driverName ="oracle.jdbc.driver.OracleDriver"; // for Oracle
		String driverName = "com.mysql.jdbc.Driver"; //for MySql
		String serverName = "127.0.0.1";
		String portNumber = "3306";
		String sid = "dbkdd";
//		String url = "jdbc:oracle:thin:@" + serverName + ":" + portNumber + ":" + sid; // for Oracle
		String url ="jdbc:mysql://" + serverName + ":" + portNumber + "/" + sid;//for Mysql
		//uri =”jdbc:mysql://server ip or address:port/database name”; //for Mysql


		String username = "root"; //your username
		String password = "123456"; //your password
		
		public TestCon()
		{
		}
		
		public Connection doConnection(){
			try {
				// Load the JDBC driver
				Class.forName(driverName);
				// Create a connection to the database
				connection = DriverManager.getConnection(url, username, password);
			} catch (ClassNotFoundException e) {
				// Could not find the database driver
				System.out.println("ClassNotFoundException : "+e.getMessage());
				return null;
			} catch (SQLException e) {
				// Could not connect to the database
				System.out.println(e.getMessage());
				return null;
			}
			return connection;
		}
		

		
		public static void main(String arg[]){
			TestCon con =new TestCon();
			Connection dbCon = con.doConnection();
			if(dbCon != null)
				System.out.println("successfully connect to db");

//			System.out.println("Connection : " +con.doConnection());
//			try{
//				con.printCounryByCapital("Paris");
//				con.printCityByCountry("D");
//				con.updateCityPopulation("Munich","Bayern","3000");
//			}catch(SQLException ex){System.out.println(ex.getMessage());}
		}
	}


