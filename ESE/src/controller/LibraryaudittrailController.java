package controller;

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
import model.Library;

public class LibraryaudittrailController {

	private ArrayList<AuditTrail> auditTrailList;
	private ObservableList<String> auditTrailItems;
	private AuthorAppSingleton authorAppInstance;
	private Library library;
	private Stage stage;
	private Logger logger = LogManager.getLogger(BooklistController.class);
	
	@FXML Label header;
	@FXML ListView<String> list;
	@FXML Button back;
	
	public LibraryaudittrailController(Library library, Stage stage, AuthorAppSingleton authorAppInstance){
		this.library = library;
		this.stage = stage;
		this.authorAppInstance = authorAppInstance;
	}
	
	public void setHeader(){
		header.setText("Audit Trail For " + library.getName() + " Library");
	}
	
	public void setListView(){
		auditTrailList = library.getAuditTrail();
		
		//in order to add items to the List View it needs to be passed an ObservableList object,
		//however ObservableList is an interface so to make a concrete implementations we will
		//instantiate it with FXCollections.observableArrayList(). We will set names as Strings
		ObservableList<String> LibraryItems = FXCollections.observableArrayList();
		
		//fill the ObservableList with the dog names
		for(int i = 0; i < auditTrailList.size(); i++){
			LibraryItems.add(auditTrailList.get(i).getDateAdded() + ":\t" + auditTrailList.get(i).getMessage());
		}
				
		//set the list with the ObservableList
		list.setItems(LibraryItems);
	}
	
	public void setButtonHandler(){
		EventHandler<MouseEvent> backHandler = new EventHandler<MouseEvent>() {
			//EventHandler has a "handle" function that must be overridden
			@Override
			public void handle(MouseEvent mouseEvent){
				authorAppInstance.getMenuControllerInstance().changeView(ViewType.LIBRARYDETAIL, library, stage);
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
