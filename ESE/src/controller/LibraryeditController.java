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
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import model.Library;
import model.Author;
import model.Book;
import model.LibraryBook;

public class LibraryeditController {

	private ObservableList<Book> bookItems;
	private ObservableList<LibraryBook> libraryBookItems;
	private ArrayList<LibraryBook> libraryBookList;
	private ArrayList<Book> bookList;
	private Library library;
	private Stage stage;
	private AuthorAppSingleton authorAppInstance;
	private Logger logger = LogManager.getLogger(BookeditController.class);
	@FXML private Label id;
	@FXML private TextField name;
	@FXML private TextField quantity;
	@FXML private ComboBox<Book> books;
	@FXML private Button cancel;
	@FXML private Button save;
	@FXML private Button delete;
	@FXML private Button add_change;
	@FXML private ListView<LibraryBook> list;
	
	public LibraryeditController(Library library, Stage stage, AuthorAppSingleton authorAppInstance){
		this.library = library;
		this.stage = stage;
		this.authorAppInstance = authorAppInstance;
	}
	
	private void setFields(){
		id.setText(String.valueOf(library.getId()));
		name.setText(library.getName());
		
		//create a copy of the LibraryBook array in library so that if changes are canceled then
		//we can just throw away any changes made (otherwise we can just set the new
		//array to be the library's array)
		libraryBookList = new ArrayList<LibraryBook>();
		for(int i = 0; i < library.getBooks().size(); i++){
			libraryBookList.add(new LibraryBook(library.getBooks().get(i).getBook(), library.getBooks().get(i).getQuantity(), library.getBooks().get(i).isNewRecord()));
		}
		libraryBookItems = FXCollections.observableArrayList();
		loadLibraryBookList();
		
		bookList = authorAppInstance.getBookGatewayInstance().getBooks();
		bookItems = FXCollections.observableArrayList();
		for(int i = 0; i < bookList.size(); i++){
			bookItems.add(bookList.get(i));
		}
		books.setItems(bookItems);
	}
	
	private void loadLibraryBookList(){
		libraryBookItems.clear();
		for(int i = 0; i < libraryBookList.size(); i++){
			libraryBookItems.add(libraryBookList.get(i));
		}
		list.setItems(libraryBookItems);
	}
	
	private void setButtonHandlers(){
		
		EventHandler<MouseEvent> cancelHandler = new EventHandler<MouseEvent>() {
			//EventHandler has a "handle" function that must be overridden
			@Override
			public void handle(MouseEvent mouseEvent){
				//MouseButton.PRIMARY indicates the left mouse button (usually)
				if(mouseEvent.getButton().equals(MouseButton.PRIMARY)){
		            if(promptSaveIfChanges()){
		            	authorAppInstance.getMenuControllerInstance().changeView(ViewType.LIBRARYDETAIL, library, stage);
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
						library.setName(name.getText());
						//sets LibraryBooks list in library to copy (and any included updates) of list
						library.setBooks(libraryBookList);
					
						library.updateLibrary();
					
						authorAppInstance.getMenuControllerInstance().changeView(ViewType.LIBRARYDETAIL, library, stage);
					} catch(LastModifiedException e){
						lastModifiedAlert();
					}
				}
			}
		};
		save.setOnMouseClicked(saveHandler);
		
		EventHandler<MouseEvent> deleteHandler = new EventHandler<MouseEvent>() {
			//EventHandler has a "handle" function that must be overridden
			@Override
			public void handle(MouseEvent mouseEvent){
				LibraryBook selected;
				//MouseButton.PRIMARY indicates the left mouse button (usually)
				if(mouseEvent.getButton().equals(MouseButton.PRIMARY)){
					selected = list.getSelectionModel().getSelectedItem();
					if(selected != null){
						libraryBookList.remove(selected);
						loadLibraryBookList();
					}
				}
			}
		};
		delete.setOnMouseClicked(deleteHandler);
		
		EventHandler<MouseEvent> addChangeHandler = new EventHandler<MouseEvent>() {
			//EventHandler has a "handle" function that must be overridden
			@Override
			public void handle(MouseEvent mouseEvent){
				Book selected;
				int qty;
				boolean foundBook = false;
				//MouseButton.PRIMARY indicates the left mouse button (usually)
				if(mouseEvent.getButton().equals(MouseButton.PRIMARY)){
					selected = books.getSelectionModel().getSelectedItem();
					if(selected != null){
						if(quantity.getText() != null && !quantity.getText().equals("")){
							//TODO: should probably add a try/catch for NumberFormatException with a dialog box alert that a number must be entered into quantity
							qty = Integer.parseInt(quantity.getText());
							//check if book already exists in array
							for(int i = 0; i < libraryBookList.size(); i++){
								if(selected.getId() == libraryBookList.get(i).getBook().getId()){
									//then just set quantity
									libraryBookList.get(i).setQuantity(qty);
									foundBook = true;
									break;
								}
							}
							//if we got through the entire list of libraryBookList and didn't set
							//foundDog to true, then the dog must not be present in the list
							if(!foundBook){
								libraryBookList.add(new LibraryBook(selected, qty));
							}
						}
						else{
							//TODO: insert modal dialog box prompting that user must enter a quantity
						}
						loadLibraryBookList();
					}
				}
			}
		};
		add_change.setOnMouseClicked(addChangeHandler);
	}
	
	private boolean promptSaveIfChanges(){
		Alert alert = new Alert(AlertType.CONFIRMATION);
		
		if(!name.getText().equals(library.getName()) || hasLibraryListChanged()){
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
					library.setName(name.getText());
					//sets LabDogs list in library to copy (and any included updates) of list
					library.setBooks(libraryBookList);
				
					library.updateLibrary();
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
	
	//checks if the LabDog list has changed, so if there are changes and user tries to
	//cancel/exit, then can prompt to save changes before discarding them.
	private boolean hasLibraryListChanged(){
		if(libraryBookList.size() == library.getBooks().size()){
			for(int i = 0; i < libraryBookList.size(); i++){
				if(libraryBookList.get(i).getQuantity() != library.getBooks().get(i).getQuantity()){
					return true;
				}
			}
		}
		else{
			return true;
		}
		return false;
	}
	
	private void lastModifiedAlert(){
		Alert alert = new Alert(AlertType.INFORMATION);
		
		alert.setTitle("Update Error");
		alert.setHeaderText("Changes have not been saved!");
		alert.setContentText("This Lab has been modified since you have tried to edit and save. Please reload the dog list and then reopen this dog from the list to view changes before attempting to re-edit and re-save.");

		alert.showAndWait();
	}
	
	//consume event if canceled
	private void setOnCloseHandler(){
		stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
	          public void handle(WindowEvent we) {
	        	  if(!promptSaveIfChanges()){
	        		  we.consume();
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
