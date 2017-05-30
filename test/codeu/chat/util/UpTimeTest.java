package codeu.chat.util;
import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import codeu.chat.common.ServerInfo; 
import java.io.IOException;
import codeu.chat.util.Time;


public class UpTimeTest {
	Boolean errorThrown = false; 
	
	
	//Tests to see if times are stored correctly in ServerInfo
	@Test
	public void testUpTimeStored() {
		//creates startTime @ a time 67050948
		Time startTime = Time.fromMs(67050948);
		ServerInfo testServer = new ServerInfo(startTime);
		assertEquals(67050948, testServer.getStartTime().inMs());
	}

	
	//tests that covertHMS() works correctly 	
	@Test 
	public void testConvertHMS() { 
		//create a long that in ms that is equivalent to 6H 3M 5S 
		long ms = 1000*5 + 1000*60*3 + 1000*60*60*6; 
		long hours = 6; long mins = 3; long secs = 5; 
		long[] converted = Time.convertHMS(ms); 
		assertEquals(hours, converted[0]); 
		assertEquals(mins, converted[1]);
		assertEquals(secs, converted[2]); 
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
