package codeu.chat.common;

import com.fasterxml.jackson.annotation.JsonValue;

import codeu.chat.util.Uuid;

public abstract class Bot {
    // This class is used for allowing bots with similar functionality to be added
    // to the chat system
    public String reactTo(String text, Uuid sender)
    {
        // This function will allow each bot to react accordingly
        // for whatever the purpose of the bot is
        // It isn't abstact in this class because controller
        // is making a call to it, however each bot class will override it
        // to allow the bot to react accordingly. This will return the
        // string that the bot should put into the chat window
        return null;
    }
    public String onAdd() {
        // This function is called when the bot is first added into the
        // Conversation. It returns the string which should go into the terminal
        // It is not abstract because the controllor makes a call to it.
        return null;
    }
    
    @JsonValue
    public String getName()
    {
        // This function will return the class name of the
        // bot in the form of a string. It's used because Model
        // stores the list of bots in the form of a string
        return this.getClass().getName();
    }



}
