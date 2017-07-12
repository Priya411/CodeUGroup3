package codeu.chat.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import codeu.chat.util.*;

public class UserInterest {

	private Uuid userID; 
	private String name; 
	private ArrayList<String> convosCreated; 
	private ArrayList<String> convosAddedTo; 
	
	public UserInterest(Uuid userID, String name) {
		this.userID = userID;
		this.name = name; 
		this.convosCreated = new ArrayList<String>(); 
		this.convosAddedTo = new ArrayList<String>(); 
	}


	public void addConvoCreated(String convo){ 
		this.convosCreated.add(convo);
	}
	
	public void addConvoAddedTo(String convo){
		this.convosAddedTo.add(convo);
	}
	
	public Uuid getUserID() {
		return userID;
	}
	
	public String getname() { 
		return name; 
	}

	public ArrayList<String> getConvosCreated() {
		return convosCreated;
	}

	public ArrayList<String> getConvosAddedTo() {
		return convosAddedTo;
	}	
	
}

