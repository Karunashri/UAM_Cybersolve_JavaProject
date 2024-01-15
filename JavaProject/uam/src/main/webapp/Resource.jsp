<%@page import="models.User"%>
<%@ page import="java.util.List" %>
<%@ page import="services.UserServices" %>
<%@ page import="models.Request" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Available Resources</title>
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
    <h1>Available Resources</h1>


    <% 
    HttpSession ssn = request.getSession(false);
    User usr=(User)ssn.getAttribute("usr");
       UserServices userServices = new UserServices();
    
      List<String> availableResources = userServices.getMyResources(usr.getUsername());
      %>

    <ul>
        <% for (String resource : availableResources) { %>
            <li><%= resource %></li>
        <% } %>
    </ul>

    <!-- Add a link back to the member.jsp page -->
    <a href="/uam/member.jsp">Go Back</a>
</body>
</html>
