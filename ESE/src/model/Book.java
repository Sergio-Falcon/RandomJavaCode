package model;

import java.sql.Timestamp;
import java.util.ArrayList;

import controller.LastModifiedException;
import driver.AuthorAppSingleton;

//CS 4743 Assignment 5 by <Sergio Falcon>
public class Book {
	
	private int id;
	private String title;
	private String publisher;
	private String datePublished;
	private String summary;
	private Author author;
	private Timestamp lastModified;
	private AuthorAppSingleton authorAppInstance;
	
	public Book(){
		id = 0;
		title = null;
		publisher = null;
		datePublished = null;
		summary = null;
		author = null;
		lastModified = null;
		authorAppInstance = null;
	}
	
	public Book(int id, String title, String publisher, String datePublished, String summary, Author author){
		this.id = id;
		this.title = title;
		this.publisher = publisher;
		this.datePublished = datePublished;
		this.summary = summary;
		this.author = author;
		lastModified = null;
		authorAppInstance = null;
	}
	
	public Book(int id, String title, String publisher, String datePublished, String summary, Author author, Timestamp lastModified){
		this.id = id;
		this.title = title;
		this.publisher = publisher;
		this.datePublished = datePublished;
		this.summary = summary;
		this.author = author;
		this.lastModified = lastModified;
		authorAppInstance = null;
	}
	
	public Book(int id, String title, String publisher, String datePublished, String summary, Author author, Timestamp lastModified, AuthorAppSingleton authorAppInstance){
		this.id = id;
		this.title = title;
		this.publisher = publisher;
		this.datePublished = datePublished;
		this.summary = summary;
		this.author = author;
		this.lastModified = lastModified;
		this.authorAppInstance = authorAppInstance;
	}
	
	//TODO: validation functions
	
	public void saveBook() throws LastModifiedException{
		if(id == 0){
			authorAppInstance.getBookGatewayInstance().saveBook(this);
		}
		else{
			try{
				updateBook();
			} catch(LastModifiedException e){
				throw new LastModifiedException(e);
			}
		}
	}
	
	public void updateBook() throws LastModifiedException{
		try{
			authorAppInstance.getBookGatewayInstance().updateBook(this);
		} catch(LastModifiedException e){
			throw new LastModifiedException(e);
		}
	}
	
	public ArrayList<AuditTrail> getAuditTrail(){
		return authorAppInstance.getBookGatewayInstance().getAudits(this);
	}
	
	//getters and setters
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getPublisher() {
		return publisher;
	}
	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}
	public String getDatePublished() {
		return datePublished;
	}
	public void setDatePublished(String datePublished) {
		this.datePublished = datePublished;
	}
	public String getSummary() {
		return summary;
	}
	public void setSummary(String summary) {
		this.summary = summary;
	}
	public Author getAuthor() {
		return author;
	}
	public void setAuthor(Author author) {
		this.author = author;
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
	
	
	@Override
	public String toString(){
		return id + ": " + title + " " + publisher;
	}

}
