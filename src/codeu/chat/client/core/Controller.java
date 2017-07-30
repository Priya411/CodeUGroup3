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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.Thread;

import codeu.chat.common.BasicController;
import codeu.chat.common.ConversationHeader;
import codeu.chat.common.ConvoInterest;
import codeu.chat.common.Message;
import codeu.chat.common.NetworkCode;
import codeu.chat.common.ServerInfo;
import codeu.chat.common.User;
import codeu.chat.common.UserType;
import codeu.chat.util.Logger;
import codeu.chat.util.Serializers;
import codeu.chat.util.Time;
import codeu.chat.util.Uuid;
import codeu.chat.util.connections.Connection;
import codeu.chat.util.connections.ConnectionSource;

final class Controller implements BasicController {

	private final static Logger.Log LOG = Logger.newLog(Controller.class);

	private final ConnectionSource source;

	public Controller(ConnectionSource source) {
		this.source = source;
	}

	@Override
	public Message newMessage(Uuid author, Uuid conversation, String body) {

		Message response = null;

		try (final Connection connection = source.connect()) {

			Serializers.INTEGER.write(connection.out(),
					NetworkCode.NEW_MESSAGE_REQUEST);
			Uuid.SERIALIZER.write(connection.out(), author);
			Uuid.SERIALIZER.write(connection.out(), conversation);
			Serializers.STRING.write(connection.out(), body);

			if (Serializers.INTEGER.read(connection.in()) == NetworkCode.NEW_MESSAGE_RESPONSE) {
				response = Serializers.nullable(Message.SERIALIZER).read(
						connection.in());
			} else {
				LOG.error("Response from server failed.");
			}
		} catch (Exception ex) {
			System.out
					.println("ERROR: Exception during call on server. Check log for details.");
			LOG.error(ex, "Exception during call on server.");
		}

		return response;
	}

	@Override
	public User newUser(String name) {

		User response = null;

		try (final Connection connection = source.connect()) {

			Serializers.INTEGER.write(connection.out(),
					NetworkCode.NEW_USER_REQUEST);
			Serializers.STRING.write(connection.out(), name);
			LOG.info("newUser: Request completed.");

			if (Serializers.INTEGER.read(connection.in()) == NetworkCode.NEW_USER_RESPONSE) {
				response = Serializers.nullable(User.SERIALIZER).read(
						connection.in());
				LOG.info("newUser: Response completed.");
			} else {
				LOG.error("Response from server failed.");
			}
		} catch (Exception ex) {
			System.out
					.println("ERROR: Exception during call on server. Check log for details.");
			LOG.error(ex, "Exception during call on server.");
		}

		return response;
	}

	@Override
	public ConversationHeader newConversation(String title, Uuid owner) {

		ConversationHeader response = null;

		try (final Connection connection = source.connect()) {

			Serializers.INTEGER.write(connection.out(),
					NetworkCode.NEW_CONVERSATION_REQUEST);
			Serializers.STRING.write(connection.out(), title);
			Uuid.SERIALIZER.write(connection.out(), owner);

			if (Serializers.INTEGER.read(connection.in()) == NetworkCode.NEW_CONVERSATION_RESPONSE) {
				response = Serializers.nullable(ConversationHeader.SERIALIZER)
						.read(connection.in());
			} else {
				LOG.error("Response from server failed.");
			}
		} catch (Exception ex) {
			System.out
					.println("ERROR: Exception during call on server. Check log for details.");
			LOG.error(ex, "Exception during call on server.");
		}

		return response;
	}

	public Time statusUpdate() {
		Time updateTime = null;
		try (final Connection connection = source.connect()) {

			Serializers.INTEGER.write(connection.out(),
					NetworkCode.STATUS_UPDATE_REQUEST);

			if (Serializers.INTEGER.read(connection.in()) == NetworkCode.STATUS_UPDATE_RESPONSE) {
				updateTime = Time.SERIALIZER.read(connection.in());
			} else {
				LOG.error("Response from server failed.");
			}
		} catch (Exception ex) {
			System.out
					.println("ERROR: Exception during call on server. Check log for details.");
			LOG.error(ex, "Exception during call on server.");
		}

		if (updateTime != null) {
			return updateTime;
		}
		// If we get here it means something went wrong and null should be
		// returned
		return null;
	}

	@Override
	public ConvoInterest newConvoInterest(Uuid userId, String title) {
		try (final Connection connection = source.connect()) {

			Serializers.INTEGER.write(connection.out(),
					NetworkCode.NEW_CONVO_INTEREST_REQUEST);
			Uuid.SERIALIZER.write(connection.out(), userId);
			Serializers.STRING.write(connection.out(), title);

			if (Serializers.INTEGER.read(connection.in()) == NetworkCode.NEW_CONVO_INTEREST_RESPONSE) {
				return Serializers.nullable(ConvoInterest.SERIALIZER).read(
						connection.in());
			} else {
				LOG.error("Response from server failed.");
			}
		} catch (Exception ex) {
			System.out
					.println("ERROR: Exception during call on server. Check log for details.");
			LOG.error(ex, "Exception during call on server.");
		}
		return null;
	}

