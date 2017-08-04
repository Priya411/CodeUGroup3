package codeu.chat.server.bots;

import java.util.ArrayList;

import codeu.chat.util.CommandTokenizer;
import codeu.chat.util.Uuid;
import codeu.chat.common.Bot;

public class HangmanBot extends Bot {

	private String word;
	private String guessString = "";
	private int lives = 6;

	public String loseOrRejectGuessWith(String message) {
		lives -= 1;
		if (lives < 0) {
			word = null;
			return "Out of lives! Sorry but you lose! The word was " + word;
		}
		return message;
	}

	@Override
	public String reactTo(String message, Uuid sender) {
		// turn the message into a command
		CommandTokenizer tokenizer = new CommandTokenizer(message);
		String command = tokenizer.getCommand();
		ArrayList<String> args = (ArrayList<String>) tokenizer.getRemainingArgs();

		switch (command) {
		// resets the guesstring to a string of x dashes where x is the length of the
		// new given word.
		// resets lives to 6
		case "hangman-start":
			word = args.get(0).toLowerCase();
			guessString = "";
			for (int i = 0; i < word.length(); i++) {
				guessString += "-";
			}
			lives = 6;
			return "Welcome to your hangman! To guess, please use \"hangman-guess <guessLetter>\"";
		case "hangman-guess":
			// if game not yet started
			if (word == null) {
				return "Please start a hangman game before you start guessing!";
			}
			// gets the guess string or single character
			String guess = args.get(0).toLowerCase();
			// if it's a word
			if (guess.length() > 1) {
				// check if the guessed word is correct
				if (word.equals(guess)) {
					word = null;
					return "You did it! You win! The word was " + word;
				} else {
					return loseOrRejectGuessWith("Nope! The word certainly isn't \"" + guess + "\"");
				}
			} else {
				int lastIndexOfLetter = -1;
				// Loop through all the insances of the guessed character
				// each time, replace the dash in guesstring with the actual letter
				while (word.indexOf(guess, lastIndexOfLetter) != -1) {
					lastIndexOfLetter = word.indexOf(guess, lastIndexOfLetter);
					guessString = guessString.substring(0, lastIndexOfLetter) + guess
							+ guessString.substring(lastIndexOfLetter + 1);
				}
				// if the guess was not found inside of the word
				if (lastIndexOfLetter == -1) {
					return loseOrRejectGuessWith(
							"Nope! Sorry but the word does not contain any " + guess + "'s...\n" + guessString);
				}
				// if the guessString is out of dashes and is complete
				if (guessString == word) {
					word = null;
					return "You WIN! The word was " + word;
				}
				return "Yay! Correct!\nguessString";
			}
		default:
			return null;
		}

	}

	@Override
	public String onAdd() {
		return "Hey All! Type \"hangman-start <Word>\" to play some hangman!";
	}

}
