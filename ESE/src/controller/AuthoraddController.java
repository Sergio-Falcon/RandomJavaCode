package controller;

import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import driver.AuthorAppSingleton;
import model.Author;

//CS 4743 Assignment 4 by <Sergio Falcon>
public class AuthoraddController {

	private ObservableList<String> genderList = FXCollections.observableArrayList("Male", "Female", "Unknown");
	private Author author;
	private AuthorAppSingleton authorAppInstance;
	private Logger logger = LogManager.getLogger(AuthoraddController.class);
	
	@FXML private Label id;
	@FXML private TextField firstName;
	@FXML private TextField lastName;
	@FXML private DatePicker dob;
	@FXML private ChoiceBox<String> gender;
	@FXML private TextField website;
	@FXML private Button cancel;
	@FXML private Button save;

	public AuthoraddController(AuthorAppSingleton authorAppInstance){
		author = new Author();
		this.authorAppInstance = authorAppInstance;
	}
	
	//Instead of creating an even handler, here we are handling an event based on the object
	//that was interacted with. This is the other way of handling events.
	@FXML private void handleButtonAction(ActionEvent event) throws Exception{
		
		if(event.getSource() == cancel) {
			logger.info("Author add selected.");
			//set center to blank
			authorAppInstance.getMenuControllerInstance().changeView(ViewType.CLEAR, null);
		}
		else if(event.getSource() == save){
			//logger.info("Add author selected.");
			//set all the instance variables for author;
			try{
			author.setFirstName(firstName.getText());
			author.setLastName(lastName.getText());
			author.setDob(String.valueOf(dob.getValue()));
			author.setGender(gender.getValue());
			author.setWebsite(website.getText());
			author.setAuthorAppInstance(authorAppInstance);
			
			//call the save function
			author.saveAuthor();
			
			//set the text in the view
			id.setText(String.valueOf(author.getId()));
			firstName.setText(author.getFirstName());
			lastName.setText(author.getLastName());
			//dob.setValue(author.getDob());
			gender.setValue(author.getGender());
			website.setText(author.getWebsite());
			} catch(LastModifiedException e){
				lastModifiedAlert();
			}
		}
	}
	private void lastModifiedAlert(){
		Alert alert = new Alert(AlertType.INFORMATION);
		
		alert.setTitle("Update Error");
		alert.setHeaderText("Changes have not been saved!");
		alert.setContentText("This author has been modified since you have tried to edit and save. Please reload the author list and then reopen this author from the list to view changes before attempting to re-edit and re-save.");

		Optional<ButtonType> result = alert.showAndWait();
	}
	@FXML public void initialize(){
		gender.setItems(genderList);
	}
}