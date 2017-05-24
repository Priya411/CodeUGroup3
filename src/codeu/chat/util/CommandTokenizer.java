package codeu.chat.util;

import java.util.ArrayList;
import java.util.List;

final public class CommandTokenizer {

	private String source;
	private int currentIndex = 0;

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

	// resets currentIndex and returns Command Strings
	public String getCommand() {
		currentIndex = 0;
		return getNextArg();
	}

	public CommandTokenizer(String source) {
		// trims the source to rid leading and lagging white space
		this.source = source.trim();
		// runs get Command once so currentIndex is ready to return args
		// after running getCommand, currentIndex will be ready to tokenize arg0
		getCommand();
	}

	// returns all remaining args from the currentIndex to the end of the source
	// string
	public List<String> getRemainingArgs() {
		ArrayList<String> args = new ArrayList<String>();
		while (hasNextArg()) {
			args.add(getNextArg());
		}
		return args;
	}
}