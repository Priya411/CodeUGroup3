package codeu.chat.common;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import codeu.chat.util.Serializer;
import codeu.chat.util.Serializers;

public enum UserType {
	
	MEMBER, 
	OWNER, 
	CREATOR;
	
	public static final Serializer<UserType> SERIALIZER = new Serializer<UserType>() {
	    @Override
	    public void write(OutputStream out, UserType value) throws IOException {
	      Serializers.STRING.write(out, value.name());
	    }

	    @Override
	    public UserType read(InputStream in) throws IOException {
	      return UserType.valueOf(Serializers.STRING.read(in));
	    }
	};
}
