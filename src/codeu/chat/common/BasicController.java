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

import codeu.chat.util.Time;
import codeu.chat.util.Uuid;

// BASIC CONTROLLER
//
//   The controller component in the Model-View-Controller pattern. This
//   component is used to write information to the model where the model
//   is the current state of the server. Data returned from the controller
//   should be treated as read only data as manipulating any data returned
//   from the controller may have no effect on the server's state.
public interface BasicController {

	// NEW MESSAGE
	//
	// Create a new message on the server. All parameters must be provided
	// or else the server won't apply the change. If the operation is
	// successful, a Message object will be returned representing the full
	// state of the message on the server.
	Message newMessage(Uuid author, Uuid conversation, String body);

	// NEW USER
	//
	// Create a new user on the server. All parameters must be provided
	// or else the server won't apply the change. If the operation is
	// successful, a User object will be returned representing the full
	// state of the user on the server. Whether user names can be shared
	// is undefined.
	User newUser(String name);

	// NEW CONVERSATION
	//
	// Create a new conversation on the server. All parameters must be
	// provided or else the server won't apply the change. If the
	// operation is successful, a Conversation object will be returned
	// representing the full state of the conversation on the server.
	// Whether conversations can have the same title is undefined.
	ConversationHeader newConversation(String title, Uuid owner);

	// create new convo interest for the given userId, a ConvoInterest object is
	// returned
	ConvoInterest newConvoInterest(Uuid userId, String title);

	// adds user interset of a user with the given name to the userId supplied.
	// returns the uuid of the name of the user being added as an interest
	Uuid newUserInterest(Uuid userId, String name);

	// removes convo with given title from user with given id
	// returns id of the convo that was removed
	Uuid removeConvoInterest(Uuid userId, String title);

	// removes interest of user with given name from user with given id
	// return id of user name who was removed
	Uuid removeUserInterest(Uuid userId, String name);

	// STATUS UPDATE
	Time statusUpdate();

	// sends the id of the user trying to change access of the user with given
	// username to the given type
	// this is sent to the server, id of the user with given username is
	// returned if this is a valid request, if not, then null is returned
	// null can be returned if username is invalid or if user with userID does
	// not have access to change the userType of the user with the given
	// username
	// all of this is for convo with convoID
	Uuid changeAccessControl(Uuid userID, Uuid convoId, String username, UserType type);

	boolean addBot(Uuid id, String botName);
	boolean removeBot(Uuid id, String botName);

}
