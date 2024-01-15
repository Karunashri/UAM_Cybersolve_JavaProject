
package b5.uam;
import com.mysql.cj.x.protobuf.MysqlxPrepare.Prepare;
import com.sun.jersey.api.view.Viewable;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import configurations.EstablishConnection;
import models.Request;
import models.User;
import models.Resources;
import services.UserServices;

/** Example resource class hosted at the URI path "/myresource"
 */
@Path("/myresource")
public class MyResource {
	
	@Context
	private HttpServletRequest srvltRqst;
	private HttpSession usersessn;
	private User u= null;
	private Request r= new Request();
	Viewable view;
    
    /** Method processing HTTP GET requests, producing "text/plain" MIME media
     * type.
     * @return String that will be send back as a response of type "text/plain".
     */
    @GET 
    @Produces("text/plain")
    public String getIt() {
        return "Hi there!";
    }
    
    
       public String Login(@FormParam("username") String usrnm, @FormParam("password") String pass){

    	EstablishConnection conn;
    	

        // Check if the user exists in the database
        String query = "SELECT * FROM users WHERE username = ?";
        try{
        	conn = new EstablishConnection();
        	Connection con = conn.getConnection();
        	PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, usrnm);
            //ps.setString(2, pass);

            System.out.println("gotusername");
            	ResultSet rs = ps.executeQuery();
                while(rs.next()) {
                	//String hashedPassword = rs.getString("password");
                   // if (UserServices.hashPassword(pass).equals(hashedPassword))
                	if(pass.equals("password"))
                    // User exists, return welcome message
                    return "Welcome " + usrnm;
                }
                    // User doesn't exist, return invalid credentials message
                    return "Invalid credentials";
                
            
        }catch (Exception e) {
        e.printStackTrace();
        return "Internal Server Error";
    }
  }
    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response authenticateUser(@FormParam("username") String unm, @FormParam("password") String pwd)
    	{
        System.out.println("hey am back");
        	UserServices um = new UserServices();
    		Viewable view;
    		try {
    			User utype = um.userLogin(unm, pwd);
    			System.out.println(utype);
    			
    			if(utype!=null)
    			{
    				HttpSession session = srvltRqst.getSession();
    				session.setAttribute("userSession", utype);
    			
    			
    			if(utype.getUsertype().equals("member"))
    			{
    				//System.out.println("you got it");
    				URI uri = URI.create("../member.jsp");
    				return Response.seeOther(uri).build();
    			}
    		else if(utype.getUsertype().equals("manager"))
    		{
    			URI uri = URI.create("../manager.jsp");
				return Response.seeOther(uri).build();
    		}
    		else if(utype.getUsertype().equals("admin"))
    		{
    			URI uri = URI.create("../admin.jsp");
				return Response.seeOther(uri).build();
    		}
    	} }
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    		view = new Viewable("/Wrong.html");
    		return Response.ok(view).build();
        
 }
    
   /* @POST
    @Path("signup")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)  
    @Produces(MediaType.TEXT_HTML)   
    public String signup(@FormParam("firstname") String firstname,
			 @FormParam("lastname") String lastname,
            @FormParam("password") String password,
            //@FormParam("user_type") String user_type,
			 @FormParam("customSecurityQuestion") String security_question, @FormParam("securityAnswer") String security_answer)
    {
    	
    	UserServices u = new UserServices();
        boolean adminavailable = u.checkAdmin();
        String utype, username;
        if(adminavailable)
        {
        	utype="admin";
        	username = firstname.toLowerCase()+"."+lastname.toLowerCase();
        }
        else {
        	utype="member";
        	username = u.createUsername(firstname.toLowerCase(), lastname.toLowerCase());
        }
        
        
    	EstablishConnection ec = new EstablishConnection();
        Connection connection = ec.getConnection();
        UserServices us = new UserServices();
        if (connection != null && utype.equals("admin")) {
            try {
            	System.out.println("start...");
            	//Hash the password before storing it in the database
                String hashedPassword = us.hashPassword(password);

                String query = "INSERT INTO users (firstname, lastname, username, password, user_type, security_question, security_answer) VALUES (?, ?, ?, ?, ?,?,?)";
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, firstname.toLowerCase());
                preparedStatement.setString(2, lastname.toLowerCase());
                preparedStatement.setString(3, username.toLowerCase());
                preparedStatement.setString(4, hashedPassword);
                username = u.createUsername(firstname, lastname);
                preparedStatement.setString(5, utype);
                preparedStatement.setString(6, security_question);
                preparedStatement.setString(7, security_answer);
               // preparedStatement.setDate(6, Date.valueOf(LocalDate.now()));

                int rowsAffected = preparedStatement.executeUpdate();
                if (rowsAffected > 0) {
                	String htmlContent = new String(Files.readAllBytes(Paths.get("D:\\JavaProject\\uam\\src\\main\\webapp\\Username.html")));
                	htmlContent = htmlContent.replace("${username}", username);
                	return htmlContent;
                } else {
                    return "Signup failed";
                }
            } catch (Exception e) {
                e.printStackTrace();
                return "Error during signup";
            } finally {
                try {
                    connection.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
        	try {
        		String qry="select * from users order by userid limit 1";
    			PreparedStatement ps = connection.prepareStatement(qry);
    			ResultSet rs = ps.executeQuery();
    			if(rs.next()) {
    				System.out.println("start...else");
                	//Hash the password before storing it in the database
                    String hashedPassword = us.hashPassword(password);

                    String query = "INSERT INTO users (firstname, lastname, username, password, user_type, manager,  security_question, security_answer) VALUES (?,?, ?, ?, ?, ?,?,?)";
                    PreparedStatement preparedStatement = connection.prepareStatement(query);
                    preparedStatement.setString(1, firstname.toLowerCase());
                    preparedStatement.setString(2, lastname.toLowerCase());
                    preparedStatement.setString(3, username.toLowerCase());
                    preparedStatement.setString(4, hashedPassword);
                    username = u.createUsername(firstname, lastname);
                    preparedStatement.setString(5, utype);
                    preparedStatement.setString(6, rs.getString(1) );
                    preparedStatement.setString(7, security_question);
                    preparedStatement.setString(8, security_answer);
                    
                    int rowsAffected = preparedStatement.executeUpdate();
                    if (rowsAffected > 0) {
                    	String htmlContent = new String(Files.readAllBytes(Paths.get("D:\\JavaProject\\uam\\src\\main\\webapp\\Username.html")));
                    	htmlContent = htmlContent.replace("${username}", username);
                    	return htmlContent;
    			}
            	
               // preparedStatement.setDate(6, Date.valueOf(LocalDate.now()));

                
                } else {
                    return "Signup failed";
                }
            } catch (Exception e) {
                e.printStackTrace();
                return "Error during signup";
            } finally {
                try {
                    connection.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
		return "";
    } */
    
    @POST
	@Path("signup")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)  
    @Produces(MediaType.TEXT_HTML)   
    public String signup(@FormParam("firstname") String firstname,
			 @FormParam("lastname") String lastname,
            @FormParam("password") String password,
            //@FormParam("user_type") String user_type,
			 @FormParam("customSecurityQuestion") String security_question, @FormParam("securityAnswer") String security_answer)
    {
		try {
			EstablishConnection ec = new EstablishConnection();
	        Connection connection = ec.getConnection();
	        UserServices us= new UserServices();
			String username = us.createUsername(firstname.trim().toLowerCase(), lastname.trim().toLowerCase());
			String epass =us.hashPassword(password);
			
			String query = "select * from users";
			PreparedStatement ps = connection.prepareStatement(query);
			ResultSet rs = ps.executeQuery();

			if(!rs.next()) {
				
				PreparedStatement ps1= connection.prepareStatement("insert into users(uerid, firstname, lastname, username, password, user_type, security_question, security_answer) values (?,?,?,?,?,?,?,?)");
				ps1.setInt(1, 101);
				ps1.setString(2, firstname.toLowerCase().trim());
				ps1.setString(3, lastname.toLowerCase().trim());
				ps1.setString(4, username);
				ps1.setString(5, epass);
				ps1.setString(6, "admin");
				ps1.setString(7, security_question);
				ps1.setString(8, security_answer);
				if(ps1.executeUpdate()>0)
				{
	
					String htmlContent = new String(Files.readAllBytes(Paths.get("D:\\JavaProject\\uam\\src\\main\\webapp\\Username.html")));
		            htmlContent = htmlContent.replace("${username}", username);
 
		            return htmlContent;
				}
			}
			else {
				String admin = "SELECT username FROM users ORDER BY userid LIMIT 1";
				PreparedStatement ps2 = connection.prepareStatement(admin);
				ResultSet rs1 = ps2.executeQuery();
				
			if (rs1.next()) {
				
				PreparedStatement ps3= connection.prepareStatement("insert into users(firstname, lastname, username, password, manager, security_question, security_answer) values (?,?,?,?,?,?,?)");
				ps3.setString(1, firstname.trim().toLowerCase());
				ps3.setString(2, lastname.trim().toLowerCase());
				ps3.setString(3, username);
				ps3.setString(4, epass);
				ps3.setString(5, rs1.getString(1));
				ps3.setString(6, security_question);
				ps3.setString(7, security_answer);
				
				if(ps3.executeUpdate()>0)
				{
	
					String htmlContent = new String(Files.readAllBytes(Paths.get("D:\\JavaProject\\uam\\src\\main\\webapp\\Username.html")));
		            htmlContent = htmlContent.replace("${username}", username);
 
		            return htmlContent;
				}
			}
		
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
     
    
 
    @POST
    @Path("/forgetpassword")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_PLAIN)
    public Response validateAnswer(@FormParam("username") String usrname) {
    	EstablishConnection ec = new EstablishConnection();
        Connection connection = ec.getConnection();
        try {
        	
			String query="select * from users where username=?";
			PreparedStatement ps= connection.prepareStatement(query);
			ps.setString(1, usrname);
			ResultSet rs=ps.executeQuery();
			if(rs.next()) {
        	usersessn= srvltRqst.getSession();
        	usersessn.setAttribute("selected", usrname);
        	URI redirectUri = URI.create("../setnewpassword.jsp");
        	return Response.seeOther(redirectUri).build();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
        
    	String htmlResponse="<!DOCTYPE html>\r\n"
    			+ "<html>\r\n"
    			+ "  <head>\r\n"
    			+ "    \r\n"
    			+ "  </head>\r\n"
    			+ "  <body>\r\n"
    			+ "      <h1>No search results</h1>\r\n"
    			+ "      <a href=\"Login.html\"> Go to Login page</a>\r\n"
    			+ "      \r\n"
    			+ "  </body>\r\n"
    			+ "</html>";
		return Response.ok(htmlResponse).build();
       
       
    }
    
    @POST 
    @Path("/setnewpassword")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String setNewPassword(@FormParam("answer") String answer, @FormParam("newpassword") String npass ) {
    	UserServices us= new UserServices();
    	EstablishConnection ec = new EstablishConnection();
        Connection connection = ec.getConnection();
        usersessn = srvltRqst.getSession(false);
    	String un= (String)usersessn.getAttribute("selected");
        if(connection != null) {
        	try {
//        		String ans=us.hashPassword(answer.toLowerCase());
        		String query="select * from users where username=? and security_answer=?";
        		PreparedStatement ps= connection.prepareStatement(query);
        		ps.setString(1, un);
        		ps.setString(2, answer);
        		ResultSet rs= ps.executeQuery();
        		if(rs.next()) {
        			try {
        				System.out.println(un);
//        				System.out.println(ans);
        				String newhashedpassword= us.hashPassword(npass);
//        				System.out.println(newhashedpassword);
        				String q="update users set password=? where username=?";
        				PreparedStatement p= connection.prepareStatement(q);
        				p.setString(1, newhashedpassword);
        				p.setString(2, un);
        				if(p.executeUpdate()>0) {
        					return "<!DOCTYPE html>\r\n"
        							+ "<html>\r\n"
        							+ "  <head>\r\n"
        							+ "    \r\n"
        							+ "  </head>\r\n"
        							+ "  <body>\r\n"
        							+ "      <h1>Password Changed Successfully</h1>\r\n"
        							+ "      <a href=\"/uam/Login.html\"> Go to Login page</a>\r\n"
        							+ "      \r\n"
        							+ "  </body>\r\n"
        							+ "</html>";
        				}
        				
        				return "<!DOCTYPE html>\r\n"
        						+ "<html>\r\n"
        						+ "  <head>\r\n"
        						+ "    \r\n"
        						+ "  </head>\r\n"
        						+ "  <body>\r\n"
        						+ "      <h1>Could not change password</h1>\r\n"
        						+ "      <a href=\"/uam/Login.html\"> Go to Login page</a>\r\n"
        						+ "      \r\n"
        						+ "  </body>\r\n"
        						+ "</html>";
        				
						
					} catch (Exception e) {
						e.printStackTrace();
					}
        		}
			} catch (Exception e) {
				e.printStackTrace();
			}
        	
        }
    	
    	
    	return "<!DOCTYPE html>\r\n"
    			+ "<html>\r\n"
    			+ "  <head>\r\n"
    			+ "    \r\n"
    			+ "  </head>\r\n"
    			+ "  <body>\r\n"
    			+ "      <h1>Error while changing password</h1>\r\n"
    			+ "      <a href=\"/uam/Login.html\"> Go to Login page</a>\r\n"
    			+ "      \r\n"
    			+ "  </body>\r\n"
    			+ "</html>";
    	
    	
    }

    
  
    
    @POST
    @Path("member/checkmyresources")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response checkMyResources()
    {
    	
    	usersessn = srvltRqst.getSession(false);
    	usersessn.setAttribute("usr", usersessn.getAttribute("userSession"));
    	URI redirectUri = URI.create("../Resource.jsp");
		return Response.seeOther(redirectUri).build();
    	
    }
    
    @POST
    @Path("/requestResource")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String requestResource(@FormParam("newResource") String newResource) {
    	
    	try
    	{
    	System.out.println("into requestResource");
    	usersessn= srvltRqst.getSession();
 	   u = (User)usersessn.getAttribute("userSession");
    	 
    	    UserServices us = new UserServices();
    	    boolean result = us.isRequestExist(u.getUsername(), newResource);
    	    if(result)	
    	    {
    	    	return "<!DOCTYPE html>\r\n"
    	    			+ "<html>\r\n"
    	    			+ "  <head>\r\n"
    	    			+ "    <title>Hello, World!</title>\r\n"
    	    			+ "    <link rel=\"stylesheet\" href=\"styles.css\" />\r\n"
    	    			+ "  </head>\r\n"
    	    			+ "  <body>\r\n"
    	    			+ "    <h2>Request already exists and is pending</h2>\r\n"
    	    			+ "      <a href=\"http://localhost:8080/uam/member.jsp\">Go Back</a>\r\n"
    	    			+ "  </body>\r\n"
    	    			+ "</html>";
    	    }
    	    else
    	    {
    	    	us.createRequest(u.getUsername(), newResource);
    	    	return "<!DOCTYPE html>\r\n"
    	    			+ "<html>\r\n"
    	    			+ "  <head>\r\n"
    	    			+ "    <title>Hello, World!</title>\r\n"
    	    			+ "    <link rel=\"stylesheet\" href=\"styles.css\" />\r\n"
    	    			+ "  </head>\r\n"
    	    			+ "  <body>\r\n"
    	    			+ "    <h2>Request sent successfully </h2>\r\n"
    	    			+ "      <a href=\"http://localhost:8080/uam/member.jsp\">Go Back</a>\r\n"
    	    			+ "  </body>\r\n"
    	    			+ "</html>";
    	    }
    	    
    	}catch(Exception e) {
    		e.printStackTrace();
    	} 
    	    return "<!DOCTYPE html>\r\n"
    	    		+ "<html>\r\n"
    	    		+ "  <head>\r\n"
    	    		+ "    <title>Hello, World!</title>\r\n"
    	    		+ "    <link rel=\"stylesheet\" href=\"styles.css\" />\r\n"
    	    		+ "  </head>\r\n"
    	    		+ "  <body>\r\n"
    	    		+ "    <h2>Failed to send request</h2>\r\n"
    	    		+ "      <a href=\"http://localhost:8080/uam/member.jsp\">Go Back</a>\r\n"
    	    		+ "  </body>\r\n"
    	    		+ "</html>";
    }


   
    @POST
    @Path("/requesttobecomemanager")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String requesttobeManager(@FormParam("requestor") String requester) {
    	usersessn= srvltRqst.getSession();
 	   u = (User)usersessn.getAttribute("userSession");
    	UserServices usrsvc = new UserServices();
    	usrsvc.requestTobeManager(r.getRequestor(), r.getRequirement());
    	
		return "<html><head><script>window.confirm('Request to become manager sent successfully');window.location.href='/uam/"+u.getUsertype()+".jsp</script></head></html>";
    }
    
    @POST
    @Path("/requesttobecomeadmin")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String requesttobeAdmin(@FormParam("requestor") String requestor) {
    	UserServices ursr = new UserServices();
    	usersessn= srvltRqst.getSession();
 	    u = (User)usersessn.getAttribute("userSession");
//    	ursr.requestToBeAdmin(r.getRequestor(), r.getRequirement());
 	  EstablishConnection ec = new EstablishConnection();
      Connection conctn = ec.getConnection();
      if(conctn != null) {
    	  try {
    		  if(ursr.isRequestExist(u.getUsername(),requestor)) {
    			  return "<!DOCTYPE html>\r\n"
    			  		+ "<html>\r\n"
    			  		+ "  <head>\r\n"
    			  		+ "    <title>Hello, World!</title>\r\n"
    			  		+ "    <link rel=\"stylesheet\" href=\"styles.css\" />\r\n"
    			  		+ "  </head>\r\n"
    			  		+ "  <body>\r\n"
    			  		+ "      <h1 class=\"title\">Already Requested!</h1>\r\n"
    			  		+ "      <a href=\"http://localhost:8080/uam/member.jsp\">Go Back</a>\r\n"
    			  		+ "  </body>\r\n"
    			  		+ "</html>";
    		  }
    		  String qurey = "insert into requests (requestor,requirement) values (?,?)";
    		  PreparedStatement ps = conctn.prepareStatement(qurey);
    		  ps.setString(1, u.getUsername());
    		  ps.setString(2, requestor);
    		  if(ps.executeUpdate()>0)
    		  {
//    			  return "<html><head><script>window.confirm('Request to become admin sent successfully');window.location.href='/uam/"+u.getUsertype()+".jsp</script></head></html>";
    			  return "<!DOCTYPE html>\r\n"
    			  		+ "<html>\r\n"
    			  		+ "  <head>\r\n"
    			  		+ "    <title>Hello, World!</title>\r\n"
    			  		+ "    <link rel=\"stylesheet\" href=\"styles.css\" />\r\n"
    			  		+ "  </head>\r\n"
    			  		+ "  <body>\r\n"
    			  		+ "      <h1 class=\"title\">Request to become "+requestor+" sent successfully!</h1>\r\n"
    			  		+ "      <a href=\"http://localhost:8080/uam/member.jsp\">Go Back</a>\r\n"
    			  		+ "  </body>\r\n"
    			  		+ "</html>";
    		  }
		} catch (Exception e) {
			e.printStackTrace();
		}
      }
//    	return "<html><head><script>window.confirm('Error');window.location.href='/uam/"+u.getUsertype()+".jsp</script></head></html>";
      return "<!DOCTYPE html>\r\n"
      		+ "<html>\r\n"
      		+ "  <head>\r\n"
      		+ "    <title>Hello, World!</title>\r\n"
      		+ "    <link rel=\"stylesheet\" href=\"styles.css\" />\r\n"
      		+ "  </head>\r\n"
      		+ "  <body>\r\n"
      		+ "      <h1 class=\"title\">Something Went Wrong!</h1>\r\n"
      		+ "      <a href=\"http://localhost:8080/uam/member.jsp\">Go Back</a>\r\n"
      		+ "  </body>\r\n"
      		+ "</html>";
    }
    
    @POST
    @Path("/removemyresource")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String removeMyOwnResource(@FormParam("myres") String myres) { 
    	EstablishConnection ec = new EstablishConnection();
        Connection connection = ec.getConnection();
        
 	    if(connection!=null)
 	    {
 	    	usersessn= srvltRqst.getSession();
 	 	    u = (User)usersessn.getAttribute("userSession");
 	    	try {
 	    		String query = "delete from requests where requestor =? AND requirement=?";
 	 	    	PreparedStatement ps = connection.prepareStatement(query);
 	 	    	ps.setString(1, u.getUsername());
 	 	    	ps.setString(2, myres);
 	 	    	if(ps.executeUpdate()>0)
 	 	    	{
 	 	    		return "<!DOCTYPE html>\r\n"
 	 	    				+ "<html>\r\n"
 	 	    				+ "  <head>\r\n"
 	 	    				+ "    <title>Hello, World!</title>\r\n"
 	 	    				+ "    <link rel=\"stylesheet\" href=\"styles.css\" />\r\n"
 	 	    				+ "  </head>\r\n"
 	 	    				+ "  <body>\r\n"
 	 	    				+ "      <h1 class=\"title\">Resource removed successfully!</h1>\r\n"
 	 	    				+ "      <a href=\"http://localhost:8080/uam/member.jsp\">Go Back</a>\r\n"
 	 	    				+ "  </body>\r\n"
 	 	    				+ "</html>";
 	 	    	}
			} catch (Exception e) {
				// TODO: handle exception
			}
 	    }
        
    	return"<!DOCTYPE html>\r\n"
    			+ "<html>\r\n"
    			+ "  <head>\r\n"
    			+ "    <title>Hello, World!</title>\r\n"
    			+ "    <link rel=\"stylesheet\" href=\"styles.css\" />\r\n"
    			+ "  </head>\r\n"
    			+ "  <body>\r\n"
    			+ "      <h1 class=\"title\">Something Went Wrong!</h1>\r\n"
    			+ "      <a href=\"http://localhost:8080/uam/member.jsp\">Go Back</a>\r\n"
    			+ "  </body>\r\n"
    			+ "</html>";
    }
    
    
    
   @POST
   @Path("/changepassword")
   @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
   public  String changePassword(@FormParam("old_pwd") String oldpass, @FormParam("new_pwd") String newpass) {
	   System.out.println("enetered changepassword rest");
	   EstablishConnection ec = new EstablishConnection();
       Connection connection = ec.getConnection();
       usersessn= srvltRqst.getSession();
 	   u = (User)usersessn.getAttribute("userSession");
 	   System.out.println("user session established in change password");
       if(connection!=null) {
    	  try {
    		  System.out.println("entered try");
    		  UserServices pw= new UserServices();
    		 if(pw.isPassword(oldpass, newpass, u.getUsername())) {
    			 System.out.println("enetered change password rest if");
    			 String newhashedpass=pw.hashPassword(newpass);
    			 System.out.println("hashedpwd");
    			 String query="update users set password=? where username=?";
    			 PreparedStatement ps= connection.prepareStatement(query);
    			 System.out.println("preparedstatement");
    			 ps.setString(1, newhashedpass);
    			 ps.setString(2,u.getUsername());
    			 if(ps.executeUpdate()>0) {
    				 System.out.println("query executed");
    				 return "password changed successfully";
    			 }
    		 }
    		 
    		 return "failed to change password";
    		 
			
		} catch (Exception e) {
			e.printStackTrace();
		}
       }
	   
	   return"Error during change password";
   }




    //Admin page
 
    
    @POST
    @Path("/admin/checkrequests")
    public String checkRequests(@FormParam("status") String status, @FormParam("user") String user, @FormParam("requirement") String requirement) {
    	
    	try {
    		EstablishConnection ec = new EstablishConnection();
            Connection connection = ec.getConnection();
            if(connection!= null)
            {
            	System.out.println(status);
            	System.out.println(user);
            	System.out.println(requirement);
            	String query="update requests set requeststatus=? where requestor=? and requirement=?";
            	PreparedStatement ps= connection.prepareStatement(query);
            	ps.setString(1, status);
            	ps.setString(2, user);
            	ps.setString(3, requirement);
            	//ps.executeUpdate();
            	System.out.println("dfghi");
            	if(ps.executeUpdate() > 0 && status.equals("approved")) {
            		System.out.println("entered 1st if of checkrequest");
            		if(requirement.equals("admin") || requirement.equals("manager")) {
            			System.out.println("entered second if of chechrequest");
            			String q="update users set user_type =? where username=?";
            			PreparedStatement ps1= connection.prepareStatement(q);
            			ps1.setString(1, requirement);
            			ps1.setString(2, user);
            			ps1.executeUpdate();    
            			System.out.println("executed the statements of checkrequest");
            	
            	return "<!DOCTYPE html>\r\n"
            			+ "<html>\r\n"
            			+ "  <head>\r\n"
            			+ "    <title>Hello, World!</title>\r\n"
            			+ "    <link rel=\"stylesheet\" href=\"styles.css\" />\r\n"
            			+ "  </head>\r\n"
            			+ "  <body>\r\n"
            			+ "      <h1>Request successfully approved</h1>\r\n"
            			+ "      <a href=\"/uam/checkrequest.jsp\">Go Back</a>\r\n"
            			+ "  </body>\r\n"
            			+ "</html>";
            		}
              
            
            else {
            	return "<!DOCTYPE html>\r\n"
            			+ "<html>\r\n"
            			+ "  <head>\r\n"
            			+ "    <title>Hello, World!</title>\r\n"
            			+ "    <link rel=\"stylesheet\" href=\"styles.css\" />\r\n"
            			+ "  </head>\r\n"
            			+ "  <body>\r\n"
            			+ "      <h1>Request approved successfully</h1>\r\n"
            			+ "      <a href=\"/uam/checkrequest.jsp\">Go Back</a>\r\n"
            			+ "  </body>\r\n"
            			+ "</html>";
            }
            	}	
            }
		} catch (Exception e) {
			
		}
    	return "<!DOCTYPE html>\r\n"
    			+ "<html>\r\n"
    			+ "  <head>\r\n"
    			+ "    <title>Hello, World!</title>\r\n"
    			+ "    <link rel=\"stylesheet\" href=\"styles.css\" />\r\n"
    			+ "  </head>\r\n"
    			+ "  <body>\r\n"
    			+ "      <h1>Request rejected</h1>\r\n"
    			+ "      <a href=\"/uam/admin.jsp\">Go Back</a>\r\n"
    			+ "  </body>\r\n"
    			+ "</html>";
    }
   
  /* @POST
   @Path("/admin/checkrequests")
   public String checkRequests(@FormParam("status") String status, @FormParam("user") String user, @FormParam("requirement") String requirement) {
   	
   	try {
   		EstablishConnection ec = new EstablishConnection();
           Connection connection = ec.getConnection();
           if(connection!= null)
           {
           	System.out.println(status);
           	System.out.println(user);
           	System.out.println(requirement);
           	String query="update requests set requeststatus=? where requestor=? and requirement=?";
           	PreparedStatement ps= connection.prepareStatement(query);
           	ps.setString(1, status);
           	ps.setString(2, user);
           	ps.setString(3, requirement);
           	//ps.executeUpdate();
           	System.out.println("dfghi");
           	if(ps.executeUpdate() > 0 && status.equals("approved")) {
           		System.out.println("entered 1st if of checkrequest");
           		if(requirement.equals("radmin")) {
           			System.out.println("entered second if of chechrequest");
           			String q="update users set user_type =? where username=?";
           			PreparedStatement ps1= connection.prepareStatement(q);
           			ps1.setString(1, requirement.equals("admin") ? requirement : "admin");
           			ps1.setString(2, user);
           			if(ps1.executeUpdate()>0) {  
           				if(requirement.equals("radmin")) {
           					String qo="select requestor from requests where requirement='rmanager' and requestor in (select username from users where manager=?)";
           					PreparedStatement ps0= connection.prepareStatement(qo);
           					ps0.setString(1, user);
           					ResultSet rs= ps0.executeQuery();
           					
           						if(rs.next()) {
           							
           							String updatemanager="update requests set requeststatus='approved' where requirement='rmanager' and requestor=?";
           							PreparedStatement psl= connection.prepareStatement(updatemanager);
           							psl.setString(1, rs.getString(1));
           							
           							if(psl.executeUpdate()>0) {}
           							String qem="select username from users order by limit=1";
           							PreparedStatement pst=connection.prepareStatement(qem);
           							ResultSet rst= pst.executeQuery();
           							
           								if(rst.next()) {
           									String querr="update users set user_type='manager', manager=? where username=?";
           									PreparedStatement pslv = connection.prepareStatement(querr);
           									pslv.setString(1, rst.getString(1));
           									pslv.setString(2, rs.getString(1));
           									
           										if(pslv.executeUpdate()>0) {
           											String refquer="update users set manager=? where manager=?";
           											PreparedStatement pstmt= connection.prepareStatement(refquer);
           											pstmt.setString(1, rs.getString(1));
           											pstmt.setString(2, user);
           											
           												if(pstmt.executeUpdate()>0) {
           													
           													String quew="delete from requests where requestor=?";
           													PreparedStatement prd=connection.prepareStatement(quew);
           													prd.setString(1, user);
           													prd.executeUpdate();
           												}
           										}
           									
           								}
           							
           						}
           				}
           			
           			}
           	
           		}
           	}else if(requirement.equals("manager")) {
           		String quer="update users set user_type=? where username=?";
           		PreparedStatement pstmtt= connection.prepareStatement(quer);
           		pstmtt.setString(1, requirement);
           		pstmtt.setString(2, user);
           		pstmtt.executeUpdate();
           	}
           
           return "<!DOCTYPE html>\r\n"
           		+ "<html>\r\n"
           		+ "  <head>\r\n"
           		+ "    <title>Hello, World!</title>\r\n"
           		+ "    <link rel=\"stylesheet\" href=\"styles.css\" />\r\n"
           		+ "  </head>\r\n"
           		+ "  <body>\r\n"
           		+ "     <h1>Request approved successfully</h1>\r\n"
           		+ "     <a href=\"/uam/admin.jsp\">Go Back</a>\r\n"
           		+ "  </body>\r\n"
           		+ "</html>";
           }} catch (Exception e) {
			e.printStackTrace();
		}
   	if(requirement.equals("radmin") && status.equals("denied")) {
   	try {
   	 EstablishConnection ec = new EstablishConnection();
     Connection connection = ec.getConnection();
     
     	String qrr="select requestor from requests where requirement='rmanager' AND requestor IN (SELECT username FROM users WHERE manager= ?)";
     	PreparedStatement pss = connection.prepareStatement(qrr);
     	pss.setString(1, user);
     	ResultSet rs= pss.executeQuery();
     	if(rs.next()) {}
     	String query="update requests set requeststatus='denied' where requestor=? and requirement='rmanager'";
   		PreparedStatement pqt= connection.prepareStatement(query);
   		pqt.executeUpdate();
   		}
   	
   			catch (Exception e) {
   				e.printStackTrace();
   			}
   	}
   	
   	return "<!DOCTYPE html>\r\n"
   			+ "<html>\r\n"
   			+ "  <head>\r\n"
   			+ "    <title>Hello, World!</title>\r\n"
   			+ "    <link rel=\"stylesheet\" href=\"styles.css\" />\r\n"
   			+ "  </head>\r\n"
   			+ "  <body>\r\n"
   			+ "      <h1>Request rejected</h1>\r\n"
   			+ "      <a href=\"/uam/admin.jsp\">Go Back</a>\r\n"
   			+ "  </body>\r\n"
   			+ "</html>";
   }*/
   
   
   
   /*@POST
   @Path("/admin/checkrequests")
   public String checkRequests(@FormParam("status") String status, @FormParam("user") String user,
           @FormParam("requirement") String requirement) {

       try {
           EstablishConnection ec = new EstablishConnection();
           Connection connection = ec.getConnection();

           if (connection != null) {
               System.out.println(status);
               System.out.println(user);
               System.out.println(requirement);

               String query = "update requests set requeststatus=? where requestor=? and requirement=?";
               try (PreparedStatement ps = connection.prepareStatement(query)) {
                   ps.setString(1, status);
                   ps.setString(2, user);
                   ps.setString(3, requirement);
                   int rowsAffected = ps.executeUpdate();

                   if (rowsAffected > 0 && status.equals("approved")) {
                       System.out.println("Entered 1st if of checkrequest");

                       if (requirement.equals("admin") || requirement.equals("manager")) {
                           System.out.println("Entered second if of checkrequest");

                           String q = "update users set user_type =? where username=?";
                           try (PreparedStatement ps1 = connection.prepareStatement(q)) {
                               ps1.setString(1, requirement);
                               ps1.setString(2, user);
                               ps1.executeUpdate();
                               System.out.println("Executed the statements of checkrequest");

                               // Check if the requirement is 'radmin' or 'rmanager'
                               if (requirement.equals("admin")) {
                                   approvePendingRequests("radmin", "rmanager", connection);
                               } else if (requirement.equals("manager")) {
                                   approvePendingRequests("rmanager", "radmin", connection);
                               }

                               return "<!DOCTYPE html>\r\n" + "<html>\r\n" + "  <head>\r\n"
                                       + "    <title>Hello, World!</title>\r\n"
                                       + "    <link rel=\"stylesheet\" href=\"styles.css\" />\r\n" + "  </head>\r\n"
                                       + "  <body>\r\n" + "      <h1>Request successfully approved</h1>\r\n"
                                       + "      <a href=\"/uam/checkrequest.jsp\">Go Back</a>\r\n" + "  </body>\r\n"
                                       + "</html>";
                           }
                       } else {
                           return "<!DOCTYPE html>\r\n" + "<html>\r\n" + "  <head>\r\n"
                                   + "    <title>Hello, World!</title>\r\n"
                                   + "    <link rel=\"stylesheet\" href=\"styles.css\" />\r\n" + "  </head>\r\n"
                                   + "  <body>\r\n" + "      <h1>Request approved successfully</h1>\r\n"
                                   + "      <a href=\"/uam/checkrequest.jsp\">Go Back</a>\r\n" + "  </body>\r\n"
                                   + "</html>";
                       }
                   }
               }
           }
       } catch (Exception e) {
           e.printStackTrace();
       }

       return "<!DOCTYPE html>\r\n" + "<html>\r\n" + "  <head>\r\n" + "    <title>Hello, World!</title>\r\n"
               + "    <link rel=\"stylesheet\" href=\"styles.css\" />\r\n" + "  </head>\r\n" + "  <body>\r\n"
               + "      <h1>Request rejected</h1>\r\n" + "      <a href=\"/uam/admin.jsp\">Go Back</a>\r\n"
               + "  </body>\r\n" + "</html>";
   }

   private void approvePendingRequests(String approvedRequirement, String correspondingRequirement,
           Connection connection) throws SQLException {
       // Update the user_type for radmin in the users table
       String updateAdminUserTypeQuery = "update users set user_type = 'admin' where user_type = ? and username in (select requestor from requests where requirement = ? and requeststatus = 'approved')";
       try (PreparedStatement psUpdateAdminUserType = connection.prepareStatement(updateAdminUserTypeQuery)) {
           psUpdateAdminUserType.setString(1, approvedRequirement);
           psUpdateAdminUserType.setString(2, approvedRequirement);
           psUpdateAdminUserType.executeUpdate();
       }

       // Update the rmanager request status to 'approved' in the requests table
       String updateRManagerRequestQuery = "update requests set requeststatus = 'approved' where requirement = ? and requeststatus = 'pending'";
       try (PreparedStatement psUpdateRManagerRequest = connection.prepareStatement(updateRManagerRequestQuery)) {
           psUpdateRManagerRequest.setString(1, correspondingRequirement);
           psUpdateRManagerRequest.executeUpdate();
       }

       // Update the user_type for rmanager in the users table
       String updateRManagerUserTypeQuery = "update users set user_type = 'manager' where username in (select requestor from requests where requirement = ? and requeststatus = 'approved')";
       try (PreparedStatement psUpdateRManagerUserType = connection.prepareStatement(updateRManagerUserTypeQuery)) {
           psUpdateRManagerUserType.setString(1, correspondingRequirement);
           psUpdateRManagerUserType.executeUpdate();
       }
   }*/



    @POST
    @Path("/admin/addResource")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_HTML)
    public String addResource(@FormParam("newResource") String newResource) {
    	// Implement logic to add the new resource to the database
    	//Connection connection = EstablishConnection.getConnection();
    	EstablishConnection ec = new EstablishConnection();
        Connection connection = ec.getConnection();
        System.out.println("connected");
        if (connection != null) {
            try {
            	System.out.println("entered if");
                String query = "INSERT INTO resources (resourcename) VALUES (?)";
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, newResource);
                System.out.println("query");
                int rowsAffected = preparedStatement.executeUpdate();
                System.out.println("query executed");
                if (rowsAffected > 0) {
                    return "<!DOCTYPE html>\r\n"
                    		+ "<html>\r\n"
                    		+ "  <head>\r\n"
                    		+ "    <title>Hello, World!</title>\r\n"
                    		+ "    <link rel=\"stylesheet\" href=\"styles.css\" />\r\n"
                    		+ "  </head>\r\n"
                    		+ "  <body>\r\n"
                    		+ "      <h1>Resource added successfully! </h1>\r\n"
                    		+ "      <a href=\"http://localhost:8080/uam/admin.jsp\">Go Back</a>\r\n"
                    		+ "  </body>\r\n"
                    		+ "</html>";
                } else {
                    return "Failed to add resource.";
                }
            } catch (SQLException e) {
                e.printStackTrace();
                return "Error during resource addition.";
            } finally {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } else {
            return "Failed to establish database connection.";
        }
    	//return "New Resource Added: " + newResource;
    }

    @POST
    @Path("/admin/removeResource")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_PLAIN)
    public String removeResource(@FormParam("resourceToRemove") String resourceToRemove) {
    	EstablishConnection ec = new EstablishConnection();
        Connection connection = ec.getConnection();
        
        System.out.println("connected");
        if (connection != null) {
            try {
            	String query = "DELETE FROM resources WHERE resourcename = ?";
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, resourceToRemove);
                System.out.println("query");
                int rowsAffected = preparedStatement.executeUpdate();
                if (rowsAffected > 0) {
                    return "Resource removed successfully!";
                } else {
                    return "Resource not found or failed to remove.";
                }
            } catch (SQLException e) {
                e.printStackTrace();
                return "Error during resource addition.";
            } finally {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } else {
            return "Failed to establish database connection.";
        
    }
    }
    
    @POST
    @Path("admin/users")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response getResourcesByUser(@FormParam("rmvresusr")String username)
    {
    	HttpSession usr1 = srvltRqst.getSession(false);
		usr1.setAttribute("selecteduser", username);
		
    	URI redirectUri = URI.create("../admin.jsp");
    	return Response.seeOther(redirectUri).build();
    }

    @POST
    @Path("/admin/removeresourcefromuser")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String removeResourceFromUser(@FormParam("rmv_res_frm_usr") String usernameToRemoveFrom) {
		
    	EstablishConnection ec = new EstablishConnection();
        Connection connection = ec.getConnection();
        if(connection!=null)
        {
        	usersessn = srvltRqst.getSession(false);
        	String ob=(String)usersessn.getAttribute("selecteduser");
        	try {
        		String query="delete from requests where requestor=? and requirement=?";
        		PreparedStatement ps= connection.prepareStatement(query);
        		ps.setString(1, ob);
        		ps.setString(2, usernameToRemoveFrom);
        		if(ps.executeUpdate()>0)
        		{
        			return "<!DOCTYPE html>\r\n"
        					+ "<html>\r\n"
        					+ "  <head>\r\n"
        					+ "    <title>Hello, World!</title>\r\n"
        					+ "    <link rel=\"stylesheet\" href=\"styles.css\" />\r\n"
        					+ "  </head>\r\n"
        					+ "  <body>\r\n"
        					+ "      <h1 class=\"title\">Resource removed successfully!</h1>\r\n"
        					+ "      <a href=\"http://localhost:8080/uam/admin.jsp\">Go Back</a>\r\n"
        					+ "  </body>\r\n"
        					+ "</html>";
        		}
				
			} catch (Exception e) {
			
			}
    		
        }
    	return "<!DOCTYPE html>\r\n"
    			+ "<html>\r\n"
    			+ "  <head>\r\n"
    			+ "    <title>Hello, World!</title>\r\n"
    			+ "    <link rel=\"stylesheet\" href=\"styles.css\" />\r\n"
    			+ "  </head>\r\n"
    			+ "  <body>\r\n"
    			+ "      <h1 class=\"title\">Could not remove resource!</h1>\r\n"
    			+ "      <a href=\"http://localhost:8080/uam/admin.jsp\">Go Back</a>\r\n"
    			+ "  </body>\r\n"
    			+ "</html>";
    	
    }

    @POST
    @Path("/admin/checkUserByResource")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_HTML)
    public String viewResourcesByUser(@FormParam("username") String usernameToCheck) {
    	UserServices un= new UserServices();
    	List<String> resourcelist= un.getAllResourcesByUser(usernameToCheck);
    	StringBuilder htmlResponse = new StringBuilder("<html><body><h1>List of resources this user have</h1>");
    	System.out.println(resourcelist.toString());
    	if(resourcelist != null && !resourcelist.isEmpty())
    	{
    		htmlResponse.append("<ul>");
    		for(String req: resourcelist) {
    			htmlResponse = htmlResponse.append("<li>").append(req).append("</li>");
    		}
    		htmlResponse.append("</ul>");
    	}
    	else {
    		htmlResponse.append("<p> No User has been allotted this resource </p>");
    	}
    	 htmlResponse.append("<a href='/uam/admin.jsp'>Go Back</a></body></html>");

         return htmlResponse.toString();
    }

   
    
    @POST
    @Path("/admin/checkResourcesByUser")
    @Produces(MediaType.TEXT_HTML)
    public Response viewUserByResource(@FormParam("resourcename") String resourcenameToCheck)
    {
    	System.out.println(resourcenameToCheck);
    	usersessn = srvltRqst.getSession(false);
    	usersessn.setAttribute("resrc_name", resourcenameToCheck);
    	URI redirectUri = URI.create("../ResourceList.jsp");
		return Response.seeOther(redirectUri).build();
    	
    }
    

    @POST
    @Path("/admin/viewallusers")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response viewAllUsers() {
    	
    	System.out.println("vielw all users");
    	usersessn= srvltRqst.getSession();
	 	u = (User)usersessn.getAttribute("userSession");
    	URI redirectUri = URI.create("../viewallusers.jsp");
		return Response.seeOther(redirectUri).build();
    }
    
    
    @POST
    @Path("/admin/removeuser")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_HTML)
    public String removeUser(@FormParam("uname") String uname) {
    	EstablishConnection ec = new EstablishConnection();
        Connection connection = ec.getConnection();
        
        System.out.println("connected");
        if (connection != null) {
            try {
            	String query = "DELETE FROM users WHERE username = ?";
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, uname);
                System.out.println("query");
                int rowsAffected = preparedStatement.executeUpdate();
                if (rowsAffected > 0) {
                    return "<!DOCTYPE html>\r\n"
                    		+ "<html>\r\n"
                    		+ "  <head>\r\n"
                    		+ "    <title>user deleted</title>\r\n"
                    		+ "  </head>\r\n"
                    		+ "  <body>\r\n"
                    		+ "      <h1>User Deleted Successfully</h1>\r\n"
                    		+ "      <a href=\"/uam/viewallusers.jsp\">Go Back</a>\r\n"
                    		+ "  </body>\r\n"
                    		+ "</html>";
                } else {
                    return "<!DOCTYPE html>\r\n"
                    		+ "<html>\r\n"
                    		+ "  <head>\r\n"
                    		+ "    <title>user deleted</title>\r\n"
                    		+ "  </head>\r\n"
                    		+ "  <body>\r\n"
                    		+ "      <h1>User not found</h1>\r\n"
                    		+ "      <a href=\"/uam/viewallusers.jsp\">Go Back</a>\r\n"
                    		+ "  </body>\r\n"
                    		+ "</html>";
                }
            } catch (SQLException e) {
                e.printStackTrace();
                return "<!DOCTYPE html>\r\n"
                		+ "<html>\r\n"
                		+ "  <head>\r\n"
                		+ "    <title>user deleted</title>\r\n"
                		+ "  </head>\r\n"
                		+ "  <body>\r\n"
                		+ "      <h1>Error during User deletion</h1>\r\n"
                		+ "      <a href=\"/uam/viewallusers.jsp\">Go Back</a>\r\n"
                		+ "  </body>\r\n"
                		+ "</html>";
            } finally {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } else {
            return "Failed to establish database connection.";
        
    }
    }
    
    
    @GET
    @Path("/logout")
    public String logout() {
    	System.out.println("entered logout rest");
    	try {
    	usersessn = srvltRqst.getSession(false);
    	if(usersessn != null) {
    		System.out.println("entered login if");
    		Enumeration<String> sessionAttribute = usersessn.getAttributeNames();
    		while(sessionAttribute.hasMoreElements()) {
    			
    			String attribute = sessionAttribute.nextElement();
    			usersessn.removeAttribute(attribute);
    		}
    		System.out.println("login while executed");
    	}
    	usersessn.invalidate();
    	
    	} catch (Exception e) {
			e.printStackTrace();
		}
    	//URI redirectUri = URI.create("../Login.html");
		return "<html><head><script>window.location.href='/uam/Login.html'</script></head></html>";
    	
    
    }
    
    @POST
    @Path("manager/addteammember")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String addTeamMember(@FormParam("selectmember") String selectmember) {
    	usersessn= srvltRqst.getSession();
  	   u = (User)usersessn.getAttribute("userSession");
    	EstablishConnection ec = new EstablishConnection();
        Connection connection = ec.getConnection();
        if(connection!=null)
        {
        	try {
				String query= "update users set manager=? where username=?";
        		//String query="UPDATE users SET manager = COALESCE(manager, 'admin') WHERE username = ?";
				PreparedStatement ps= connection.prepareStatement(query);
				ps.setString(1, u.getUsername());
				ps.setString(2, selectmember);
				if(ps.executeUpdate()>0) {
					return "<!DOCTYPE html>\r\n"
							+ "<html>\r\n"
							+ "  <head>\r\n"
							+ "    <title>Hello, World!</title>\r\n"
							+ "    <link rel=\"stylesheet\" href=\"styles.css\" />\r\n"
							+ "  </head>\r\n"
							+ "  <body>\r\n"
							+ "      <h1 class=\"title\">Member Added Successfully</h1>\r\n"
							+ "      <a href=\"http://localhost:8080/uam/manager.jsp\">Go Back</a>\r\n"
							+ "  </body>\r\n"
							+ "</html>";
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
        }
    	
    	
    	return "<!DOCTYPE html>\r\n"
    			+ "<html>\r\n"
    			+ "  <head>\r\n"
    			+ "    <title>Hello, World!</title>\r\n"
    			+ "    <link rel=\"stylesheet\" href=\"styles.css\" />\r\n"
    			+ "  </head>\r\n"
    			+ "  <body>\r\n"
    			+ "      <h1 class=\"title\">Could not add member</h1>\r\n"
    			+ "      <a href=\"http://localhost:8080/uam/manager.jsp\">Go Back</a>\r\n"
    			+ "  </body>\r\n"
    			+ "</html>";
    }
    
    @POST
    @Path("manager/removeteammember")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String removeTeamMember(@FormParam("selectedmember") String selectmember) {
       usersessn= srvltRqst.getSession();
  	   u = (User)usersessn.getAttribute("userSession");
    	EstablishConnection ec = new EstablishConnection();
        Connection connection = ec.getConnection();
        UserServices s= new UserServices();
        if(connection!=null) {
        System.out.println("entered remove teammate");
        	try {
        		  if (s.isTeamMember(u.getUsername(), selectmember)) {
                      String query = "UPDATE users SET manager='admin' WHERE username=?";
                      PreparedStatement ps = connection.prepareStatement(query);
                      ps.setString(1, selectmember);

                      if (ps.executeUpdate() > 0) {
                          return "<!DOCTYPE html>\r\n"
                                  + "<html>\r\n"
                                  + "  <head>\r\n"
                                  + "    <title>Success</title>\r\n"
                                  + "    <link rel=\"stylesheet\" href=\"styles.css\" />\r\n"
                                  + "  </head>\r\n"
                                  + "  <body>\r\n"
                                  + "     <h1>Member Removed Successfully</h1>\r\n"
                                  + "  </body>\r\n"
                                  + "</html>";
                      }
                  } else {
                      return "<!DOCTYPE html>\r\n"
                              + "<html>\r\n"
                              + "  <head>\r\n"
                              + "    <title>Error</title>\r\n"
                              + "    <link rel=\"stylesheet\" href=\"styles.css\" />\r\n"
                              + "  </head>\r\n"
                              + "  <body>\r\n"
                              + "     <h1>Selected member is not in your team</h1>\r\n"
                              + "  </body>\r\n"
                              + "</html>";
                  }
              } catch (Exception e) {
                  e.printStackTrace();
              }
          }

          return "<!DOCTYPE html>\r\n"
                  + "<html>\r\n"
                  + "  <head>\r\n"
                  + "    <title>Error</title>\r\n"
                  + "    <link rel=\"stylesheet\" href=\"styles.css\" />\r\n"
                  + "  </head>\r\n"
                  + "  <body>\r\n"
                  + "     <h1>Error in removing member</h1>\r\n"
                  + "  </body>\r\n"
                  + "</html>";
      }
    
    
    @POST
    @Path("/manager/requestResource")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String managerRequestsforResource(@FormParam("newResource") String newResource) {
    	
    	try
    	{
    	System.out.println("into requestResource");
    	usersessn= srvltRqst.getSession();
    	   u = (User)usersessn.getAttribute("userSession");
    	 
    	    UserServices us = new UserServices();
    	    System.out.println(u.getUsername());
    	    System.out.println(newResource);
    	    
    	    if(us.isRequestExist(u.getUsername(), newResource))	
    	    {
    	    	System.out.println("enetered rest isrequest");
    	    	return "<!DOCTYPE html>\r\n"
    	    			+ "<html>\r\n"
    	    			+ "  <head>\r\n"
    	    			+ "    <title>Hello, World!</title>\r\n"
    	    			+ "    <link rel=\"stylesheet\" href=\"styles.css\" />\r\n"
    	    			+ "  </head>\r\n"
    	    			+ "  <body>\r\n"
    	    			+ "      <h1 class=\"title\">Request for the resource already exists</h1>\r\n"
    	    			+ "      <a href=\"http://localhost:8080/uam/manager.jsp\">Go Back</a>\r\n"
    	    			+ "  </body>\r\n"
    	    			+ "</html>";
    	    }
    	    else
    	    {
    	    	 us.createRequest(u.getUsername(), newResource);
    	    	 System.out.println(u.getUsername());
    	    	    System.out.println(newResource);
    	    	return "<!DOCTYPE html>\r\n"
    	    			+ "<html>\r\n"
    	    			+ "  <head>\r\n"
    	    			+ "    <title>Hello, World!</title>\r\n"
    	    			+ "    <link rel=\"stylesheet\" href=\"styles.css\" />\r\n"
    	    			+ "  </head>\r\n"
    	    			+ "  <body>\r\n"
    	    			+ "      <h1 class=\"title\">Request for the resource submitted successfully</h1>\r\n"
    	    			+ "      <a href=\"http://localhost:8080/uam/manager.jsp\">Go Back</a>\r\n"
    	    			+ "  </body>\r\n"
    	    			+ "</html>";
    	    }
    	    
    	}catch(Exception e) {
    		e.printStackTrace();
    	} 
    	    return "<html><head><script>window.confirm('Failed to send request');window.location.href=/uam/"+u.getUsertype()+".jsp';</script></head></body></html>";
    }
    
    /*@POST
    @Path("/manager/requestResource")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response managerRequestsforResource(@FormParam("newResource") String newResource) {
        try {
            System.out.println("into requestResource");
            usersessn = srvltRqst.getSession();
            u = (User) usersessn.getAttribute("userSession");

            UserServices us = new UserServices();

            if (us.isRequestExist(u.getUsername(), newResource)) {
                // Request already exists
                return Response.status(Response.Status.CONFLICT)
                        .entity("<html><head><title>Request Conflict</title></head><body><h1>Request for the resource already exists</h1><a href=\"http://localhost:8080/uam/manager.jsp\">Go Back</a></body></html>")
                        .build();
            } else {
                // Submitting a new request
                us.createRequest(u.getUsername(), newResource);
                return Response.ok()
                        .entity("<html><head><title>Request Submitted</title></head><body><h1>Request for the resource submitted successfully</h1><a href=\"http://localhost:8080/uam/manager.jsp\">Go Back</a></body></html>")
                        .build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Response.serverError().build();
        }
    }*/
    
    
    
    @POST
    @Path("/manager/removemyresource")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String managerRemovesOwnResource(@FormParam("myres") String myres) {
    	EstablishConnection ec = new EstablishConnection();
        Connection connection = ec.getConnection();
        
 	    if(connection!=null)
 	    {
 	    	usersessn= srvltRqst.getSession();
 	 	    u = (User)usersessn.getAttribute("userSession");
 	    	try {
 	    		String query = "delete from requests where requestor =? AND requirement=?";
 	 	    	PreparedStatement ps = connection.prepareStatement(query);
 	 	    	ps.setString(1, u.getUsername());
 	 	    	ps.setString(2, myres);
 	 	    	if(ps.executeUpdate()>0)
 	 	    	{
 	 	    		return "<!DOCTYPE html>\r\n"
 	 	    				+ "<html>\r\n"
 	 	    				+ "  <head>\r\n"
 	 	    				+ "    <title>Hello, World!</title>\r\n"
 	 	    				+ "    <link rel=\"stylesheet\" href=\"styles.css\" />\r\n"
 	 	    				+ "  </head>\r\n"
 	 	    				+ "  <body>\r\n"
 	 	    				+ "      <h1 class=\"title\">Resource removed successfully!</h1>\r\n"
 	 	    				+ "      <a href=\"http://localhost:8080/uam/member.jsp\">Go Back</a>\r\n"
 	 	    				+ "  </body>\r\n"
 	 	    				+ "</html>";
 	 	    	}
			} catch (Exception e) {
				// TODO: handle exception
			}
 	    }
        
    	return"<!DOCTYPE html>\r\n"
    			+ "<html>\r\n"
    			+ "  <head>\r\n"
    			+ "    <title>Hello, World!</title>\r\n"
    			+ "    <link rel=\"stylesheet\" href=\"styles.css\" />\r\n"
    			+ "  </head>\r\n"
    			+ "  <body>\r\n"
    			+ "      <h1 class=\"title\">Something Went Wrong!</h1>\r\n"
    			+ "      <a href=\"http://localhost:8080/uam/member.jsp\">Go Back</a>\r\n"
    			+ "  </body>\r\n"
    			+ "</html>";
    }
    
    @POST
    @Path("/manager/becomeadmid")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String managerToBecomeAdmin(@FormParam("new_manager") String assignedmanager ) {
    	
    	EstablishConnection ec = new EstablishConnection();
        Connection connection = ec.getConnection();
        try {
 	    if(connection!=null)
 	    {
 	    	usersessn= srvltRqst.getSession();
 	 	    u = (User)usersessn.getAttribute("userSession");
 	 	    UserServices us = new UserServices();
 	 	    if(us.isRequestExist(u.getUsername(), "admin")) {
 	 	    	return "<!DOCTYPE html>\r\n"
 	 	    			+ "<html>\r\n"
 	 	    			+ "  <head>\r\n"
 	 	    			+ "    <title>Hello, World!</title>\r\n"
 	 	    			+ "    <link rel=\"stylesheet\" href=\"styles.css\" />\r\n"
 	 	    			+ "  </head>\r\n"
 	 	    			+ "  <body>\r\n"
 	 	    			+ "      <h1 class=\"title\">Request already exists</h1>\r\n"
 	 	    			+ "      <a href=\"http://localhost:8080/uam/manager.jsp\">Go Back</a>\r\n"
 	 	    			+ "  </body>\r\n"
 	 	    			+ "</html>";
 	 	    }
 	 	    else {
 	 	    String query="INSERT INTO requests (requestor, requirement) VALUES (?,?)";
 	 	    PreparedStatement ps= connection.prepareStatement(query);
 	 	    ps.setString(1, u.getUsername());
 	 	    ps.setString(2, "admin");
 	 	    if(ps.executeUpdate()>0) {
 	 	    	if(us.isRequestExist(assignedmanager, "manager")) {
 	 	    		return "The request already exists";
 	 	    	}
 	 	    	else {
 	 	    		String qory="insert into requests (requestor, requirement) VALUES (?,?)";
 	 	    		PreparedStatement p= connection.prepareStatement(qory);
 	 	    		p.setString(1, assignedmanager);
 	 	    		p.setString(2, "manager");
 	 	    		p.executeUpdate();
 	 	    	}
 	 	    	
 	 	    }
 	 	    }
 	 	    
 	    }
        } catch (Exception e) {
			// TODO: handle exception
		}
		return "<!DOCTYPE html>\r\n"
				+ "<html>\r\n"
				+ "  <head>\r\n"
				+ "    <title>Hello, World!</title>\r\n"
				+ "    <link rel=\"stylesheet\" href=\"styles.css\" />\r\n"
				+ "  </head>\r\n"
				+ "  <body>\r\n"
				+ "      <h1 class=\"title\">Request to become admin submitted successfully and also assigned new manager</h1>\r\n"
				+ "      <a href=\"http://localhost:8080/uam/manager.jsp\">Go Back</a>\r\n"
				+ "  </body>\r\n"
				+ "</html>";
    }
}
