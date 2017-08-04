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

package codeu.chat.server;

import java.util.LinkedList;

import codeu.chat.common.BasicController;
import codeu.chat.common.ConversationHeader;
import codeu.chat.common.ConversationPayload;
import codeu.chat.common.ConvoInterest;
import codeu.chat.common.Message;
import codeu.chat.common.RandomUuidGenerator;
import codeu.chat.common.RawController;
import codeu.chat.common.User;
import codeu.chat.common.Bot;
import codeu.chat.common.UserType;
import codeu.chat.util.Logger;
import codeu.chat.util.Time;
import codeu.chat.util.Uuid;
import codeu.chat.common.UserType;

public final class Controller implements RawController, BasicController {

	private final static Logger.Log LOG = Logger.newLog(Controller.class);

	private final Model model;
	private final Uuid.Generator uuidGenerator;

	public Controller(Uuid serverId, Model model) {
		this.model = model;
		this.uuidGenerator = new RandomUuidGenerator(serverId, System.currentTimeMillis());
	}

	@Override
	public Message newMessage(Uuid author, Uuid conversation, String body) {
		return newMessage(createId(), author, conversation, body, Time.now());
	}

	@Override
	public User newUser(String name) {
		return newUser(createId(), name, Time.now());
	}

	@Override
	public ConversationHeader newConversation(String title, Uuid owner) {
		return newConversation(createId(), title, owner, Time.now());
	}

	@Override
	public Message newMessage(Uuid id, Uuid author, Uuid conversation, String body, Time creationTime) {
		final User foundUser = model.userById().first(author);
		final ConversationPayload foundConversation = model.conversationPayloadById().first(conversation);

		Message message = null;

		if (foundConversation != null && isIdFree(id)) {

			message = new Message(id, Uuid.NULL, Uuid.NULL, creationTime, author, body);
			model.add(message);

			LOG.info("Message added: %s", message.id);

			// Find and update the previous "last" message so that it's "next"
			// value
			// will point to the new message.

			if (Uuid.equals(foundConversation.lastMessage, Uuid.NULL)) {

				// The conversation has no messages in it, that's why the last
				// message is NULL (the first
				// message should be NULL too. Since there is no last message,
				// then it is not possible
				// to update the last message's "next" value.
			} else {
				final Message lastMessage = model.messageById().first(foundConversation.lastMessage);
				lastMessage.next = message.id;
				new JSON().save(lastMessage);

			}

			// If the first message points to NULL it means that the
			// conversation was empty and that
			// the first message should be set to the new message. Otherwise the
			// message should
			// not change.

			foundConversation.firstMessage = Uuid.equals(foundConversation.firstMessage, Uuid.NULL) ? message.id
					: foundConversation.firstMessage;

			// Update the conversation to point to the new last message as it
			// has changed.

			foundConversation.lastMessage = message.id;

			new JSON().save(foundConversation);
			new JSON().save(message);

			for (Bot bot : model.conversationById().first(conversation).bots) {

				String potentialMessage = bot.reactTo(body, author);

				if (potentialMessage != null) {
					System.out.println("newmessage");
					newMessage(createId(), Uuid.NULL, conversation, potentialMessage, creationTime);
				}
			}
		}

		return message;
	}

	@Override
	public User newUser(Uuid id, String name, Time creationTime) {

		User user = null;

		if (isIdFree(id)) {

			user = new User(id, name, creationTime);
			model.add(user);
			new JSON().save(user);

			LOG.info("newUser success (user.id=%s user.name=%s user.time=%s)", id, name, creationTime);

		} else {

			LOG.info("newUser fail - id in use (user.id=%s user.name=%s user.time=%s)", id, name, creationTime);
		}

		return user;
	}

	@Override
	public ConversationHeader newConversation(Uuid id, String title, Uuid owner, Time creationTime) {

		final User foundOwner = model.userById().first(owner);

		ConversationHeader conversation = null;

		if (foundOwner != null && isIdFree(id)) {
			conversation = new ConversationHeader(id, owner, creationTime, title);
			model.add(conversation);
			new JSON().save(conversation);
			LOG.info("Conversation added: " + id);
		}

		return conversation;
	}

	public ConvoInterest newConvoInterest(Uuid user, String title) {
		final User foundUser = model.userById().first(user);

		// count the number of messages
		// create header and payload objects from string title
		final ConversationHeader convoHeader = model.conversationByText().first(title);
		// return null if not ofund
		if (convoHeader == null) {
			return null;
		}
		final ConversationPayload convoPayload = model.conversationPayloadById().first(convoHeader.id);

		Message currentMessage = model.messageById().first(convoPayload.firstMessage);
		int count = 0;
		while (currentMessage != null) {
			currentMessage = model.messageById().first(currentMessage.next);
			count++;
		}
		ConvoInterest interest = new ConvoInterest(convoPayload.id, count);
		foundUser.addConvoInterest(interest);
		new JSON().save(foundUser);
		return interest;
	}

