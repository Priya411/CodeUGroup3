package codeu.chat.server;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import codeu.chat.common.*;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import codeu.chat.util.Time;
import codeu.chat.util.Uuid;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.*;

/**
 * JSON class used to write and read from the given save file.
 * 
 * This file is to be used to allow the server to get a log of all actions
 * completed This class saves all inputted commands It creates a new command so
 * that the user can then access the log This log can be used by the server to
 * re-load all commands from previous uses of the server, and to save all users
 * and conversations on the server
 */
public final class JSON {

	private String fileName = "data.json";

	public JSON() {
	}

	public JSON(String fileName) {
		this.fileName = fileName;
	}

	/**
	 * Writes empty arrays to the json file. Should be used to reset the file or
	 * initialize basic backbone if file is blank
	 *
	 * param mapper
	 */
	private JsonNode createNodeWithBlankArrays() {
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode outerNode = mapper.createObjectNode();
		outerNode.set("users", mapper.createArrayNode());
		outerNode.set("conversations", mapper.createArrayNode());
		outerNode.set("messages", mapper.createArrayNode());
		return outerNode;
	}

	/**
	 * Saves the given object inside of the array with given arrayName
	 *
	 * @param obj
	 *            Object to be saved
	 * @param arrayName
	 *            Given array to save obj in
	 */
	private void save(Object obj, String arrayName, String UUID) {
		final ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		File file = new File(fileName);
		// if the file doesn't exist, try to create it
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				// prints whats wrong if cant create file
				e.printStackTrace();
			}
		}
		// convert the file to a node and append given convo
		JsonNode fileNode;
		try {
			fileNode = mapper.readTree(file);
		} catch (Exception e) {
			e.printStackTrace();
			// create a node with blank arrays in case there is invalid json
			// in the file
			fileNode = createNodeWithBlankArrays();
		}
		// if the node is null orthere is valid json, but the given array doesnt
		// exist, then create the given array
		if (fileNode == null || !fileNode.has(arrayName)) {
			fileNode = createNodeWithBlankArrays();
		}
		// fetch the requested array and append the new object
		final ArrayNode requestedArrayNode = (ArrayNode) fileNode.path(arrayName);
		int index = 0;
		boolean isAlreadyPresent = false;
		// loop through to check that there are no same uuid already saved
		for (JsonNode curObj : requestedArrayNode) {
			if (curObj.get("uuid") != null && UUID.equals(curObj.get("uuid").asText())) {
				System.out.println("Found same UUID, updating existing object");
				// if anything else, just rewrite with new version of object
				JsonNode nodeToSave = mapper.convertValue(obj, JsonNode.class);
				requestedArrayNode.set(index, nodeToSave);
				isAlreadyPresent = true;
			}
			index++;
		}
		if (!isAlreadyPresent) {
			requestedArrayNode.addPOJO(obj);
		}
		// save to the file
		ObjectWriter writer = mapper.writer(new DefaultPrettyPrinter());
		try {
			writer.writeValue(file, fileNode);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Used to erase all saved content and place a blank array
	 */
	public void clearFile() {
		JsonNode blankNode = createNodeWithBlankArrays();
		try {
			new ObjectMapper().writer(new DefaultPrettyPrinter()).writeValue(new File(fileName), blankNode);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Appends a user to the json file
	 *
	 * @param user
	 */
	public void save(User user) {
		// save the object user into the array "users"
		save(user, "users", user.getUUID());
	}

	/**
	 * Appends a message to the json file
	 *
	 * @param message
	 */
	public void save(Message message) {
		// save the object message into the array "messages"
		save(message, "messages", message.getUUID());
	}

	/**
	 * Appends a conversation to the json file
	 *
	 * @param conversation
	 */
	public void save(ConversationHeader conversation) {
		// save the object conversation into the array "conversations"
		save(conversation, "conversations", conversation.getUUID());
	}

	/**
	 * @return
	 */
	public void save(ConversationPayload foundConversation) {
		save(foundConversation, "conversations", foundConversation.id.toString());
	}

    public Model createModelForServer()
    {
        // This function is to be used by the Server class
        // If the file, input.json, which holds the transactions
        // and commands used into the server at a previous time,
        // exists, this means that the server had been used before.
        // This will then call readFromFile using that previous
        // log, allowing the sever to access all previous transactions
        // If the server hasn't been used before, then there have
        // been no transactions and therefore there has not yet been a
        // file created, causing the IOException to be thrown.
        // Other times when this exception can be thrown
        // are when the transactions haven't been converted to JSON
        // properly. In any of these cases, a new Model instance will be
        // created, essentially starting the server without any memory of
        // previous transactions/commands.
        try
        {
            // data.json is the predetermined folder in which
            // the transaction log will go, located in the Server directory
            // The path may need to be updated based on the way the local project is set up
            return readFromFile("data.json");
        }
        catch (IOException e)
        {
            return new Model();
        }
    }

    public Model readFromFile (String file) throws IOException
    {
        Model model = new Model();
        JsonFactory jsonF = new JsonFactory();
        JsonParser jp = null;
        try
        {
            jp = jsonF.createParser(new FileReader(new File(file)));
        }
        catch(IOException e)
        {
            // If the file is invalid or can't be parsed correctly
            // a version of Model without any transaction logs in
            // will be used and returned.
            System.out.println("Invalid file");
        }
        if (jp!=null) {
            // If the file exists, then the file will be parsed
            // in this set of code
            jp.nextToken();
            while (jp.nextToken() != JsonToken.END_OBJECT)
            // This ensures that we haven't reached the end of the
            // file because the data is being parsed in such a way
            // that this END_OBJECT specifically relates to the
            // very last } in the json file
            {
                if(jp.getText().equals("users")) {
                    // If the object type represents a User, then
                    // specific steps will take place to create
                    // a User variable
                    //jp.nextToken();
                    Time time = null;
                    String name = "";
                    Uuid id = null;
                    long updateTime = -1;
                    HashMap<Uuid, Integer> conversationInterests = new HashMap<>();
                    ArrayList<Uuid> userInterests = new ArrayList<Uuid>();
                    boolean conversationInterestsRun = false;
                    boolean userInterestsRun = false;
                    while (jp.nextToken() != JsonToken.END_ARRAY) {
                        if (jp.getText().equals("name")) {
                            jp.nextToken();
                            name = jp.getText();
                        }
                        if (jp.getText().equals("conversationInterests")) {
                            conversationInterestsRun = true;
                            jp.nextToken();
                            while(jp.nextToken()!=JsonToken.END_OBJECT)
                            {
                                Uuid convoId = null;
                                try {
                                    convoId = Uuid.parse(jp.getText());
                                } catch (IOException e) {
                                    System.out.println("Invalid Uuid");
                                }
                                jp.nextToken();
                                Integer conversationAmount = jp.getIntValue();
                                conversationInterests.put(convoId,conversationAmount);
                            }
                        }
                        if (jp.getText().equals("userInterests")) {
                            userInterestsRun = true;
                            jp.nextToken();
                            while(jp.nextToken()!=JsonToken.END_ARRAY)
                            {
                                Uuid UserId = null;
                                try {
                                    UserId = Uuid.parse(jp.getText());
                                } catch (IOException e) {
                                    System.out.println("Invalid Uuid");
                                }
                                userInterests.add(UserId);
                            }
                        }
                        if (jp.getText().equals("uuid")) {
                            jp.nextToken();
                            try {
                                id = Uuid.parse(jp.getText());
                            } catch (IOException e) {
                                System.out.println("Invalid Uuid");
                            }
                        }
                        if (jp.getText().equals("creationTime")) {
                            jp.nextToken();
                            time = Time.fromMs(jp.getLongValue());
                        }
                        if (jp.getText().equals("lastUpdateTime")) {
                            jp.nextToken();
                            updateTime = jp.getLongValue();
                        }
                        if (!(name.equals("")) && time != null && id != null && userInterestsRun && conversationInterestsRun && updateTime!= -1) {
                            User user = new User(id, name, time);
                            user.setUpdateTime(updateTime);
                            for (Uuid userID: userInterests) {
                                user.addUserInterest(userID);
                            }
                            for(Uuid convID: conversationInterests.keySet())
                            {
                                user.addConvoInterest(convID, conversationInterests.get(convID));
                            }
                            model.add(user);
                            name = "";
                            time = null;
                            id = null;
                            userInterestsRun = false;
                            conversationInterestsRun = false;
                            conversationInterests = new HashMap<>();
                            userInterests = new ArrayList<Uuid>();
                            updateTime = -1;
                        }
                    }
                    continue;
                }
                if(jp.getText().equals("conversations"))
                {
                    // If the object type represents a Conversation, then
                    // specific steps will take place to create
                    // a ConversationHeader variable
                    Uuid id = null;
                    Uuid owner = null;
                    Time creation = null;
                    String title = "";
                    Uuid last = null;
                    Uuid first = null;
                    boolean userRolesRun = false;
                    UserType defaultType = null;
                    HashMap<Uuid, UserType> userAccessRoles = new HashMap<Uuid, UserType>();
                    LinkedList<Bot> bots = new LinkedList<Bot>();
                    while(jp.nextToken()!=JsonToken.END_ARRAY) {
                        if (jp.getText().equals("userAccessRoles")) {
                            userRolesRun = true;
                            jp.nextToken();
                            while(jp.nextToken()!=JsonToken.END_OBJECT)
                            {
                                Uuid userId = null;
                                try {
                                    userId = Uuid.parse(jp.getText());
                                } catch (IOException e) {
                                    System.out.println("Invalid Uuid");
                                }
                                jp.nextToken();
                                UserType type = UserType.valueOf(jp.getText());
                                userAccessRoles.put(userId,type);
                            }
                        }
                        if (jp.getText().equals("title")) {
                            jp.nextToken();
                            title = jp.getText();
                        }
                        if (jp.getText().equals("uuid")) {
                            jp.nextToken();
                            try {
                                id = Uuid.parse(jp.getText());
                            } catch (IOException e) {
                                System.out.println("Invalid Uuid");
                            }
                        }
                        if (jp.getText().equals("ownerUUID")) {
                            jp.nextToken();
                            try {
                                owner = Uuid.parse(jp.getText());
                            } catch (IOException e) {
                                System.out.println("Invalid Uuid");
                            }
                        }
                        if (jp.getText().equals("firstMessageUUID")) {
                            jp.nextToken();
                            try {
                                first = Uuid.parse(jp.getText());
                            } catch (IOException e) {
                                System.out.println("Invalid Uuid");
                            }
                        }
                        if (jp.getText().equals("lastMessageUUID")) {
                            jp.nextToken();
                            try {
                                last = Uuid.parse(jp.getText());
                            } catch (IOException e) {
                                System.out.println("Invalid Uuid");
                            }
                        }
                        if (jp.getText().equals("creationTime")) {
                            jp.nextToken();
                            creation = Time.fromMs(jp.getLongValue());
                        }

                        if (jp.getText().equals("defaultType")) {
                            jp.nextToken();
                            defaultType = UserType.valueOf(jp.getText());
                        }
                        if(jp.getText().equals("bots"))
                        {
                            jp.nextToken();
                            while(jp.nextToken()!=JsonToken.END_ARRAY) {
                                try {
                                    Bot bot = (Bot) Class.forName(jp.getText()).newInstance();
                                    bots.add(bot);
                                } catch (Exception e) {
                                }
                            }
                        }
                        if (!(title.equals("")) && id != null && owner != null && creation != null && last!=null && first!=null && userRolesRun && defaultType!=null) {
                            ConversationHeader conv = new ConversationHeader(id, owner, creation, title);
                            for(Uuid Uid: userAccessRoles.keySet())
                            {
                                conv.setAccessOf(Uid,userAccessRoles.get(Uid));
                            }
                            for(Bot bot: bots)
                            {
                                conv.bots.add(bot);
                            }
                            model.add(conv, new ConversationPayload(id,first,last));
                            title = "";
                            id= null;
                            owner = null;
                            creation = null;
                            first = null;
                            userRolesRun = false;
                            userAccessRoles = new HashMap<Uuid, UserType>();
                            last = null;
                            defaultType = null;
                            bots = new LinkedList<Bot>();
                        }
                    }
                    continue;
                }
                if(jp.getText().equals("messages")) {
                    // If the object type represents a Message, then
                    // specific steps will take place to create
                    // a Message variable
                      Uuid id = null;
                      Uuid owner = null;
                      Time creation = null;
                      String body = "";
                      Uuid next = null;
                      Uuid previous = null;
                      boolean ownerRun = false;
                    while (jp.nextToken() != JsonToken.END_ARRAY) {
                        if (jp.getText().equals("content")) {
                            jp.nextToken();
                            body = jp.getText();
                        }
                        if (jp.getText().equals("uuid")) {
                            jp.nextToken();
                            try {
                                id = Uuid.parse(jp.getText());
                            } catch (IOException e) {
                                System.out.println("Invalid Uuid");
                            }
                        }
                        if (jp.getText().equals("authorUUID")) {
                            ownerRun = true;
                            jp.nextToken();
                            try {
                                owner = Uuid.parse(jp.getText());
                            } catch (IOException e) {
                                System.out.println("Invalid Uuid");
                            }
                        }
                        if (jp.getText().equals("creationTime")) {
                            jp.nextToken();
                            creation = Time.fromMs(jp.getLongValue());
                        }
                        if (jp.getText().equals("nextUUID")) {
                            jp.nextToken();
                            try {
                                next = Uuid.parse(jp.getText());
                            } catch (IOException e) {
                                System.out.println("Invalid Uuid");
                            }
                        }
                        if (jp.getText().equals("previousUUID")) {
                            jp.nextToken();
                            try {
                                previous = Uuid.parse(jp.getText());
                            } catch (IOException e) {
                                System.out.println("Invalid Uuid");
                            }
                        }
                        if (!(body.equals("")) && id != null && ownerRun && creation != null && previous!=null && next!=null) {
                            model.add(new Message(id, next, previous, creation, owner, body));
                            body = "";
                            id = null;
                            owner = null;
                            creation = null;
                            next = null;
                            previous = null;
                            ownerRun = false;
                        }
                    }
                }
            }
            jp.close();
        }
        return model;
    }
}
