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

package codeu.chat.client.commandline;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import codeu.chat.client.core.Context;
import codeu.chat.client.core.ConversationContext;
import codeu.chat.client.core.MessageContext;
import codeu.chat.client.core.UserContext;
import codeu.chat.common.ServerInfo;
import codeu.chat.common.UserInterest;
import codeu.chat.common.UserType;
import codeu.chat.util.CommandTokenizer;
import codeu.chat.util.Logger;
import codeu.chat.util.Time;
import codeu.chat.util.Uuid;

public final class Chat {

	// PANELS
	//
	// We are going to use a stack of panels to track where in the application
	// we are. The command will always be routed to the panel at the top of the
	// stack. When a command wants to go to another panel, it will add a new
	// panel to the top of the stack. When a command wants to go to the previous
	// panel all it needs to do is pop the top panel.
	private final Stack<Panel> panels = new Stack<>();

	private static final Logger.Log LOG = Logger.newLog(Chat.class);

	public Chat(Context context) {
		this.panels.push(createRootPanel(context));
	}

	// RECREATE
	//
	// Recreates the server's last known state to display for the client
	// upon relaunching the client
	// Will not display anything if there was no information saved
	//
	//
	public void recreate(Context context) {

		// HashMap keeps track of users and UUIDs for easy access
		HashMap<Uuid, String> allUsers = new HashMap<Uuid, String>();
		// HashMap keeps track of all the conversations that exist
		HashMap<Uuid, ConversationContext> allConvos = new HashMap<Uuid, ConversationContext>();

		// this function only does something if there was at least one user saved from
		// the
		// last session
		if (context.allUsers().iterator().hasNext()) {

			try {
				Logger.enableFileOutput("chat_history_log.log");
			} catch (IOException ex) {
				LOG.error(ex, "Failed to set logger to write to file");
			}
			LOG.info("============================= START OF SERVER HISTORY =============================");

			Iterator<UserContext> users = context.allUsers().iterator();
			UserContext user;
			System.out.format("USERS: \n");

			while (users.hasNext()) {
				user = users.next();
				System.out.format("\t\"%s\" Added at %s || UUID: %s\n", user.user.name, user.user.creation.HMtime(),
						user.user.id);
				allUsers.put(user.user.id, user.user.name);
				LOG.info("\nUSER: \"%s\" Added at %s || UUID: %s\n", user.user.name, user.user.creation.HMtime(),
						user.user.id);
				allUsers.put(user.user.id, user.user.name);
				for (final ConversationContext conversation : user.conversations()) {
					allConvos.put(conversation.conversation.id, conversation);
				}
			}

			for (Uuid convoUUID : allConvos.keySet()) {
				ConversationContext conversation = allConvos.get(convoUUID);
				System.out.format("\nCONVERSATION: \"%s\" Created by %s at %s || UUID: %s \n",
						conversation.conversation.title, allUsers.get(conversation.conversation.owner),
						conversation.conversation.creation.HMtime(), conversation.conversation.id);
				LOG.info("\nCONVERSATION: \"%s\" Created by %s at %s || UUID: %s \n", conversation.conversation.title,
						allUsers.get(conversation.conversation.owner), conversation.conversation.creation.HMtime(),
						conversation.conversation.id);

				for (MessageContext message = conversation.firstMessage(); message != null; message = message.next()) {
					System.out.format("\tMESSAGE: %s | %s: \"%s\" || UUID: %s \n", message.message.creation.HMtime(),
							allUsers.get(message.message.author), message.message.content, message.message.id);
					LOG.info("\nMESSAGE: %s | %s: \"%s\" || UUID: %s \n", message.message.creation.HMtime(),
							allUsers.get(message.message.author), message.message.content, message.message.id);
				}
			}

			LOG.info("============================= END OF SERVER HISTORY =============================");

		}
	}

	// HANDLE COMMAND
	//
	// Take a single line of input and parse a command from it. If the system
	// is willing to take another command, the function will return true. If
	// the system wants to exit, the function will return false.
	//
	public boolean handleCommand(String line) {

		CommandTokenizer tokenizer = new CommandTokenizer(line);
		String command = tokenizer.getCommand();

		// Because "exit" and "back" are applicable to every panel, handle
		// those commands here to avoid having to implement them for each
		// panel.

		if ("exit".equals(command)) {
			// The user does not want to process any more commands
			return false;
		}

		// Do not allow the root panel to be removed.
		if ("back".equals(command) && panels.size() > 1) {
			panels.pop();
			return true;
		}

		if (panels.peek().handleCommand(command, tokenizer.getRemainingArgs())) {
			// the command was handled
			return true;
		}

		// If we get to here it means that the command was not correctly handled
		// so we should let the user know. Still return true as we want to
		// continue
		// processing future commands.
		System.out.println("ERROR: Unsupported command");
		return true;
	}

