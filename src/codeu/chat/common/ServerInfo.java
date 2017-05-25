/* Class created by Priyanka Agarwal. It allows the version of the server
to be stored and updated. ServerInfo contains the variable Server_Version
which represents the version, starting with 1.0.0. It can then be updated 
as needed, whenever the server version changes. The toString method allows 
the version to be converted from Uuid to a string so that it can be 
displayed in the Panels, to be use by the Chat.java class.
 */

/* This class is used to hold the information of the server, including
* its version. The version can be set with the Parametrized constructor.
* The default version on the server side is 1.0.0, as is set in the Server class
* in the server folder. This version can be changed, and this is updated in the View class
* accordingly.
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
