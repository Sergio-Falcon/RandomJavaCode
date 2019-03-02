package driver;

import javafx.application.*;
import javafx.fxml.*;
import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.stage.*;

//extends Application to launch it as a javafx application
public class AuthorApp extends Application{

	private Stage mainStage = new Stage();
	private BorderPane rootPane;
	private AuthorAppSingleton authorAppInstance;
	
	public AuthorApp(){
		rootPane = new BorderPane();
		authorAppInstance = new AuthorAppSingleton(this);
	}
	
	public AuthorApp(AuthorAppSingleton authorAppInstance){
		rootPane = new BorderPane();
		this.authorAppInstance = authorAppInstance;
	}
	
	public BorderPane getRootPane(){
		return rootPane;
	}
	
	public Stage getMainStage(){
		return mainStage;
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		mainStage = primaryStage;
		
		FXMLLoader loader = new FXMLLoader(getClass().getResource("../view/menu.fxml"));
		loader.setController(authorAppInstance.getMenuControllerInstance());
		MenuBar menu = loader.load();
		rootPane.setTop(menu);
		
		Scene scene = new Scene(rootPane, 640, 480);
		mainStage.setTitle("AuthorDemo");
		mainStage.setScene(scene);
		mainStage.show();
	}
}
