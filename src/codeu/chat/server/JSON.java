package codeu.chat.server;

import java.io.File;
import java.io.IOException;

import codeu.chat.common.ConversationHeader;
import codeu.chat.common.Message;
import codeu.chat.common.User;

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

public final class JSON {

	private static void createNewJSON(ObjectMapper mapper) {
		ObjectNode outerNode = mapper.createObjectNode();
		outerNode.set("users", mapper.createArrayNode());
		outerNode.set("conversations", mapper.createArrayNode());
		outerNode.set("messages", mapper.createArrayNode());
		ObjectWriter writer = mapper.writer(new DefaultPrettyPrinter());
		try {
			writer.writeValue(new File("data.json"), outerNode);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * Appends a user to the json file
	 * 
	 * @param user
	 */
	public static void save(User user) {
		final ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
				false);
		// will catch if the file doesn't exist or there is an error reading
		try {
			JsonNode fileNode = mapper.readTree(new File("data.json"));
			final ArrayNode usersArrayNode = (ArrayNode) fileNode.path("users");
			usersArrayNode.addPOJO(user);
			ObjectWriter writer = mapper.writer(new DefaultPrettyPrinter());
			writer.writeValue(new File("data.json"), fileNode);
		} catch (Exception e) {
			// create the empty JSON and save it to the file
			createNewJSON(mapper);
			save(user);
			return;
		}
	}

	/**
	 * Appends a message to the json file
	 * 
	 * @param user
	 */
	public static void save(Message message) {
		final ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
				false);
		// will catch if the file doesn't exist or there is an error reading
		try {
			JsonNode fileNode = mapper.readTree(new File("data.json"));
			final ArrayNode usersArrayNode = (ArrayNode) fileNode
					.path("messages");
			usersArrayNode.addPOJO(message);
			ObjectWriter writer = mapper.writer(new DefaultPrettyPrinter());
			writer.writeValue(new File("data.json"), fileNode);
		} catch (Exception e) {
			// create the empty JSON and save it to the file
			createNewJSON(mapper);
			save(message);
			return;
		}
	}

	/**
	 * Appends a conversation to the json file
	 * 
	 * @param user
	 */
	public static void save(ConversationHeader conversation) {
		final ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
				false);
		// will catch if the file doesn't exist or there is an error reading
		try {
			JsonNode fileNode = mapper.readTree(new File("data.json"));
			final ArrayNode usersArrayNode = (ArrayNode) fileNode
					.path("conversations");
			usersArrayNode.addPOJO(conversation);
			ObjectWriter writer = mapper.writer(new DefaultPrettyPrinter());
			writer.writeValue(new File("data.json"), fileNode);
		} catch (Exception e) {
			// create the empty JSON and save it to the file
			createNewJSON(mapper);
			save(conversation);
			return;
		}
	}
}
