package codeu.chat.common;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Test;

import codeu.chat.util.Time;
import codeu.chat.util.Uuid;

public class CAccessTest {

    @Test
    public void testGetAccessOf() {
        ConversationHeader convo = new ConversationHeader(new Uuid(10), new Uuid(15), Time.now(), "Title"); 
		convo.getAccessOf(new Uuid(10)); 
		assertEquals(UserType.MEMBER, convo.getAccessOf(new Uuid(10)));
    }
    
    @Test
    public void TestToString() {
        assertEquals("Creator", UserType.CREATOR.toString()); 
		assertEquals("Member", UserType.MEMBER.toString()); 
		assertEquals("Owner", UserType.OWNER.toString());
    }

}
