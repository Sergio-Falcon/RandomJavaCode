package controller;

import java.util.ArrayList;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import driver.AuthorAppSingleton;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import model.Book;
import model.Author;

//CS 4743 Assignment 4 by <Sergio Falcon>
public class BookeditController {
	private ObservableList<Author> authorItems;
	private Book book;
	private Stage stage;
	private AuthorAppSingleton authorAppInstance;
	private Logger logger = LogManager.getLogger(AuthoreditController.class);
	
	@FXML private Label id;
	@FXML private TextField title;
	@FXML private TextField publisher;
	@FXML private DatePicker datePublished;
	@FXML private TextField summary;
	@FXML private ComboBox<Author> authors;
	@FXML private Button cancel;
	@FXML private Button save;
	
	public BookeditController(Book book, Stage stage, AuthorAppSingleton authorAppInstance){
		this.book = book;
		this.stage = stage;
		this.authorAppInstance = authorAppInstance;
	}
	
	private void setFields(){
		Book relaunchData = (Book) stage.getUserData();
		if(relaunchData == null){
			id.setText(String.valueOf(book.getId()));
			title.setText(book.getTitle());
			publisher.setText(book.getPublisher());
			datePublished.setPromptText(String.valueOf(book.getDatePublished()));
			summary.setText(book.getSummary());
	    	authors.setValue(book.getAuthor());
			setAuthorItems();
		}
		else{
			id.setText(String.valueOf(relaunchData.getId()));
			title.setText(relaunchData.getTitle());
			publisher.setText(relaunchData.getPublisher());
			datePublished.setPromptText(String.valueOf(relaunchData.getDatePublished()));
			summary.setText(relaunchData.getSummary());
			authors.setValue(relaunchData.getAuthor());
			authors.setItems(authorItems);
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
		            	authorAppInstance.getMenuControllerInstance().changeView(ViewType.BOOKDETAIL, book, stage);
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
						book.setTitle(title.getText());
						book.setPublisher(publisher.getText());
						book.setDatePublished(String.valueOf(datePublished.getValue()));
						book.setSummary(summary.getText());
						book.setAuthor(authors.getValue());
					
						book.updateBook();
					
						authorAppInstance.getMenuControllerInstance().changeView(ViewType.BOOKDETAIL, book, stage);
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
		
		if(!title.getText().equals(book.getTitle()) || !publisher.getText().equals(book.getPublisher()) || !datePublished.getValue().equals(book.getDatePublished()) || !summary.getText().equals(book.getSummary()) || authors.getValue().getId() != book.getAuthor().getId()){
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
					book.setTitle(title.getText());
					book.setPublisher(publisher.getText());
					book.setDatePublished(String.valueOf(datePublished.getValue()));
					book.setSummary(summary.getText());
					book.setAuthor(authors.getValue());
				
					book.updateBook();
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
		alert.setContentText("This book has been modified since you have tried to edit and save. Please reload the book list and then reopen this book from the list to view changes before attempting to re-edit and re-save.");

		alert.showAndWait();
	}
	
	private void setOnCloseHandler(){
		stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
	          public void handle(WindowEvent we) {
	        	  if(!promptSaveIfChanges()){
	        		  we.consume();
	        	  }
	          }
		});
	}
	
	private void setAuthorItems(){
		
		ArrayList<Author> authorsList = authorAppInstance.getAuthorGatewayInstance().getAuthors();
		authorItems = FXCollections.observableArrayList();
		
		for(int i = 0; i < authorsList.size(); i++){
			authorItems.add(authorsList.get(i));
		}
		
		authors.setItems(authorItems);
	}
	
	@FXML public void initialize(){
		setFields();
		setButtonHandlers();
		setOnCloseHandler();
	}
}
