package model;

import java.sql.Timestamp;

import driver.AuthorAppSingleton;

//CS 4743 Assignment 4 by <Sergio Falcon>
public class AuditTrail {

	private String recordDescriptor;
	private Timestamp dateAdded;
	private String message;
	private AuthorAppSingleton authorAppInstance;
	
	public AuditTrail(){
		recordDescriptor = null;
		dateAdded = null;
		message = null;
		authorAppInstance = null;
	}
	
	public AuditTrail(String recordDescriptor, Timestamp dateAdded, String message, AuthorAppSingleton authorAppInstance){
		this.recordDescriptor = recordDescriptor;
		this.dateAdded = dateAdded;
		this.message = message;
		this.authorAppInstance = authorAppInstance;
	}

	public String getRecordDescriptor() {
		return recordDescriptor;
	}

	public void setRecordDescriptor(String recordDescriptor) {
		this.recordDescriptor = recordDescriptor;
	}

	public Timestamp getDateAdded() {
		return dateAdded;
	}

	public void setDateAdded(Timestamp dateAdded) {
		this.dateAdded = dateAdded;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public AuthorAppSingleton getAuthorAppInstance() {
		return authorAppInstance;
	}

	public void setAuthorAppInstance(AuthorAppSingleton authorAppInstance) {
		this.authorAppInstance = authorAppInstance;
	}
	
	
}
