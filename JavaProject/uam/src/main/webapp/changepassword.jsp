<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    <%@ page import="models.User" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>Insert title here</title>
</head>
<body>

<%
	session = request.getSession(false);
	User usr=(User)session.getAttribute("usersessn");
	
%>

	<form action="/uam/webresources/myresource/changepassword" method="post">
	<label for="Old_password">Enter old password:</label>
	 <input type="text" id="old_password" name="old_pwd" required>


	<label for="Old_password">Enter new password:</label>
	 <input type="text" id="new_password" name="new_pwd" required>
	 
	 <button type="submit">Submit</button>
	
</form>

</body>
</html>