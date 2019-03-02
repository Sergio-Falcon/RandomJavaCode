package gateway;

import java.io.*;
import java.util.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

import controller.LastModifiedException;
import driver.AuthorAppSingleton;
import model.AuditTrail;
import model.Author;
import model.Book;

//CS 4743 Assignment 4 by <Sergio Falcon>
public class AuthorTableGateway {

	private Connection conn;
	private AuthorAppSingleton authorAppInstance;
	private Logger logger = LogManager.getLogger(AuthorTableGateway.class);
	
	public AuthorTableGateway(){
		try{
			//create new connection object
			makeConnection();
		} catch (Exception e){
			logger.error("An error occured in creating a connection to the database: " + e.getMessage());
		}
		authorAppInstance = null;
	}
	
	public AuthorTableGateway(AuthorAppSingleton authorAppInstance){
		try{
			//create new connection object
			makeConnection();
		} catch (Exception e){
			logger.error("An error occured in creating a connection to the database: " + e.getMessage());
		}
		this.authorAppInstance = authorAppInstance;
	}
	
	//reads and creates objects of all the author objects currently in the table
	public ArrayList<Author> getAuthors(){
		
		ArrayList<Author> authors = new ArrayList<Author>();
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			
			String query = "select * "
					+ " from author ";
			st = conn.prepareStatement(query);

			//used to run select statements
			rs = st.executeQuery();

			//create a author object for each row in the author table
			while(rs.next()) {
				authors.add(new Author(rs.getInt("id"), rs.getString("firstName"), rs.getString("lastName"), 
						rs.getString("dob"), rs.getString("gender"), rs.getString("website"), rs.getTimestamp("last_modified"), authorAppInstance));
			}
			
		} catch (SQLException e) {
			logger.error("Error reading from author table in database: " + e.getMessage());
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
		
		return authors;
	}
	
