<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="b5.uam.MyResource" %>
<%@ page import="services.UserServices" %>
<%@ page import="javax.ws.rs.core.MediaType" %>
<%@ page import="javax.ws.rs.FormParam" %>
<%@ page import="models.User" %>
<%@ page import="models.Request" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta http-equiv="Cache-Control" content="no-store, no-cache, must-revalidate">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Admin Dashboard</title>
    <!-- Add your CSS stylesheets or link them here -->
    <style>
    #rqstBtn{
    	border: 0px;
    	text-decoration: underline;
    	cursor: pointer;
    	font-size: 20px;
    }
    	#rqsts{
    		visibility: hidden;
    	}
    </style>
    <script>
    	function showRequests(){
    	console.log("hello...");
    		var shw = document.getElementById("rqsts");
    		shw.style.visibility = 'visible';
    		
    
    	}
    </script>
    <% response.setHeader("Cache-Control", "no-store"); %> 
</head>
<body>
    
	<%
		HttpSession sess = request.getSession(false);
	String selecteduser = null;
		User usrSession = null;
		if(sess!=null)
		{
			Object ob = sess.getAttribute("userSession");
			if(ob instanceof User)
			{
				usrSession = (User)ob;
			}
		}
	%>
	<h1>Welcome <%=usrSession.getFirstName()%> Admin!</h1>

	<a href="/uam/checkrequest.jsp" Check requests>Check requests</a>    
    
	
    

    <!-- Add new resource -->
    <form action="/uam/webresources/myresource/admin/addResource" method="post">
        <label for="newResource">Add New Resource:</label>
        <input type="text" id="newResource" name="newResource" required>
        <input type="submit" value="Add Resource">
    </form><br>

    <!-- Remove resource from the database -->
	<form action="/uam/webresources/myresource/admin/removeResource" method="post">
        <label for="resourceToRemove">Remove Resource from Database:</label>
        <select id="resourceToRemove" name="resourceToRemove" required>
            <!-- Populate dropdown options with resources from the database -->
            <option value="select"> Select Resource</option>
            <%
                UserServices us = new UserServices();
                List<String> resources = us.getAllResources();
                for (String resource : resources) {
            %>
                <option value="<%= resource %>"><%= resource %></option>
            <%
                }
            %>
        </select>
        <input type="submit" value="Remove Resource">
    </form><br>


    
    <form action="/uam/webresources/myresource/admin/checkUserByResource" method="post">
        <label for="usernameToRemoveFrom">Check resources for user:</label>
        <input type="text" id="username" name="username" required>
        <input type="submit" value="Check resources allocated">
    </form><br>

    
    <form action="/uam/webresources/myresource/admin/checkResourcesByUser" method="post">
        <label for="resourcename">Check users for resource:</label>
        <input type="text" id="resourcename" name="resourcename" required>
        <input type="submit" value="Check Users">

    </form><br>
    
    <%
    	HttpSession rsrcSession = request.getSession(false);
    	if(rsrcSession != null)
    	{
    		selecteduser =(String)rsrcSession.getAttribute("selecteduser");
    	}
    %>
    
    <!-- display drop down of users to remove his resource -->
    <form action="/uam/webresources/myresource/admin/users" method ="post">
    <label for="">Remove resource from user :</label>
    
    <%
	UserServices um = new UserServices();
    List<String> al1 = um.getAllUsers();
    %>
	<select name="rmvresusr" id ="rmvresusr">
	<%for(String str : al1) {%>
	<option value="<%=str%>"><%=str%></option>
	<% } %>
    	
    </select>
    <button type="submit">Submit</button>
    </form>
    
<% List<String> al2 = um.removeResourceFromUser(request); %>
    <form action="/uam/webresources/myresource/admin/removeresourcefromuser" method="post">
    <label for="user"> Resource_Name:</label>
    <select id="rmv_res_frm_usr" name="rmv_res_frm_usr" required>
        <!-- Populate dropdown options with resources allocated to a specific user -->
        <option value="select"> Select Resource</option>
        
        <%
//             UserServices un = new UserServices();
//             List<String> usrbyres = un.getAllResourcesByUser(selecteduser);
            for (String res : al2) {
        %>
            <option value="<%=res %>"><%=res %></option>
        <% } %>
    </select>
    <input type="submit" value="Submit">
</form>
        
<form action="/uam/webresources/myresource/admin/viewallusers" method="post">
<button type="submit">View All Users</button>
</form>
<form action="/uam/webresources/myresource/logout" method="get">
<button type="submit">Logout</button>
</form>
</body>
</html>