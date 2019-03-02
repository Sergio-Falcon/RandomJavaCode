package gateway;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

import controller.LastModifiedException;
import driver.AuthorAppSingleton;
import model.AuditTrail;
import model.Author;
import model.Book;

//CS 4743 Assignment 4 by <Sergio Falcon>
public class BookTableGateway {

	private Connection conn;
	private AuthorAppSingleton authorAppInstance;
	private Logger logger = LogManager.getLogger(BookTableGateway.class);
	
	public BookTableGateway(){
		try{
			//create new connection object
			makeConnection();
		} catch (Exception e){
			logger.error("An error occured in creating a connection to the database: " + e.getMessage());
		}
		authorAppInstance = null;
	}
	
	public BookTableGateway(AuthorAppSingleton authorAppInstance){
		try{
			//create new connection object
			makeConnection();
		} catch (Exception e){
			logger.error("An error occured in creating a connection to the database: " + e.getMessage());
		}
		this.authorAppInstance = authorAppInstance;
	}
	
	//reads and creates objects of all the author objects currently in the table
	public ArrayList<Book> getBooks(){
		
		ArrayList<Book> books = new ArrayList<Book>();
		PreparedStatement st = null;
		ResultSet rs = null;
		Author temp;
		try {
			
			String query = "select book.*, author.id as author_id, author.firstName, author.lastName, author.dob, author.gender, author.website, " + 
							"author.last_modified as author_last_modified from book inner join author " + 
							"on book.author_id = author.id";
			st = conn.prepareStatement(query);
				//used to run select statements
			rs = st.executeQuery();
				//create a author object for each row in the author table
			while(rs.next()) {
				temp = new Author(rs.getInt("author_id"), rs.getString("firstName"), rs.getString("lastName"), rs.getString("dob"), rs.getString("gender"), 
						rs.getString("website"), rs.getTimestamp("author_last_Modified"), authorAppInstance);
				books.add(new Book(rs.getInt("id"), rs.getString("title"), rs.getString("publisher"), rs.getString("date_published"), rs.getString("summary"), 
						temp, rs.getTimestamp("last_modified"), authorAppInstance));
			}
			
		} catch (SQLException e) {
			logger.error("Error reading from book (and/or author) table in database: " + e.getMessage());
		} finally {
			try {
				if(rs != null)
					rs.close();
				if(st != null)
					st.close();
			} catch (SQLException e) {
				logger.error("Statement or Result Set close error: " + e.getMessage());
			}
		}
		
		return books;
	}	
	
	public ArrayList<AuditTrail> getAudits(Book book){
		ArrayList<AuditTrail> audits = new ArrayList<AuditTrail>();
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			
			String query = "select date_added, entry_msg " +
						"from audit_trail_table " +
						"where record_id = ? and record_type = ?";
			st = conn.prepareStatement(query);
			st.setInt(1, book.getId());
			st.setString(2, "B");

			//used to run select statements
			rs = st.executeQuery();

			//create a author object for each row in the author table
			while(rs.next()) {
				audits.add(new AuditTrail(book.getTitle() + " " + book.getPublisher(), rs.getTimestamp("date_added"), rs.getString("entry_msg"), authorAppInstance));
			}
			System.out.println(audits.size() + "");
		} catch (SQLException e) {
			logger.error("Error reading from audit trail table in database: " + e.getMessage());
		} finally {
			try {
				if(rs != null)
					rs.close();
				if(st != null)
					st.close();
			} catch (SQLException e) {
				logger.error("Statement or Result Set close error: " + e.getMessage());
			}
		}
		
