package model;

import java.sql.Timestamp;
import java.util.ArrayList;

import controller.LastModifiedException;
import driver.AuthorAppSingleton;

public class Library {

	private int id;
	private String libraryName;
	private ArrayList<LibraryBook> books;
	private Timestamp lastModified;
	private AuthorAppSingleton authorAppInstance;
	
	public Library(){
		id = 0;
		libraryName = null;
		books = new ArrayList<LibraryBook>();
		lastModified = null;
		authorAppInstance = null;
	}
	
	public Library(AuthorAppSingleton authorAppInstance){
		id = 0;
		libraryName = null;
		books = new ArrayList<LibraryBook>();
		lastModified = null;
		this.authorAppInstance = authorAppInstance;
	}
	
	public Library(int id, String libraryName, ArrayList<LibraryBook> books, Timestamp lastModified, AuthorAppSingleton authorAppInstance){
		this.id = id;
		this.libraryName = libraryName;
		this.books = books;
		this.lastModified = lastModified;
		this.authorAppInstance = authorAppInstance;
	}
	
	//TODO: validation functions
	
	public void saveLibrary() throws LastModifiedException{
		if(id == 0){
			authorAppInstance.getLibraryGatewayInstance().saveLibrary(this);
		}
		else{
			try{
				updateLibrary();
			} catch(LastModifiedException e){
				throw new LastModifiedException(e);
			}
		}
	}
	
	public void updateLibrary() throws LastModifiedException{
		try{
			authorAppInstance.getLibraryGatewayInstance().updateLibrary(this);
		} catch(LastModifiedException e){
			throw new LastModifiedException(e);
		}
	}
	
	public ArrayList<AuditTrail> getAuditTrail(){
		return authorAppInstance.getLibraryGatewayInstance().getAudits(this);
	}
	
	//getters and setters
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return libraryName;
	}
	public void setName(String libraryName) {
		this.libraryName = libraryName;
	}
	public ArrayList<LibraryBook> getBooks() {
		return books;
	}
	public void setBooks(ArrayList<LibraryBook> books) {
		this.books = books;
	}
	public Timestamp getLastModified() {
		return lastModified;
	}
	public void setLastModified(Timestamp lastModified) {
		this.lastModified = lastModified;
	}
	
	@Override
	public String toString(){
		return id + ": " + libraryName;
	}
}