	public Uuid newUserInterest(Uuid user, String name) {
		final User foundUser = model.userById().first(user);
		final User userToSave = model.userByText().first(name);
		System.out.println(userToSave);
		if (userToSave == null) {
			return null;
		}
		foundUser.addUserInterest(userToSave.id);
		new JSON().save(foundUser);
		return userToSave.id;
	}

	public Uuid removeConvoInterest(Uuid user, String title) {
		final User foundUser = model.userById().first(user);
		// create header and payload objects from string title
		final ConversationHeader convoHeader = model.conversationByText().first(title);
		// return null if not found
		if (convoHeader == null) {
			return null;
		}
		final ConversationPayload convoPayload = model.conversationPayloadById().first(convoHeader.id);

		foundUser.remConvoInterest(convoPayload.id);
		new JSON().save(foundUser);
		return convoPayload.id;
	}

	public Uuid removeUserInterest(Uuid user, String name) {
		final User foundUser = model.userById().first(user);
		final User userToSave = model.userByText().first(name);
		if (userToSave == null) {
			return null;
		}
		foundUser.remUserInterest(userToSave.id);
		new JSON().save(foundUser);
		return userToSave.id;
	}

	private Uuid createId() {

		Uuid candidate;

		for (candidate = uuidGenerator.make(); isIdInUse(candidate); candidate = uuidGenerator.make()) {

			// Assuming that "randomUuid" is actually well implemented, this
			// loop should never be needed, but just incase make sure that the
			// Uuid is not actually in use before returning it.

		}

		return candidate;
	}

	private boolean isIdInUse(Uuid id) {
		return model.messageById().first(id) != null || model.conversationById().first(id) != null
				|| model.userById().first(id) != null;
	}

	private boolean isIdFree(Uuid id) {
		return !isIdInUse(id);
	}

	@Override
	public Time statusUpdate() {
		return null;
	}

	// needs to check if user with userId has permission to change the Type of
	// user with username in given convo
	// if so, update model, if not, return null
	@Override
	public Uuid changeAccessControl(Uuid userID, Uuid convo, String username, UserType type) {
		System.out.println(model.conversationById().first(convo).getAccessOf(userID));
		// This function will update the UserType of the User with the User Name
		// username
		// If they have the right to. If not, it will not update and simply
		// return null
		if (model.conversationById().first(convo).getAccessOf(userID) == UserType.CREATOR) {
			System.out.println("CREATOR");
			User toUpdate = model.userByText().first(username);
			if (toUpdate == null) {
				System.out.println("could not find " + username);
				return null;
			}
			if (type == UserType.OWNER || type == UserType.MEMBER) {
				ConversationHeader header = model.conversationById().first(convo);
				header.setAccessOf(model.userByText().first(username).id, type);
				new JSON().save(header);
				return model.userByText().first(username).id;
			} else {
				return null;
			}
		}

		if (model.conversationById().first(convo).getAccessOf(userID) == UserType.OWNER) {
			User toUpdate = model.userByText().first(username);
			if (toUpdate == null) {
				return null;
			}
			if (type == UserType.MEMBER) {
				ConversationHeader header = model.conversationById().first(convo);
				header.setAccessOf(model.userByText().first(username).id, type);
				new JSON().save(header);
			} else {
				return null;
			}

		}

		return null;
	}

	@Override
	public boolean addBot(Uuid convoId, String botName) {
		// This function will allow the user to add in a bot to a conversation
		// The conversation is specified by the UUID and the bot is specified
		// by its name, which is just a string version of the class name.
		// Any user can add a bot, but multiple bots can't be added into
		// a conversation. It will return true if the bot was added.
		ConversationHeader conv = model.conversationById().first(convoId);
		if (conv != null) {
			if (model.bots.contains(botName)) {
				for (Bot bot : conv.bots) {
					if (bot.getName().equals(botName)) {
						System.out.println("Bot already added");
						return false;
					}
				}				
				try {
					Bot bot = (Bot) Class.forName("codeu.chat.server.bots." + botName).newInstance();

					String onAddMessage = bot.onAdd();
					if (onAddMessage != null) {
						this.newMessage(Uuid.NULL, convoId, onAddMessage);
					}
					conv.bots.add(bot);
					new JSON().save(conv);
					return true;

				} catch (Exception e) {
					e.printStackTrace();
					return false;
				}

			}
			return false;
		}
		return false;
	}

	@Override
	public boolean removeBot(Uuid convoId, String botName) {
		// This function will allow the user to remove a bot from a conversation
		// The conversation is specified by the UUID and the bot is specified
		// by its name, which is just a string version of the class name.
		// Any user can remove a bot, and it will return true if the bot was removed.
		ConversationHeader conv = model.conversationById().first(convoId);
		if (conv != null) {
			for (Bot bot : conv.bots) {
				if (bot.getName().equals(botName)) {
					conv.bots.remove(bot);
					return true;
				}
			}
			return false;
		}
		return false;
	}

	public LinkedList<String> getAllBots() {
		return this.model.bots;
	}
}
