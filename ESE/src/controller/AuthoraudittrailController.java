package controller;

import java.util.ArrayList;

//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;

import driver.AuthorAppSingleton;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import model.AuditTrail;
import model.Author;

public class AuthoraudittrailController {

	private ArrayList<AuditTrail> auditTrailList;
	private ObservableList<String> auditTrailItems;
	private AuthorAppSingleton authorAppInstance;
	private Author author;
	private Stage stage;
	//private Logger logger = LogManager.getLogger(AuthorlistController.class);
	
	@FXML Label header;
	@FXML ListView<String> list;
	@FXML Button back;
	
	public AuthoraudittrailController(Author author, Stage stage, AuthorAppSingleton authorAppInstance){
		this.author = author;
		this.stage = stage;
		this.authorAppInstance = authorAppInstance;
	}
	
	public void setHeader(){
		header.setText("Audit Trail For " + author.getFirstName());
	}
	
	public void setListView(){
		auditTrailList = author.getAuditTrail();
		
		//in order to add items to the List View it needs to be passed an ObservableList object,
		//however ObservableList is an interface so to make a concrete implementations we will
		//instantiate it with FXCollections.observableArrayList(). We will set names as Strings
		ObservableList<String> authorItems = FXCollections.observableArrayList();
		
		//fill the ObservableList with the author names
		for(int i = 0; i < auditTrailList.size(); i++){
			authorItems.add(auditTrailList.get(i).getDateAdded() + ":\t" + auditTrailList.get(i).getMessage());
		}
				
		//set the list with the ObservableList
		list.setItems(authorItems);
	}
	
	public void setButtonHandler(){
		EventHandler<MouseEvent> backHandler = new EventHandler<MouseEvent>() {
			//EventHandler has a "handle" function that must be overridden
			@Override
			public void handle(MouseEvent mouseEvent){
				authorAppInstance.getMenuControllerInstance().changeView(ViewType.AUTHORDETAIL, author, stage);
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