package controller;

import driver.AuthorAppSingleton;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import model.Author;

//CS 4743 Assignment 4 by <Sergio Falcon>
public class AuthordetailController {

	private Author author;
	private Stage stage;
	AuthorAppSingleton authorAppInstance;
	@FXML private Label id;
	@FXML private Label firstName;
	@FXML private Label lastName;
	@FXML private Label dob;
	@FXML private Label gender;
	@FXML private Label website;
	@FXML private Button edit;
	@FXML private Button audit;
	
	public AuthordetailController(Author author, Stage stage, AuthorAppSingleton authorAppInstance){
		this.author = author;
		this.stage = stage;
		this.authorAppInstance = authorAppInstance;
	}
	
	//set the text in the labels for the author's id, name, DOB, gender, and website
	public void setLabels(){
		id.setText(String.valueOf(author.getId()));
		firstName.setText(author.getFirstName());
		lastName.setText(author.getLastName());
		dob.setText(String.valueOf(author.getDob()));
		gender.setText(author.getGender());
		website.setText(author.getWebsite());
	}
	
	private void setButtonHandlers(){
		EventHandler<MouseEvent> editHandler = new EventHandler<MouseEvent>() {
			//EventHandler has a "handle" function that must be overriden
			@Override
			public void handle(MouseEvent mouseEvent){
				//MouseButton.PRIMARY indicates the left mouse button (usually)
				if(mouseEvent.getButton().equals(MouseButton.PRIMARY)){
		            	
					authorAppInstance.getMenuControllerInstance().changeView(ViewType.AUTHOREDIT, author, stage);
		           
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
		            	
					authorAppInstance.getMenuControllerInstance().changeView(ViewType.AUTHORAUDIT, author, stage);
		           
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