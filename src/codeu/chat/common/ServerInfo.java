
public final class ServerInfo {
	
	//Time for UpTime function which gives the time the server started up 
	public final Time startTime;
	
	public ServerInfo() {
		this.startTime = Time.now();
	}
	
	public ServerInfo(Time startTime) {
		this.startTime = startTime;
	}
	
}
