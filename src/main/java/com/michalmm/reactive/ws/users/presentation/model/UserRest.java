package com.michalmm.reactive.ws.users.presentation.model;

import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonInclude;

public class UserRest {

	private UUID id;
	private String firstName;
	private String lastName;
	private String email;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<AlbumRest> albums;
	
	public UserRest() {}

	public UserRest(UUID id, String firstName, String lastName, String email, List<AlbumRest> albums) {
		super();
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.albums = albums;
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
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

	public List<AlbumRest> getAlbums() {
		return albums;
	}

	public void setAlbums(List<AlbumRest> albums) {
		this.albums = albums;
	}
	
	
}
