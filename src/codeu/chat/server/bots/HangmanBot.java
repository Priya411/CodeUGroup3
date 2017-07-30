package codeu.chat.server.bots;

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

	public String reactTo(String command, String[] args) {
		switch (command) {
		case "hangman-start":
			word = args[0].toLowerCase();
			guessString = "";
			for (int i = 0; i < word.length(); i++) {
				guessString += "-";
			}
			lives = 6;
			return "Welcome to your hangman! To guess, please use \"hangman-guess <guessLetter>\"";
		case "hangman-guess":
			if (word == null) {
				return "Please start a hangman game before you start guessing!";
			}
			String guess = args[0].toLowerCase();
			if (guess.length() > 1) {
				if (word.equals(guess)) {
					word = null;
					return "You did it! You win! The word was " + word;
				} else {
					return loseOrRejectGuessWith("Nope! The word certainly isn't \"" + guess + "\"");
				}
			} else {
				int lastIndexOfLetter = -1;
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
				if (guessString == word) {
					word = null;
					return "You WIN! The word was " + word;
				}
				return "Yay! Correct!\nguessString";
			}
		}
	}

	public String onAdd() {
		return "Hey All! Type \"hangman-start <Word>\" to play some hangman!";
	}

}
