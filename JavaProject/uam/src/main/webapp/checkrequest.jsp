<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ page import="services.UserServices"%>
<%@ page import="java.util.*"%>
<%@ page import="models.Request"%>
<%@ page import="models.User" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>Check Request</title>

<style>
body {
	font-family: Arial, sans-serif;
	background-color: #f4f4f4;
	margin: 0;
}

/* Style for the table */
table {
	width: 80%;
	margin: 20px auto;
	border-collapse: collapse;
	background-color: #fff;
	box-shadow: 0 0 20px rgba(0, 0, 0, 0.1);
}

/* Style for the table headers */
th, td {
	padding: 15px;
	text-align: left;
}

th {
	background-color: #3498db;
	color: #fff;
}

/* Style for alternating row colors */
tbody tr:nth-child(even) {
	background-color: #f2f2f2;
}

/* Style for table cells */
td {
	border-bottom: 1px solid #ddd;
}

/* Hover effect for table rows */
tbody tr:hover {
	background-color: #e0e0e0;
}

/* Style for the header */
h1 {
	text-align: center;
	color: #333;
	padding: 20px;
	background-color: #3498db;
	margin: 0;
}
a.goBackLink {
    position: fixed;
    bottom: 10px; /* Adjust the distance from the bottom */
    right: 10px; /* Adjust the distance from the right */
    font-size: 16px;
    color: blue;
    text-decoration: none;
    padding: 10px;
    background-color: #fff;
    border: 1px solid #3498db;
    border-radius: 5px;
  }
</style>

<script>
	function approveRequest(status, user, resource){
		document.getElementById('stts').value = status;
		document.getElementById('unm').value = user;
		document.getElementById('req').value = resource;
		document.getElementById('btn1').click();
	}
</script>
</head>
<body>

<%
	session = request.getSession(false);
	User usr=(User)session.getAttribute("usersessn");
	
%>
<a href="/uam/admin.jsp" class="goBackLink">Go back</a>
	<form action="/uam/webresources/myresource/admin/checkrequests" method="post">
	<table>
		<thead>
			<tr>
		
				<th>UserName</th>
				<th>Requirement</th>
				<th>Approve</th>
				<th>Reject</th>
	
			</tr>
		</thead>
		<tbody>
			<%
        UserServices userServices = new UserServices();
        List<Request> resourceRequests = userServices.getAllResourceRequests();
        
        for (Request req : resourceRequests) {
        	if(!req.getRequirement().equals("rmanager")){
        	
    %>
			<tr>
			
				<td><%= req.getRequestor() %></td>
				<td><%= req.getRequirement() %></td>
				<td><button type="button" onclick="approveRequest('approved', '<%=req.getRequestor()%>','<%=req.getRequirement()%>' )">Approve</button></td>
				<td><button type="button" onclick="approveRequest('denied', '<%=req.getRequestor()%>','<%=req.getRequirement()%>' )">Reject</button></td>
				
			</tr>
			<%
        	}
        }
    %>


		</tbody>
	</table>
	
	<input type="hidden" id="stts" name="status" value="">
	<input type="hidden" id="unm" name="user" value="">
	<input type="hidden" id="req" name="requirement" value="">
	<button type="submit" id="btn1" style="display: none"></button>
	
</form>


</body>
</html>