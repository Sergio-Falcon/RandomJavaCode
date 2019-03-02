package model;
//CS 4743 Assignment 4 by <Sergio Falcon>

import java.sql.Timestamp;
import java.util.ArrayList;

import controller.LastModifiedException;
import driver.AuthorAppSingleton;
import model.AuditTrail;

public class Author {
	
	private int id;
	private String firstName;
	private String lastName;
	private String dob;
	private String gender;
	private String website;
	private Timestamp lastModified;
	private AuthorAppSingleton authorAppInstance;
	
	private String name;
	
	public Author(){
		id = 0;
		firstName = null;
		lastName = null;
		dob = null;
		gender = null;
		website = null;
		lastModified = null;
		authorAppInstance = null;
	}
	public Author(int id, String firstName, String lastName, String dob, String gender, String website){
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
		this.dob = dob;
		this.gender = gender;
		this.website = website;
		lastModified = null;
		authorAppInstance = null;
	}
	
	public Author(int id, String firstName, String lastName, String dob, String gender, String website, Timestamp lastModified){
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
		this.dob = dob;
		this.gender = gender;
		this.website = website;
		this.lastModified = lastModified;
		authorAppInstance = null;
	}
	
	public Author(int id, String firstName, String lastName, String dob, String gender, String website, Timestamp lastModified, AuthorAppSingleton authorAppInstance){
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
		this.dob = dob;
		this.gender = gender;
		this.website = website;
		this.lastModified = lastModified;
		this.authorAppInstance = authorAppInstance;
	}
	
	public void updateAuthor()throws LastModifiedException{
		try{
			authorAppInstance.getAuthorGatewayInstance().updateAuthor(this);
		} catch(LastModifiedException e){
			throw new LastModifiedException(e);
		}
	}
	public void saveAuthor() throws LastModifiedException{
		if(id == 0){
			authorAppInstance.getAuthorGatewayInstance().saveAuthor(this);
		}
		else{
			try{
				updateAuthor();
			} catch(LastModifiedException e){
				throw new LastModifiedException(e);
			}
		}
	}
	
	public ArrayList<AuditTrail> getAuditTrail() {
		return authorAppInstance.getAuthorGatewayInstance().getAudits(this);
	}
	
	//Getters and setters
	//TODO: may want to do validation in the setter functions
	public int getId(){
		return id;
	}
	public void setId(int id){
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
	
	
	public String getDate() {
		String datestring = dob.toString();
		return datestring;
	}
	
	public String getDob() {
		return dob;
	}
	public void setDob(String dob) {
		this.dob = dob;
	}

	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public String getWebsite() {
		return website;
	}
	public void setWebsite(String website) {
		this.website = website;
	}
	public Timestamp getLastModified() {
		return lastModified;
	}
	public void setLastModified(Timestamp lastModified) {
		this.lastModified = lastModified;
	}
	public AuthorAppSingleton getAuthorAppInstance() {
		return authorAppInstance;
	}
	public void setAuthorAppInstance(AuthorAppSingleton authorAppInstance) {
		this.authorAppInstance = authorAppInstance;
	}
	public String getName() {
		name = String.format("%s %s", firstName, lastName);
		return name;
	}
	
	@Override
	public String toString(){
		return id + ": " + firstName + lastName;
	}
}
	
