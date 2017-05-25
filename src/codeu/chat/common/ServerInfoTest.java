package codeu.chat.common;
import static org.junit.Assert.*;
import org.junit.Test;
import codeu.chat.util.Uuid;

import java.io.IOException;

public class ServerInfoTest {
    boolean errorThrown = false;

    @Test
    public void testServerInfo() {
        // Server version should just take out negative sign
        // because it gets converted to unsigned, however
        // an error is still thrown to alert user
        try{
            ServerInfo server = new ServerInfo(Uuid.parse("-1.0.1"));
            assertEquals("1.0.1", server.toString());
        }
        catch (IOException e){
            errorThrown = true;
        }
        assertTrue(errorThrown);
        errorThrown = false;
    }
    
    @Test
    public void testServerInfo2() {
        // This is the general case where no error should be thrown
        // All versions will be in this format if inputted correctly
        try{
            ServerInfo server2 = new ServerInfo(Uuid.parse("1.0.1"));
            assertEquals("1.0.1", server2.toString());
        }
        catch (IOException e){
            fail();
        }
    }

    @Test
    public void testServerInfoError() {
        // Exception only caught when version is larger than 32 bits
        try {
            ServerInfo server3 = new ServerInfo(Uuid.parse("1.999999999999999999"));
            fail(); // if we got here, no exception was thrown, which is bad
        }
        catch (IOException e) {
            errorThrown = true;
        }
        assertTrue(errorThrown);
        errorThrown = false;
    }

    @Test
    public void testServerInfoError2() {
        // Exception only caught when version is larger than 32 bits
        // Testing that it doesn't matter that the equal sign is there
        try {
            ServerInfo server4 = new ServerInfo(Uuid.parse("-1.999999999999999999"));
            fail(); // if we got here, no exception was thrown, which is bad
        }
        catch (IOException e) {
            errorThrown = true;
        }
        assertTrue(errorThrown);

    }
}