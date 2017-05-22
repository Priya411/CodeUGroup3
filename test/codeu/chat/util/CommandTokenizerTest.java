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
	}
}
