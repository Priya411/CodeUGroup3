package codeu.chat.util;

import java.util.ArrayList;
import java.util.List;

/**
 * CommandTokenizer tokenizes the input String and allows for easy use of
 * methods such as getCommand(), or getNextArg(). Meant to take input from a
 * command line following format: "Command Arg0 Arg1..." Arguments and commands
 * can be surrounded by quotes or just spaces.
 * 
 * @author MatthewKrager
 *
 */
final public class CommandTokenizer {

	/**
	 * String that will be tokenized. Passed Through constructor
	 */
	private String source;
	/**
	 * The index that the tokenizer is currently at. When nextArg() is called,
	 * the argument after the current index will be returned
	 */
	private int currentIndex = 0;

	/**
	 * 
	 * @return If there are any args the tokenizer has not yet returned
	 */
	public boolean hasNextArg() {
		int remainingIndices = source.length() - currentIndex;
		return remainingIndices > 0;
	}

	// returns a String starting at currentIndex until the next occurrence of
	// endingChar.
	// Does not include endingChar in the String
	private String nextStringUntil(char endingChar) {
		String toReturn = "";
		while (hasNextArg() && source.charAt(currentIndex) != endingChar) {
			toReturn += source.charAt(currentIndex);
			currentIndex++;
		}
		currentIndex++;
		return toReturn;
	}

	/**
	 * Returns the argument directly after the currentIndex
	 * 
	 * @return the next argument in the source string
	 */
	public String getNextArg() {
		if (!this.hasNextArg()) {
			return null;
		}
		// skip all spaces
		while (source.charAt(currentIndex) == ' ') {
			currentIndex++;
		}
		// if quotes is first, return string between the two quotes
		if (source.charAt(currentIndex) == '"') {
			currentIndex++;
			return nextStringUntil('"');
		}
		return nextStringUntil(' ');
	}

	/**
	 * Leaves currentIndex right before the first argument. Calling getCommand
	 * will reset all calls made to getNextArg()
	 * 
	 * @return Returns command (first section of the source String)
	 */
	public String getCommand() {
		currentIndex = 0;
		return getNextArg();
	}

	/**
	 * Initializes a CommandTokenizer with the given source String. Leaves
	 * currentIndex right before the first argument.
	 * 
	 * @param source
	 */
	public CommandTokenizer(String source) {
		// trims the source to rid leading and lagging white space
		this.source = source.trim();
		// runs get Command once so currentIndex is ready to return args
		// after running getCommand, currentIndex will be ready to tokenize arg0
		getCommand();
	}

	/**
	 * 
	 * @return all remaining args from the currentIndex to the end of the source
	 *         string
	 */
	public List<String> getRemainingArgs() {
		ArrayList<String> args = new ArrayList<String>();
		while (hasNextArg()) {
			args.add(getNextArg());
		}
		return args;
	}
}