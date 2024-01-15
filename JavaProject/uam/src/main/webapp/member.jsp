<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="b5.uam.MyResource" %>
<%@ page import="java.util.List" %>
<%@ page import="services.UserServices" %>
<%@ page import="models.User" %>
<%@ page import="models.Request" %>
<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta http-equiv="Cache-Control" content="no-store, no-cache, must-revalidate">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Member Dashboard</title>
  
   <style>
   body, h1, h2, h3, p, label, input, select, button {
    margin: 10px;
    padding: 0;
}

body {
	text-align: center;
    font-family: Arial, sans-serif;
}

.container {
    width: 40%;
    margin: 100px auto;
}

.row {
    margin-bottom: 20px;
}

/* Style for the links */
a {
    text-decoration: none;
    color: #0066cc;
}

/* Style for the modal */
.modal-content, .modal-content1 {
    display: none;
    position: fixed;
    transform: translate(-10%, -95%);
    background-color: #f1f1f1;
    padding: 20px;
    border: 1px solid #ccc;
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
    z-index: 1;
}

/* Style for the close button in the modal */
.close-btn {
 
    cursor: pointer;
    font-weight: bold;
}

/* Style for the table */
table {
    width: 90%;
    border-collapse: collapse;
    margin-top: 10px;
}

table, th, td {
    border: 1px solid #ddd;
}

th, td {
    padding: 10px;
     text-align: left;
}

/* Style for the form */
form {
    margin-bottom: 20px;
}

/* Style for buttons */
button {
    background-color: #4CAF50;
    color: white;
    padding: 10px 15px;
    border: none;
    border-radius: 3px;
    cursor: pointer;
}

button:hover {
    background-color: #45a049;
}

/* Style for labels */
label {
    display: block;
    margin-bottom: 5px;
}

/* Style for input fields */
input, select {
    padding: 8px;
    margin-bottom: 10px;
}

/* Style for the body message */
#login {
    text-align: center;
    margin-top: 50px;
}

/* Additional styling for specific elements */
#role {
    color: #333;
    font-weight: normal;
}

#userlogout {
        position: fixed;
        bottom: 10px; /* Adjust the distance from the bottom */
        right: 20px; /* Adjust the distance from the right */
        font-size: 19px;
        color: #ff0000;
    }

#chk {
    background-color: #008CBA;
}

   </style>
    <script>
    function openRequests(){
    	document.getElementById('modal-content1').style.display ='block';
    }
    /* function openResources(){
    	document.getElementById('modal-content').style.display ='block';
    } */
    function closeResources(){
    	document.getElementById('modal-content').style.display ='none';
    }
   /*  function closeRequests(){
    	document.getElementById('modal-content1').style.display ='none';
    } */
    
    </script>
<% response.setHeader("Cache-Control", "no-store"); %> 
</head>
<%
	session = request.getSession(false);
	//User u =(User)session.getAttribute("userSession");
	User usrSession =null;
	String selecteduser=null;
	String resourceforuser=null;
	if(session!=null)
	{
		Object ob= session.getAttribute("userSession");
		if(ob instanceof User)
		{
			usrSession=(User)ob;
		}
		if(usrSession ==null)
		{%>
			
			<body>
			<div id="login">
			<h2>Session expired log in again</h2>
			<a href ="Login.html">Login here</a>
			</div>
			</body>
			<% 
		}
		else{
	
%>
<body>
<form>
   
   	<h1>Welcome, <%=usrSession.getFirstName() %><label id="role">[member]</label></h1><br>
    <label><a href ="/uam/webresources/myresource/logout" id="userlogout">Log out</a></label>
    <div clas="container">
    <div class="row">
    
		<!-- Link to check available resources -->
   <!--   <a href="/uam/Resource.jsp" id="chck">Check My Resources</a><br><br>-->
   
</form>
<form action="/uam/webresources/myresource/member/checkmyresources" method="post">
	<button type="submit" name="mereresources">Check My Resources</button>
</form>
<div class="modal-content" id="modal-content">
<div class="content">
<%
	UserServices us= new UserServices();
	List<String> availableresources= us.getAllResourcesByUser(usrSession.getUsername());
%>

<%
	if(availableresources==null || availableresources.isEmpty()){%>
		<h3>No Resources in Database</h3>
	<%}else{ %>
		<table cellpadding="10">
			<tr>
				<th>Available Resources</th>
			</tr>
			<%for(String r: availableresources){ %>
			<tr>
				<td><input type="text" name="resc" value="<%=r %>" readonly></td>
			</tr>
	<%} %>
	</table>
	<%} %>
	</div>
	</div>
	</div>
	

<!-- Form to request new resources -->
<div class="row">
    <form action="/uam/webresources/myresource/requestResource" method="post">
	 <label for="newResource">Select New Resource:</label>
	 <%UserServices un= new UserServices();
	 List<String> newresreq= un.resourcesToRequest(usrSession.getUsername());
	 //List<String> newresreq =un.resourcesToRequest(usrSession.getUsername());
	 %>
        <select id="newResource" name="newResource">
        <option value="select"> Select Resource</option>
            <% 
                for (String s : newresreq) {
            %>
            <option value="<%=s %>"> <%= s %> </option>
            <% } %>
        </select>
        <button type="submit">Submit Request</button><br><br>
        </form>
        </div>
        
        <%-- <div class="row">
        <form>
        <button type="button" id="chk" onclick="openRequests()">Check Request Status</button>
        </form>
        <div class="modal-content1" id="modal-content1">
        <div class="content">
        <%
        	List<Request> rq= us.getAllRequestsByUser(usrSession.getUsername());
        %>
        <span class="close-btn" onclick="closeRequests()"> close </span>
        <label><h4>User : <%=usrSession.getUsername() %></h4></label>
        <hr><br>
        <%if(rq==null || rq.isEmpty()){ %>
        <h3>No requests made</h3>
        <%}else{ %>
        <table cellpadding="10">
        <tr>
        <th>Resource</th>
        <th>Status</th>
        </tr>
        <%for(Request r: rq){ %>
        <tr>
        <td><input type="text" name="resrc" value="<%=r.getRequirement()%>" readonly></td>
        <td><input type="text" name="status" value="<%=r.getRequestStatus()%>" readonly></td>
        
        </tr>
        <%} %>
        </table>
        <% }%>
        </div>
        </div>
        </div> --%>
        
        <form action="checkrequests.jsp">
       		<button type="submit">Check Requests</button>
       	</form>
        
        <div class="row">

		<form action="/uam/webresources/myresource/requesttobecomeadmin" method="post">
			<button type="submit" name="requestor" value="admin">Request Admin Role</button>
		</form>
   <form action="/uam/webresources/myresource/requesttobecomeadmin" method="post">
      <button type="submit" name="requestor" value="manager">Request Manager Role</button> 
 </form>
</div>
        
        <div class="row">
        <form action ="/uam/webresources/myresource/removemyresource" method = "post">
        <label for="RemoveMyResource">Remove my own resource:</label>
        <%
        
        List<String> removeresource= us.getAllResourcesByUser(usrSession.getUsername());
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
    
    <form action="/uam/changepassword.jsp" method="post">
    <button type="submit" id="pass">Change Password</button>
    </form>
    <form action="/uam/webresources/myresource/logout" method="get">
       <button type="submit">Logout</button>
    </form>
</body>
<%}} %>
</html>