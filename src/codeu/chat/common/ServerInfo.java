
/* This class allows the version of the server
to be stored and updated. ServerInfo contains the variable Server_Version
which represents the version. It can be updated as needed, whenever the
server version changes by using the constructor. The toString method allows
the version to be converted from Uuid to a string so that it can be 
displayed in the Panels, to be use by the Chat.java class.
 */

/* This class is used to hold the information of the server, including
* its version. The version can be set with the Parametrized constructor.
* The version on the server side is set in the Server class in the server
* folder. This version can be changed, and this is updated in the View class
* accordingly.
 */

package codeu.chat.common;
import codeu.chat.util.Uuid;
  
import codeu.chat.util.Serializer;
import codeu.chat.util.Serializers;
import codeu.chat.util.Time; 


public class ServerInfo {

    private Uuid version;
  	public final Time startTime;

    // This constructor is the base case constructor as the version always must be initialized
    public ServerInfo(Uuid version) {
        this.version = version;
        this.startTime = Time.now();
    }
    
    // This constuctor is for testing upTime only
    //There will never be a instance where the version is null 
    public ServerInfo(Time startTime) {
      this.version = null;
      this.startTime = startTime;
    }    
  
    // This method allows the version to be printed as a string, 
    // to help with displaying the information. 
    public String toString() {
        return version.toString();
    }

    public Uuid getVersion() { 
        return version;
    }

    public Time getStartTime() { 
      return this.startTime; 
    }

}
