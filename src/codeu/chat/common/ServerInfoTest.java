package codeu.chat.common;
import static org.junit.Assert.*;
import org.junit.Test;
import codeu.chat.util.Uuid;

import java.io.IOException;

public class ServerInfoTest {
    boolean errorThrown = false;
    @Test
    public void testServerInfo() {
        try{
            // Server version should just take out negative sign
            ServerInfo server = new ServerInfo(Uuid.parse("-1.0.1"));
            assertEquals("1.0.1", server.toString());
        }
        catch (IOException e){
            System.out.println("Invalid input");
        }
        try{
            ServerInfo server2 = new ServerInfo(Uuid.parse("1.0.1"));
            assertEquals("1.0.1", server2.toString());
        }
        catch (IOException e){
            System.out.println("Invalid input");
        }
        try {
            // Exception only caught when version is larger than 32 bits
            ServerInfo server3 = new ServerInfo(Uuid.parse("1.999999999999999999"));
            fail(); // if we got here, no exception was thrown, which is bad
        }
        catch (IOException e) {
            errorThrown = true;
        }
        assertTrue(errorThrown);
        errorThrown = false;

        try {
            // Exception only caught when version is larger than 32 bits
            // Testing that it doesn't matter that the equal sign is there
            ServerInfo server4 = new ServerInfo(Uuid.parse("-1.999999999999999999"));
            fail(); // if we got here, no exception was thrown, which is bad
        }
        catch (IOException e) {
            errorThrown = true;
        }
        assertTrue(errorThrown);

    }
}