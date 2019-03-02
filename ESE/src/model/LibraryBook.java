package model;

public class LibraryBook {

	Book book;
	int quantity;
	boolean newRecord;
	
	public LibraryBook(){
		book = null;
		quantity = 0;
		newRecord = true;
	}
	
	public LibraryBook(Book book, int quantity){
		this.book = book;
		this.quantity = quantity;
		newRecord = true;
	}
	
	public LibraryBook(Book book, int quantity, boolean isNew){
		this.book = book;
		this.quantity = quantity;
		newRecord = isNew;
	}
	
	//getters and setters
	public Book getBook() {
		return book;
	}
	public void setBook(Book book) {
		this.book = book;
	}
	public int getQuantity() {
		return quantity;
	}
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	public boolean isNewRecord() {
		return newRecord;
	}
	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
	}
	
	@Override
	public String toString(){
		return "id: " + book.getId() + ", " + book.getTitle() + ", qty: " + quantity;
	}
}
