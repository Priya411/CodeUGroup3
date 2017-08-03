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

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import codeu.chat.common.BasicController;
import codeu.chat.common.BasicView;
import codeu.chat.common.ConversationHeader;
import codeu.chat.common.ConversationPayload;
import codeu.chat.common.Message;
import codeu.chat.common.User;
import codeu.chat.common.UserType;
import codeu.chat.util.Uuid;

public final class ConversationContext {

  public final User user;
  public final ConversationHeader conversation;

  private final BasicView view;
  private final BasicController controller;

  public ConversationContext(User user,
                             ConversationHeader conversation,
                             BasicView view,
                             BasicController controller) {

    this.user = user;
    this.conversation = conversation;
    this.view = view;
    this.controller = controller;
  }

  public MessageContext add(String messageBody) {

    final Message message = controller.newMessage(user.id,
                                                  conversation.id,
                                                  messageBody);

    return message == null ?
        null :
        new MessageContext(message, view);
  }

  public MessageContext firstMessage() {

    // As it is possible for the conversation to have been updated, so fetch
    // a new copy.
    final ConversationPayload updated = getUpdated();

    return updated == null ?
        null :
        getMessage(updated.firstMessage);
  }
  
  public int getMessageCount() {
	  MessageContext currentMessage = firstMessage();
	  if (currentMessage == null) {
		  return 0;
	  }
	  int count = 1; // start with first message
	  while(currentMessage != null && currentMessage.next() != null) {
		  currentMessage = currentMessage.next();
		  count++;
	  }
	  return count;
  }

  public MessageContext lastMessage() {

    // As it is possible for the conversation to have been updated, so fetch
    // a new copy.
    final ConversationPayload updated = getUpdated();

    return updated == null ?
        null :
        getMessage(updated.lastMessage);
  }
  
  // sets access control level of user with given username to UserType type
  // returns true if it was successful
  public boolean setAccessOf(String username, UserType type) {
	  Uuid idOfUser = controller.changeAccessControl(this.user.id, this.conversation.id, username, type);
	  // null if user name is invalid
	  if (idOfUser == null) {
		  return false;
	  }
	  this.conversation.setAccessOf(idOfUser, type);
	  return true;
  }

  private ConversationPayload getUpdated() {
    final Collection<Uuid> ids = Arrays.asList(conversation.id);
    final Iterator<ConversationPayload> payloads = view.getConversationPayloads(ids).iterator();
    return payloads.hasNext() ? payloads.next() : null;
  }

  private MessageContext getMessage(Uuid id) {
    final Iterator<Message> messages = view.getMessages(Arrays.asList(id)).iterator();
    return messages.hasNext() ? new MessageContext(messages.next(), view) : null;
  }

  /**
   * Adds a bot the conversationHeader associated with this context
   * @param botName name of the bot being added
   * @return
   */
  public boolean addBot(String botName) {	
	if (controller.addBot(this.conversation.id, botName)) {
	  return true;
	}
	return false;	
  }

  /**
   * removes bot from conversationheader associated with this context
   * @param botName name of bot being removed
   * @return
   */
  public boolean removeBot(String botName) {
	if (controller.removeBot(this.conversation.id, botName)) {
		return true;
	}
	return false;
  }
}