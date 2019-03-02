package controller;

//CS 4743 Assignment 4 by <Sergio Falcon>
import java.util.ArrayList;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import driver.AuthorAppSingleton;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import model.Book;

public class BooklistController {
	
	private ArrayList<Book> booksList;
	private ObservableList<Book> bookItems;
	private AuthorAppSingleton authorAppInstance;
	private Logger logger = LogManager.getLogger(BooklistController.class);
	
	@FXML private ListView<Book> list = new ListView<Book>();
	@FXML private Button delete;
	
	public BooklistController(AuthorAppSingleton authorAppInstance){
		this.authorAppInstance = authorAppInstance;
	}
	
	public void setListView(){
		
		booksList = authorAppInstance.getBookGatewayInstance().getBooks();
		
		//in order to add items to the List View it needs to be passed an ObservableList object,
		//however ObservableList is an interface so to make a concrete implementations we will
		//instantiate it with FXCollections.observableArrayList(). We will set names as Strings
		ObservableList<Book> bookItems = FXCollections.observableArrayList();
		//fill the ObservableList with the author names
		for(int i = 0; i < booksList.size(); i++){
			bookItems.add(booksList.get(i));
		}
		
		//set the list with the ObservableList
		list.setItems(bookItems);
		
		//Event Handler for a MouseEvent. We will use this to handle a double click on a list item  
		EventHandler<MouseEvent> doubleClick = new EventHandler<MouseEvent>() {
			//EventHandler has a "handle" function that must be overriden
			@Override
			public void handle(MouseEvent mouseEvent){
				//MouseButton.PRIMARY indicates the left mouse button (usually)
				if(mouseEvent.getButton().equals(MouseButton.PRIMARY)){
					//There is no explicit "double click" property, instead you see how many
					//times the event "counted" clicks. 2 means it was clicked twice (or double clicked)
		            if(mouseEvent.getClickCount() == 2){
		            	//in order to tell which author name was clicked on, we will see what name with id
		            	//the list had highlighted at the time it was double clicked
		            	//list.getSelectionModel().getSelectedItem() returns the list item that
		            	//was hightlighted; from there we can parse out the id using "indexOf" and "substring"
		            	
		            	Stage stage = new Stage();
		            	authorAppInstance.getMenuControllerInstance().changeView(ViewType.BOOKDETAIL, list.getSelectionModel().getSelectedItem(), stage);
		            	
		            	logger.info("Book " + list.getSelectionModel().getSelectedItem().getTitle() + " " + list.getSelectionModel().getSelectedItem().getPublisher() + " selected.");
		            }
		        }
			}
		};
		//after creating the double click event, set it to the List View object as a
		//mouse click event
		list.setOnMouseClicked(doubleClick);
	}
	
	public void setButtonHandler(){
		EventHandler<MouseEvent> deleteHandler = new EventHandler<MouseEvent>() {
			//EventHandler has a "handle" function that must be overridden
			@Override
			public void handle(MouseEvent mouseEvent){
				//MouseButton.PRIMARY indicates the left mouse button (usually)
				if(mouseEvent.getButton().equals(MouseButton.PRIMARY)){
					Alert alert = new Alert(AlertType.CONFIRMATION);
					
					//ask if they really want to delete
					//customize the buttons
					alert.getButtonTypes().clear();
					ButtonType buttonTypeOne = new ButtonType("Yes");
					ButtonType buttonTypeTwo = new ButtonType("No");
					alert.getButtonTypes().setAll(buttonTypeOne, buttonTypeTwo);

					alert.setTitle("Delete Confirmation");
					alert.setHeaderText("This action will delete the selected Book from the list and all data associated with it.");
					alert.setContentText("Are you sure you want to delete the selected Book?");

					Optional<ButtonType> result = alert.showAndWait();
					
					//show the text of the pressed button
					if(result.get().getText().equals("Yes")){
						//delete author in the database
						authorAppInstance.getBookGatewayInstance().deleteBook(list.getSelectionModel().getSelectedItem());
						//delete author from the authorsList
						booksList.remove(list.getSelectionModel().getSelectedItem());
						//reload the list view
						setListView();
					}
				}
			}
		};
		delete.setOnMouseClicked(deleteHandler);
	}
	
	//use initialize to force fxml to run "setListView" as part of instantiating the controller
	@FXML public void initialize(){
		setListView();
		setButtonHandler();
	}
	
	/*public Author findAuthor(int id){
		for(int i = 0; i < authorsList.size(); i++){
			if(authorsList.get(i).getId() == id){
				return authorsList.get(i);
			}
		}
		return null;
	}*/

}
