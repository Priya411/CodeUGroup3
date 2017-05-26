package codeu.chat.util;
import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import codeu.chat.common.ServerInfo; 
import java.io.IOException;


public class UpTimeTest {
	Boolean errorThrown = false; 
	//Tests to see if times are stored correctly in ServerInfo
  @Test
  public void testUpTime() {
	  //creates startTime @ a time 5000ms 
	Time startTime = Time.fromMs(5000);
    ServerInfo testServer = new ServerInfo(startTime);
    assertEquals(5000, testServer.getStartTime().inMs());
  }
  
  @Test
  public void testUpTime2() {
	  //creates startTime @ a time 67050948
	Time startTime = Time.fromMs(67050948);
    ServerInfo testServer = new ServerInfo(startTime);
    assertEquals(67050948, testServer.getStartTime().inMs());
  }
  
  //makes sure that only longs are accepted; error should be thrown 
  @Test 
  public void testUpTimeError() { 
	  try{
		  Long l1 = Long.parseUnsignedLong("17916881237904312345");
		  Time startTime = Time.fromMs(l1);
		  ServerInfo server = new ServerInfo(startTime); 
		  fail(); 
	  }
	  catch (Error e) {
		  errorThrown = true;
	  }
	  assertTrue(errorThrown);
  }
  
}
