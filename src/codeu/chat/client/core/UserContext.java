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

package codeu.chat.client.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import codeu.chat.common.BasicController;
import codeu.chat.common.BasicView;
import codeu.chat.common.ConversationHeader;
import codeu.chat.common.ConversationPayload;
import codeu.chat.common.User;
import codeu.chat.util.Uuid;

public final class UserContext {

  public final User user;
  private final BasicView view;
  private final BasicController controller;

  public UserContext(User user, BasicView view, BasicController controller) {
    this.user = user;
    this.view = view;
    this.controller = controller;
  }
  
  public User addUserInterest(String name) {
	  Uuid idToSave = null;
	  // find the user with the same name as the one given 
	  for(User u: view.getUsers()) {
		  if (name.equals(u.name)){
			  idToSave = u.id;
		  }
	  }
	  if (idToSave == null)
		  return null;
	  // adds to the current user object stored on client
	  user.addUserInterest(idToSave);
	  // sends info to model to be saved also
	  controller.newUserInterest(user, idToSave);
	  return user;
  }
  
  public User addConvoInterest(String title) {
	  Uuid idToSave = null;
	  for(ConversationContext c: conversations()) {
		  if (title.equals(c.conversation.title)){
			  idToSave = c.conversation.id;
		  }
	  }
	  if (idToSave == null)
		  return null;
	  
	  List<Uuid> idArray = new LinkedList<Uuid>();
	  
	  idArray.add(idToSave);
	  Collection<ConversationPayload> payloads = view.getConversationPayloads(idArray);
	  if (!payloads.iterator().hasNext())
		  return null;
	  ConversationPayload convoPayload = payloads.iterator().next(); // still gotta figure out how to get message count
	  int numberOfMessageOfConvo = convoPayload.getNumberOfMessages();
	  user.addConvoInterest(idToSave, numberOfMessageOfConvo);
	  controller.newConvoInterest(user, idToSave, numberOfMessageOfConvo);
	  return user;
  }
  
  public Object removeUserInterest(String name) {
	  Uuid idToSave = null;
	  // find the user with the same name as the one given 
	  for(User u: view.getUsers()) {
		  if (name.equals(u.name)){
			  idToSave = u.id;
		  }
	  }
	  if (idToSave == null)
		  return null;
	  user.removeUserInterest(idToSave);
	  controller.removeUserInterest(user, idToSave);
	  return user;
  }
  
  public Object removeConvoInterest(String title) {
	  Uuid idToRemove = null;
	  for(ConversationContext c: conversations()) {
		  if (title.equals(c.conversation.title)){
			  idToRemove = c.conversation.id;
		  }
	  }
	  if (idToRemove == null)
		  return null;
	  user.removeConvoInterest(idToRemove);
	  controller.removeConvoInterest(user, idToRemove);
      return user;
  }

  public ConversationContext start(String name) {
    final ConversationHeader conversation = controller.newConversation(name, user.id);
    return conversation == null ?
        null :
        new ConversationContext(user, conversation, view, controller);
  }

  public Iterable<ConversationContext> conversations() {

    // Use all the ids to get all the conversations and convert them to
    // Conversation Contexts.
    final Collection<ConversationContext> all = new ArrayList<>();
    for (final ConversationHeader conversation : view.getConversations()) {
      all.add(new ConversationContext(user, conversation, view, controller));
    }

    return all;
  }
}
