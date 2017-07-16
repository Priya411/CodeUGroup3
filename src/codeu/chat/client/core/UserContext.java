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
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import codeu.chat.common.BasicController;
import codeu.chat.common.BasicView;
import codeu.chat.common.ConversationHeader;
import codeu.chat.common.ConversationPayload;
import codeu.chat.common.Message;
import codeu.chat.common.User;
import codeu.chat.common.UserType;
import codeu.chat.common.UserInterest;
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
		Uuid idToAdd = controller.newUserInterest(user.id, name);
		// is null when name is invalid
		// ids are equal when name being added is the user
		if (idToAdd == null || idToAdd.id() == user.id.id()) {
			return null;
		}
		// adds to the current user object stored on client
		user.addUserInterest(idToAdd);
		
		return user;
	}

	public User addConvoInterest(String title) {
		ConvoInterest intersestToAdd = controller.newConvoInterest(user.id, title);
		// is null when title is invalid
		if (intersestToAdd == null) {
			return null;
		}
		// adds to the current user object stored on client
		user.addConvoInterest(intersestToAdd);
		
		return user;
	}

	public User removeUserInterest(String name) {
		Uuid idToRemove = controller.removeUserInterest(user.id, name);
		// is null when name is invalid
		if (idToRemove == null) {
			return null;
		}
		// adds to the current user object stored on client
		user.remUserInterest(idToRemove);
		
		return user;
	}

	public User removeConvoInterest(String title) {
		Uuid idToRemove = controller.removeConvoInterest(user.id, title);
		// is null when title is invalid
		if (idToRemove == null) {
			return null;
		}
		// adds to the current user object stored on client
		user.remConvoInterest(idToRemove);
		
		return user;
	}

	public ConversationContext start(String name) {
		final ConversationHeader conversation = controller.newConversation(
				name, user.id);
		// sets creator's id to creator in hashmap
		conversation.setAccessOf(user.id, UserType.CREATOR);
		return conversation == null ? null : new ConversationContext(user,
				conversation, view, controller);
	}

	public Iterable<ConversationContext> conversations() {

		// Use all the ids to get all the conversations and convert them to
		// Conversation Contexts.
		final Collection<ConversationContext> all = new ArrayList<>();
		for (final ConversationHeader conversation : view.getConversations()) {
			all.add(new ConversationContext(user, conversation, view,
					controller));
		}

		return all;
	}

	public HashMap<Uuid, ArrayList<ArrayList<String>>> userStatusUpdate() {
		return this.user.userStatusUpdate(this.conversations(),
				controller.statusUpdate());
	}

	public HashMap<String, Integer> convoStatusUpdate() {
		return this.user.convoStatusUpdate(this.conversations());
	}
}
