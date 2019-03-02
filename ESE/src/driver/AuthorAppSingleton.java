package driver;

import controller.MenuController;
import gateway.*;

//CS 4743 Assignment 5 by <Sergio Falcon>
public class AuthorAppSingleton{

	private AuthorApp authorApp;
	private MenuController menuControl;
	private AuthorTableGateway authorGateway;
	private BookTableGateway bookGateway;
	private LibraryTableGateway libraryGateway;
	
	public AuthorAppSingleton(){
		authorApp = null;
		menuControl = null;
		authorGateway = null;
		bookGateway = null;
	}
	
	public AuthorAppSingleton(AuthorApp authorApp){
		this.authorApp = authorApp;
		menuControl = null;
		authorGateway = null;
		bookGateway = null;
	}
	
	//singleton pattern: if it doesn't exist, then create it,
	//otherwise return what already exists
	public AuthorApp getAuthorAppInstance(){
		if(authorApp == null){
			authorApp = new AuthorApp(this);
		}
		
		return authorApp;
	}
	
	public MenuController getMenuControllerInstance(){
		if(menuControl == null){
			menuControl = new MenuController(this);
		}
		
		return menuControl;
	}
	
	public AuthorTableGateway getAuthorGatewayInstance(){
		if(authorGateway == null){
			authorGateway = new AuthorTableGateway(this);
		}
		
		return authorGateway;
	}
	
	public BookTableGateway getBookGatewayInstance(){
		if(bookGateway == null){
			bookGateway = new BookTableGateway(this);
		}
		
		return bookGateway;
	}
	public LibraryTableGateway getLibraryGatewayInstance(){
		if(libraryGateway == null){
			libraryGateway = new LibraryTableGateway(this);
		}
		
		return libraryGateway;
	}
		
}
