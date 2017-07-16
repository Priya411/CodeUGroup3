package codeu.chat.common;

public enum UserType {
	MEMBER("Member"), 
	OWNER("Owner"), 
	CREATOR("Creator");
	
    private final String textRepresentation;

    private UserType(String textRepresentation) {
        this.textRepresentation = textRepresentation;
    }

    @Override public String toString() {
         return textRepresentation;
    }
}