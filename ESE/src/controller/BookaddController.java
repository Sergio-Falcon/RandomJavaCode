package controller;

import java.util.ArrayList;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import driver.AuthorAppSingleton;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import model.Author;
import model.Book;

//CS 4743 Assignment 4 by <Sergio Falcon>
public class BookaddController {
	private ObservableList<Author> authorItems;
	private Book book;
	private AuthorAppSingleton authorAppInstance;
	private Logger logger = LogManager.getLogger(BookaddController.class);
	
	@FXML private Label id;
	@FXML private TextField title;
	@FXML private TextField publisher;
	@FXML private DatePicker datePublished;
	@FXML private TextField summary;
	@FXML private ComboBox<Author> authors;
	
	@FXML private Button cancel;
	@FXML private Button save;
	
	public BookaddController(AuthorAppSingleton authorAppInstance){
		book = new Book();
		this.authorAppInstance = authorAppInstance;
	}
	
	//Instead of creating an even handler, here we are handling an event based on the object
	//that was interacted with. This is the other way of handling events.
	@FXML private void handleButtonAction(ActionEvent event) throws Exception{
		if(event.getSource() == cancel) {
			logger.info("Cancel selected");
			//set center to blank
			authorAppInstance.getMenuControllerInstance().changeView(ViewType.CLEAR, null);
		}
		else if(event.getSource() == save){
			//logger.info("Add author selected.");
			//set all the instance variables for author
			try{
			book.setTitle(title.getText());
			book.setPublisher(publisher.getText());
			book.setDatePublished(String.valueOf(datePublished.getValue()));
			book.setSummary(summary.getText());
			book.setAuthor(authors.getValue());
			book.setAuthorAppInstance(authorAppInstance);
			
			//call the save function
			book.saveBook();
			
			//set the text in the view
			id.setText(String.valueOf(book.getId()));
			title.setText(book.getTitle());
			publisher.setText(book.getPublisher());
			//datePublished.setText(book.getDatePublished());
			summary.setText(book.getSummary());
			authors.setValue(book.getAuthor());
			} catch(LastModifiedException e){
				lastModifiedAlert();
			}
		}
	}
	
	private void lastModifiedAlert(){
		Alert alert = new Alert(AlertType.INFORMATION);
		
		alert.setTitle("Update Error");
		alert.setHeaderText("Changes have not been saved!");
		alert.setContentText("This book has been modified since you have tried to edit and save. Please reload the book list and then reopen this book from the list to view changes before attempting to re-edit and re-save.");

		alert.showAndWait();
	}
	
	private void setAuthorsList(){
		
		ArrayList<Author> authorsList = authorAppInstance.getAuthorGatewayInstance().getAuthors();
		authorItems = FXCollections.observableArrayList();
		
		for(int i = 0; i < authorsList.size(); i++){
			authorItems.add(authorsList.get(i));
		}
		
		authors.setItems(authorItems);
	}
	
	@FXML public void initialize(){
		
		setAuthorsList();
	}
}
