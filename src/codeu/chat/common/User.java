// Copyright 2017 Google Inc.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package codeu.chat.common;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import codeu.chat.client.core.ConversationContext;
import codeu.chat.client.core.MessageContext;
import codeu.chat.util.Serializer;
import codeu.chat.util.Serializers;
import codeu.chat.util.Time;
import codeu.chat.util.Uuid;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

 

// ignore anything that may cause problems when converting to JSON
// prevents crashes for items that cant be serialized.
@JsonIgnoreProperties(ignoreUnknown=true)
public final class User {
  
  public HashMap<Uuid, Integer> conversationInterests = new HashMap<>();
  public ArrayList<Uuid> userInterests = new ArrayList<Uuid>();
	
  // no need to serialize the serializer when converting to JOSN
  @JsonIgnore
  public static final Serializer<User> SERIALIZER = new Serializer<User>() {

    @Override
    public void write(OutputStream out, User value) throws IOException {

      Uuid.SERIALIZER.write(out, value.id);
      Serializers.STRING.write(out, value.name);
      Time.SERIALIZER.write(out, value.creation);

    }

    @Override
    public User read(InputStream in) throws IOException {
      return new User(
          Uuid.SERIALIZER.read(in),
          Serializers.STRING.read(in),
          Time.SERIALIZER.read(in)
      );
    }
  };

  // ignore objects that aren't built-in types (string, int, etc)
  // Jackons will default to the getters since instance vars are ignored
  @JsonIgnore
  public final Uuid id;
  public final String name;
  @JsonIgnore
  public final Time creation;
  
  // time of user's last update recorded as a long
  // initially set to user's creation time 
  public long lastUpdateTime; 
  public static int CONVOS_CREATED_ARRAY = 0; 
  public static int CONVOS_CONTRIBUTED_TO_ARRAY = 1; 

  public User(Uuid id, String name, Time creation) {
    this.id = id;
    this.name = name;
    this.creation = creation;
    this.lastUpdateTime = creation.inMs(); 
  }
  
  public String getUUID() {
	  return id.toString();
  }
  
  public long getCreationTime() {
	  return creation.inMs();
  }


  @Override
  public boolean equals (Object toCompare)
  {
    if(!(toCompare instanceof User)) {
      return false;
    }
    User toCompareUser = (User)(toCompare);
    if(!this.id.equals(toCompareUser.id)) {
      return false;
    }
    if(!this.name.equals(toCompareUser.name)) {
      return false;
    }
    if(!this.creation.equals(toCompareUser.creation)) {
      return false;
    }
    return true;
  }

  public int hashCode() { return hash(this); }

  private static int hash(User user) {
    int hash = 0;
    hash+=user.id.hashCode();
    hash+=user.name.hashCode();
    hash+=user.creation.hashCode();
    return hash;
  }
  
  // NEW userStatusUpdate() 
  // returns all the user updates in a list of UserInterest Objects 
  // each UserInterst objects has two arraylists: 
  // ConvosCreated and ConvosAddedTo
  // which track the new convos a user created 
  // and the convos a user has added messages to since the last update 
  public HashMap<Uuid, UserInterest> userStatusUpdate(Iterable<ConversationContext> conversations, Collection<User> users){
	  // HashMap keeps track of users and UUIDs for easy access 
	  HashMap<Uuid, String> allUsers = new HashMap<Uuid, String>();	  

	  for(User u: users) {
		  allUsers.put(u.id, u.name); 
	  }
	  
	  HashMap<Uuid, UserInterest> userUpdates = new HashMap<Uuid, UserInterest>(); 
	  
	  // creating the dictionary userUpdates
	  for (Uuid uuid : this.userInterests){
		  userUpdates.put(uuid, new UserInterest(uuid, allUsers.get(uuid))); 
	  }
	  
	  // looping through every conversation in the chat to organize updates 
	  for (ConversationContext convo : conversations){ 
		  
		  if (convo.lastMessage().message.getCreationTime() < this.lastUpdateTime){ 
			  // if the last message in the conversation was sent before lastUpdateTime 
			  // that means it was covered in the last status update and we can move on 
			  continue;
		  }
		  
		  if (convo.conversation.creation.inMs() > this.lastUpdateTime && 
				  this.userInterests.contains(convo.conversation.owner)){ 
			  // if the convo was created by a user of interest since the last update, 
			  // it will be added to the userInterest's ConvosCreated arraylist 
			  userUpdates.get(convo.conversation.owner).addConvoAddedTo(convo.conversation.title);
		  }
		  
		  
		  for (MessageContext currentmessage = convo.firstMessage(); 
				  currentmessage != null; currentmessage = currentmessage.next()){ 
			  // goes through all the messages in the conversation starting from the first one 
			  if (currentmessage.message.getCreationTime() > this.lastUpdateTime){
				  // if the currentmessage was created after the last update time 
				  // and it was contributed by a user of interest. 
				  // If so, we add it to the userInterest's convosAddedTo 
				  // otherwise we continue iterating through the messages in the convo 
				  if (this.userInterests.contains(currentmessage.message.author) && 
						  !userUpdates.get(convo.conversation.owner).getConvosAddedTo().contains(convo.conversation.title)){
					  userUpdates.get(convo.conversation.owner).addConvoAddedTo(convo.conversation.title);
				  }
			  }
		   }
	  }
	  this.lastUpdateTime = Time.now().inMs();
	  return userUpdates; 
  }
  
