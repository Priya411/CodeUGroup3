package codeu.chat.server;

import java.io.File;
import java.io.IOException;

import codeu.chat.common.ConversationHeader;
import codeu.chat.common.Message;
import codeu.chat.common.User;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * JSON class used to write and read from the given save file.
 * 
 *
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
	 * @param mapper
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
	private void save(Object obj, String arrayName) {
		final ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
				false);
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
		final ArrayNode usersArrayNode = (ArrayNode) fileNode.path(arrayName);
		usersArrayNode.addPOJO(obj);
		// save to the file
		ObjectWriter writer = mapper.writer(new DefaultPrettyPrinter());
		try {
			writer.writeValue(file, fileNode);
		} catch (IOException e) {
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
		save(user, "users");
	}

	/**
	 * Appends a message to the json file
	 * 
	 * @param message
	 */
	public void save(Message message) {
		// save the object message into the array "messages"
		save(message, "messages");
	}

	/**
	 * Appends a conversation to the json file
	 * 
	 * @param conversation
	 */
	public void save(ConversationHeader conversation) {
		// save the object conversation into the array "conversations"
		save(conversation, "conversations");
	}
}
