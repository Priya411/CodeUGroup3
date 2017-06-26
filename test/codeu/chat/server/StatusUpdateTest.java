package codeu.chat.server;

import static org.junit.Assert.*;

import codeu.chat.client.core.ConversationContext;
import codeu.chat.common.BasicController;
import codeu.chat.common.BasicView;
import codeu.chat.common.ConversationHeader;
import codeu.chat.common.ConversationPayload;
import codeu.chat.common.Message;
import codeu.chat.common.ServerInfo;
import codeu.chat.common.User;
import codeu.chat.util.Uuid;

import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.*;

import java.io.IOException;

import codeu.chat.util.store.Store;

import java.lang.Iterable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import codeu.chat.util.Time;

public class StatusUpdateTest {
	
	public Model createFakeModel() { 
        Model myModel = new Model();
        // MODEL SUMMARY: 
        // USERS: Matt || UUID = 1 
        //		  Julia || UUID = 2
        // CONVO: Chat1 || UUID = 1.1 created by Matt(UUID=1) 
        //		3 MESSAGES IN ALL 
        // CONVO: Chat2 || UUID = 1.2 created by Matt(UUID=1) 
        // 		0 MESSAGES IN ALL  
        myModel.add(new User(createUuid("1"), "Matt", Time.fromMs(4098590)));
        myModel.add(new User(createUuid("2"), "Julia", Time.fromMs(48574)));
        // ConversationHeader(Uuid id, Uuid owner, Time creation, String title)
        myModel.add(new ConversationHeader(createUuid("1.1"), createUuid("1"),  Time.fromMs(98094), "Chat1"), 
        		// ConversationPayload(Uuid id, Uuid firstMessage, Uuid lastMessage)
        		new ConversationPayload( createUuid("1.1"), createUuid("1.11"), createUuid("1.13")));
        myModel.add(new ConversationHeader(createUuid("1.2"), createUuid("1"),  Time.fromMs(59485), "Chat2"), 
        		new ConversationPayload( createUuid("1.2"), null, null));
        // Message(Uuid id, Uuid next, Uuid previous, Time creation, Uuid author, String content)
        myModel.add(new Message(createUuid("1.11"), createUuid("1.12"), null, Time.fromMs(49589), createUuid("1"),   "I'm so alone"));
        myModel.add(new Message(createUuid("1.12"), createUuid("1.12"), createUuid("1.11"), Time.fromMs(9485), createUuid("1"),   "I need friends"));
        myModel.add(new Message(createUuid("1.13"), createUuid("1.14"), createUuid("1.12"), Time.fromMs(5098), createUuid("1"),   "Julia'sFixesAreNotImplementedYetCanYouTell?"));
        
        
    	ArrayList<Uuid> userInterests = new ArrayList<Uuid>(); 
    	// both Matt(UUID=1) and Julia(UUID=2) are added as interests 
    	userInterests.add(createUuid("1")); 
    	userInterests.add(createUuid("2")); 
    	
    	myModel.userById().first(createUuid("1")).userInterests = userInterests; 
    	myModel.userById().first(createUuid("1")).lastUpdateTime = 0; 
    	
        return myModel; 
	}


    @Test
    public void testUserInterestList() {
        // tests to make sure UserInterests are properly saved 
    	
        Model myModel = this.createFakeModel(); 
    	ArrayList<Uuid> userInterests = new ArrayList<Uuid>(); 
    	// both Matt(UUID=1) and Julia(UUID=2) are added as interests 
    	userInterests.add(createUuid("1")); 
    	userInterests.add(createUuid("2")); 
  	  	
    	ArrayList<Uuid> RetreivedUserInterests = myModel.userById().at(createUuid("1")).iterator().next().userInterests;

    	System.out.println("running testUserInterestList");
    	assertEquals(RetreivedUserInterests, userInterests);
    	System.out.println("completed testUserInterestList\n");
    }

    // this test doesn't work :( 
    // still working on it 
//    @Test 
//    public void testUserInterestsAll() { 
//    	// tests to make sure user interests are printed correctly 
//    	Model myModel = this.createFakeModel(); 
//    	//creating the expected output 
//  	  	HashMap<Uuid, ArrayList<ArrayList<String>>> expectedUserUpdates = new HashMap<Uuid, ArrayList<ArrayList<String>>>(); 
//  	  	ArrayList<ArrayList<String>> MattsConvos = new ArrayList<ArrayList<String>>(); 
//  	  	MattsConvos.add(new ArrayList<String>(Arrays.asList("Chat1", "Chat2"))); 
//  	  	MattsConvos.add(new ArrayList<String>());
//  	  	expectedUserUpdates.put(createUuid("1"), MattsConvos); 
//  	  	
//  	  	ArrayList<ArrayList<String>> JuliasConvos = new ArrayList<ArrayList<String>>(); 
//  	  	JuliasConvos.add(new ArrayList<String>()); 
//  	  	JuliasConvos.add(new ArrayList<String>());
//  	  	expectedUserUpdates.put(createUuid("2"), JuliasConvos); 
//  	  	
//  	  	BasicView view = new View(null); 
//  	  	Controller controller = new Controller(null, null); 
//  	  	
//  	  	ConversationContext convo1 = new ConversationContext(
//  	  		new User(createUuid("1"), "Matt", Time.fromMs(4098590)),
//  	  		new ConversationHeader(createUuid("1.1"), createUuid("1"),  Time.fromMs(59485), "Chat1"),
//            view,
//            controller); 
//  	  	ConversationContext convo2 = new ConversationContext(
//    	  	new User(createUuid("1"), "Matt", Time.fromMs(4098590)),
//    	  	new ConversationHeader(createUuid("1.2"), createUuid("1"),  Time.fromMs(59485), "Chat2"),
//    	  	view,
//            controller); 
//  	  	
//  	  	ArrayList<ConversationContext> allConvos = new ArrayList<ConversationContext>();
//  	  	allConvos.add(convo1); 
//  	  	allConvos.add(convo2); 
//  	  	Iterable<ConversationContext> convos = allConvos; 
//  	  	
//  	  	HashMap<Uuid, ArrayList<ArrayList<String>>> RetreivedUserUpdates = 
//  	  			myModel.userById().at(createUuid("1")).iterator().next().USERstatusUpdate(convos);
//  	  	
//  	  	System.out.println(RetreivedUserUpdates);
//  	  	System.out.println(expectedUserUpdates);
//  	  	
//    	System.out.println("running testUserInterestsAll");
//    	//assertEquals(true, false);
//    	System.out.println("completed testUserInterestsAll\n");
//
//    }
    
	
    public Uuid createUuid(String id)
    {
        // This function is used to create a Uuid from a string
        // It was created since this code would be used multiple times
        Uuid toReturn = null;
        try {
            toReturn = Uuid.parse(id);
        } catch (IOException e) {
            System.out.println("Invalid Uuid");
        }
        return toReturn;
    }
}



