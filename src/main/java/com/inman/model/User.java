package com.inman.model;


public class User {
	private long id;
	private String userName;
	private String password;
	
	public User( long id, String userName, String password ) {
		this.id = id;
		this.userName = userName;
		this.password = password;
	}
	
	@Override
	public String toString() {
		return String.format( "User[ id = %d, userName = %s, password = %s ]", 
				id, userName, password );
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}

