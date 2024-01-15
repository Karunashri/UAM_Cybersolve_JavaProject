<%@page import="services.UserServices"%>
<%@ page import="models.User" %>
<%@ page import="java.util.List" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>Insert title here</title>
</head>
<%	session= request.getSession(false);
	UserServices us = new UserServices();
	User manager = (User) session.getAttribute("userSession");
	//List<String> al= us.listMyTeam(request);
	List<User> al = us.getAllUserManager(manager.getUsername());
%>
<body>
		<table border="2">
			<tr>
				<th>Team Members</th>
			</tr>
			<tr><% for (User str : al) { %>
				<td><%= str.getUsername() %></td>
				<% } %>
			</tr>
		</table>
</body>
</html>