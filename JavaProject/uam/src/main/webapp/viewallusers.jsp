<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ page import="services.UserServices" %>
<%@ page import="models.User" %>
<%@ page import="java.util.List" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>View All Users</title>
<style>
        table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 20px;
        }

        th, td {
            border: 1px solid #ddd;
            padding: 8px;
            text-align: left;
        }

        th {
            background-color: #f2f2f2;
        }
        a {
            display: inline-block;
            padding: 10px 20px;
            text-decoration: none;
            background-color: #007bff;
            color: #fff;
            border-radius: 5px;
        }

        a:hover {
            background-color: #0056b3;
        }
    </style>

</head>
<body>

	<h1>List Of All Users With Details</h1>
	
	<table>
    <thead>
        <tr>
            <th>First Name</th>
            <th>Last Name</th>
            <th>Username</th>
            <th>User Type</th>
            <th>Manager</th>
            <th>Action</th>
        </tr>
    </thead>
    
    <tbody>
    	
    	<%
        UserServices userServices = new UserServices();
        List<User> allusers = userServices.viewAllUsers();
        
        for (User users : allusers) {
    %>
			<tr>
			
				<td><%= users.getFirstName() %></td>
				<td><%= users.getLastName() %></td>
				<td><%= users.getUsername() %></td>
				<td><%= users.getUsertype() %></td>
				<td><%= users.getManager() %></td>
				<td>
                        <form action="/uam/webresources/myresource/admin/removeuser" method="post">
                            <input type="hidden" name="uname" value="<%= users.getUsername() %>">
                            <button type="submit">Remove</button>
                        </form>
                    </td>
			
			</tr>
			<%
        }
    %>
    	
    </tbody>
    </table>
<a href="/uam/admin.jsp">Go Back</a>

</body>
</html>