	// CREATE ROOT PANEL
	//
	// Create a panel for the root of the application. Root in this context
	// means
	// the first panel and the only panel that should always be at the bottom of
	// the panels stack.
	//
	// The root panel is for commands that require no specific contextual
	// information.
	// This is before a user has signed in. Most commands handled by the root
	// panel
	// will be user selection focused.
	//
	private Panel createRootPanel(final Context context) {

		final Panel panel = new Panel();

		// HELP
		//
		// Add a command to print a list of all commands and their description
		// when
		// the user for "help" while on the root panel.
		//
		panel.register("help", new Panel.Command() {
			@Override
			public void invoke(List<String> args) {
				System.out.println("ROOT MODE");
				System.out.println("  info");
				System.out.println("    Provides server information including version number and up time.");
				System.out.println("  u-list");
				System.out.println("    List all users.");
				System.out.println("  u-add <name>");
				System.out.println("    Add a new user with the given name.");
				System.out.println("  u-sign-in <name>");
				System.out.println("    Sign in as the user with the given name.");
				System.out.println("  exit");
				System.out.println("    Exit the program.");
			}
		});

		// INFO
		//
		// Gives user information on the server
		// Added only for UpTime which returns the amount of time the server has
		// been running for

		panel.register("info", new Panel.Command() {
			@Override
			public void invoke(List<String> args) {
				final ServerInfo info = context.getInfo();
				final Time currentTime = Time.now();
				if (info == null) {
					System.out.println("ERROR: Failed to retrieve server information");
				} else {
					System.out.print("Your version number is: ");
					System.out.println(info.toString());

					long upTime = currentTime.inMs() - info.getStartTime().inMs();
					long[] HMS = Time.convertHMS(upTime);
					long hours, mins, secs;
					hours = HMS[0];
					mins = HMS[1];
					secs = HMS[2];
					System.out.format("Up Time: %s hours %s minutes %s seconds", hours, mins, secs);

				}
			}
		});

		// U-LIST (user list)
		//
		// Add a command to print all users registered on the server when the
		// user
		// enters "u-list" while on the root panel.
		//
		panel.register("u-list", new Panel.Command() {
			@Override
			public void invoke(List<String> args) {
				for (final UserContext user : context.allUsers()) {
					System.out.format("USER %s (UUID:%s)\n", user.user.name, user.user.id);
				}
			}
		});

		// U-ADD (add user)
		//
		// Add a command to add and sign-in as a new user when the user enters
		// "u-add" while on the root panel.
		// uses the entire list args as the argument since the user name may
		// be longer than a single word
		//
		panel.register("u-add", new Panel.Command() {
			@Override
			public void invoke(List<String> args) {
				final String name = !args.isEmpty() ? String.join(" ", args).trim() : "";
				if (name.length() > 0) {
					if (context.create(name) == null) {
						System.out.println("ERROR: Failed to create new user. ");
						System.out.println("User with this name might already exist");
					}
				} else {
					System.out.println("ERROR: Missing <username>");
				}
			}
		});

		// U-SIGN-IN (sign in user)
		//
		// Add a command to sign-in as a user when the user enters "u-sign-in"
		// while on the root panel.
		// uses the entire list args as the argument since the user name may
		// be longer than a single word
		//
		panel.register("u-sign-in", new Panel.Command() {
			@Override
			public void invoke(List<String> args) {
				final String name = !args.isEmpty() ? String.join(" ", args).trim() : "";
				if (name.length() > 0) {
					final UserContext user = findUser(name);
					if (user == null) {
						System.out.format("ERROR: Failed to sign in as '%s'\n", name);
					} else {
						panels.push(createUserPanel(user));
					}
				} else {
					System.out.println("ERROR: Missing <username>");
				}
			}

			// Find the first user with the given name and return a user context
			// for that user. If no user is found, the function will return
			// null.
			private UserContext findUser(String name) {
				for (final UserContext user : context.allUsers()) {
					if (user.user.name.equals(name)) {
						return user;
					}
				}
				return null;
			}
		});

		// Now that the panel has all its commands registered, return the panel
		// so that it can be used.
		return panel;
	}

