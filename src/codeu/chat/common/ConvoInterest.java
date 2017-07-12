package codeu.chat.common;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import codeu.chat.util.Serializer;
import codeu.chat.util.Serializers;
import codeu.chat.util.Uuid;

public class ConvoInterest {
	
	public static final Serializer<ConvoInterest> SERIALIZER = new Serializer<ConvoInterest>() {

	    @Override
	    public void write(OutputStream out, ConvoInterest value) throws IOException {
	      Uuid.SERIALIZER.write(out, value.convoId);
	      Serializers.INTEGER.write(out, value.messagesCount);
	    }

	    @Override
	    public ConvoInterest read(InputStream in) throws IOException {
	      return new ConvoInterest(
	          Uuid.SERIALIZER.read(in),
	          Serializers.INTEGER.read(in));
	    }
	  };

	public Uuid convoId;
	public int messagesCount;
	
	public ConvoInterest(Uuid id, int messagesCount) {
		this.convoId = id;
		this.messagesCount = messagesCount;
	}
}
