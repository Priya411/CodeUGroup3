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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import codeu.chat.common.ConversationHeader;
import codeu.chat.common.ConversationPayload;
import codeu.chat.common.ConvoInterest;
import codeu.chat.common.LinearUuidGenerator;
import codeu.chat.common.Message;
import codeu.chat.common.NetworkCode;
import codeu.chat.common.Relay;
import codeu.chat.common.Secret;
import codeu.chat.common.User;
import codeu.chat.common.UserType;
import codeu.chat.util.Logger;
import codeu.chat.util.Serializers;
import codeu.chat.util.Time;
import codeu.chat.util.Timeline;
import codeu.chat.util.Uuid;
import codeu.chat.util.connections.Connection;
import codeu.chat.common.ServerInfo;

public final class Server {
	

  private interface Command {
    void onMessage(InputStream in, OutputStream out) throws IOException;
  }
  
  private static final Logger.Log LOG = Logger.newLog(Server.class);

  private static final int RELAY_REFRESH_MS = 5000;  // 5 seconds

  private final Timeline timeline = new Timeline();

  private final Map<Integer, Command> commands = new HashMap<>();
  private final Uuid id;
  private final Secret secret;
  private ServerInfo info = null;
  // Changed model initialization
  // If there were previous transactions, the createModelForServer
  // function will reload the past transactions into this model
  // If there haven't, and the server is being used for the first time
  // then it will simply create a new variable of type Model
  // without any "log" or data pre-loaded into it
  private final Model model = new JSON().createModelForServer();
  private final View view = new View(model);
  private final Controller controller;
  private final Relay relay;
  private Uuid lastSeen = Uuid.NULL;
  // This string should be changed whenever the current version value for the server
  // needs to be changed. 1.0.0 is simply the default initial value, however this
  // can also be updated and customized.
  private String version = "1.0.0";