	private Panel createUserPanel(final UserContext user) {

		final Panel panel = new Panel();

		// HELP
		//
		// Add a command that will print a list of all commands and their
		// descriptions when the user enters "help" while on the user panel.
		//
		panel.register("help", new Panel.Command() {
			@Override
			public void invoke(List<String> args) {
				System.out.println("USER MODE");
				System.out.println("  c-list");
				System.out.println("    List all conversations that the current user can interact with.");
				System.out.println("  c-add <title>");
				System.out.println("    Add a new conversation with the given title and join it as the current user.");
				System.out.println("  c-join <title>");
				System.out.println("    Join the conversation as the current user.");
				System.out.println("  c-status");
				System.out.println("  	Displays access status for all conversations");

				System.out.println("  status-update");
				System.out.println("  	Displays updates for user's conversation and user interests");

				System.out.println("  i-add-user <name>");
				System.out.println("    Add requested user to interests");
				System.out.println("  i-add-convo <title>");
				System.out.println("    Add requested convo to interests");
				System.out.println("  i-remove-user <name>");
				System.out.println("    Removes requested user from interests");
				System.out.println("  i-remove-convo <title>");
				System.out.println("    Removes requested convo from interests");
				System.out.println("  info");
				System.out.println("    Display all info for the current user");
				System.out.println("  back");
				System.out.println("    Go back to ROOT MODE.");
				System.out.println("  exit");
				System.out.println("    Exit the program.");
			}
		});

		// C-LIST (list conversations)
		//
		// Add a command that will print all conversations when the user enters
		// "c-list" while on the user panel.
		//
		panel.register("c-list", new Panel.Command() {
			@Override
			public void invoke(List<String> args) {
				for (final ConversationContext conversation : user.conversations()) {
					System.out.format("CONVERSATION %s (UUID:%s)\n", conversation.conversation.title,
							conversation.conversation.id);
				}
			}
		});

		// C-ADD (add conversation)
		//
		// Add a command that will create and join a new conversation when the
		// user
		// enters "c-add" while on the user panel.
		// uses the entire list args as the argument since the conversation name
		// may be longer than a single word
		//
		panel.register("c-add", new Panel.Command() {
			@Override
			public void invoke(List<String> args) {
				final String name = !args.isEmpty() ? String.join(" ", args).trim() : "";
				if (name.length() > 0) {
					final ConversationContext conversation = user.start(name);
					if (conversation == null) {
						System.out.println("ERROR: Failed to create new conversation");
					} else {
						panels.push(createConversationPanel(conversation));
					}
				} else {
					System.out.println("ERROR: Missing <title>");
				}
			}
		});

		// C-JOIN (join conversation)
		//
		// Add a command that will joing a conversation when the user enters
		// "c-join" while on the user panel.
		// uses the entire list args as the argument since the conversation name
		// may be longer than a single word
		//
		panel.register("c-join", new Panel.Command() {
			@Override
			public void invoke(List<String> args) {
				final String name = !args.isEmpty() ? String.join(" ", args).trim() : "";
				if (name.length() > 0) {
					final ConversationContext conversation = find(name);
					if (conversation == null) {
						System.out.format("ERROR: No conversation with name '%s'\n", name);
					} else {
						panels.push(createConversationPanel(conversation));
					}
				} else {
					System.out.println("ERROR: Missing <title>");
				}
			}

			// Find the first conversation with the given name and return its
			// context.
			// If no conversation has the given name, this will return null.
			private ConversationContext find(String title) {
				for (final ConversationContext conversation : user.conversations()) {
					if (title.equals(conversation.conversation.title)) {
						return conversation;
					}
				}
				return null;
			}
		});

		// Interest commands
		//
		// Add
		panel.register("i-add-user", new Panel.Command() {
			public void invoke(List<String> args) {
				// append the name by space from args and send to usercontext
				String name = !args.isEmpty() ? String.join(" ", args).trim() : "";
				if (!name.isEmpty()) {
					if (user.addUserInterest(name) == null) {
						System.out.println("Oh no! I can't find that user! Try again");
					} else {
						System.out.println("Thanks for expressing your interest!");
					}
				} else {
					System.out.println("Not a valid name");
				}
			}
		});

		panel.register("i-add-convo", new Panel.Command() {
			public void invoke(List<String> args) {

				String name = !args.isEmpty() ? String.join(" ", args).trim() : "";
				if (!name.isEmpty()) {
					if (user.addConvoInterest(name) == null) {
						System.out.println("Oh no! I can't find that convo! Try again");
					} else {
						System.out.println("Thanks for expressing your interest!");
					}
				} else {
					System.out.println("Not a valid name");
				}
			}
		});
		// Remove
		panel.register("i-remove-user", new Panel.Command() {
			public void invoke(List<String> args) {
				String name = !args.isEmpty() ? String.join(" ", args).trim() : "";
				if (!name.isEmpty()) {
					if (user.removeUserInterest(name) == null) {
						System.out.println("Oh no! I can't find that user! Try again!");
					} else {
						System.out.println("Interest == gone!");
					}
				} else {
					System.out.println("Not a valid name");
				}
			}
		});

		panel.register("i-remove-convo", new Panel.Command() {
			public void invoke(List<String> args) {
				String name = !args.isEmpty() ? String.join(" ", args).trim() : "";
				if (!name.isEmpty()) {
					if (user.removeConvoInterest(name) == null) {
						System.out.println("Oh no! I can't find that convo! Try again");
					} else {
						System.out.println("Interest == gone!");
					}
				} else {
					System.out.println("Not a valid name");
				}
			}
		});

		// INFO
		//
		// Add a command that will print info about the current context when the
		// user enters "info" while on the user panel.
		//
		panel.register("info", new Panel.Command() {
			@Override
			public void invoke(List<String> args) {
				System.out.println("User Info:");
				System.out.format("  Name : %s\n", user.user.name);
				System.out.format("  Id   : UUID:%s\n", user.user.id);
			}
		});

		// STATUS UPDATE
		//
		// Allows user to see status update
		// A status update includes :
		// 1) The number of messages that have been send in each of the user's
		// conversation interests since the last updates
		// 2) A list of conversations created and conversations added to by each
		// of the user's user interests
		//
		panel.register("status-update", new Panel.Command() {
			@Override
			public void invoke(List<String> args) {
				HashMap<String, Integer> convoUpdates = user.convoStatusUpdate();
				HashMap<Uuid, UserInterest> userUpdates = user.userStatusUpdate();

				// All of the print statements to view update
				System.out.println("Conversations:");
				for (String convoName : convoUpdates.keySet()) {
					System.out.format("\t%s : %d \n", convoName, convoUpdates.get(convoName));
				}
				System.out.println("Users:");
				for (Uuid userID : userUpdates.keySet()) {
					System.out.format("\t%s\n", userUpdates.get(userID).getname());
					System.out.println("\tConversations created: ");
					String convosCreated = String.join("\n\t", userUpdates.get(userID).getConvosCreated());
					System.out.format("\t%s", convosCreated);
					System.out.println("");
					System.out.println("\tConversations contributed to: ");
					String convosContributed = String.join("\n\t", userUpdates.get(userID).getConvosAddedTo());
					System.out.format("\t%s\n", convosContributed);
					System.out.println(" ");
				}
			}
		});

		// C-STATUS
		// allows user to see their access status in
		// every conversation they are currently a part of
		panel.register("c-status", new Panel.Command() {
			@Override
			public void invoke(List<String> args) {
				// HashMap stores the name of every convo the user is a part of and the access
				// level
				// for that conversation
				HashMap<String, UserType> convoAccess = new HashMap<String, UserType>();
				// adds all convos to convo access with name of convo as key
				// and user's access status as value
				for (ConversationContext convo : user.conversations()) {
					convoAccess.put(convo.conversation.title, convo.conversation.getAccessOf(user.user.id));
				}
				// All of the print statements to view conversation access statuses
				System.out.println("Conversations:");
				for (String convoName : convoAccess.keySet()) {
					System.out.format("\t%s: %s\n", convoName, convoAccess.get(convoName).toString());
				}
			}
		});

		// Now that the panel has all its commands registered, return the panel
		// so that it can be used.
		return panel;
	}

