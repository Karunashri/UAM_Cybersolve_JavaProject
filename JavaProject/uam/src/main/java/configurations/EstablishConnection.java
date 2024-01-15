package configurations;

import java.sql.Connection;
import java.sql.DriverManager;

public class EstablishConnection {
	
	public  Connection getConnection()
	{
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			String url = "jdbc:mysql://localhost:3306/uamproject";
			String username = "root";
			String password = "cybersolve@123";
			Connection con = DriverManager.getConnection(url, username, password);
			System.out.println("connection establised");
			return con;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}


}
