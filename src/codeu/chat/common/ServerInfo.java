/*Class created by Priyanka Agarwal as per code written
on codeU directions. Only added package and import statements
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
}