package services;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.FormParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import com.sun.jersey.api.view.Viewable;

import configurations.EstablishConnection;
import models.Request;
import models.Resources;
import models.User;


public class UserServices {

	@Context
	private HttpServletRequest servletRequest;
	private static Connection c; 
	
	public UserServices() {
		EstablishConnection ec = new EstablishConnection();
		c = ec.getConnection();
	}

public String createUsername(String firstname, String lastname)
{
	
	String username = firstname.concat(".").concat(lastname);
	int count =1;
	if(c!=null)
	{
		try {
			PreparedStatement st = c.prepareStatement("select username from users where username like '"+username+"%'");
			ResultSet rs = st.executeQuery();
			while(rs.next())
			{
				if(username.equals(rs.getString(1)))
				{
					username = firstname+"."+lastname+ count++;
				}
			}
			
			
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	return username;	
	
}

	public boolean checkAdmin()
	{
		try {
			PreparedStatement st = c.prepareStatement("select * from users limit 1");
			ResultSet rs = st.executeQuery();
			if(rs.next())
			{
				return false;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
		
	}
	
	public String hashPassword(String password) {
        try {
            // Create a MessageDigest instance for SHA-256
            MessageDigest md = MessageDigest.getInstance("SHA-256");

            // Get the password bytes
            byte[] passwordBytes = password.getBytes(StandardCharsets.UTF_8);

            // Update the message digest with the password bytes
            md.update(passwordBytes);

            // Get the hashed password bytes
            byte[] hashedBytes = md.digest();

            // Convert the hashed bytes to a hexadecimal string
            StringBuilder sb = new StringBuilder();
            for (byte b : hashedBytes) {
                sb.append(String.format("%02x", b));
            }

            // Return the hashed password as a string
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            // Handle the exception or log it
            e.printStackTrace();
            return null;
        }
    }
	
	public User userLogin(String usrnm, String pwd)
	{
		try {
			
			PreparedStatement ps = c.prepareStatement("select * from users where username=?");
			ps.setString(1, usrnm);
			ResultSet rs = ps.executeQuery();
			while(rs.next())
			{
				if(usrnm.equals(rs.getString("username")))
				{
					System.out.println("entered login if");
					String passwor = hashPassword(pwd);
					if(passwor.equals(rs.getString("password")))
					{
//						System.out.println("entered another if");
//						
//						return new User(rs.getLong(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5), rs.getString(6), rs.getDate(7), rs.getString(8));
//						
						User user = new User();
		                user.setId(rs.getLong("userid"));
		                user.setFirstName(rs.getString("firstname"));
		                user.setLastName(rs.getString("lastname"));
		                user.setUsername(rs.getString("username"));
		                user.setPassword(rs.getString("password"));
		                user.setUsertype(rs.getString("user_type"));
		                user.setManager(rs.getString("manager"));

		                return user;
						
					}
					else {
					System.out.println("pass not matched");
					//return "wrong";
					return null;
					}
				}
			}	
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		//return "incorrect values";
		return null;
	}
	
	
	public String getSecurityQuestion(String username) {
		System.out.println("eneterd secquestion");
        EstablishConnection ec = new EstablishConnection();
        Connection connection = ec.getConnection();
        

        if (connection != null) {
            try {
            	System.out.println("got connection here");
                String query = "SELECT security_question from users where username=?";
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, username);
                System.out.println(username);
                System.out.println("query");

                ResultSet resultSet = preparedStatement.executeQuery();
                
                while(resultSet.next()) {
                	
                	System.out.println(resultSet.getString(1));
                    return resultSet.getString(1);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        return null;
    }

	
	public List<String> getAllResources() {
        EstablishConnection ec = new EstablishConnection();
        Connection conn = ec.getConnection();

        if (conn != null) {
            try {
                String query = "select * from resources";
                PreparedStatement preparedStatement = conn.prepareStatement(query);
                ResultSet resultSet = preparedStatement.executeQuery();
                List<String> a1 = new ArrayList<String>();
                while (resultSet.next()) {
                    
                    a1.add(resultSet.getString("resourcename"));
                }
                return a1;
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        return null;
    }
	
	
	public List<String> getMyResources(String user) {
        List<String> resources = new ArrayList<>();
        EstablishConnection ec = new EstablishConnection();
        Connection conn = ec.getConnection();
        if (conn != null) {
        	
            try {
            	System.out.println("kabir");
                String query = "select * from requests where requestor=? and requeststatus=? and requirement not in ('manager','admin')";
                PreparedStatement preparedStatement = conn.prepareStatement(query); 
                preparedStatement.setString(1, user);
                preparedStatement.setString(2, "approved");
                System.out.println(user);
                ResultSet resultSet = preparedStatement.executeQuery();
                 
                while (resultSet.next()) {
                    
                    resources.add(resultSet.getString(3));
                }
                
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        return resources;
    }
	
	    
	
	
	public String createRequest(String username, String requirement)
	{
		EstablishConnection ec = new EstablishConnection();
        Connection connection = ec.getConnection();
		try {
			String query="insert into requests  (requestor, requirement) values (?,?)";
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setString(1, username);
            ps.setString(2, requirement);
            if(ps.executeUpdate()>0)
            {
            	return "requested";
            }
            
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "failed to create request";
	}
	
	
	public List<String> resourcesToRequest(String user) {
        EstablishConnection ec = new EstablishConnection();
        Connection connection = ec.getConnection();
        List<String> resources=new ArrayList<String>();
        if (connection != null) {
            try {
                
               String query="select resourcename from resources where resourcename not in(select requirement from requests where requestor=? and requeststatus='approved' and requirement not in('manager', 'member', 'admin'))";
            	
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, user);
               //preparedStatement.setString(2, resourcenames);
                ResultSet rs = preparedStatement.executeQuery();
                System.out.println("got the data from database");
                // Execute the query
                while(rs.next())
                {
                	resources.add(rs.getString(1));
                }
            }catch(Exception e) {
            	e.printStackTrace();
            }   
        }
        System.out.println(resources);
        return resources;
    }
	
	public boolean updateRequest(String username, String requirement, String status)
	{
		try {
			EstablishConnection ec = new EstablishConnection();
	        Connection connection = ec.getConnection();
	        
	        boolean flag= false;
	        String query ="update requests set requeststatus=? where requestor=? and requirement=?";
	        PreparedStatement ps = connection.prepareStatement(query);
	        ps.setString(1, status);
	        ps.setString(2, username);
	        ps.setString(3, requirement);
	        flag= ps.executeUpdate()>0;
	        if(flag && "approved".equals(status) && ("manager".equals(requirement) || "admin".equals(requirement)))
	        {
	        	ps= connection.prepareStatement("update users set user_type =? where username=?");
	        	ps.setString(1, requirement);
	        	ps.setString(2, username);
	        	flag = ps.executeUpdate()>0;
	        }
	        
	        return flag;
	        
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return false;
	}
	
	public List<Request> getAllRequestsByUser(String user)
	{
		List<Request> al= new ArrayList<Request>();
		try {
		EstablishConnection ec = new EstablishConnection();
        Connection connection = ec.getConnection();
        String query ="select * from requests where requestor=?";
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setString(1, user);
        ResultSet rs= ps.executeQuery();
        while(rs.next())
        {
        	al.add(new Request(rs.getLong(1), rs.getString(2), rs.getString(3), rs.getString(4)));
        }
        return al;
		}catch (Exception e) {
			// TODO: handle exception
		}
        
		return null;
	}

	
	
	public boolean isPassword(String enteredPassword, String oldHashedPassword, String username) {
	    try {
	        EstablishConnection ec = new EstablishConnection();
	        Connection connection = ec.getConnection();

	        if (connection != null) {
	            String newHashedPassword = hashPassword(enteredPassword);
	            String query = "SELECT password FROM users WHERE username=?";
	            PreparedStatement ps = connection.prepareStatement(query);
	            ps.setString(1, username);
	            
	            ResultSet resultSet = ps.executeQuery();

	            if (resultSet.next()) {
	                String storedHashedPassword = resultSet.getString("password");
	                return newHashedPassword.equals(storedHashedPassword);
	            }
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

	    return false;
	}
	    
	
	
	//ADMIN
	
	private List<Request> request = new ArrayList<>();
	
	 public boolean approveRequest(int requestId) 
	 {
		 	        Request request = getRequestById(requestId);
	        
	        if (request != null && request.getRequestStatus().equals("PENDING")) {
	            // Approve the request
	            request.setRequestStatus("APPROVED");
	            return true;
	        }

	        return false;
	    }

	    public boolean rejectRequest(int requestId) {
	        Request request = getRequestById(requestId);

	        if (request != null && request.getRequestStatus().equals("PENDING")) {
	            // Reject the request
	            request.setRequestStatus("REJECTED");
	            return true;
	        }

	        return false;
	    }

	    private Request getRequestById(int requestId) {
	        // Find the request in the list by its ID
	        for (Request req : request) {
	            if (req.getRequestId() == requestId) {
	                return req;
	            }
	        }

	        return null;
	    }
	
	
	public List<Request> getAllResourceRequests() {
        List<Request> resourceRequests = new ArrayList<Request>();
        
        EstablishConnection ec = new EstablishConnection();
        Connection connection = ec.getConnection();
      //  HttpSession sessn = req.getSession(false);
//        User ob = (User)sessn.getAttribute("usersessn");
        // Establish a database connection (update the connection details)
        try {
            // Prepare and execute the query
            String query = "select * from requests where requeststatus='pending'";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query);
                 ResultSet resultSet = preparedStatement.executeQuery()) {

                // Process the result set
                while (resultSet.next()) {
                	
                	resourceRequests.add(new Request(resultSet.getInt(1), resultSet.getString(2), resultSet.getString(3), resultSet.getString(4)));
                	
                }
                            }
        } catch (SQLException e) {
            e.printStackTrace(); // Handle the exception appropriately in your application
        }

        return resourceRequests;

    }
    
    
    
    public List<String> getAllUsersByResource(String resource)
    {
    	List<String> al = new ArrayList<String>();
    	EstablishConnection ec = new EstablishConnection();
        Connection connection = ec.getConnection();
        try {
        //String q = "";
        PreparedStatement preparedStatement = connection.prepareStatement("select distinct(requestor) from requests where requirement=? and requeststatus='approved'");
        preparedStatement.setString(1, resource);
        ResultSet rs = preparedStatement.executeQuery();
        while(rs.next())
        {
        	al.add( rs.getString("requestor"));
        	
        }
        }catch(Exception e) { 
        	e.printStackTrace();
        }
        return al.isEmpty()?null:al;
        	
    }
    
    
    
    public List<String> getAllResourcesByUser(String requestor)
    {
    	List<String> l1 = new ArrayList<>();
    	System.out.println("requestor "+requestor);
    	try {
    		EstablishConnection ec = new EstablishConnection();
            Connection connection = ec.getConnection();
            PreparedStatement ps = connection.prepareStatement("select requirement from requests where requestor=? and requeststatus='approved' and requirement not in('admin', 'manager', 'member')");
            ps.setString(1, requestor);
            ResultSet rs = ps.executeQuery();
            while(rs.next())
            {
            	System.out.println(rs.getString(1)+"\t kk");
            	l1.add(rs.getString(1));
            }
            System.out.println(rs.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return l1;
    }
    
    public boolean removeResourcefromUser(String username, String resourcename)
    {
    	EstablishConnection ec = new EstablishConnection();
        Connection connection = ec.getConnection();

    	try {
    		String query = "delete from requests where requestor=? and requirement=?";
    		PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, resourcename);
            return preparedStatement.executeUpdate()>0?true:false;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return false;
    	
    }

	
    public List<String> getAllUsers(){
    	EstablishConnection ec = new EstablishConnection();
        Connection connection = ec.getConnection();
        List<String> al = new ArrayList<String>();
        if(connection!=null)
        {
        	try {
            	String query="select username from users where not user_type='admin'";
    			PreparedStatement ps= connection.prepareStatement(query);
    			ResultSet rs = ps.executeQuery();
    			while(rs.next())
    			{
    				al.add(rs.getString(1));
    			}
    			
    		} catch (Exception e) {
    			e.printStackTrace();
    		}
        }
        
		return al;
    	
    }
    
    public List<String> removeResourceFromUser(HttpServletRequest req){
    	EstablishConnection ec = new EstablishConnection();
        Connection connection = ec.getConnection();
        List<String> al = new ArrayList<String>();
        if(connection!=null)
        {
        	HttpSession usessn = req.getSession(false);
        	String ob =(String)usessn.getAttribute("selecteduser");
        	try {
        		String query= "select requirement from requests where requestor=? and requeststatus='approved'";
        		PreparedStatement ps = connection.prepareStatement(query);
        		ps.setString(1, ob);
        		ResultSet rs= ps.executeQuery();
        		
        		while(rs.next())
        		{
        			al.add(rs.getString(1));
        		}
				
			} catch (Exception e) {
				e.printStackTrace();
			}
        }
    	
    	
    	return al;
    }
    
    public List<User> viewAllUsers(){
    	
    	EstablishConnection ec = new EstablishConnection();
        Connection connection = ec.getConnection();
        List<User> al = new ArrayList<User>();
        if(connection!=null) {
        	try {
        		
            	String query="select * from users where user_type!='admin'";
            	PreparedStatement p= connection.prepareStatement(query);
            	ResultSet rs= p.executeQuery();	
        		while(rs.next())
        		{
        			al.add(new User(rs.getLong(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5), rs.getString(6), rs.getString(7), rs.getString(8), rs.getString(9)));
        		}
			} catch (Exception e) {
				e.printStackTrace();
			}
        	return al;
        }
        
        return null;
    	
    }
    
    public boolean removeUser(String uname) {
    	try {
    		
    		EstablishConnection ec = new EstablishConnection();
            Connection connection = ec.getConnection();
            String query="delete from users where username=?";
            System.out.println("remove user query");
            PreparedStatement ps= connection.prepareStatement(query);
            ps.setString(1, uname);
            System.out.println("remuser query executed");
            return ps.executeUpdate()>0?true:false;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
    
		return false;
	}

    public boolean isRequestExist(String requestor, String requirement) {
    	EstablishConnection ec = new EstablishConnection();
        Connection connection = ec.getConnection();
        if( connection != null) {
        	try {
        		System.out.println("eneterd isreques");
				String qry = "select * from requests where requestor = ? and requirement = ? and requeststatus = 'pending'";
				PreparedStatement ps= connection.prepareStatement(qry);
				ps.setString(1, requestor);
		    	ps.setString(2, requirement);
		    	ResultSet rs= ps.executeQuery();
		    	if(rs.next()) {
		    		return true;
		    	}
			} catch (Exception e) {
				// TODO: handle exception
			}
        }
    	return false;
    }
	
    public String requestTobeManager(String requestor, String requirement) {
	try {
		EstablishConnection ec = new EstablishConnection();
        Connection cnctn = ec.getConnection();
        if(isRequestExist(requestor, requirement))
        {
        	System.out.println("tobe manager");
        	return "<html><body><h2>the request is already made</h2></body></html>";
        }
        
        else
        {
        	String querypass="INSERT INTO requests (requestor, requirement) values (?, ?)";
        	PreparedStatement pstmt= cnctn.prepareStatement(querypass);
        	pstmt.setString(1, requestor);
        	pstmt.setString(2, requirement);
        	if(pstmt.executeUpdate()>0) {
        		return "<html><body><h2>requestsent successfully</h2></body></html>";
        	}
        }
		
	} catch (Exception e) {
		e.printStackTrace();
	}
	return "";
		
	}

	public void requestToBeAdmin(String requestor, String requirement) {
		try {
			EstablishConnection ec = new EstablishConnection();
            Connection conctn = ec.getConnection();
            String qry="insert into requests(requestor, requirement) values(?,?)";
            PreparedStatement ps = conctn.prepareStatement(qry);
            ps.setString(1, requestor);
            ps.setString(2, requirement);
            System.out.println("tobe admin");
            if(isRequestExist(requestor, requirement))
            {
            	System.out.println("the request to be admin is already made");
            }
            else
            {
            String qory="insert into requests(requestor, requirement) values(?,?)";
            PreparedStatement pst= conctn.prepareStatement(qory);
            pst.setString(1, requestor);
            pst.setString(2, requirement);
            System.out.println("to be admin finally");
            }
           
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	//Manager
	
	public boolean isTeamMember(String managerUsername, String teamMemberUsername) {
        EstablishConnection ec = new EstablishConnection();
        Connection connection = ec.getConnection();
        System.out.println("enetred is teammember");
        if (connection != null) {
            try {
                String query = "SELECT * FROM users WHERE manager=? AND username=?";
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, managerUsername);
                preparedStatement.setString(2, teamMemberUsername);

                ResultSet resultSet = preparedStatement.executeQuery();
                System.out.println("resultset of isteammember"+resultSet);
                // If there is a result, it means the team member exists
                return resultSet.next();
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        // Return false in case of errors or no connection
        return false;
    }

	
	
	public List<User> getAllUserManager(String manager) {
		
		List<User> a1= new ArrayList<User>();
		try {
			EstablishConnection ec = new EstablishConnection();
			Connection conecton = ec.getConnection();
			String query="select * from users where manager=?";
			PreparedStatement ps= conecton.prepareStatement(query);
			ps.setString(1, manager);
			ResultSet rs=ps.executeQuery();
			System.out.println("got all the members of manager query executed");
			while(rs.next())
			{
				a1.add(new User(rs.getLong(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5), rs.getString(6), rs.getString(7), rs.getString(8), rs.getString(9)));
			}
			return a1;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}


	public List<String> getAllUserNoManager(){
		List<String> li= new ArrayList<String>();
		try {
			EstablishConnection ec = new EstablishConnection();
			Connection connection = ec.getConnection();
			String querry = "select username from users where manager is null"; 
			PreparedStatement ps= connection.prepareStatement(querry);
			ResultSet rs= ps.executeQuery();
			while(rs.next())
			{
				li.add(rs.getString(1));
			}
			return li;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public boolean allocateManager(String uname, String mngr) {
		try {
			EstablishConnection ec = new EstablishConnection();
			Connection connection = ec.getConnection();
			String query="update users set manager=? where username=?";
			PreparedStatement ps= connection.prepareStatement(query);
			ps.setString(1, uname);
			ps.setString(2, mngr);
			return ps.executeUpdate()>0 ? true : false;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
		
	}
	
	public boolean deallocateManager(String usrname) {
		try {
			EstablishConnection ec = new EstablishConnection();
			Connection c = ec.getConnection();
			String query="update users set manager=null where username=?";
			PreparedStatement ps= c.prepareStatement(query);
			ps.setString(1, usrname);
			return ps.executeUpdate()>0 ? true : false;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
		
	}
	
	public List<String> listMyTeam(HttpServletRequest srvltRqst ) {
		
		List<String> al = new ArrayList<String>();
		try {
			EstablishConnection ec = new EstablishConnection();
			Connection c = ec.getConnection();
			if(c!=null)
			{
				HttpSession usersessn= srvltRqst.getSession();
		 	   User u = (User)usersessn.getAttribute("userSession");
				String query="select username from users where manager=?";
				PreparedStatement ps= c.prepareStatement(query);
				ps.setString(1, u.getUsername());
				ResultSet rs=ps.executeQuery();
				while(rs.next())
				{
					al.add(rs.getString(1));
				}
			}
		} catch (Exception e) {
		e.printStackTrace();
		}
		return al;
	}	
	
	
	public List<String> getAllMember() {
		EstablishConnection ec = new EstablishConnection();
		Connection c = ec.getConnection();
		List<String> al = new ArrayList<String>();
		if(c!=null)
		{	
		try {
//			HttpSession usessn = servletRequest.getSession(false);
//	    	String ob =(String)usessn.getAttribute("usersessn");
	    	String query="select username from users where user_type = 'member'";
			PreparedStatement ps= c.prepareStatement(query);
			ResultSet rs=ps.executeQuery();
			while(rs.next())
			{
				al.add(rs.getString(1));
			}
		} catch (Exception e) {
			// TODO: handle exception
			}
		}
		
		return al;
	}

	
}

	
	
	



