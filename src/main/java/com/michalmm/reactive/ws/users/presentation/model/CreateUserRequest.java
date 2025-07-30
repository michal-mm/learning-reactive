package com.michalmm.reactive.ws.users.presentation.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;


public class CreateUserRequest {

	@NotBlank(message="First name can't be empty")
	@Size(min=2, max=50, message="first name has to have between 2 and 50 characters")
	private String firstName;
	
	@NotBlank(message="Last name can't be empty")
	@Size(min=2, max=50, message="Last name has to have between 2 and 50 characters")
	private String lastName;
	
	@NotBlank(message="email can't be empty")
	@Email(message="Please enter a valid email address")
	private String email;
	
	@NotBlank(message="Password can't be empty")
	@Size(min=8, max=16, message="passsword has to have between 8 and 16 characters!")
	private String password;
	
	public CreateUserRequest () {}
	
	public CreateUserRequest(String firstName, String lastName, String email, String password) {
		super();
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.password = password;
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

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public String toString() {
		return "CreateUserRequest [firstName=" + firstName + ", lastName=" + lastName + ", email=" + email
				+ ", password=" + password + "]";
	}
	
	
}
