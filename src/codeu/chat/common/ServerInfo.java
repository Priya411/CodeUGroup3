/*Class created by Priyanka Agarwal. It allows the version of the server
to be stored and updated. ServerInfo contains the variable Server_Version
which represents the version, starting with 1.0.0. It can then be updated 
as needed. The toString method allows the version to be converted from Uuid
to a string so that it can be displayed in the Panels, to be use by the Chat.java
class.
 */

package codeu.chat.common;
import codeu.chat.util.Uuid;
import java.io.IOException;


public final class ServerInfo {
    private final static String SERVER_VERSION = "1.0.0";

    public Uuid version;
    public ServerInfo() {
        try {
            this.version = Uuid.parse(SERVER_VERSION);
        } catch (IOException e){
            System.out.println("Invalid input");
        }
    }
    public ServerInfo(Uuid version) {
        this.version = version;
    }

    public String toString()
    {
        return version.toString();
    }
}