		return audits;
	}
	
	//create a new book
	public void saveBook(Book book){
		PreparedStatement st = null;
		ResultSet rs = null;
		
		//set AutoCommit to false
		try{
			conn.setAutoCommit(false);
		} catch (SQLException e) {
			logger.error("Error setting AutoCommit to false: " + e.getMessage());
			return;
		}
		
		//insert new book into table
		try {
			
			String query = "insert into book (title, publisher, date_published, summary, author_id) "
					+ "values (?, ?, ?, ?, ?)";
			st = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			//st.setInt(1, book.getId());
			st.setString(1, book.getTitle());
			st.setString(2, book.getPublisher());
			st.setString(3, book.getDatePublished());
			st.setString(4, book.getSummary());
			st.setInt(5, book.getAuthor().getId());
			
			//executeUpdate is used to run insert, update, and delete statements
			st.executeUpdate();
			
			rs = st.getGeneratedKeys();
			if(rs.first()){
				book.setId(rs.getInt(1));
			}
			else{
				logger.error("Didn't get the new key.");
			}
			
		} catch (SQLException e) {
			logger.error("Error inserting into book table in database: " + e.getMessage());
			try{
				conn.rollback();
			}
			catch(SQLException e2){
				logger.error("Error rolling back transaction from error adding new book to database: " + e2.getMessage());
			}
			
			try{
				conn.setAutoCommit(true);
			}catch (SQLException e2) {
				logger.error("Error setting AutoCommit back to true: " + e2.getMessage());
			}
			return;
			
		} finally {
			
			try {
				if(rs != null)
					rs.close();
				if(st != null)
					st.close();
			} catch (SQLException e) {
				logger.error("Statement or Result Set close error: " + e.getMessage());
			}
			
		}
		
		try{
			String query = "select last_modified from book where id = ?";
			st = conn.prepareStatement(query);
			st.setInt(1, book.getId());
			
			rs = st.executeQuery();
			
			if(rs.first()){
				book.setLastModified(rs.getTimestamp("last_modified"));
			}
			else{
				logger.error("Didn't get the new last_modified timestamp.");
			}
			
		}catch (SQLException e) {
			logger.error("Error getting last_modified from book in database: " + e.getMessage());
			try{
				conn.rollback();
			}
			catch(SQLException e2){
				logger.error("Error rolling back transaction from getting last_modified from book database: " + e2.getMessage());
			}
			
			try{
				conn.setAutoCommit(true);
			}catch (SQLException e2) {
				logger.error("Error setting AutoCommit back to true: " + e2.getMessage());
			}
			return;
			
		} finally {
			
			try {
				if(rs != null)
					rs.close();
				if(st != null)
					st.close();
			} catch (SQLException e) {
				logger.error("Statement or Result Set close error: " + e.getMessage());
			}
			
		}
		
		try{
			
			String query = "insert into audit_trail_table (record_type, record_id, entry_msg) "
					+ "values (?, ?, \"Added\")";
			st = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			st.setString(1, "B");
			st.setInt(2, book.getId());
			
			//executeUpdate is used to run insert, update, and delete statements
			st.executeUpdate();
			
		}
		catch (SQLException e) {
			logger.error("Error inserting book into audit table in database: " + e.getMessage());
			try{
				conn.rollback();
			}
			catch(SQLException e2){
				logger.error("Error rolling back transaction from error adding to audit trail in database: " + e2.getMessage());
			}
			
			try{
				conn.setAutoCommit(true);
			}catch (SQLException e2) {
				logger.error("Error setting AutoCommit back to true: " + e2.getMessage());
			}
			
			return;
		} finally {
			
			try {
				if(rs != null)
					rs.close();
				if(st != null)
					st.close();
			} catch (SQLException e) {
				logger.error("Statement or Result Set close error: " + e.getMessage());
			}
		}
		
		try{
			conn.commit();
			logger.info("New book created. Id = " + book.getId());
		}catch (SQLException e) {
			logger.error("Error commiting new book in database: " + e.getMessage());
			try{
				conn.rollback();
			}
			catch(SQLException e2){
				logger.error("Error rolling back transaction from error commiting transaction: " + e2.getMessage());
			}
		}
		
		try{
			conn.setAutoCommit(true);
		}catch (SQLException e) {
			logger.error("Error setting AutoCommit back to true: " + e.getMessage());
		}
	}
	
	//modify an existing author
	public void updateBook(Book book) throws LastModifiedException{
		Book oldEntry = getBookById(book.getId());
		PreparedStatement st = null;
		ResultSet rs = null;
		
		if(oldEntry == null){
			logger.error("Could not find specified book in database to update");
			return;
		}
		
		if(!oldEntry.getLastModified().equals(book.getLastModified())){
			throw new LastModifiedException("Last Modified data mismatch between database entry and current object");
		}
		
		try{
			conn.setAutoCommit(false);
		} catch (SQLException e) {
			logger.error("Error setting AutoCommit to false: " + e.getMessage());
			return;
		}
		
		try {
			
			String query = "update book set title = ?, publisher = ?, date_published = ?, summary = ?, author_id = ? "
					+ "where id = ? ";
			st = conn.prepareStatement(query);

			st.setString(1, book.getTitle());
			st.setString(2, book.getPublisher());
			st.setString(3, book.getDatePublished());
			st.setString(4, book.getSummary());
			st.setInt(5, book.getAuthor().getId());
			st.setInt(6, book.getId());
			
			//executeUpdate is used to run insert, update, and delete statements
			st.executeUpdate();
			
			logger.info("Updated book");
		} catch (SQLException e) {
			logger.error("Error updating book table in database: " + e.getMessage());
			
			try{
				conn.rollback();
			}
			catch(SQLException e2){
				logger.error("Error rolling back transaction from error updating book in database: " + e2.getMessage());
			}
			
			try{
				conn.setAutoCommit(true);
			}catch (SQLException e2) {
				logger.error("Error setting AutoCommit back to true: " + e2.getMessage());
			}
			return;
		} finally {
			try {
				if(rs != null)
					rs.close();
				if(st != null)
					st.close();
			} catch (SQLException e) {
				logger.error("Statement or Result Set close error: " + e.getMessage());
			}
		}
		
		try{
			String query = "select last_modified from book where id = ?";
			st = conn.prepareStatement(query);
			st.setInt(1,  book.getId());
			
			rs = st.executeQuery();
			
			if(rs.first()){
				book.setLastModified(rs.getTimestamp("last_modified"));
			}
			else{
				logger.error("Didn't get the new last_modified timestamp.");
			}
			System.out.println(book.getLastModified() + "");
		}catch (SQLException e) {
			logger.error("Error getting last_modified from author in database: " + e.getMessage());
			try{
				conn.rollback();
			}
			catch(SQLException e2){
				logger.error("Error rolling back transaction from getting last_modified from book database: " + e2.getMessage());
			}
			
			try{
				conn.setAutoCommit(true);
			}catch (SQLException e2) {
				logger.error("Error setting AutoCommit back to true: " + e2.getMessage());
			}
			return;
			
		} finally {
			
			try {
				if(rs != null)
					rs.close();
				if(st != null)
					st.close();
			} catch (SQLException e) {
				logger.error("Statement or Result Set close error: " + e.getMessage());
			}
			
		}
		
		if(!oldEntry.getTitle().equals(book.getTitle())){
			try {
				
				String query = "insert into audit_trail_table (record_type, record_id, entry_msg) "
						+ "values (?, ?, ?)";
				st = conn.prepareStatement(query);
				st.setString(1, "B");
				st.setInt(2, book.getId());
				st.setString(3, "Title changed from " + oldEntry.getTitle() + " to " + book.getTitle());
				
				//executeUpdate is used to run insert, update, and delete statements
				st.executeUpdate();
				
			} catch (SQLException e) {
				logger.error("Error inserting into audit_trail_table table in database: " + e.getMessage());
				
				try{
					conn.rollback();
				}
				catch(SQLException e2){
					logger.error("Error rolling back transaction from error inserting into audit_trail_table in database: " + e2.getMessage());
				}
				
				try{
					conn.setAutoCommit(true);
				}catch (SQLException e2) {
					logger.error("Error setting AutoCommit back to true: " + e2.getMessage());
				}
				return;
			} finally {
				try {
					if(rs != null)
						rs.close();
					if(st != null)
						st.close();
				} catch (SQLException e) {
					logger.error("Statement or Result Set close error: " + e.getMessage());
				}
			}
		}
		
		if(!oldEntry.getPublisher().equals(book.getPublisher())){
			try {
				
				String query = "insert into audit_trail_table (record_type, record_id, entry_msg) "
						+ "values (?, ?, ?)";
				st = conn.prepareStatement(query);
				st.setString(1, "B");
				st.setInt(2, book.getId());
				st.setString(3, "Last name changed from " + oldEntry.getPublisher() + " to " + book.getPublisher());
				
				//executeUpdate is used to run insert, update, and delete statements
				st.executeUpdate();
				
				logger.info("Updated audit trail");
			} catch (SQLException e) {
				logger.error("Error inserting into audit_trail_table table in database: " + e.getMessage());
				
				try{
					conn.rollback();
				}
				catch(SQLException e2){
					logger.error("Error rolling back transaction from error inserting into audit_trail_table in database: " + e2.getMessage());
				}
				
				try{
					conn.setAutoCommit(true);
				}catch (SQLException e2) {
					logger.error("Error setting AutoCommit back to true: " + e2.getMessage());
				}
				return;
			} finally {
				try {
					if(rs != null)
						rs.close();
					if(st != null)
						st.close();
				} catch (SQLException e) {
					logger.error("Statement or Result Set close error: " + e.getMessage());
				}
			}
		}
		
		if(!oldEntry.getDatePublished().equals(book.getDatePublished())){
			try {
				
				String query = "insert into audit_trail_table (record_type, record_id, entry_msg) "
						+ "values (?, ?, ?)";
				st = conn.prepareStatement(query);
				st.setString(1, "B");
				st.setInt(2, book.getId());
				st.setString(3, "Last name changed from " + oldEntry.getDatePublished() + " to " + book.getDatePublished());
				
				//executeUpdate is used to run insert, update, and delete statements
				st.executeUpdate();
				
				logger.info("Updated audit trail");
			} catch (SQLException e) {
				logger.error("Error inserting into audit_trail_table table in database: " + e.getMessage());
				
				try{
					conn.rollback();
				}
				catch(SQLException e2){
					logger.error("Error rolling back transaction from error inserting into audit_trail_table in database: " + e2.getMessage());
				}
				
				try{
					conn.setAutoCommit(true);
				}catch (SQLException e2) {
					logger.error("Error setting AutoCommit back to true: " + e2.getMessage());
				}
				return;
			} finally {
				try {
					if(rs != null)
						rs.close();
					if(st != null)
						st.close();
				} catch (SQLException e) {
					logger.error("Statement or Result Set close error: " + e.getMessage());
				}
			}
		}
		
		if(!oldEntry.getSummary().equals(book.getSummary())){
			try {
				
				String query = "insert into audit_trail_table (record_type, record_id, entry_msg) "
						+ "values (?, ?, ?)";
				st = conn.prepareStatement(query);
				st.setString(1, "B");
				st.setInt(2, book.getId());
				st.setString(3, "Last name changed from " + oldEntry.getSummary() + " to " + book.getSummary());
				
				//executeUpdate is used to run insert, update, and delete statements
				st.executeUpdate();
				
				logger.info("Updated audit trail");
			} catch (SQLException e) {
				logger.error("Error inserting into audit_trail_table table in database: " + e.getMessage());
				
				try{
					conn.rollback();
				}
				catch(SQLException e2){
					logger.error("Error rolling back transaction from error inserting into audit_trail_table in database: " + e2.getMessage());
				}
				
				try{
					conn.setAutoCommit(true);
				}catch (SQLException e2) {
					logger.error("Error setting AutoCommit back to true: " + e2.getMessage());
				}
				return;
			} finally {
				try {
					if(rs != null)
						rs.close();
					if(st != null)
						st.close();
				} catch (SQLException e) {
					logger.error("Statement or Result Set close error: " + e.getMessage());
				}
			}
		}
		
		if(oldEntry.getAuthor().getId() != book.getAuthor().getId()){
			try {
				
				String query = "insert into audit_trail_table (record_type, record_id, entry_msg) "
						+ "values (?, ?, ?)";
				st = conn.prepareStatement(query);
				st.setString(1, "B");
				st.setInt(2, book.getId());
				st.setString(3, "Author changed from " + oldEntry.getAuthor().getId() +": " + oldEntry.getAuthor().getName() + " to " + book.getAuthor().getId() + ": " + book.getAuthor().getName());
				
				//executeUpdate is used to run insert, update, and delete statements
				st.executeUpdate();
				
				logger.info("Updated audit trail");
			} catch (SQLException e) {
				logger.error("Error inserting into audit_trail_table table in database: " + e.getMessage());
				
				try{
					conn.rollback();
				}
				catch(SQLException e2){
					logger.error("Error rolling back transaction from error inserting into audit_trail_table in database: " + e2.getMessage());
				}
				
				try{
					conn.setAutoCommit(true);
				}catch (SQLException e2) {
					logger.error("Error setting AutoCommit back to true: " + e2.getMessage());
				}
				return;
			} finally {
				try {
					if(rs != null)
						rs.close();
					if(st != null)
						st.close();
				} catch (SQLException e) {
					logger.error("Statement or Result Set close error: " + e.getMessage());
				}
			}
		}
		
		try{
			conn.commit();
			logger.info("Updated audit trail");
		}catch (SQLException e) {
			logger.error("Error commiting book update in database: " + e.getMessage());
			try{
				conn.rollback();
			}
			catch(SQLException e2){
				logger.error("Error rolling back transaction from error commiting transaction: " + e2.getMessage());
			}
		}
		
		try{
			conn.setAutoCommit(true);
		}catch (SQLException e) {
			logger.error("Error setting AutoCommit back to true: " + e.getMessage());
		}
		
		
	}

	//delete author
	public void deleteBook(Book book){
		ArrayList<Integer> Ids = new ArrayList<Integer>();
		PreparedStatement st = null;
		ResultSet rs = null;
		
		try{
			conn.setAutoCommit(false);
		} catch (SQLException e) {
			logger.error("Error setting AutoCommit to false: " + e.getMessage());
			return;
		}
		
		try {
			
			String query = "select id from audit_trail_table " + 
					"where record_type = ? and record_id = ? ";
			st = conn.prepareStatement(query);
			st.setString(1, "B");
			st.setInt(2, book.getId());
				//used to run select statements
			rs = st.executeQuery();
				//create a author object for each row in the author table
			while(rs.next()) {
				Ids.add(new Integer(rs.getInt("id")));
			}
			
		} catch (SQLException e) {
			logger.error("Error querying audit trails for Book id = " + book.getId() + ": " + e.getMessage());
			try{
				conn.rollback();
			}
			catch(SQLException e2){
				logger.error("Error rolling back transaction from error querying audit trails in database: " + e2.getMessage());
			}
			
			try{
				conn.setAutoCommit(true);
			}catch (SQLException e2) {
				logger.error("Error setting AutoCommit back to true: " + e2.getMessage());
			}
			
			return;
		} finally {
			try {
				if(rs != null)
					rs.close();
				if(st != null)
					st.close();
			} catch (SQLException e) {
				logger.error("Statement or Result Set close error: " + e.getMessage());
			}
		}
		
		for(int i = 0; i < Ids.size(); i++){
			try {
				
				String query = "delete from audit_trail_table where id = ?";
				st = conn.prepareStatement(query);
				st.setInt(1, Ids.get(i).intValue());
					//used to run select statements
				st.executeUpdate();
				
			} catch (SQLException e) {
				logger.error("Error deleting audit trails for Book id = " + book.getId() + ": " + e.getMessage());
				try{
					conn.rollback();
				}
				catch(SQLException e2){
					logger.error("Error rolling back transaction from deleting from audit trails in database: " + e2.getMessage());
				}
				
				try{
					conn.setAutoCommit(true);
				}catch (SQLException e2) {
					logger.error("Error setting AutoCommit back to true: " + e2.getMessage());
				}
				
				return;
			} finally {
				try {
					if(rs != null)
						rs.close();
					if(st != null)
						st.close();
				} catch (SQLException e) {
					logger.error("Statement or Result Set close error: " + e.getMessage());
				}
			}
		}
		
		try {
			
			String query = "delete from book where id = ?";
			st = conn.prepareStatement(query);
			st.setInt(1, book.getId());
				//used to run select statements
			st.executeUpdate();
			
		} catch (SQLException e) {
			logger.error("Error deleting book id = " + book.getId() + ": " + e.getMessage());
			try{
				conn.rollback();
			}
			catch(SQLException e2){
				logger.error("Error rolling back transaction from deleting from book in database: " + e2.getMessage());
			}
			
			try{
				conn.setAutoCommit(true);
			}catch (SQLException e2) {
				logger.error("Error setting AutoCommit back to true: " + e2.getMessage());
			}
			
			return;
		} finally {
			try {
				if(rs != null)
					rs.close();
				if(st != null)
					st.close();
			} catch (SQLException e) {
				logger.error("Statement or Result Set close error: " + e.getMessage());
			}
		}
		
		try{
			conn.commit();
			logger.info("Book with id = " + book.getId() + " deleted from database.");
		}catch (SQLException e) {
			logger.error("Error commiting book delete in database: " + e.getMessage());
			try{
				conn.rollback();
			}
			catch(SQLException e2){
				logger.error("Error rolling back transaction from error commiting transaction: " + e2.getMessage());
			}
		}
		
		try{
			conn.setAutoCommit(true);
		}catch (SQLException e) {
			logger.error("Error setting AutoCommit back to true: " + e.getMessage());
		}
		
		//Delete all book in library_book junction table
		try {
			
			String query = "delete from library_book where book_id = ?";
			st = conn.prepareStatement(query);
			st.setInt(1, book.getId());
			
			//used to run select statements
			st.executeUpdate();
			
		} catch (SQLException e) {
			logger.error("Error deleting from library_book table: " + e.getMessage());
			try{
				conn.rollback();
			}
			catch(SQLException e2){
				logger.error("Error rolling back transaction from deleting from library_book in database: " + e2.getMessage());
			}
			
			try{
				conn.setAutoCommit(true);
			}catch (SQLException e2) {
				logger.error("Error setting AutoCommit back to true: " + e2.getMessage());
			}
			
			return;
		} finally {
			try {
				if(rs != null)
					rs.close();
				if(st != null)
					st.close();
			} catch (SQLException e) {
				logger.error("Statement or Result Set close error: " + e.getMessage());
			}
		}
		
	}
	
	public Book getBookById(int id){
		Book book = null;
		Author temp = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			
			String query = "select book.*, author.id as author_id, author.firstName, author.lastName, author.dob, author.gender, author.website, " + 
					"author.last_modified as author_last_modified from book inner join author " + 
					"on book.author_id = author.id where book.id = ?";
			st = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			st.setInt(1, id);
			
			rs = st.executeQuery();
			if(rs.first()){
				temp = new Author(rs.getInt("author_id"), rs.getString("firstName"), rs.getString("lastName"), rs.getString("dob"),
						rs.getString("gender"), rs.getString("website"), rs.getTimestamp("author_last_modified"), authorAppInstance);
				book = new Book(rs.getInt("id"), rs.getString("title"), rs.getString("publisher"), 
						rs.getString("date_published"), rs.getString("summary"), temp, rs.getTimestamp("last_modified"));
			}
			
		} catch (SQLException e) {
			logger.error("Error querying from book table in database: " + e.getMessage());
			return null;
		} finally {
			try {
				if(rs != null) 
					rs.close();
				if(st != null)
					st.close();
			} catch (SQLException e) {
				logger.error("Statement or Result Set close error: " + e.getMessage());
			}
		}
		
		return book;
	}
	
	public void close(){
		try{
			if(conn != null){
				//close the connection
				conn.close();
				logger.info("Database connection closed.");
			}
		} catch(SQLException e){
			
			logger.error("Error closing connection: " + e.getMessage());
		}
	}
	
	public void setAuthorAppInstance(AuthorAppSingleton authorAppInstance){
		this.authorAppInstance = authorAppInstance;
	}
	
	//Creates a new connection object
	private void makeConnection() throws IOException, SQLException{
		Properties props = new Properties();
		FileInputStream fis = null;
		//load properties from properties file
		//TODO: change the properties file to point to your own database
        fis = new FileInputStream("db.properties");
        props.load(fis);
        fis.close();
        
        //create the datasource
        MysqlDataSource ds = new MysqlDataSource();
        ds.setURL(props.getProperty("MYSQL_DB_URL"));
        ds.setUser(props.getProperty("MYSQL_DB_USERNAME"));
        ds.setPassword(props.getProperty("MYSQL_DB_PASSWORD"));

		//create the connection
		conn = ds.getConnection();
	}
}
