package com.bookmyride.demo.model;

/**
 * 
 * @author seeya.wamane
 *
 */
public class User {
	
	private Long id;
	private String firstName;
	private String lastName;
	private String phoneNo;
	private String email;
	
	public User(Long id, String firstName, String lastName, String phoneNo, String email) {
		super();
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
		this.phoneNo = phoneNo;
		this.email = email;
	}

	public Long getId() {
		return id;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public String getPhoneNo() {
		return phoneNo;
	}

	public String getEmail() {
		return email;
	}
	
}
