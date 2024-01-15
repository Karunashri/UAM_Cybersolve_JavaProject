<%@page import="java.lang.reflect.Array"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    <%@ page import="models.User" %>   
    <%@ page import="models.Request" %>
     <%@ page import="java.util.List" %>
<%@ page import="services.UserServices" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>Insert title here</title>
<style>
body {
            font-family: Arial, sans-serif;
            background-color: #f4f4f4;
            margin: 20px;
        }

        h1 {
            color: #333;
        }

        table {
            width: 80%;
            border-collapse: collapse;
            margin-top: 20px;
            margin-bottom: 20px;
        }

        th, td {
            border: 1px solid #ddd;
            padding: 10px;
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
<% 
UserServices userService = new UserServices();
HttpSession sess = request.getSession(false);
User usr = (User) session.getAttribute("userSession");
UserServices us = new UserServices();
%>
<body>

<h1>Requests made by me and their status</h1>
	
		<%
			List<Request> al= us.getAllRequestsByUser(usr.getUsername());
		%>
		<table border="2">
			<tr>
				<th>Requested Resource</th>
				<th>Request Status</th>
			</tr>
			<% for (Request rq : al) {%>
			<tr>
				<td><%=rq.getRequirement() %></td>
				<td><%=rq.getRequestStatus() %></td>
			</tr>	
			<% } %>	
		</table>
		
		<a href="http://localhost:8080/uam/<%=usr.getUsertype()%>.jsp">Go Back</a>
</body>
</html>