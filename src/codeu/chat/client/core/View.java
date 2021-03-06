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
import codeu.chat.common.BasicView;
import codeu.chat.common.ConversationHeader;
import codeu.chat.common.ConversationPayload;
import codeu.chat.common.Message;
import codeu.chat.common.NetworkCode;
import codeu.chat.common.User;
import codeu.chat.util.Logger;
import codeu.chat.util.Serializers;
import codeu.chat.util.Time;
import codeu.chat.common.ServerInfo; 
import codeu.chat.util.Uuid;
import codeu.chat.util.connections.Connection;
import codeu.chat.util.connections.ConnectionSource;
import codeu.chat.common.ServerInfo;

// VIEW
//
// This is the view component of the Model-View-Controller pattern used by the
// the client to reterive readonly data from the server. All methods are blocking
// calls.
final class View implements BasicView {

	private final static Logger.Log LOG = Logger.newLog(View.class);

	private final ConnectionSource source;

	public View(ConnectionSource source) {
		this.source = source;
	}

	@Override
	public Collection<User> getUsers() {

		final Collection<User> users = new ArrayList<>();

		try (final Connection connection = source.connect()) {

			Serializers.INTEGER.write(connection.out(), NetworkCode.GET_USERS_REQUEST);

			if (Serializers.INTEGER.read(connection.in()) == NetworkCode.GET_USERS_RESPONSE) {
				users.addAll(Serializers.collection(User.SERIALIZER).read(connection.in()));
			} else {
				LOG.error("Response from server failed.");
			}

		} catch (Exception ex) {
			System.out.println("ERROR: Exception during call on server. Check log for details.");
			LOG.error(ex, "Exception during call on server.");
		}

		return users;
	}

	@Override
	public Collection<ConversationHeader> getConversations() {

		final Collection<ConversationHeader> summaries = new ArrayList<>();

		try (final Connection connection = source.connect()) {

			Serializers.INTEGER.write(connection.out(), NetworkCode.GET_ALL_CONVERSATIONS_REQUEST);

			if (Serializers.INTEGER.read(connection.in()) == NetworkCode.GET_ALL_CONVERSATIONS_RESPONSE) {
				summaries.addAll(Serializers.collection(ConversationHeader.SERIALIZER).read(connection.in()));
			} else {
				LOG.error("Response from server failed.");
			}

		} catch (Exception ex) {
			System.out.println("ERROR: Exception during call on server. Check log for details.");
			LOG.error(ex, "Exception during call on server.");
		}

		return summaries;
	}

	@Override
	public Collection<ConversationPayload> getConversationPayloads(Collection<Uuid> ids) {

		final Collection<ConversationPayload> conversations = new ArrayList<>();

		try (final Connection connection = source.connect()) {

			Serializers.INTEGER.write(connection.out(), NetworkCode.GET_CONVERSATIONS_BY_ID_REQUEST);
			Serializers.collection(Uuid.SERIALIZER).write(connection.out(), ids);

			if (Serializers.INTEGER.read(connection.in()) == NetworkCode.GET_CONVERSATIONS_BY_ID_RESPONSE) {
				conversations.addAll(Serializers.collection(ConversationPayload.SERIALIZER).read(connection.in()));
			} else {
				LOG.error("Response from server failed.");
			}
		} catch (Exception ex) {
			System.out.println("ERROR: Exception during call on server. Check log for details.");
			LOG.error(ex, "Exception during call on server.");
		}

		return conversations;
	}

	@Override
	public Collection<Message> getMessages(Collection<Uuid> ids) {

		final Collection<Message> messages = new ArrayList<>();

		try (final Connection connection = source.connect()) {

			Serializers.INTEGER.write(connection.out(), NetworkCode.GET_MESSAGES_BY_ID_REQUEST);
			Serializers.collection(Uuid.SERIALIZER).write(connection.out(), ids);

			if (Serializers.INTEGER.read(connection.in()) == NetworkCode.GET_MESSAGES_BY_ID_RESPONSE) {
				messages.addAll(Serializers.collection(Message.SERIALIZER).read(connection.in()));
			} else {
				LOG.error("Response from server failed.");
			}
		} catch (Exception ex) {
			System.out.println("ERROR: Exception during call on server. Check log for details.");
			LOG.error(ex, "Exception during call on server.");
		}

		return messages;
	}

	@Override
	public Collection<String> getBots() {

		Collection<String> allBots = new ArrayList<>();

		try (final Connection connection = source.connect()) {

			Serializers.INTEGER.write(connection.out(), NetworkCode.GET_ALL_BOTS_REQUEST);

			if (Serializers.INTEGER.read(connection.in()) == NetworkCode.GET_ALL_BOTS_RESPONSE) {
				allBots = Serializers.collection(Serializers.STRING).read(connection.in());
			} else {
				LOG.error("Response from server failed.");
			}

		} catch (Exception ex) {
			System.out.println("ERROR: Exception during call on server. Check log for details.");
			LOG.error(ex, "Exception during call on server.");
		}
		//returns allBots
		//allBots will be empty if there are no bots available 
		return allBots;
	}
	

  
  // This function returns the information about the Server,
  // including its Versions based on the specific server it
  // is using and is connected to and the up time for the server 
  public ServerInfo getInfo() {
    Uuid version = null;
    Time startTime = null;
    try (final Connection connection = this.source.connect()) {
      Serializers.INTEGER.write(connection.out(), NetworkCode.SERVER_VERSION_REQUEST);
      if (Serializers.INTEGER.read(connection.in()) == NetworkCode.SERVER_VERSION_RESPONSE) {
        version = Uuid.SERIALIZER.read(connection.in());
      } else {
        // Communicate this error - the server did not respond with the type of
        // response we expected.
        System.out.println("The server couldn't process the inputted information");
      }
    } catch (Exception ex) {
      // Communicate this error - something went wrong with the connection.
      System.out.println("There were some problems with the connection, so the information couldn't be accessed");
    }
    try (final Connection connection = source.connect()) {

			Serializers.INTEGER.write(connection.out(), NetworkCode.SERVER_UPTIME_REQUEST);
			if (Serializers.INTEGER.read(connection.in()) == NetworkCode.SERVER_UPTIME_RESPONSE) {
				startTime = Time.SERIALIZER.read(connection.in());
			} else {
				System.out.println("Unexpected Input: server cannot interpret information"); 
				LOG.error("Response from server failed."); 
			}
		} catch (Exception ex) {
			System.out.println("ERROR: Exception during call on server. Check log for details.");
			LOG.error(ex, "Exception during call on server.");
		}
    if(version!=null && startTime!=null) {
    	return new ServerInfo(version, startTime);
    }
    // If we get here it means something went wrong and null should be returned
    return null;
  }
  


}

