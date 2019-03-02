package gateway;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

import controller.LastModifiedException;
import driver.AuthorAppSingleton;
import model.AuditTrail;
import model.Library;
import model.Author;
import model.Book;
import model.LibraryBook;

public class LibraryTableGateway {

	private Connection conn;
	private AuthorAppSingleton authorAppInstance;
	private Logger logger = LogManager.getLogger(LibraryTableGateway.class);
	
	public LibraryTableGateway(){
		try{
			//create new connection object
			makeConnection();
		} catch (Exception e){
			logger.error("An error occured in creating a connection to the database: " + e.getMessage());
		}
		authorAppInstance = null;
	}
	
	public LibraryTableGateway(AuthorAppSingleton authorAppInstance){
		try{
			//create new connection object
			makeConnection();
		} catch (Exception e){
			logger.error("An error occured in creating a connection to the database: " + e.getMessage());
		}
		this.authorAppInstance = authorAppInstance;
	}
	
	public ArrayList<Library> getLibrarys(){
		
		ArrayList<Library> librarys = new ArrayList<Library>();
		PreparedStatement st = null;
		ResultSet rs = null;
		ArrayList<LibraryBook> libraryBooks = null;
		Library tempLibrary = null;
		LibraryBook tempLibraryBook = null;
		int tempId;
		
		try {
			
			String query = "select library.*, library_book.library_id, library_book.book_id, library_book.quantity " +
							"from library inner join library_book on library.id = library_book.library_id";
			st = conn.prepareStatement(query);
			rs = st.executeQuery();
			while(rs.next()) {
				//check if this Library already exists
				tempId = rs.getInt("id");
				for(int i = 0; i < librarys.size(); i++){
					if(librarys.get(i).getId() == tempId){
						tempLibrary = librarys.get(i);
						break;
					}
				}
				
				//if it does not
				if(tempLibrary == null){
					//create a new list of LibraryBooks and Library
					libraryBooks = new ArrayList<LibraryBook>();
					tempLibraryBook = new LibraryBook(authorAppInstance.getBookGatewayInstance().getBookById(rs.getInt("book_id")) , rs.getInt("quantity"), false);
					libraryBooks.add(tempLibraryBook);
					librarys.add(new Library(tempId, rs.getString("library_name"), libraryBooks, rs.getTimestamp("last_modified"), authorAppInstance));
				}
				//otherwise, if it already exists
				else{
					//just add that LibraryBook to the Library
					tempLibraryBook = new LibraryBook(authorAppInstance.getBookGatewayInstance().getBookById(rs.getInt("book_id")) , rs.getInt("quantity"), false);
					tempLibrary.getBooks().add(tempLibraryBook);
				}
				tempLibrary = null;
				tempLibraryBook = null;
				libraryBooks = null;
			}
			
		} catch (SQLException e) {
			logger.error("Error reading from library table in database: " + e.getMessage());
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
		
		return librarys;
	}
	
	public ArrayList<AuditTrail> getAudits(Library library){
		ArrayList<AuditTrail> audits = new ArrayList<AuditTrail>();
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			
			String query = "select date_added, entry_msg " +
						"from audit_trail_table " +
						"where record_id = ? and record_type = ?";
			st = conn.prepareStatement(query);
			st.setInt(1, library.getId());
			st.setString(2, "L");

			//used to run select statements
			rs = st.executeQuery();

			//create a book object for each row in the book table
			while(rs.next()) {
				audits.add(new AuditTrail(library.getName(), rs.getTimestamp("date_added"), rs.getString("entry_msg"), authorAppInstance));
			}
			
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
	
	//add new library
	public void saveLibrary(Library library){
		PreparedStatement st = null;
		ResultSet rs = null;
		
		//set AutoCommit to false
		try{
			conn.setAutoCommit(false);
		} catch (SQLException e) {
			logger.error("Error setting AutoCommit to false: " + e.getMessage());
			return;
		}
		
		//insert new books into table
		try {
			
			String query = "insert into library (library_name) "
					+ "values (?)";
			st = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			st.setString(1, library.getName());
			
			//executeUpdate is used to run insert, update, and delete statements
			st.executeUpdate();
			
			rs = st.getGeneratedKeys();
			if(rs.first()){
				library.setId(rs.getInt(1));
			}
			else{
				logger.error("Didn't get the new key.");
			}
			
		} catch (SQLException e) {
			logger.error("Error inserting into library table in database: " + e.getMessage());
			try{
				conn.rollback();
			}
			catch(SQLException e2){
				logger.error("Error rolling back transaction from error adding new library to database: " + e2.getMessage());
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
			String query = "select last_modified from library where id = ?";
			st = conn.prepareStatement(query);
			st.setInt(1,  library.getId());
			
			rs = st.executeQuery();
			
			if(rs.first()){
				library.setLastModified(rs.getTimestamp("last_modified"));
			}
			else{
				logger.error("Didn't get the new last_modified timestamp.");
			}
			
		}catch (SQLException e) {
			logger.error("Error getting last_modified from library in database: " + e.getMessage());
			try{
				conn.rollback();
			}
			catch(SQLException e2){
				logger.error("Error rolling back transaction from getting last_modified from library database: " + e2.getMessage());
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
		
		for(int i = 0; i < library.getBooks().size(); i++){
			try{
				String query = "insert into library_book (library_id, book_id, quantity) "
						+ "values (?, ?, ?)";
				st = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
				st.setInt(1, library.getId());
				st.setInt(2, library.getBooks().get(i).getBook().getId());
				st.setInt(3, library.getBooks().get(i).getQuantity());
			
				//executeUpdate is used to run insert, update, and delete statements
				st.executeUpdate();
				library.getBooks().get(i).setNewRecord(false);
			}
			catch (SQLException e) {
				logger.error("Error inserting into library_book database: " + e.getMessage());
				try{
					conn.rollback();
				}
				catch(SQLException e2){
					logger.error("Error rolling back transaction from error adding to library_book in database: " + e2.getMessage());
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
			
			String query = "insert into audit_trail_table (record_type, record_id, entry_msg) "
					+ "values (?, ?, \"Added\")";
			st = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			st.setString(1, "L");
			st.setInt(2, library.getId());
			
			//executeUpdate is used to run insert, update, and delete statements
			st.executeUpdate();
			
		}
		catch (SQLException e) {
			logger.error("Error inserting library into audit table in database: " + e.getMessage());
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
			logger.info("New library created. Id = " + library.getId());
		}catch (SQLException e) {
			logger.error("Error commiting new library in database: " + e.getMessage());
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
	
	//modify existing library
	public void updateLibrary(Library library) throws LastModifiedException{
		Library oldEntry = getLibraryById(library.getId());
		PreparedStatement st = null;
		ResultSet rs = null;
		boolean bookExists = false;
		
		if(oldEntry == null){
			logger.error("Could not find specified library in database to update");
			return;
		}
		
		if(!oldEntry.getLastModified().equals(library.getLastModified())){
			throw new LastModifiedException("Last Modified data mismatch between database entry and current object");
		}
		
		try{
			conn.setAutoCommit(false);
		} catch (SQLException e) {
			logger.error("Error setting AutoCommit to false: " + e.getMessage());
			return;
		}
		
		try {
			
			String query = "update library set library_name = ? where id = ? ";
			st = conn.prepareStatement(query);
			st.setString(1, library.getName());
			st.setInt(2, library.getId());
			
			//executeUpdate is used to run insert, update, and delete statements
			st.executeUpdate();
			
			logger.info("Updated library");
		} catch (SQLException e) {
			logger.error("Error updating library table in database: " + e.getMessage());
			
			try{
				conn.rollback();
			}
			catch(SQLException e2){
				logger.error("Error rolling back transaction from error updating library in database: " + e2.getMessage());
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
			String query = "select last_modified from library where id = ?";
			st = conn.prepareStatement(query);
			st.setInt(1,  library.getId());
			
			rs = st.executeQuery();
			
			if(rs.first()){
				library.setLastModified(rs.getTimestamp("last_modified"));
			}
			else{
				logger.error("Didn't get the new last_modified timestamp.");
			}
			
		}catch (SQLException e) {
			logger.error("Error getting last_modified from library in database: " + e.getMessage());
			try{
				conn.rollback();
			}
			catch(SQLException e2){
				logger.error("Error rolling back transaction from getting last_modified from library database: " + e2.getMessage());
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
		
		if(!oldEntry.getName().equals(library.getName())){
			try {
				
				String query = "insert into audit_trail_table (record_type, record_id, entry_msg) "
						+ "values (?, ?, ?)";
				st = conn.prepareStatement(query);
				st.setString(1, "L");
				st.setInt(2, library.getId());
				st.setString(3, "Name changed from " + oldEntry.getName() + " to " + library.getName());
				
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
		
		//for each record in the old entry
		for(int i = 0; i < oldEntry.getBooks().size(); i++){
			//check the updated entry for each book associated with the library
			for(int j = 0; j < library.getBooks().size(); j++){
				//if the book is found
				if(oldEntry.getBooks().get(i).getBook().getId() == library.getBooks().get(j).getBook().getId()){
					//check if the quantity has changed
					if(oldEntry.getBooks().get(i).getQuantity() != library.getBooks().get(j).getQuantity()){
						//if so, then change the quantity in the database and then break
						try {
							
							String query = "update library_book set quantity = ? where library_id = ? and book_id = ?";
							st = conn.prepareStatement(query);
							st.setInt(1, library.getBooks().get(j).getQuantity());
							st.setInt(2, library.getId());
							st.setInt(3, library.getBooks().get(j).getBook().getId());
							
							//executeUpdate is used to run insert, update, and delete statements
							st.executeUpdate();
							
						} catch (SQLException e) {
							logger.error("Error updating library_book table in database: " + e.getMessage());
							
							try{
								conn.rollback();
							}
							catch(SQLException e2){
								logger.error("Error rolling back transaction from error updating library_dob in database: " + e2.getMessage());
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
						
						//insert into audit trail
						try {
							
							String query = "insert into audit_trail_table (record_type, record_id, entry_msg) "
										+ "values (?, ?, ?)";
							st = conn.prepareStatement(query);
							st.setString(1, "L");
							st.setInt(2, library.getId());
							st.setString(3, "Dog " + library.getBooks().get(j).getBook().getTitle() + ", id: " +
											library.getBooks().get(j).getBook().getId() + ", quantity changed from " +
											oldEntry.getBooks().get(i).getQuantity() + " to " + library.getBooks().get(j).getQuantity());
							
							//executeUpdate is used to run insert, update, and delete statements
							st.executeUpdate();
							
						} catch (SQLException e) {
							logger.error("Error inserting into audit_trail_table table in database: " + e.getMessage());
							
							try{
								conn.rollback();
							}
							catch(SQLException e2){
								logger.error("Error rolling back transaction from error inserting into audit trail in database: " + e2.getMessage());
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
					//set bookExists to true
					bookExists = true;
					//break to check next old entry
					break;
				}
			}
			//if made it here and bookExists is true, then old entry went through the entire updated library and
			//didn't find a match for the current library_book entry. This means that library book
			//entry must have been deleted, so we need to update the library_book table to reflect that
			if(!bookExists){
				try {
				
					String query = "delete from library_book where library_id = ? and book_id = ?";
					st = conn.prepareStatement(query);
					st.setInt(1, library.getId());
					st.setInt(2, oldEntry.getBooks().get(i).getBook().getId());
				
					//executeUpdate is used to run insert, update, and delete statements
					st.executeUpdate();
				
				} catch (SQLException e) {
					logger.error("Error deleting from library_book table in database: " + e.getMessage());
				
					try{
						conn.rollback();
					}
					catch(SQLException e2){
						logger.error("Error rolling back transaction from error deleting from library_dob in database: " + e2.getMessage());
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
				
				//add audit trail
				try {
					
					String query = "insert into audit_trail_table (record_type, record_id, entry_msg) "
								+ "values (?, ?, ?)";
					st = conn.prepareStatement(query);
					st.setString(1, "L");
					st.setInt(2, oldEntry.getId());
					st.setString(3, "Dog " + oldEntry.getBooks().get(i).getBook().getTitle() + ", id: " +
									oldEntry.getBooks().get(i).getBook().getId() + ", deleted from Library");
					
					//executeUpdate is used to run insert, update, and delete statements
					st.executeUpdate();
					
				} catch (SQLException e) {
					logger.error("Error inserting into audit_trail_table table in database: " + e.getMessage());
					
					try{
						conn.rollback();
					}
					catch(SQLException e2){
						logger.error("Error rolling back transaction from error inserting into audit trail in database: " + e2.getMessage());
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
			//set bookExists back to false for next entry
			bookExists = false;
		}
		
		//for each record in the updated library entry
		for(int i = 0; i < library.getBooks().size(); i++){
			//check any etries are new, if they are, then add them to library_book
			if(library.getBooks().get(i).isNewRecord()){
				try{
					String query = "insert into library_book (library_id, book_id, quantity) " +
							"values (?, ?, ?)";
					st = conn.prepareStatement(query);
					st.setInt(1, library.getId());
					st.setInt(2, library.getBooks().get(i).getBook().getId());
					st.setInt(3, library.getBooks().get(i).getQuantity());
		
					//executeUpdate is used to run insert, update, and delete statements
					st.executeUpdate();
		
				} catch (SQLException e) {
					logger.error("Error inserting into library_book table in database: " + e.getMessage());
		
					try{
						conn.rollback();
					}
					catch(SQLException e2){
						logger.error("Error rolling back transaction from error inserting into library_dob in database: " + e2.getMessage());
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
		
				//add audit trail
				try {
					
					String query = "insert into audit_trail_table (record_type, record_id, entry_msg) "
							+ "values (?, ?, ?)";
					st = conn.prepareStatement(query);
					st.setString(1, "L");
					st.setInt(2, library.getId());
					st.setString(3, "Dog " + library.getBooks().get(i).getBook().getTitle() + ", id: " +
							library.getBooks().get(i).getBook().getId() + ", added to Library " +
							"with quantity: " + library.getBooks().get(i).getQuantity());
			
					//executeUpdate is used to run insert, update, and delete statements
					st.executeUpdate();
			
				} catch (SQLException e) {
					logger.error("Error inserting into audit_trail_table table in database: " + e.getMessage());
			
					try{
						conn.rollback();
					}
					catch(SQLException e2){
						logger.error("Error rolling back transaction from error inserting into audit trail in database: " + e2.getMessage());
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
				library.getBooks().get(i).setNewRecord(false);
			}
		}
		
		try{
			conn.commit();
			logger.info("Updated audit trail");
		}catch (SQLException e) {
			logger.error("Error commiting library update in database: " + e.getMessage());
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
	
	//delete library
	public void deleteLibrary(Library library){
		ArrayList<Integer> Ids = new ArrayList<Integer>();
		PreparedStatement st = null;
		ResultSet rs = null;
		
		try{
			conn.setAutoCommit(false);
		} catch (SQLException e) {
			logger.error("Error setting AutoCommit to false: " + e.getMessage());
			return;
		}
		
		//fix audit trail stuff
		try {
			
			String query = "delete from audit_trail_table where record_type = ? and record_id = ?";
			st = conn.prepareStatement(query);
			st.setString(1, "L");
			st.setInt(2, library.getId());
			
			st.executeUpdate();
			
		} catch (SQLException e) {
			logger.error("Error deleting audit trails for Library id = " + library.getId() + ": " + e.getMessage());
			try{
				conn.rollback();
			}
			catch(SQLException e2){
				logger.error("Error rolling back transaction from error deleting audit trails in database: " + e2.getMessage());
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
		
		//delete from library_book
		try {
			
			String query = "delete from library_book where library_id = ?";
			st = conn.prepareStatement(query);
			st.setInt(1, library.getId());
			
			st.executeUpdate();
			
		} catch (SQLException e) {
			logger.error("Error deleting library id = " + library.getId() + ": " + e.getMessage());
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
		
		//delete library from library
		try {
			
			String query = "delete from library where id = ?";
			st = conn.prepareStatement(query);
			st.setInt(1, library.getId());
			
			st.executeUpdate();
			
		} catch (SQLException e) {
			logger.error("Error deleting library id = " + library.getId() + ": " + e.getMessage());
			try{
				conn.rollback();
			}
			catch(SQLException e2){
				logger.error("Error rolling back transaction from deleting from library in database: " + e2.getMessage());
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
			logger.info("Library with id = " + library.getId() + " deleted from database.");
		}catch (SQLException e) {
			logger.error("Error commiting library delete in database: " + e.getMessage());
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
	
	public Library getLibraryById(int id){
		Library library = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		ArrayList<LibraryBook> libraryBooks = new ArrayList<LibraryBook>();
		String libraryName;
		Timestamp lastModified;
		
		try {
			
			String query = "select library.*, library_book.library_id, library_book.book_id, library_book.quantity " +
							"from library inner join library_book on library.id = library_book.library_id " +
							"where id = ?";
			st = conn.prepareStatement(query);
			st.setInt(1, id);
			
			rs = st.executeQuery();
			
			if(rs.first()){
				libraryName = rs.getString("library_name");
				lastModified = rs.getTimestamp("last_modified");
				
				do{
					libraryBooks.add(new LibraryBook(authorAppInstance.getBookGatewayInstance().getBookById(rs.getInt("book_id")) , rs.getInt("quantity"), false));
				}while(rs.next());
				
				library = new Library(id, libraryName, libraryBooks, lastModified, authorAppInstance);
			}
			
		} catch (SQLException e) {
			logger.error("Error reading from library table in database: " + e.getMessage());
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
		
		return library;
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
	
	public void setDogAppInstance(AuthorAppSingleton authorAppInstance){
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
