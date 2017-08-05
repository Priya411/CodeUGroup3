package codeu.chat.server.bots;

import java.util.ArrayList;
import java.util.Random;

import codeu.chat.common.Bot;
import codeu.chat.util.CommandTokenizer;
import codeu.chat.util.Uuid;

/**
 * A bot that strives to be Kevin
 * @author matthewkrager
 *
 */
public class KevinBot extends Bot {
	
	String[] quotes = {
			"But Other than that, LGTM!",
			"As always, feel free to ask me questions",
			"I'll let you guys talk amongst yourselves",
			"I just sent out the invitation",
			"I'm just here to answer questions",
			"meow",
			"Happy Monday Everybody!",
			"At first glance",
			"That's all I had planned for this meeting",
			"I added a few comments",
			"Comments Comments Comments",
			"Does this need to public?",
			"As always, let me know if you have any questions or want to talk about anything!",
			"Going to lunch now, be back in an hour or so!",
			"What if there were a million users?"
	};
	@Override
	public String reactTo(String message, Uuid sender) {
		// turn the message into a command
		CommandTokenizer tokenizer = new CommandTokenizer(message);
		String command = tokenizer.getCommand();
		ArrayList<String> args = (ArrayList<String>) tokenizer.getRemainingArgs();

		switch (command) {
		case "kevin-wisdom":
			Random random = new Random();
			int index = random.nextInt(quotes.length);
			return quotes[index] + "\n - Kevin Workman";
		default:
			return null;
		}

	}

	@Override
	public String onAdd() {
		return "Hey it's KevinBot! Type \"kevin-wisdom\" to hear some Kevin quotes!";
	}
}
