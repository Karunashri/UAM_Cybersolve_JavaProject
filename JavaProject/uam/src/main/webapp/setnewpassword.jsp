<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
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
            margin: 0;
            padding: 0;
            display: flex;
            justify-content: center;
            align-items: center;
            height: 100vh;
        }

        form {
            background-color: #fff;
            padding: 20px;
            border-radius: 8px;
            box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
            max-width: 400px;
            width: 100%;
            text-align: center;
        }

        h3 {
            color: #333;
            margin-bottom: 20px;
        }

        input {
            width: 100%;
            padding: 10px;
            margin-bottom: 16px;
            box-sizing: border-box;
            border: 1px solid #ccc;
            border-radius: 4px;
            font-size: 16px;
        }

        button {
            background-color: #007bff;
            color: #fff;
            padding: 12px;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            font-size: 16px;
        }

        button:hover {
            background-color: #0056b3;
        }
    </style>

</head>
<body>
<%
	HttpSession sess = request.getSession(false);
	String ob= (String)sess.getAttribute("selected");
	
%>
	
	<form action="/uam/webresources/myresource/setnewpassword" method="post">
	<%
		UserServices us= new UserServices();
	
	%>
	<h1>Set new password by answering your security question </h1>
	<input type="text" value="<%=us.getSecurityQuestion(ob) %>" readonly="readonly">
	<input type="text" name="answer" placeholder="Answer" required="required">
	<h3>Set New Password</h3>
	<input type="password" name="newpassword">
	<button type="submit">Submit</button>
	
	</form>

</body>
</html>