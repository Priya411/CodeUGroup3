package codeu.chat.common;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import codeu.chat.util.Uuid;

import java.io.IOException;

public class ServerInfoTest {

    @Test
    public void testServerInfo() {
        ServerInfo server1 = new ServerInfo();
        assertEquals("1.0.0", server1.toString());
        try{
            ServerInfo server2 = new ServerInfo(Uuid.parse("1.0.1"));
            assertEquals("1.0.1", server2.toString());
        }
        catch (IOException e){
            System.out.println("Invalid input");
        }
    }
}