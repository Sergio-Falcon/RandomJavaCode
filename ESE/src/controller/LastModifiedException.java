package controller;

//CS 4743 Assignment 4 by <Sergio Falcon>
public class LastModifiedException extends Exception{

	public LastModifiedException (Exception e) {
		super(e);
	}
	
	public LastModifiedException(String msg) {
		super(msg);
	}
}
