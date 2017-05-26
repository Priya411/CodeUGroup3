package codeu.chat.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class CommandTokenizerTest {

	@Test
	public void testTokenizer() {
		CommandTokenizer quotesTokenizer = new CommandTokenizer(
				"Command \"arg0\" \"arg1\" \"arg2\" \"arg3\"");
		assertEquals("Command", quotesTokenizer.getCommand());
		assertEquals("arg0", quotesTokenizer.getNextArg());
		assertEquals(3, quotesTokenizer.getRemainingArgs().size());
		
		CommandTokenizer spaceTokenizer = new CommandTokenizer(
				"Command arg0 arg1 arg2 arg3");
		assertEquals("Command", spaceTokenizer.getCommand());
		assertEquals("arg0", spaceTokenizer.getNextArg());
		assertEquals("arg1", spaceTokenizer.getNextArg());
		assertEquals(2, spaceTokenizer.getRemainingArgs().size());
		
		CommandTokenizer noSpaceTokenizerWithQuotes = new CommandTokenizer(
				"Command \"arg0\"\"arg1\"\"arg2\"\"arg3\"");
		assertEquals("Command", noSpaceTokenizerWithQuotes.getCommand());
		assertEquals("arg0", noSpaceTokenizerWithQuotes.getNextArg());
		assertEquals("arg1", noSpaceTokenizerWithQuotes.getNextArg());
		assertEquals(2, noSpaceTokenizerWithQuotes.getRemainingArgs().size());
		
		CommandTokenizer spacesWithinQuotesTokenizer = new CommandTokenizer(
				"Command \"arg0 \"\"arg 1\"\"arg2\"\"arg3\"");
		assertEquals("Command", spacesWithinQuotesTokenizer.getCommand());
		assertEquals("arg0 ", spacesWithinQuotesTokenizer.getNextArg());
		assertEquals("arg 1", spacesWithinQuotesTokenizer.getNextArg());
		assertEquals(2, spacesWithinQuotesTokenizer.getRemainingArgs().size());
		
		CommandTokenizer alternatingTokenizer = new CommandTokenizer(
				"Command \"arg0 \" arg1 \"arg2\" arg3");
		assertEquals("Command", alternatingTokenizer.getCommand());
		assertEquals("arg0 ", alternatingTokenizer.getNextArg());
		assertEquals("arg1", alternatingTokenizer.getNextArg());
		assertEquals("arg2", alternatingTokenizer.getNextArg());
		assertEquals("arg3", alternatingTokenizer.getNextArg());
		
		CommandTokenizer blankTokenizer = new CommandTokenizer(
				"Command");
		assertEquals(0, blankTokenizer.getRemainingArgs().size());
		assertEquals(null, blankTokenizer.getNextArg());
	}
}
