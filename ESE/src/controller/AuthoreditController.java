package controller;

import java.sql.Timestamp;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import driver.AuthorAppSingleton;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import model.Author;

//CS 4743 Assignment 4 by <Sergio Falcon>
public class AuthoreditController {
	
	private ObservableList<String> genderList = FXCollections.observableArrayList("Male", "Female", "Unknown");
	private Author author;
	private Stage stage;
	private AuthorAppSingleton authorAppInstance;
	private Logger logger = LogManager.getLogger(AuthoreditController.class);

	@FXML private Label id;
	@FXML private TextField firstName;
	@FXML private TextField lastName;
	@FXML private DatePicker dob;
	@FXML private ChoiceBox<String> gender;
	@FXML private TextField website;
	@FXML private Button cancel;
	@FXML private Button save;
	
	public AuthoreditController(Author author, Stage stage, AuthorAppSingleton authorAppInstance){
		this.author = author;
		this.stage = stage;
		this.authorAppInstance = authorAppInstance;
	}
	
	private void setFields(){
		Author relaunchData = (Author) stage.getUserData();
		if(relaunchData == null){
			id.setText(String.valueOf(author.getId()));
			firstName.setText(author.getFirstName());
			lastName.setText(author.getLastName());
	    	dob.setPromptText(String.valueOf(author.getDob()));
			gender.setValue(author.getGender());
	    	gender.setItems(genderList);
	    	website.setText(author.getWebsite());
		}
		else{
			id.setText(String.valueOf(relaunchData.getId()));
			firstName.setText(relaunchData.getFirstName());
			lastName.setText(relaunchData.getLastName());
			dob.setPromptText(String.valueOf(relaunchData.getDob()));
			gender.setValue(relaunchData.getGender());
			gender.setItems(genderList);
		}
	}
	
	private void setButtonHandlers(){
		EventHandler<MouseEvent> cancelHandler = new EventHandler<MouseEvent>() {
			//EventHandler has a "handle" function that must be overridden
			@Override
			public void handle(MouseEvent mouseEvent){
				//MouseButton.PRIMARY indicates the left mouse button (usually)
				if(mouseEvent.getButton().equals(MouseButton.PRIMARY)){
		            if(promptSaveIfChanges()){
		            	authorAppInstance.getMenuControllerInstance().changeView(ViewType.AUTHORDETAIL, author, stage);
					}
		        }
			}
		};
		//after creating the double click event, set it to the List View object as a
		//mouse click event
		cancel.setOnMouseClicked(cancelHandler);
		
		EventHandler<MouseEvent> saveHandler = new EventHandler<MouseEvent>() {
			//EventHandler has a "handle" function that must be overridden
			@Override
			public void handle(MouseEvent mouseEvent){
				//MouseButton.PRIMARY indicates the left mouse button (usually)
				if(mouseEvent.getButton().equals(MouseButton.PRIMARY)){
					try{
					author.setFirstName(firstName.getText());
					author.setLastName(lastName.getText());
					author.setDob(String.valueOf(dob.getValue()));
					author.setGender(gender.getValue());
					author.setWebsite(website.getText());
					
					author.updateAuthor();
					
					authorAppInstance.getMenuControllerInstance().changeView(ViewType.AUTHORDETAIL, author, stage);
					} catch(LastModifiedException e){
						lastModifiedAlert();
					}
				}
			}
		};
		save.setOnMouseClicked(saveHandler);
	}
	
	private boolean promptSaveIfChanges(){
		Alert alert = new Alert(AlertType.CONFIRMATION);
		
		if(!firstName.getText().equals(author.getFirstName()) || !lastName.getText().equals(author.getLastName()) || !gender.getValue().equals(author.getGender()) || !website.getText().equals(author.getWebsite()) ){
			alert.getButtonTypes().clear();
			ButtonType buttonTypeOne = new ButtonType("Yes");
			ButtonType buttonTypeTwo = new ButtonType("No");
			ButtonType buttonTypeThree = new ButtonType("Cancel");
			alert.getButtonTypes().setAll(buttonTypeOne, buttonTypeTwo, buttonTypeThree);

			alert.setTitle("Update Confirmation");
			alert.setHeaderText("Information has been changed but not saved.");
			alert.setContentText("Would you like to save your changes?");

			Optional<ButtonType> result = alert.showAndWait();
			
			if(result.get().getText().equals("Yes")){
				try{
					author.setFirstName(firstName.getText());
					author.setLastName(lastName.getText());
					author.setDob(String.valueOf(dob.getValue()));
					author.setGender(gender.getValue());
					author.setWebsite(website.getText());
					
					author.updateAuthor();
					return true;
				}
				catch(LastModifiedException e){
					
					return false;
				}
			}
			else if(result.get().getText().equals("No")){
				return true;
			}
			else{
				return false;
			}
		}
		
		return true;
	}
	
	private void lastModifiedAlert(){
		Alert alert = new Alert(AlertType.INFORMATION);
		
		alert.setTitle("Update Error");
		alert.setHeaderText("Changes have not been saved!");
		alert.setContentText("This author has been modified since you have tried to edit and save. Please reload the author list and then reopen this author from the list to view changes before attempting to re-edit and re-save.");

		Optional<ButtonType> result = alert.showAndWait();
	}
	
	//can also just consume event if you want the stage to not close
	//but wanted to show the setUserData functionality
	private void setOnCloseHandler(){
		stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
	          public void handle(WindowEvent we) {
	        	  if(!promptSaveIfChanges()){
	        		  Stage newStage = new Stage();
	        		  //TODO: may need to modify this?
	        		  newStage.setUserData(new Author(author.getId(), firstName.getText(), lastName.getText(), String.valueOf(dob.getValue()), gender.getValue(), website.getText()));
	        		  authorAppInstance.getMenuControllerInstance().changeView(ViewType.AUTHOREDIT, author, newStage);
	        	  }
	          }
		});
	}
	
	@FXML public void initialize(){
		setFields();
		setButtonHandlers();
		setOnCloseHandler();
	}

}
