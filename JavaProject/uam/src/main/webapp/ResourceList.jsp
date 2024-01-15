<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ page import="models.User" %>
<%@ page import="services.UserServices" %>
<%@ page import="java.util.*" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>Insert title here</title>
 <style>
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

<%
HttpSession ssn = request.getSession(false);
String resname=(String)ssn.getAttribute("resrc_name");
//out.println("resource name -> "+resname);
UserServices us= new UserServices();
String resource=null;
List<String> userswithresources = us.getAllUsersByResource(resname);
%>

    <h2>Users with Resource: <%= resname %></h2>

    <% if(userswithresources != null && !userswithresources.isEmpty()) { %>
        <ul>
            <% for (String user : userswithresources) { %>
                <li><%= user %></li>
            <% } %>
        </ul>
    <% } else { %>
        <p>No users found with the specified resource.</p>
    <% } %>
     <a href="/uam/admin.jsp">Go Back</a>
</body>

</html>