	@Override
	public Uuid newUserInterest(Uuid userId, String name) {
		try (final Connection connection = source.connect()) {

			Serializers.INTEGER.write(connection.out(),
					NetworkCode.NEW_USER_INTEREST_REQUEST);
			Uuid.SERIALIZER.write(connection.out(), userId);
			Serializers.STRING.write(connection.out(), name);
			if (Serializers.INTEGER.read(connection.in()) == NetworkCode.NEW_USER_INTEREST_RESPONSE) {
				return Serializers.nullable(Uuid.SERIALIZER).read(
						connection.in());
			} else {
				LOG.error("Response from server failed.");
			}
		} catch (Exception ex) {
			System.out
					.println("ERROR: Exception during call on server. Check log for details.");
			LOG.error(ex, "Exception during call on server.");
		}
		return null;
	}

	@Override
	public Uuid removeConvoInterest(Uuid userId, String name) {
		try (final Connection connection = source.connect()) {

			Serializers.INTEGER.write(connection.out(),
					NetworkCode.REM_CONVO_INTEREST_REQUEST);
			Uuid.SERIALIZER.write(connection.out(), userId);
			Serializers.STRING.write(connection.out(), name);
			if (Serializers.INTEGER.read(connection.in()) == NetworkCode.REM_CONVO_INTEREST_RESPONSE) {
				return Serializers.nullable(Uuid.SERIALIZER).read(
						connection.in());
			} else {
				LOG.error("Response from server failed.");
			}
		} catch (Exception ex) {
			System.out
					.println("ERROR: Exception during call on server. Check log for details.");
			LOG.error(ex, "Exception during call on server.");
		}
		return null;
	}

	@Override
	public Uuid removeUserInterest(Uuid userId, String name) {
		try (final Connection connection = source.connect()) {

			Serializers.INTEGER.write(connection.out(),
					NetworkCode.REM_USER_INTEREST_REQUEST);
			Uuid.SERIALIZER.write(connection.out(), userId);
			Serializers.STRING.write(connection.out(), name);
			if (Serializers.INTEGER.read(connection.in()) == NetworkCode.REM_USER_INTEREST_RESPONSE) {
				return Serializers.nullable(Uuid.SERIALIZER).read(
						connection.in());
			} else {
				LOG.error("Response from server failed.");
			}
		} catch (Exception ex) {
			System.out
					.println("ERROR: Exception during call on server. Check log for details.");
			LOG.error(ex, "Exception during call on server.");
		}
		return null;
	}

	@Override
	public Uuid changeAccessControl(Uuid userID, Uuid convoID, String username, UserType type) {
		try (final Connection connection = source.connect()) {
			Serializers.INTEGER.write(connection.out(),
					NetworkCode.CHANGE_ACCESS_CONTROL_REQUEST);
			Uuid.SERIALIZER.write(connection.out(), userID);
			Uuid.SERIALIZER.write(connection.out(), convoID);
			Serializers.STRING.write(connection.out(), username);
			Serializers.STRING.write(connection.out(), type.name());
			if (Serializers.INTEGER.read(connection.in()) == NetworkCode.CHANGE_ACCESS_CONTROL_RESPONSE) {
				return Serializers.nullable(Uuid.SERIALIZER).read(
						connection.in());
			} else {
				LOG.error("Response from server failed.");
			}
		} catch (Exception ex) {
			System.out
					.println("ERROR: Exception during call on server. Check log for details.");
			LOG.error(ex, "Exception during call on server.");
		}
		return null;
	}

	// send an add bot request along with the id of the convo and the name of the bot that should be added to the convo
	// it expects a boolean indicating whether or not a bot with that name was successfully added to the convo
	// true = bot added, false = bot not added
	@Override
	public boolean addBot(Uuid convoId, String botName) {
		try (final Connection connection = source.connect()) {
			Serializers.INTEGER.write(connection.out(),
					NetworkCode.BOT_ADD_REQUEST);
			Uuid.SERIALIZER.write(connection.out(), convoId);
			Serializers.STRING.write(connection.out(), botName);
			if (Serializers.INTEGER.read(connection.in()) == NetworkCode.BOT_ADD_RESPONSE) {
				return Serializers.nullable(Serializers.BOOLEAN).read(
						connection.in());
			} else {
				LOG.error("Response from server failed.");
			}
		} catch (Exception ex) {
			System.out
					.println("ERROR: Exception during call on server. Check log for details.");
			LOG.error(ex, "Exception during call on server.");
		}
		return false;
	}
	
	// send an remove bot request along with the id of the convo and the name of the bot that should be added to the convo
	// it expects a boolean indicating whether or not a bot with that name was successfully removed from the convo
	// true = bot removed, false = bot not removed
	@Override
	public boolean removeBot(Uuid convoId, String botName) {
		try (final Connection connection = source.connect()) {
			Serializers.INTEGER.write(connection.out(),
					NetworkCode.BOT_REMOVE_REQUEST);
			Uuid.SERIALIZER.write(connection.out(), convoId);
			Serializers.STRING.write(connection.out(), botName);
			if (Serializers.INTEGER.read(connection.in()) == NetworkCode.BOT_REMOVE_RESPONSE) {
				return Serializers.nullable(Serializers.BOOLEAN).read(
						connection.in());
			} else {
				LOG.error("Response from server failed.");
			}
		} catch (Exception ex) {
			System.out
					.println("ERROR: Exception during call on server. Check log for details.");
			LOG.error(ex, "Exception during call on server.");
		}
		return false;
	}
}
