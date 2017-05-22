package codeu.chat.util;

import java.util.ArrayList;
import java.util.List;

public class CommandTokenizer {

	private String source;
	private int currentIndex = 0;

	public CommandTokenizer(String source) {
		// trims the source to rid leading and lagging white space
		this.source = source.trim();
		// runs get Command once so currentIndex is ready to return args
		getCommand();
	}

	public boolean hasNextArg() {
		int remainingIndices = source.length() - currentIndex;
		return remainingIndices > 0;
	}

	public List<String> getRemainingArgs() {
		ArrayList<String> args = new ArrayList<String>();
		while (hasNextArg()) {
			args.add(getNextArg());
		}
		return args;
	}

	public String getNextArg() {
		if (!this.hasNextArg()) {
			return null;
		}
		while (source.charAt(currentIndex) == ' ') {
			currentIndex++;
		}
		if (source.charAt(currentIndex) == '"') {
			currentIndex++;
			return nextStringUntil('"');
		}
		return nextStringUntil(' ');
	}

	public String getCommand() {
		currentIndex = 0;
		return getNextArg();
	}

	private String nextStringUntil(char endingChar) {
		String toReturn = "";
		while (hasNextArg() && source.charAt(currentIndex) != endingChar) {
			toReturn += source.charAt(currentIndex);
			currentIndex++;
		}
		currentIndex++;
		return toReturn;
	}

}
