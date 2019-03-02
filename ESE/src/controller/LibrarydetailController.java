package controller;

import java.util.ArrayList;

import driver.AuthorAppSingleton;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import model.Library;
import model.LibraryBook;

public class LibrarydetailController {

	private Library library;
	private Stage stage;
	private AuthorAppSingleton authorAppInstance;
	private ArrayList<LibraryBook> bookList;
	private ObservableList<LibraryBook> bookItems;
	@FXML private Label id;
	@FXML private Label name;
	@FXML private Button edit;
	@FXML private Button audit;
	@FXML private ListView<LibraryBook> list;
	
	public LibrarydetailController(Library library, Stage stage, AuthorAppSingleton authorAppInstance){
		this.library = library;
		this.stage = stage;
		this.authorAppInstance = authorAppInstance;
	}
	
	//set the text in the labels for the library
	private void setLabels(){
		id.setText(String.valueOf(library.getId()));
		name.setText(library.getName());
		
    	bookList = library.getBooks();
    	ObservableList<LibraryBook> bookItems = FXCollections.observableArrayList();
		for(int i = 0; i < bookList.size(); i++){
			bookItems.add(bookList.get(i));
		}
		list.setItems(bookItems);
	}
	
	private void setButtonHandlers(){
		EventHandler<MouseEvent> editHandler = new EventHandler<MouseEvent>() {
			//EventHandler has a "handle" function that must be overriden
			@Override
			public void handle(MouseEvent mouseEvent){
				//MouseButton.PRIMARY indicates the left mouse button (usually)
				if(mouseEvent.getButton().equals(MouseButton.PRIMARY)){
		            	
					authorAppInstance.getMenuControllerInstance().changeView(ViewType.LIBRARYEDIT, library, stage);
		           
		           	//TODO: make logger
		           	//System.out.println("Dog " + selected.getName() + " selected.");
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
		            	
					authorAppInstance.getMenuControllerInstance().changeView(ViewType.LIBRARYAUDIT, library, stage);
		           
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
