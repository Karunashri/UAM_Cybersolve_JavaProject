<%@ page import="services.UserServices" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="models.User" %>
<%@ page import="models.Request" %>

<html>
<head>
<title>User List</title>
<style>

</style>

</head>
<body>

<%
HttpSession sess = request.getSession(false);
String resource = (String) sess.getAttribute("");
UserServices rq= new UserServices();
List<String> userList = rq. getAllUsersByResource(resource);
%>
<div class="container">
	<h2>Resource: <%=resource %></h2>
	<%
	try{
		if(userList==null || userList.isEmpty()){
			%>
			<label>No User with selected Resource</label>
			<%
				} else {
			%>
			<table cellpadding="10">
			
			<tr>
			<th>Request Id</th>
			<th>Requestor</th>
			<th>Resource</th>
			</tr>
			</table>
		
	<%}}
	catch(Exception e){
		e.printStackTrace();
		}
	%>
	<div id="btn"><button type ="button" onclick="closeWindow()">Close</button></div>
	<script>
	function closeWindow(){
		window.location.href = "/uam/Admin.jsp";
		}
	}
	</script>

</div>

</body>


</html>