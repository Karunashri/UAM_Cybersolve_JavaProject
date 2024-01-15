package models;

import java.sql.Date;


public class User {
	
	private long userId;
	private String firstName;
	private String lastName;
	private String username;
	private String password;
	private String user_type;
	private String manager;
	private String security_question;
	private String security_answer;
	
	
	
	public User() {
		super();
	}



	public User(long userId, String firstName, String lastName, String username, String password,
			String user_type, String manager, String secuity_question, String security_answer) {
		super();
		this.userId = userId;
		this.firstName = firstName;
		this.lastName = lastName;
		this.username = username;
		this.password = password;
		this.user_type = user_type;
		this.manager = manager;
		this.security_question = security_question;
		this.security_answer = security_answer;
	}



	public long getId() {
		return userId;
	}



	public void setId(long userId) {
		this.userId = userId;
	}



	public String getFirstName() {
		return firstName;
	}



	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}



	public String getLastName() {
		return lastName;
	}



	public void setLastName(String lastName) {
		this.lastName = lastName;
	}



	public String getUsername() {
		return username;
	}



	public void setUsername(String username) {
		this.username = username;
	}



	public String getPassword() {
		return password;
	}



	public void setPassword(String password) {
		this.password = password;
	}



	public String getUsertype() {
		return user_type;
	}



	public void setUsertype(String user_type) {
		this.user_type = user_type;
	}
	


	public String getManager() {
		return manager;
	}



	public void setManager(String manager) {
		this.manager = manager;
	}


	

	public String getSecurity_question() {
		return security_question;
	}



	public void setSecurity_question(String security_question) {
		this.security_question = security_question;
	}



	public String getSecurity_answer() {
		return security_answer;
	}



	public void setSecurity_answer(String security_answer) {
		this.security_answer = security_answer;
	}



	@Override
	public String toString() {
		return "User [userId=" + userId + ", firstName=" + firstName + ", lastName=" + lastName + ", username="
				+ username + ", password=" + password + ", user_type=" + user_type + ", manager=" + manager + ", security_question=" + security_question
				+ ", security_answer=" + security_answer + "]";
	}



	


	
	
	
	
	
	

}
