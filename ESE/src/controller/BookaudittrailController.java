package controller;

//CS 4743 Assignment 4 by <Sergio Falcon>
import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import driver.AuthorAppSingleton;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import model.AuditTrail;
import model.Book;

public class BookaudittrailController {

	private ArrayList<AuditTrail> auditTrailList;
	private ObservableList<String> auditTrailItems;
	private AuthorAppSingleton authorAppInstance;
	private Book book;
	private Stage stage;
	private Logger logger = LogManager.getLogger(AuthorlistController.class);
	
	@FXML Label header;
	@FXML ListView<String> list;
	@FXML Button back;
	
	public BookaudittrailController(Book book, Stage stage, AuthorAppSingleton authorAppInstance){
		this.book = book;
		this.stage = stage;
		this.authorAppInstance = authorAppInstance;
	}
	
	public void setHeader(){
		header.setText("Audit Trail For " + book.getTitle() + " " + book.getPublisher());
	}
	
	public void setListView(){
		auditTrailList = book.getAuditTrail();
		
		//in order to add items to the List View it needs to be passed an ObservableList object,
		//however ObservableList is an interface so to make a concrete implementations we will
		//instantiate it with FXCollections.observableArrayList(). We will set names as Strings
		ObservableList<String> bookItems = FXCollections.observableArrayList();
		
		//fill the ObservableList with the author names
		for(int i = 0; i < auditTrailList.size(); i++){
			bookItems.add(auditTrailList.get(i).getDateAdded() + ":\t" + auditTrailList.get(i).getMessage());
		}
				
		//set the list with the ObservableList
		list.setItems(bookItems);
	}
	
	public void setButtonHandler(){
		EventHandler<MouseEvent> backHandler = new EventHandler<MouseEvent>() {
			//EventHandler has a "handle" function that must be overridden
			@Override
			public void handle(MouseEvent mouseEvent){
				authorAppInstance.getMenuControllerInstance().changeView(ViewType.BOOKDETAIL, book, stage);
			}
		};
		back.setOnMouseClicked(backHandler);
	}
	
	@FXML public void initialize(){
		setHeader();
		setListView();
		setButtonHandler();
	}
}
