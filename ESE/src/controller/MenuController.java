package controller;

import java.io.IOException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import driver.AuthorAppSingleton;
import model.*;
import javafx.event.*;
import javafx.fxml.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

//CS 4743 Assignment 5 by <Sergio Falcon>
public class MenuController{
	
	@FXML private MenuBar menuBar;
	@FXML private MenuItem quit;
	@FXML private MenuItem authorList;
	@FXML private MenuItem authorAdd;
	@FXML private MenuItem bookList;
	@FXML private MenuItem bookAdd;
	@FXML private MenuItem libraryList;
	@FXML private MenuItem libraryAdd;
	private AuthorAppSingleton authorAppInstance;
	private Logger logger = LogManager.getLogger(MenuController.class);
	
	public MenuController(){
		
	}
	
	public MenuController(AuthorAppSingleton authorAppInstance){
		this.authorAppInstance = authorAppInstance;
	}

	//action handler for the "Quit", "Add Author", and "Author List" menu items
	@FXML private void handleMenuAction(ActionEvent event) throws Exception{
		if(event.getSource() == authorList) {
			logger.info("Author list selected.");
			changeView(ViewType.AUTHORLIST, null);
		}
		else if(event.getSource() == authorAdd){
			logger.info("Add author selected.");
			changeView(ViewType.AUTHORADD, null);
		}
		else if(event.getSource() == bookList){
			logger.info("Book list selected");
			changeView(ViewType.BOOKLIST, null);
		}
		else if(event.getSource() == bookAdd){
			logger.info("Add book selected.");
			changeView(ViewType.BOOKADD, null);
		}
		else if(event.getSource() == libraryList){
			logger.info("Library list selected.");
			changeView(ViewType.LIBRARYLIST, null);
		}
		else if(event.getSource() == libraryAdd){
			logger.info("Add library selected.");
			changeView(ViewType.LIBRARYADD, null);
		}
		else if(event.getSource() == quit){
			logger.info("Quit selected.");
			System.exit(0);
		}
	}
	
	//set the center of the border layout
	public boolean changeView(ViewType vType, Object data){
		
		FXMLLoader loader = null;
		Pane pane;
		
		switch(vType){
		
		case CLEAR:
			pane = new Pane();
			authorAppInstance.getAuthorAppInstance().getRootPane().setCenter(pane);
			return true;
		case AUTHORLIST:
			loader = new FXMLLoader(getClass().getResource("../view/authorlist.fxml"));
			loader.setController(new AuthorlistController(authorAppInstance));
			break;
		case AUTHORADD:
			loader = new FXMLLoader(getClass().getResource("../view/addauthor.fxml"));
			loader.setController(new AuthoraddController(authorAppInstance));
			break;
		case BOOKLIST:
			loader = new FXMLLoader(getClass().getResource("../view/booklist.fxml"));
			loader.setController(new BooklistController(authorAppInstance));
			break;
		case BOOKADD:
			loader = new FXMLLoader(getClass().getResource("../view/addbook.fxml"));
			loader.setController(new BookaddController(authorAppInstance));
			break;
		case LIBRARYLIST:
			loader = new FXMLLoader(getClass().getResource("../view/librarylist.fxml"));
			loader.setController(new LibrarylistController(authorAppInstance));
			break;
		case LIBRARYADD:
			loader = new FXMLLoader(getClass().getResource("../view/addlibrary.fxml"));
			loader.setController(new LibraryaddController(authorAppInstance));
			break;
		default:
			return false;
			
		}
		
		try{
			pane = (Pane) loader.load();
		} catch (IOException e){
			logger.error("Error opening fxml file: " + e.getMessage());
			return false;
		}
		authorAppInstance.getAuthorAppInstance().getRootPane().setCenter(pane);
		
		return true;
	}
	
	public boolean changeView(ViewType vType, Object data, Stage stage){
		
		FXMLLoader loader = null;
		Pane pane;
		
		switch(vType){
		
		case AUTHORDETAIL:
			loader = new FXMLLoader(getClass().getResource("../view/authordetail.fxml"));
			loader.setController(new AuthordetailController((Author) data, stage, authorAppInstance));
			stage.setTitle("Author Details");
			break;
		case AUTHOREDIT:
			loader = new FXMLLoader(getClass().getResource("../view/authordetailedit.fxml"));
			loader.setController(new AuthoreditController((Author) data, stage, authorAppInstance));
			stage.setTitle("Edit Author Details");
			break;
		case AUTHORAUDIT:
			loader = new FXMLLoader(getClass().getResource("../view/audittrail.fxml"));
			loader.setController(new AuthoraudittrailController((Author) data, stage, authorAppInstance));
			stage.setTitle("Author Audit Trail");
			break;
		case BOOKDETAIL:
			loader = new FXMLLoader(getClass().getResource("../view/bookdetail.fxml"));
			loader.setController(new BookdetailController((Book) data, stage, authorAppInstance));
			stage.setTitle("Book Details");
			break;
		case BOOKEDIT:
			loader = new FXMLLoader(getClass().getResource("../view/bookdetailedit.fxml"));
			loader.setController(new BookeditController((Book) data, stage, authorAppInstance));
			stage.setTitle("Edit Book Details");
			break;
		case BOOKAUDIT:
			loader = new FXMLLoader(getClass().getResource("../view/audittrail.fxml"));
			loader.setController(new BookaudittrailController((Book) data, stage, authorAppInstance));
			stage.setTitle("Book Audit Trail");
			break;
		case LIBRARYDETAIL:
			loader = new FXMLLoader(getClass().getResource("../view/librarydetail.fxml"));
			loader.setController(new LibrarydetailController((Library) data, stage, authorAppInstance));
			stage.setTitle("Library Details");
			break;
		case LIBRARYEDIT:
			loader = new FXMLLoader(getClass().getResource("../view/librarydetailedit.fxml"));
			loader.setController(new LibraryeditController((Library) data, stage, authorAppInstance));
			stage.setTitle("Edit Library Details");
			break;
		case LIBRARYAUDIT:
			loader = new FXMLLoader(getClass().getResource("../view/audittrail.fxml"));
			loader.setController(new LibraryaudittrailController((Library) data, stage, authorAppInstance));
			stage.setTitle("Lab Audit Trail");
			break;	
		default:
			return false;
		
		}
		
		try{
			pane = (Pane) loader.load();
		} catch (IOException e){
			logger.error("Error opening fxml file: " + e.getMessage());
			return false;
		}
		Scene scene = new Scene(pane);
   		stage.setScene(scene);
   		stage.show();
		
		return true;
	}
	
	@FXML public void initialize(){
		//when the stage is closed, the gateway connection is closed
		authorAppInstance.getAuthorAppInstance().getMainStage().setOnCloseRequest(new EventHandler<WindowEvent>() {
	          public void handle(WindowEvent we) {
	        	  authorAppInstance.getAuthorGatewayInstance().close();
	        	  authorAppInstance.getBookGatewayInstance().close();
	        	  authorAppInstance.getLibraryGatewayInstance().close();
	              logger.info("Application closed.");
	          }
		});
	}
}
