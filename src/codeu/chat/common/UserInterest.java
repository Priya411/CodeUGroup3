package codeu.chat.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import codeu.chat.util.Uuid;

public class UserInterest {

	Uuid UserID; 
	String name; 
	private ArrayList<String> ConvosCreated; 
	private ArrayList<String> ConvosAddedTo; 
	
	public UserInterest(Uuid userID, String name) {
		this.UserID = userID;
		this.name = name; 
		this.ConvosCreated = new ArrayList<String>(); 
		this.ConvosAddedTo = new ArrayList<String>(); 
	}


	public void addConvoCreated(String convo){ 
		this.ConvosCreated.add(convo);
	}
	
	public void addConvoAddedTo(String convo){
		this.ConvosAddedTo.add(convo);
	}
	
	public Uuid getUserID() {
		return UserID;
	}
	
	public String getname() { 
		return name; 
	}

	public ArrayList<String> getConvosCreated() {
		return ConvosCreated;
	}

	public ArrayList<String> getConvosAddedTo() {
		return ConvosAddedTo;
	}	
	
}