  public Server(final Uuid id, final Secret secret, final Relay relay) {

    this.id = id;
    this.secret = secret;
    this.controller = new Controller(id, model);
    this.relay = relay;

    // This try catch block initializes the server version based on the version
    // set in object 'version'
    // automatically sets up startTime to the time at which the server is launched 

    try {
      info = new ServerInfo(Uuid.parse(version));
    } catch (IOException e) {
      System.out.println("Invalid input");
      System.out.println("The version must be able to be represented as an unsigned 32 bit long");
    }


    // New Message - A client wants to add a new message to the back end.
    this.commands.put(NetworkCode.NEW_MESSAGE_REQUEST, new Command() {
      @Override
      public void onMessage(InputStream in, OutputStream out) throws IOException {

        final Uuid author = Uuid.SERIALIZER.read(in);
        final Uuid conversation = Uuid.SERIALIZER.read(in);
        final String content = Serializers.STRING.read(in);

        final Message message = controller.newMessage(author, conversation, content);

        Serializers.INTEGER.write(out, NetworkCode.NEW_MESSAGE_RESPONSE);
        Serializers.nullable(Message.SERIALIZER).write(out, message);

        timeline.scheduleNow(createSendToRelayEvent(
            author,
            conversation,
            message.id));
      }
    });

    // New User - A client wants to add a new user to the back end.
    this.commands.put(NetworkCode.NEW_USER_REQUEST,  new Command() {
      @Override
      public void onMessage(InputStream in, OutputStream out) throws IOException {

        final String name = Serializers.STRING.read(in);
        final User user = controller.newUser(name);

        Serializers.INTEGER.write(out, NetworkCode.NEW_USER_RESPONSE);
        Serializers.nullable(User.SERIALIZER).write(out, user);
      }
    });

    // New Conversation - A client wants to add a new conversation to the back end.
    this.commands.put(NetworkCode.NEW_CONVERSATION_REQUEST,  new Command() {
      @Override
      public void onMessage(InputStream in, OutputStream out) throws IOException {

        final String title = Serializers.STRING.read(in);
        final Uuid owner = Uuid.SERIALIZER.read(in);
        final ConversationHeader conversation = controller.newConversation(title, owner);

        Serializers.INTEGER.write(out, NetworkCode.NEW_CONVERSATION_RESPONSE);
        Serializers.nullable(ConversationHeader.SERIALIZER).write(out, conversation);
      }
    });

    // Get Users - A client wants to get all the users from the back end.
    this.commands.put(NetworkCode.GET_USERS_REQUEST, new Command() {
      @Override
      public void onMessage(InputStream in, OutputStream out) throws IOException {

        final Collection<User> users = view.getUsers();

        Serializers.INTEGER.write(out, NetworkCode.GET_USERS_RESPONSE);
        Serializers.collection(User.SERIALIZER).write(out, users);
      }
    });

    // Get Conversations - A client wants to get all the conversations from the back end.
    this.commands.put(NetworkCode.GET_ALL_CONVERSATIONS_REQUEST, new Command() {
      @Override
      public void onMessage(InputStream in, OutputStream out) throws IOException {

        final Collection<ConversationHeader> conversations = view.getConversations();

        Serializers.INTEGER.write(out, NetworkCode.GET_ALL_CONVERSATIONS_RESPONSE);
        Serializers.collection(ConversationHeader.SERIALIZER).write(out, conversations);
      }
    });
    

    // Get Conversations By Id - A client wants to get a subset of the converations from
    //                           the back end. Normally this will be done after calling
    //                           Get Conversations to get all the headers and now the client
    //                           wants to get a subset of the payloads.
    this.commands.put(NetworkCode.GET_CONVERSATIONS_BY_ID_REQUEST, new Command() {
      @Override
      public void onMessage(InputStream in, OutputStream out) throws IOException {

        final Collection<Uuid> ids = Serializers.collection(Uuid.SERIALIZER).read(in);
        final Collection<ConversationPayload> conversations = view.getConversationPayloads(ids);

        Serializers.INTEGER.write(out, NetworkCode.GET_CONVERSATIONS_BY_ID_RESPONSE);
        Serializers.collection(ConversationPayload.SERIALIZER).write(out, conversations);
      }
    });

    // Get Messages By Id - A client wants to get a subset of the messages from the back end.
    this.commands.put(NetworkCode.GET_MESSAGES_BY_ID_REQUEST, new Command() {
      @Override
      public void onMessage(InputStream in, OutputStream out) throws IOException {

        final Collection<Uuid> ids = Serializers.collection(Uuid.SERIALIZER).read(in);
        final Collection<Message> messages = view.getMessages(ids);

        Serializers.INTEGER.write(out, NetworkCode.GET_MESSAGES_BY_ID_RESPONSE);
        Serializers.collection(Message.SERIALIZER).write(out, messages);
      }
    });



    // Get Server Info - **for Up Time function**
    //					The client wants to know the time the server was started
   
    this.commands.put(NetworkCode.SERVER_UPTIME_REQUEST, new Command() {
    	@Override
        public void onMessage(InputStream in, OutputStream out) throws IOException {		
    		Serializers.INTEGER.write(out, NetworkCode.SERVER_UPTIME_RESPONSE);
    		Time.SERIALIZER.write(out, info.getStartTime());
        }
      });


    // Allows user to request Server Info and handles this request, added by Priyanka Agarwal
    // Modeled after given code of:
    /* if (type == NetworkCode.SERVER_INFO_REQUEST) {
    Serializers.INTEGER.write(out, NetworkCode.SERVER_INFO_RESPONSE);
    Uuid.SERIALIZER.write(out, info.version);
    } else if …
    */
      this.commands.put(NetworkCode.SERVER_VERSION_REQUEST, new Command() {
      @Override
      public void onMessage(InputStream in, OutputStream out) throws IOException {
        Serializers.INTEGER.write(out, NetworkCode.SERVER_VERSION_RESPONSE);
        Uuid.SERIALIZER.write(out, info.getVersion());
      }
    });

      // Allows user to request that another user be added as an interest
    this.commands.put(NetworkCode.NEW_USER_INTEREST_REQUEST, new Command() {
      @Override
      public void onMessage(InputStream in, OutputStream out) throws IOException {

          final Uuid user = Uuid.SERIALIZER.read(in);
          final String name = Serializers.STRING.read(in);
          final Uuid idOfUserWithName = controller.newUserInterest(user, name);
          Serializers.INTEGER.write(out, NetworkCode.NEW_USER_INTEREST_RESPONSE);
          Serializers.nullable(Uuid.SERIALIZER).write(out, idOfUserWithName);
      }
    });

    // Allows user to request that a conversation be added as an interest
    this.commands.put(NetworkCode.NEW_CONVO_INTEREST_REQUEST, new Command() {
      @Override
      public void onMessage(InputStream in, OutputStream out) throws IOException {
          final Uuid user = Uuid.SERIALIZER.read(in);
          final String name = Serializers.STRING.read(in);
          final ConvoInterest convoInterest = controller.newConvoInterest(user, name);
          Serializers.INTEGER.write(out, NetworkCode.NEW_CONVO_INTEREST_RESPONSE);
          Serializers.nullable(ConvoInterest.SERIALIZER).write(out, convoInterest);
      }
    });

    // Allows user to request that another user be removed as an interest
    this.commands.put(NetworkCode.REM_USER_INTEREST_REQUEST, new Command() {
      @Override
      public void onMessage(InputStream in, OutputStream out) throws IOException {

          final Uuid user = Uuid.SERIALIZER.read(in);
          final String name = Serializers.STRING.read(in);
          final Uuid idOfUserWithName = controller.removeUserInterest(user, name);
          System.out.println(idOfUserWithName);
          Serializers.INTEGER.write(out, NetworkCode.REM_USER_INTEREST_RESPONSE);
          Serializers.nullable(Uuid.SERIALIZER).write(out, idOfUserWithName);
      }
    });

      // Allows user to request that a conversation be removed as an interest
    this.commands.put(NetworkCode.REM_CONVO_INTEREST_REQUEST, new Command() {
      @Override
      public void onMessage(InputStream in, OutputStream out) throws IOException {
          final Uuid user = Uuid.SERIALIZER.read(in);
          final String name = Serializers.STRING.read(in);
          final Uuid idOfConvoToRemove = controller.removeConvoInterest(user, name);
          Serializers.INTEGER.write(out, NetworkCode.REM_CONVO_INTEREST_RESPONSE);
          Serializers.nullable(Uuid.SERIALIZER).write(out, idOfConvoToRemove);
      }
    });
    
    // Allows user to request that a conversation be removed as an interest
    this.commands.put(NetworkCode.CHANGE_ACCESS_CONTROL_REQUEST, new Command() {
      @Override
      public void onMessage(InputStream in, OutputStream out) throws IOException {
          final Uuid userId = Uuid.SERIALIZER.read(in);
          final Uuid convoId = Uuid.SERIALIZER.read(in);
          final String name = Serializers.STRING.read(in);
          final String typeString = Serializers.STRING.read(in);
          
          // default to member, will be set in try
          UserType userType = UserType.MEMBER;
          try {
        	  userType = UserType.valueOf(typeString);
          } catch (Exception e) {
        	  // invalid user type, return null ( failed request )
        	  Serializers.INTEGER.write(out, NetworkCode.CHANGE_ACCESS_CONTROL_RESPONSE);
              Serializers.nullable(Uuid.SERIALIZER).write(out, null);
              return;
          }
          System.out.println("id: " + userId + " convoId: " + convoId + " name " + name + " user type " + userType);
          final Uuid idOfConvoToRemove = controller.changeAccessControl(userId, convoId, name, userType);
          Serializers.INTEGER.write(out, NetworkCode.CHANGE_ACCESS_CONTROL_RESPONSE);
          Serializers.nullable(Uuid.SERIALIZER).write(out, idOfConvoToRemove);
      }
    });
    
    // Allows user to add a bot to given conversation
    this.commands.put(NetworkCode.BOT_ADD_REQUEST, new Command() {
      @Override
      public void onMessage(InputStream in, OutputStream out) throws IOException {
          final Uuid convoId = Uuid.SERIALIZER.read(in);
          final String botName = Serializers.STRING.read(in);
          
          Serializers.INTEGER.write(out, NetworkCode.BOT_ADD_RESPONSE);
          Serializers.nullable(Serializers.BOOLEAN).write(out, controller.addBot(convoId, botName));
      }
    });
    
 // Allows user to remove a bot to given conversation
    this.commands.put(NetworkCode.BOT_REMOVE_REQUEST, new Command() {
      @Override
      public void onMessage(InputStream in, OutputStream out) throws IOException {
          final Uuid convoId = Uuid.SERIALIZER.read(in);
          final String botName = Serializers.STRING.read(in);
          
          Serializers.INTEGER.write(out, NetworkCode.BOT_REMOVE_RESPONSE);
          Serializers.nullable(Serializers.BOOLEAN).write(out, controller.removeBot(convoId, botName));
      }
    });
    
 // Allows user to remove a bot to given conversation
    this.commands.put(NetworkCode.GET_ALL_BOTS_REQUEST, new Command() {
      @Override
      public void onMessage(InputStream in, OutputStream out) throws IOException {
          
          Serializers.INTEGER.write(out, NetworkCode.GET_ALL_BOTS_RESPONSE);
          Serializers.collection(Serializers.STRING).write(out, controller.getAllBots());
      }
    });
    
    


    this.timeline.scheduleNow(new Runnable() {
      @Override
      public void run() {
        try {

          LOG.info("Reading update from relay...");

          for (final Relay.Bundle bundle : relay.read(id, secret, lastSeen, 32)) {
            onBundle(bundle);
            lastSeen = bundle.id();
          }

        } catch (Exception ex) {
          LOG.error(ex, "Failed to read update from relay.");
        }

        timeline.scheduleIn(RELAY_REFRESH_MS, this);
      }
    });
  }