	private Panel createConversationPanel(final ConversationContext conversation) {

		final Panel panel = new Panel();

		// HELP
		//
		// Add a command that will print all the commands and their descriptions
		// when the user enters "help" while on the conversation panel.
		//
		panel.register("help", new Panel.Command() {
			@Override
			public void invoke(List<String> args) {
				System.out.println("USER MODE");
				System.out.println("  m-list");
				System.out.println("    List all messages in the current conversation.");
				System.out.println("  m-add <message>");
				System.out
						.println("    Add a new message to the current conversation as the current user.");
				System.out.println("  bot-add <Name>");
				System.out.println("    Adds bot with a given name to the conversation"); 
				System.out.println("  bot-remove <Name>");
				System.out.println("    Removes bot with a given name from the conversation"); 
				System.out.println("  list-bots");
				System.out.println("    Lists all of the available bots that can be added to the conversation"); 
				System.out.println("  info");
				System.out.println("    Display all info about the current conversation.");
				System.out.println("  m-assign-access <Username> <Role>");
				System.out.println("    assigns access to a given username");
				System.out.println("  back");
				System.out.println("    Go back to USER MODE.");
				System.out.println("  exit");
				System.out.println("    Exit the program.");
			}
		});

		// M-LIST (list messages)
		//
		// Add a command to print all messages in the current conversation when
		// the
		// user enters "m-list" while on the conversation panel.
		//
		panel.register("m-list", new Panel.Command() {
			@Override
			public void invoke(List<String> args) {
				System.out.println("--- start of conversation ---");
				for (MessageContext message = conversation.firstMessage(); message != null; message = message.next()) {
					System.out.println();
					System.out.format("USER : %s\n", message.message.author);
					System.out.format("SENT : %s\n", message.message.creation);
					System.out.println();
					System.out.println(message.message.content);
					System.out.println();
				}
				System.out.println("---  end of conversation  ---");
			}
		});

		// M-ADD (add message)
		//
		// Add a command to add a new message to the current conversation when
		// the
		// user enters "m-add" while on the conversation panel.
		// uses the entire list args as the argument since messages are likely to
		// be longer than a single string
		//
		panel.register("m-add", new Panel.Command() {
			@Override
			public void invoke(List<String> args) {
				final String message = !args.isEmpty() ? String.join(" ", args).trim() : "";
				if (message.length() > 0) {
					conversation.add(message);
				} else {
					System.out.println("ERROR: Messages must contain text");
				}
			}
		});
		
		// LIST-BOTS 
		//
		// Allows user to see all the available bots that can be added to the conversation 
		//
		panel.register("list-bots", new Panel.Command() {
			@Override
			public void invoke(List<String> args) {
				System.out.println("List of bots:");
				Iterator<String> botList = conversation.listBots().iterator();
				while (botList.hasNext()) {
					System.out.format("   %s\n", botList.next()); 
				}
			}
		});

		// INFO
		//
		// Add a command to print info about the current conversation when the
		// user
		// enters "info" while on the conversation panel.
		//
		panel.register("info", new Panel.Command() {
			@Override
			public void invoke(List<String> args) {
				System.out.println("Conversation Info:");
				System.out.format("  Title : %s\n", conversation.conversation.title);
				System.out.format("  Id    : UUID:%s\n", conversation.conversation.id);
				System.out.format("  Owner : %s\n", conversation.conversation.owner);
			}
		});

		panel.register("m-assign-access", new Panel.Command() {
			@Override
			public void invoke(List<String> args) {
				if (args.size() != 2) {
					System.out.println("Invalid Args!");
					System.out.println("m-assign-access <username> <role>");
					return;
				}
				String username = args.get(0);
				// default to MEMBER, will be set in try
				UserType type = UserType.MEMBER;
				// catch if string is not valid userType
				try {
					type = UserType.valueOf(args.get(1).toUpperCase());
				} catch (Exception e) {
					System.out.println("Not a valid UserType");
					System.out.println("m-assign-access <username> <role>");
					System.out.println("Here are your options: User, Owner, Creator");
					return;
				}
				if (conversation.setAccessOf(username, type)) {
					System.out.println("Access set!");
				} else {
					System.out.println("Could not find " + username);
				}

			}
		});

		panel.register("bot-add", new Panel.Command() {
			@Override
			public void invoke(List<String> args) {
				String botName = args.get(0);
				if (conversation.addBot(botName)) {
					System.out.println("Beep Bop! Bot succssfully added!");
				} else {
					System.out.println("Not A valid bot name! Please check use list-bots to see all bots!");
				}

			}
		});
		
		panel.register("bot-remove", new Panel.Command() {
			@Override
			public void invoke(List<String> args) {
				String botName = args.get(0);
				if (conversation.removeBot(botName)) {
					System.out.println("Beep Bop! Bot succssfully removed!");
				} else {
					System.out.println("Not A valid bot name! Please check use list-bots to see all bots!");
				}

			}
		});
		return panel;
	}
}
