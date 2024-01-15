<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page import="java.util.List" %>
<%@ page import="models.User" %>
<%@ page import="models.Request" %>
<%@ page import="services.UserServices" %>

<!DOCTYPE html>
<html>
<head>
    <!-- Add your existing head content here -->
   <meta charset="UTF-8">
    <meta http-equiv="Cache-Control" content="no-store, no-cache, must-revalidate">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <% response.setHeader("Cache-Control", "no-store"); %> 
</head>
<body>

<%
 	UserServices userService = new UserServices();
    HttpSession sess = request.getSession(false);
    User manager = (User) session.getAttribute("userSession");

    if (manager == null) {
%>
        <div>
            <h2>Session Expired.... Please <a href="/uam/Login.html">login</a> again</h2>
        </div>
<%
    } else {
        
        

        // Display team members
        List<User> teamMembers = userService.getAllUserManager(manager.getUsername());
%>
        <div>
            <h1>Welcome <%= manager.getFirstName() %> [Manager]</h1>
            <h2>Your Team Members:</h2>
            <form action="showmyteam.jsp">
            <button type="submit">Show Team</button>
            </form>
           <ul> 
<%
                for (User teamMember : teamMembers) {
			%>
                   <li><%= teamMember.getFirstName() %> <%= teamMember.getLastName() %> (<%= teamMember.getUsername() %>)</li>
			<%
                }
%>
           </ul> 
        </div>

        <!-- Form to get details of a team member -->
        <div>
            <h4>Add a Team Member:</h4>
            <% List<String> al = userService.getAllMember(); %>
            <form action="/uam/webresources/myresource/manager/addteammember" method="post">
                <label for="teamMemberUsername">Enter Team Member Username:</label>
                <select name="selectmember">
                <option>select</option>
               <% for (String str : al) { %>
                	<option value="<%=str %>"><%=str %></option>
                	<% } %>
                </select>
                <button type="submit">Add</button>
            </form>
        </div><br>
        
         <div>
            <h4>Remove a Team Member:</h4>
            <% 
           // HttpSession session = request.getSession(false);
           // String managerUsername = (String) sess.getAttribute("managerUsername");
            List<String> al1 = userService.listMyTeam(request); %>
            <form action="/uam/webresources/myresource/manager/removeteammember" method="post">
                <label for="teamMemberUsername">Enter Team Member Username:</label>
                <select name="selectedmember">
                <option>select</option>
               <% for (String str : al1) { %>
                	<option value="<%=str %>"><%=str %></option>
                	<% } %>
                </select>
                <button type="submit">Remove</button>
            </form>
        </div><br>

        <!-- Form to request new resources -->
<div class="row">
    <form action="/uam/webresources/myresource/manager/requestResource" method="post">
	 <label for="newResource">Select New Resource:</label>
	 <%UserServices un= new UserServices();
	 List<String> newresreq= un.resourcesToRequest(manager.getUsername());
	 //List<String> newresreq =un.resourcesToRequest(usrSession.getUsername());
	 %>
        <select id="newResource" name="newResource">
        <option value="select"> Select Resource</option>
            <% 
                for (String s : newresreq) {
            %>
            <option value="<%=s %>"><%= s %></option>
            <% } %>
        </select>
        <button type="submit">Submit Request</button><br><br>
        </form>
        </div>
        
       	<form action="checkrequests.jsp">
       		<button type="submit">Check Requests</button>
       	</form>

        <!-- Form to add a team member -->
        <div>
            <h4>Request to become Admin:</h4>
            <form action="/uam/webresources/myresource/manager/becomeadmid" method="post">
                <label for="newManager">Assign manager to:</label>
                <select name="new_manager" required>
                    <option value="select">Select</option>
<%
                        for (User teamMember : teamMembers) {
%>
                            <option value="<%= teamMember.getUsername() %>"><%= teamMember.getUsername() %></option>
<%
                        }
%>
                </select>
                <button type="submit">Submit</button>
                <input type="hidden" name="usr_role_rqst" value="admin">
            </form>
        </div><br>
<%
    }
%>


	<form action ="/uam/webresources/myresource/manager/removemyresource" method = "post">
        <label for="RemoveMyResource">Remove my own resource:</label>
        <%
        
        List<String> removeresource= userService.getAllResourcesByUser(manager.getUsername());
        %>
        <select id="myres" name="myres">
			<%-- Options for removing resources --%>
			<option value="select"> Select Resource</option>
            <% for (String r : removeresource) { %>
            <option value="<%= r %>"><%= r %></option>
            <% } %>

		</select>
		<button type="submit">Submit Request</button><br>
    </form>


<form action="/uam/webresources/myresource/logout" method="get">
       <button type="submit">Logout</button>
    </form>

</body>
</html>