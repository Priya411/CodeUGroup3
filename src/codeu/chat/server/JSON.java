package codeu.chat.server;
import codeu.chat.common.ConversationHeader;
import codeu.chat.common.Message;
import codeu.chat.common.User;

import java.io.FileReader;
import java.io.IOException;
import java.util.UUID;

import codeu.chat.util.Time;
import codeu.chat.util.Uuid;
import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.*;

/**
 * This file is to be used to allow the server to get a log of all actions completed
 * This class saves all inputted commands
 * It creates a new command so that the user can then access the log
 * This log can be used by the server to re-load all commands from previous
 * uses of the server, and to save all users and conversations on the server
 */


public final class JSON {

    public Model createModelForServer()
    {
        // This function is to be used by the Server class
        // If the file, input.json, which holds the transactions
        // and commands used into the server at a previous time,
        // exists, this means that the server had been used before.
        // This will then call readFromFile using that previous
        // log, allowing the sever to access all previous transactions
        // If the server hasn't been used before, then there have
        // been no transactions and therefore there has not yet been a
        // file created, causing the IOException to be thrown.
        // Other times when this exception can be thrown
        // are when the transactions haven't been converted to JSON
        // properly. In any of these cases, a new Model instance will be
        // created, essentially starting the server without any memory of
        // previous transactions/commands.
        try
        {
            // input.json is the predetermined folder in which
            // the transaction log will go, located in the Server directory
            return readFromFile("input.json");
        }
        catch (IOException e)
        {
            return new Model();
        }
    }

    public Model readFromFile (String file) throws IOException
    {
        Model model = new Model();
        JsonFactory jsonF = new JsonFactory();
        JsonParser jp = null;
        try
        {
            jp = jsonF.createParser(new FileReader(file));
        }
        catch(IOException e)
        {
            // If the file is invalid or can't be parsed correctly
            // a version of Model without any transaction logs in
            // will be used and returned.
            System.out.println("Invalid file");
        }
       if (jp!=null) {
            // If the file exists, then the file will be parsed
           // in this set of code
           jp.nextToken();
            while (jp.nextToken() != JsonToken.END_OBJECT)
            // This ensures that we haven't reached the end of the
            // file because the data is being parsed in such a way
            // that this END_OBJECT specifically relates to the
            // very last } in the json file
            {
            if(jp.getText()=="users") {
                // If the object type represents a User, then
                // specific steps will take place to create
                // a User variable
                jp.nextToken();
                while (jp.nextToken() != JsonToken.END_ARRAY) {
                    String name = "";
                    Uuid id = null;
                    Time time = null;
                    jp.nextToken();
                    if (jp.getText() == "name") {
                        jp.nextToken();
                        name = jp.getText();
                    }
                    jp.nextToken();
                    if (jp.getText() == "uuid") {
                        jp.nextToken();
                        try {
                            id = Uuid.parse(jp.getText());
                        } catch (IOException e) {
                            System.out.println("Invalid Uuid");
                        }
                    }
                    jp.nextToken();
                    if (jp.getText() == "creationTime") {
                        jp.nextToken();
                        time = Time.fromMs(jp.getLongValue());
                    }
                    jp.nextToken();
                    System.out.println(name);
                    System.out.println(id);
                    System.out.println(time);
                    if (name != "" && time != null && id != null)
                        model.add(new User(id, name, time));
                }
                continue;
            }
            if(jp.getText()=="conversations")
            {
                // If the object type represents a Conversation, then
                // specific steps will take place to create
                // a ConversationHeader variable
                jp.nextToken();
                while(jp.nextToken()!=JsonToken.END_ARRAY) {
                    Uuid id = null;
                    Uuid owner = null;
                    Time creation = null;
                    String title = "";
                    jp.nextToken();
                    if (jp.getText() == "title") {
                        jp.nextToken();
                        title = jp.getText();
                    }
                    jp.nextToken();
                    if (jp.getText() == "uuid") {
                        jp.nextToken();
                        try {
                            id = Uuid.parse(jp.getText());
                        } catch (IOException e) {
                            System.out.println("Invalid Uuid");
                        }
                    }
                    jp.nextToken();
                    if (jp.getText() == "ownerUUID") {
                        jp.nextToken();
                        try {
                            owner = Uuid.parse(jp.getText());
                        } catch (IOException e) {
                            System.out.println("Invalid Uuid");
                        }
                    }
                    jp.nextToken();
                    if (jp.getText() == "creationTime") {
                        jp.nextToken();
                        creation = Time.fromMs(jp.getLongValue());
                    }
                    jp.nextToken();
                    System.out.println(title);
                    System.out.println(id);
                    System.out.println(owner);
                    System.out.println(creation);
                    if (title != "" && id != null && owner != null && creation != null)
                        model.add(new ConversationHeader(id, owner, creation, title));
                }
                continue;
            }
            if(jp.getText()== "messages") {
                // If the object type represents a Message, then
                // specific steps will take place to create
                // a Message variable
                jp.nextToken();
                while (jp.nextToken() != JsonToken.END_ARRAY) {
                    Uuid id = null;
                    Uuid owner = null;
                    Time creation = null;
                    String body = "";
                    jp.nextToken();
                    if (jp.getText() == "content") {
                        jp.nextToken();
                        body = jp.getText();
                    }
                    jp.nextToken();
                    if (jp.getText() == "uuid") {
                        jp.nextToken();
                        try {
                            id = Uuid.parse(jp.getText());
                        } catch (IOException e) {
                            System.out.println("Invalid Uuid");
                        }
                    }
                    jp.nextToken();
                    if (jp.getText() == "authorUUID") {
                        jp.nextToken();
                        try {
                            owner = Uuid.parse(jp.getText());
                        } catch (IOException e) {
                            System.out.println("Invalid Uuid");
                        }
                    }
                    jp.nextToken();
                    if (jp.getText() == "creationTime") {
                        jp.nextToken();
                        creation = Time.fromMs(jp.getLongValue());
                    }
                    jp.nextToken();
                    System.out.println(body);
                    System.out.println(id);
                    System.out.println(owner);
                    System.out.println(creation);
                    if (body != "" && id != null && owner != null && creation != null)
                        model.add(new Message(id, Uuid.NULL, Uuid.NULL, creation, owner, body));
                }
            }
           }
           jp.close();
       }
        return model;
    }
}
