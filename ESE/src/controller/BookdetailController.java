package controller;

//CS 4743 Assignment 4 by <Sergio Falcon>
import driver.AuthorAppSingleton;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import model.Book;

public class BookdetailController {

	private Book book;
	private Stage stage;
	AuthorAppSingleton authorAppInstance;
	@FXML private Label id;
	@FXML private Label title;
	@FXML private Label publisher;
	@FXML private Label datePublished;
	@FXML private Label summary;
	@FXML private Label author;
	@FXML private Button edit;
	@FXML private Button audit;
	
	public BookdetailController(Book data, Stage stage, AuthorAppSingleton authorAppInstance){
		this.book = data;
		this.stage = stage;
		this.authorAppInstance = authorAppInstance;
	}
	
	//set the text in the labels for the author's id, name, breed, and gender
	private void setLabels(){
		id.setText(String.valueOf(book.getId()));
		title.setText(book.getTitle());
		publisher.setText(book.getPublisher());
		datePublished.setText(String.valueOf(book.getDatePublished()));
		summary.setText(book.getSummary());
    	author.setText(book.getAuthor().getName());
	}
	
	private void setButtonHandlers(){
		EventHandler<MouseEvent> editHandler = new EventHandler<MouseEvent>() {
			//EventHandler has a "handle" function that must be overriden
			@Override
			public void handle(MouseEvent mouseEvent){
				//MouseButton.PRIMARY indicates the left mouse button (usually)
				if(mouseEvent.getButton().equals(MouseButton.PRIMARY)){
		            	
					authorAppInstance.getMenuControllerInstance().changeView(ViewType.BOOKEDIT, book, stage);
		           
		           	//TODO: make logger
		           	//System.out.println("Author " + selected.getName() + " selected.");
		        }
			}
		};
		
		edit.setOnMouseClicked(editHandler);
		
		EventHandler<MouseEvent> auditHandler = new EventHandler<MouseEvent>() {
			//EventHandler has a "handle" function that must be overriden
			@Override
			public void handle(MouseEvent mouseEvent){
				//MouseButton.PRIMARY indicates the left mouse button (usually)
				if(mouseEvent.getButton().equals(MouseButton.PRIMARY)){
		            	
					authorAppInstance.getMenuControllerInstance().changeView(ViewType.BOOKAUDIT, book, stage);
		           
		           	//TODO: make logger
		           	//System.out.println("Audit selected.");
		        }
			}
		};
		
		audit.setOnMouseClicked(auditHandler);
	}
	
	private void setOnCloseHandler(){
		stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
	          public void handle(WindowEvent we) {
	        	  //do nothing
	        	  //this is hear to clear out the event handler from the edit controller
	        	  //as it can cause some strange things to happen
	          }
		});
	}
	
	//use initialize to force fxml to run "setLabels" as part of instantiating the controller
	@FXML public void initialize(){
		setLabels();
		setButtonHandlers();
		setOnCloseHandler();
	}
}
