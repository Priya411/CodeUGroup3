/* Class created by Priyanka Agarwal. It allows the version of the server
to be stored and updated. ServerInfo contains the variable Server_Version
which represents the version, starting with 1.0.0. It can then be updated 
as needed, whenever the server version changes. The toString method allows 
the version to be converted from Uuid to a string so that it can be 
displayed in the Panels, to be use by the Chat.java class.
 */

package codeu.chat.common;
import codeu.chat.util.Uuid;


public final class ServerInfo {

    public Uuid version;

    // This constructor allows for an updated version to be made
    public ServerInfo(Uuid version) {
        this.version = version;
    }

    // This method allows the version to be printed as a string, 
    // to help with displaying the information. 
    public String toString() {
        return version.toString();
    }
}
