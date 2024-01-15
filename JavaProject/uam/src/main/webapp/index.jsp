<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <style>
    body {
	  font-family: 'Arial', sans-serif;
	  background: linear-gradient(45deg, #000, #2ecc71); /* Black to green gradient background */
	  color: #fff; /* Text color */
	  margin: 0;
	  display: flex;
	  justify-content: center;
	  align-items: center;
	  height: 100vh;
    }

    .container {
      display: flex;
    }

    .button {
      padding: 10px 20px;
      margin: 0 10px;
      font-size: 16px;
      border: none;
      cursor: pointer;
      border-radius: 5px;
    }

    .login-button {
      background-color: #3498db;
      color: #fff;
    }

    .signup-button {
      background-color: #2ecc71;
      color: #fff;
    }
    h1 {
	  font-family: 'Arial', sans-serif;
	  color: #333; /* Text color */
	  background-color: #f8f8f8; /* Background color */
	  padding: 20px; /* Padding around the text */
	  text-align: center; /* Center the text */
	  border-radius: 8px; /* Rounded corners */
	  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1); /* Box shadow for a subtle lift */
	}
    
  </style>
</head>
<body>
	<h1>User Access Management System</h1>
  <div class="container">
    <form action="http://localhost:8080/uam/Login.html"><button  class="button login-button" type="submit">Login</button></form>
    <form action="http://localhost:8080/uam/Signup.html"><button class="button signup-button" type="submit">Sign Up</button></form>
  </div>
</body>
</html>