	public ArrayList<AuditTrail> getAudits(Author author){
		ArrayList<AuditTrail> audits = new ArrayList<AuditTrail>();
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			
			String query = "select author.firstName, audit_trail_table.date_added, audit_trail_table.entry_msg " + 
						"from author inner join audit_trail_table on author.id = audit_trail_table.record_id " + 
						"where author.id = ? and audit_trail_table.record_type = ?";
			st = conn.prepareStatement(query);
			st.setInt(1, author.getId());
			st.setString(2, "A");

			//used to run select statements
			rs = st.executeQuery();

			//create a author object for each row in the author table
			while(rs.next()) {
				audits.add(new AuditTrail(rs.getString("firstName") + " " + rs.getString("lastName"), rs.getTimestamp("date_added"), rs.getString("entry_msg"), authorAppInstance));
			}
			
		} catch (SQLException e) {
			logger.error("Error reading from author table in database: " + e.getMessage());
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
	
	//modify an existing author
	public void updateAuthor(Author author) throws LastModifiedException{
		Author oldEntry = getAuthorById(author.getId());
		PreparedStatement st = null;
		ResultSet rs = null;
		
		if (oldEntry == null){
			logger.error("Could not find specified author in database to update.");
			return;
		}
		
		if(!oldEntry.getLastModified().equals(author.getLastModified())){
			throw new LastModifiedException("Last Modified data mismatch between database entry and current object");
		}
		
		try{
			conn.setAutoCommit(false);
		} catch (SQLException e) {
			logger.error("Error setting AutoCommit to false: " + e.getMessage());
			return;
		}
		
		try {
			
			String query = "update author set firstName = ?, lastName = ?, dob = ?, gender = ?, website = ?"
					+ "where id = ? ";
			st = conn.prepareStatement(query);
			
			st.setString(1, author.getFirstName());
			st.setString(2, author.getLastName());
			st.setString(3, author.getDob());
			st.setString(4, author.getGender());
			st.setString(5, author.getWebsite());
			st.setInt(6, author.getId());
			
			//executeUpdate is used to run insert, update, and delete statements
			st.executeUpdate();
			
			logger.info("Updated author");
		} catch (SQLException e) {
			logger.error("Error updating author table in database: " + e.getMessage());
			
			try{
				conn.rollback();
			}
			catch(SQLException e2){
				logger.error("Error rolling back transaction from error updating Author in database: " + e2.getMessage());
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
			String query = "select last_modified from author where id = ?";
			st = conn.prepareStatement(query);
			st.setInt(1, author.getId());
			
			rs = st.executeQuery();
			
			if(rs.first()){
				author.setLastModified(rs.getTimestamp("last_modified"));
			}
			else{
				logger.error("Didn't get the new last_modified timestamp.");
			}
			System.out.println(author.getLastModified() + "");
		}catch (SQLException e) {
			logger.error("Error getting last_modified from author in database: " + e.getMessage());
			try{
				conn.rollback();
			}
			catch(SQLException e2){
				logger.error("Error rolling back transaction from getting last_modified from author database: " + e2.getMessage());
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
		
		if(!oldEntry.getFirstName().equals(author.getFirstName())){
			try {
				
				String query = "insert into audit_trail_table (record_id, entry_msg) "
						+ "values (?, ?)";
				st = conn.prepareStatement(query);
				st.setInt(1, author.getId());
				st.setString(2, "First Name changed from " + oldEntry.getFirstName() + " to " + author.getFirstName());
				
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
		
		if(!oldEntry.getLastName().equals(author.getLastName())){
			try {
				
				String query = "insert into audit_trail_table (record_id, entry_msg) "
						+ "values (?, ?)";
				st = conn.prepareStatement(query);
				st.setInt(1, author.getId());
				st.setString(2, "Last Name changed from " + oldEntry.getLastName() + " to " + author.getLastName());
				
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
		
		if(!oldEntry.getDob().equals(author.getDob())){
			try {
				
				String query = "insert into audit_trail_table (record_id, entry_msg) "
						+ "values (?, ?)";
				st = conn.prepareStatement(query);
				st.setInt(1, author.getId());
				st.setString(2, "DOB changed from " + oldEntry.getDob() + " to " + author.getDob());
				
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
		
		if(!oldEntry.getGender().equals(author.getGender())){
			try {
				
				String query = "insert into audit_trail_table (record_id, entry_msg) "
						+ "values (?, ?)";
				st = conn.prepareStatement(query);
				st.setInt(1, author.getId());
				st.setString(2, "Gender changed from " + oldEntry.getGender() + " to " + author.getGender());
				
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
		
		if(!oldEntry.getWebsite().equals(author.getWebsite())){
			try {
				
				String query = "insert into audit_trail_table (record_id, entry_msg) "
						+ "values (?, ?)";
				st = conn.prepareStatement(query);
				st.setInt(1, author.getId());
				st.setString(2, "Website changed from " + oldEntry.getWebsite() + " to " + author.getWebsite());
				
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
			logger.error("Error commiting author update in database: " + e.getMessage());
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
	
	//create a new author
	public void saveAuthor(Author author){
		PreparedStatement st = null;
		ResultSet rs = null;
		
		try{
			conn.setAutoCommit(false);
		} catch (SQLException e) {
			logger.error("Error setting AutoCommit to false: " + e.getMessage());
			return;
		}
		
		try {
			
			String query = "insert into author (id, firstName, lastName, dob, gender, website) "
					+ "values (?, ?, ?, ?, ?, ?)";
			st = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			st.setInt(1, author.getId());
			st.setString(2, author.getFirstName());
			st.setString(3, author.getLastName());
			st.setString(4, author.getDob());
			st.setString(5, author.getGender());
			st.setString(6, author.getWebsite());
			
			//executeUpdate is used to run insert, update, and delete statements
			st.executeUpdate();
			
			rs = st.getGeneratedKeys();
			if(rs.first()){
				author.setId(rs.getInt(1));
			}
			else{
				logger.error("Didn't get the new key.");
			}
			
		} catch (SQLException e) {
			logger.error("Error inserting into author table in database: " + e.getMessage());
			try{
				conn.rollback();
			}
			catch(SQLException e2){
				logger.error("Error rolling back transaction from error adding new Author to database: " + e2.getMessage());
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
			String query = "select last_modified from author where id = ?";
			st = conn.prepareStatement(query);
			st.setInt(1, author.getId());
			
			rs = st.executeQuery();
			
			if(rs.first()){
				author.setLastModified(rs.getTimestamp("last_modified"));
			}
			else{
				logger.error("Didn't get the new last_modified timestamp.");
			}
			
		}catch (SQLException e) {
			logger.error("Error getting last_modified from author in database: " + e.getMessage());
			try{
				conn.rollback();
			}
			catch(SQLException e2){
				logger.error("Error rolling back transaction from getting last_modified from author database: " + e2.getMessage());
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
			
			String query = "insert into audit_trail_table (record_id, entry_msg) "
					+ "values (?, \"Added\")";
			st = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			st.setInt(1, author.getId());
			
			//executeUpdate is used to run insert, update, and delete statements
			st.executeUpdate();
			
		}
		catch (SQLException e) {
			logger.error("Error inserting into author table in database: " + e.getMessage());
			try{
				conn.rollback();
			}
			catch(SQLException e2){
				logger.error("Error rolling back transaction from error adding new Author to database: " + e2.getMessage());
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
			logger.info("New author created. Id = " + author.getId());
		}catch (SQLException e) {
			logger.error("Error commiting author delete in database: " + e.getMessage());
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
	public void deleteAuthor(Author author){
		ArrayList<Integer> Ids = new ArrayList<Integer>();
		PreparedStatement st = null;
		ResultSet rs = null;
		
		try{
			conn.setAutoCommit(false);
		} catch (SQLException e) {
			logger.error("Error setting AutoCommit to false: " + e.getMessage());
			return;
		}
		//Find and delete Books
				try{
					Book temp;
					String query = "select id from book where author_id = ?";
					st = conn.prepareStatement(query);
					st.setInt(1, author.getId());
					
					rs = st.executeQuery();

					//create a author object for each row in the author table
					while(rs.next()) {
						Ids.add(new Integer(rs.getInt("id")));
					}
					
					for(int i = 0; i < Ids.size(); i++){
						temp = new Book();
						temp.setId(Ids.get(i).intValue());
						authorAppInstance.getBookGatewayInstance().deleteBook(temp);
					}
				} catch (SQLException e) {
					logger.error("Error querying book for author_id = " + author.getId() + ": " + e.getMessage());
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
				
				//Find all audit trails associated with this author
				try {
					
					String query = "select id  from audit_trail_table "+ 
							"where record_type = ? and record_id = ? ";
					st = conn.prepareStatement(query);
					st.setString(1, "D");
					st.setInt(2, author.getId());

					//used to run select statements
					rs = st.executeQuery();

					//create a author object for each row in the author table
					while(rs.next()) {
						Ids.add(new Integer(rs.getInt("id")));
					}
					
				} catch (SQLException e) {
					logger.error("Error querying audit trails for Author id = " + author.getId() + ": " + e.getMessage());
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
				
				//delete every audit trail for this author
				for(int i = 0; i < Ids.size(); i++){
					try {
						
						String query = "delete from audit_trail_table where id = ?";
						st = conn.prepareStatement(query);
						st.setInt(1, Ids.get(i).intValue());

						//used to run select statements
						st.executeUpdate();
						
					} catch (SQLException e) {
						logger.error("Error querying audit trails for Author id = " + author.getId() + ": " + e.getMessage());
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
				
				//Delete all authors in library_book junction table
				try {
					
					String query = "delete from library_book where author_id = ?";
					st = conn.prepareStatement(query);
					st.setInt(1, author.getId());
					
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
				
				
				//Delete this author
				try {
					
					String query = "delete from author where id = ?";
					st = conn.prepareStatement(query);
					st.setInt(1, author.getId());

					//used to run select statements
					st.executeUpdate();
					
				} catch (SQLException e) {
					logger.error("Error querying audit trails for Author id = " + author.getId() + ": " + e.getMessage());
					try{
						conn.rollback();
					}
					catch(SQLException e2){
						logger.error("Error rolling back transaction from deleting from author in database: " + e2.getMessage());
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
					logger.info("Author with id = " + author.getId() + " deleted from database.");
				}catch (SQLException e) {
					logger.error("Error commiting author delete in database: " + e.getMessage());
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

	
	public Author getAuthorById(int id){
		Author author = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			
			String query = "select * from author "
					+ "where id = ?";
			st = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			st.setInt(1, id);

			rs = st.executeQuery();
			if(rs.first()){
				author = new Author(rs.getInt("id"), rs.getString("firstName"), rs.getString("lastName"), 
						rs.getString("dob"), rs.getString("gender"), rs.getString("website"), rs.getTimestamp("last_modified"));
			}
			
		} catch (SQLException e) {
			logger.error("Error querying from author table in database: " + e.getMessage());
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
		
		return author;
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