  // userStatusUpdate()
  // 
  // Returns all the user updates in an dictionary 
  // where keys are the user's user interests (UUIDs) 
  // and a value is an arraylist of arraylists<String> 
  // where the 1st arraylist keeps track of each userinterest's 
  // newly created conversations (CONVOS_CREATED_ARRAY) and the second arraylist keeps 
  // track of the conversations the userinterest has contributed messages to (CONVOS_CONTRIBUTED_TO_ARRAY)
  //
  public HashMap<Uuid, ArrayList<ArrayList<String>>> userStatusUpdate(Iterable<ConversationContext> conversations, Time updateTime){ 
	  // ASSUMPTION: all of the names saved in userInterests are indeed 
	  //			 valid users in the system 
	  
	  HashMap<Uuid, ArrayList<ArrayList<String>>> userUpdates = new HashMap<Uuid, ArrayList<ArrayList<String>>>(); 


	  // creating the dictionary userUpdates
	  for (Uuid uuid : this.userInterests){
		  userUpdates.put(uuid, new ArrayList<ArrayList<String>>(Arrays.asList(
				  new ArrayList<String>() {}, new ArrayList<String>() {}))); 
	  }
	  
	  // looping through every conversation in the chat to organize updates 
	  for (ConversationContext convo : conversations){
		  MessageContext lastmessage = convo.lastMessage(); 
		  
		  if (lastmessage.message.getCreationTime() < this.lastUpdateTime){ 
			  // if the last message in the conversation was sent before lastUpdateTime 
			  // that means it was covered in the last status update and we can move on 
			  continue;
		  }
		  
		  if (convo.conversation.creation.inMs() > this.lastUpdateTime && 
				  this.userInterests.contains(convo.conversation.owner)){ 
			  // if the convo was created by a user of interest, it will be added 
			  // to that user's value's CONVOS_CREATED_ARRAY
			  userUpdates.get(convo.conversation.owner).get(CONVOS_CREATED_ARRAY).add(convo.conversation.title); 
		  }
		  
		  
		  for (MessageContext currentmessage = convo.firstMessage(); 
				  currentmessage != null; currentmessage = currentmessage.next()){ 
			  // goes through all the messages in the conversation starting from the first one 
			  if (currentmessage.message.getCreationTime() > this.lastUpdateTime){
				  // if the currentmessage was created after the last update time 
				  // we need to look at it to see if it was contributed by a user 
				  // of interest. 
				  // If so, we add it to the user's CONVOS_CONTRIBUTED_TO_ARRAY, 
				  // otherwise we continue iterating through the messages in the convo 
				  if (this.userInterests.contains(currentmessage.message.author) && 
						  !userUpdates.get(convo.conversation.owner).get(CONVOS_CONTRIBUTED_TO_ARRAY).contains(convo.conversation.title)){
					  // Also checks to make sure that conversation hasn't already been added to CONVOS_CONTRIBUTED_TO_ARRAY
					  userUpdates.get(convo.conversation.owner).get(CONVOS_CONTRIBUTED_TO_ARRAY).add(convo.conversation.title); 
				  }
			  }
		   }	  
	  	}
	  // updates the lastUpdateTime for the user 
	  this.lastUpdateTime = Time.now().inMs();
	  return userUpdates; 
  	}
  
  
  // convoStatusUpdate 
  //
  // returns the conversation updates in a dictionary 
  // where the name of the convo is the key 
  // and the number of messages sent since the last update is 
  // the value 
  // 
  public HashMap<String, Integer> convoStatusUpdate(Iterable<ConversationContext> conversations){ 
	  HashMap<String, Integer> convoUpdates = new HashMap<String, Integer>(); 
	  int totalNumMessages, numNewMessages; 
	  
	  for (ConversationContext convo : conversations) {
		  // iterates through all the convos
		  if (this.conversationInterests.keySet().contains(convo.conversation.id)) { 
			  // if the convo is one of interest, 
			  // first add the conversation and the number of messages 
			  // since last update to conversationInterests
			  totalNumMessages = convo.getMessageCount(); 
			  numNewMessages = totalNumMessages - this.conversationInterests.get(convo.conversation.id); 
			  convoUpdates.put(convo.conversation.title, numNewMessages); 
			  
			  // then update the value for the number of messages at lastUpdate in conversationInterests
			  this.conversationInterests.put(convo.conversation.id, totalNumMessages); 
		  }
	  }
	  return convoUpdates; 
  } 

  public void addUserInterest(Uuid userId)
  {
    // This function is used to allow a User to add
    // another user as an interest, and saves this
    // user's id
    userInterests.add(userId);
  }

  public void addConvoInterest(Uuid conversationId, int messagesAmount)
  {
    // This function is used to allow a User to add
    // another conversation as an interest, and saves this
    // conversation's id
    conversationInterests.put(conversationId, new Integer(messagesAmount));
  }

  public void remUserInterest(Uuid userId)
  {
    // This function is used to allow a User to remove
    // another user as an interest, and removes this
    // user's id
    userInterests.remove(userId);
  }

  public void remConvoInterest(Uuid conversationId)
  {
    // This function is used to allow a User to remove
    // another conversation as an interest, and saves this
    // conversation's id
    conversationInterests.remove(conversationId);
  }

  public void setUpdateTime(long time)
  {
    // This updates the time, updateTime
    // This is used to indicate the last time the user
    // called for a status update on interested fields
    this.lastUpdateTime = time;
  }

}