  public void handleConnection(final Connection connection) {
    timeline.scheduleNow(new Runnable() {
      @Override
      public void run() {
        try {

          LOG.info("Handling connection...");

          final int type = Serializers.INTEGER.read(connection.in());
          final Command command = commands.get(type);

          if (command == null) {
            // The message type cannot be handled so return a dummy message.
            Serializers.INTEGER.write(connection.out(), NetworkCode.NO_MESSAGE);
            LOG.info("Connection rejected");
          } else {
            command.onMessage(connection.in(), connection.out());
            LOG.info("Connection accepted");
          }

        } catch (Exception ex) {

          LOG.error(ex, "Exception while handling connection.");

        }

        try {
          connection.close();
        } catch (Exception ex) {
          LOG.error(ex, "Exception while closing connection.");
        }
      }
    });
  }

  private void onBundle(Relay.Bundle bundle) {

    final Relay.Bundle.Component relayUser = bundle.user();
    final Relay.Bundle.Component relayConversation = bundle.conversation();
    final Relay.Bundle.Component relayMessage = bundle.user();

    User user = model.userById().first(relayUser.id());

    if (user == null) {
      user = controller.newUser(relayUser.id(), relayUser.text(), relayUser.time());
    }

    ConversationHeader conversation = model.conversationById().first(relayConversation.id());

    if (conversation == null) {

      // As the relay does not tell us who made the conversation - the first person who
      // has a message in the conversation will get ownership over this server's copy
      // of the conversation.
      conversation = controller.newConversation(relayConversation.id(),
                                                relayConversation.text(),
                                                user.id,
                                                relayConversation.time());
    }

    Message message = model.messageById().first(relayMessage.id());

    if (message == null) {
      message = controller.newMessage(relayMessage.id(),
                                      user.id,
                                      conversation.id,
                                      relayMessage.text(),
                                      relayMessage.time());
    }
  }

  private Runnable createSendToRelayEvent(final Uuid userId,
                                          final Uuid conversationId,
                                          final Uuid messageId) {
    return new Runnable() {
      @Override
      public void run() {
        final User user = view.findUser(userId);
        final ConversationHeader conversation = view.findConversation(conversationId);
        final Message message = view.findMessage(messageId);
        relay.write(id,
                    secret,
                    relay.pack(user.id, user.name, user.creation),
                    relay.pack(conversation.id, conversation.title, conversation.creation),
                    relay.pack(message.id, message.content, message.creation));
      }
    };
  }
}
