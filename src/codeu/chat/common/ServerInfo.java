package codeu.chat.common;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import codeu.chat.util.Serializer;
import codeu.chat.util.Serializers;
import codeu.chat.util.Time; 



public final class ServerInfo {

	//Time for Up Time function which gives the time the server started
	public final Time startTime;

	public ServerInfo() {
		this.startTime = Time.now();
	}

	public ServerInfo(Time startTime) {
		this.startTime = startTime;
	}

	public Time getStartTime() { 
		return this.startTime; 
	}

}
