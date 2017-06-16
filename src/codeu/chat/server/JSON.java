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
            // data.json is the predetermined folder in which
            // the transaction log will go, located in the Server directory
            // The path may need to be updated based on the way the local project is set up
            return readFromFile("data.json");
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
            if(jp.getText().equals("users")) {
                // If the object type represents a User, then
                // specific steps will take place to create
                // a User variable
                jp.nextToken();
                while (jp.nextToken() != JsonToken.END_ARRAY) {
                    String name = "";
                    Uuid id = null;
                    Time time = null;
                    jp.nextToken();
                    if (jp.getText().equals("name")) {
                        jp.nextToken();
                        name = jp.getText();
                    }
                    jp.nextToken();
                    if (jp.getText().equals("uuid")) {
                        jp.nextToken();
                        try {
                            id = Uuid.parse(jp.getText());
                        } catch (IOException e) {
                            System.out.println("Invalid Uuid");
                        }
                    }
                    jp.nextToken();
                    if (jp.getText().equals("creationTime")) {
                        jp.nextToken();
                        time = Time.fromMs(jp.getLongValue());
                    }
                    jp.nextToken();
                    if (!(name.equals("")) && time != null && id != null)
                        model.add(new User(id, name, time));
                }
                continue;
            }
            if(jp.getText().equals("conversations"))
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
                    if (jp.getText().equals("title")) {
                        jp.nextToken();
                        title = jp.getText();
                    }
                    jp.nextToken();
                    if (jp.getText().equals("uuid")) {
                        jp.nextToken();
                        try {
                            id = Uuid.parse(jp.getText());
                        } catch (IOException e) {
                            System.out.println("Invalid Uuid");
                        }
                    }
                    jp.nextToken();
                    if (jp.getText().equals("ownerUUID")) {
                        jp.nextToken();
                        try {
                            owner = Uuid.parse(jp.getText());
                        } catch (IOException e) {
                            System.out.println("Invalid Uuid");
                        }
                    }
                    jp.nextToken();
                    if (jp.getText().equals("creationTime")) {
                        jp.nextToken();
                        creation = Time.fromMs(jp.getLongValue());
                    }
                    jp.nextToken();
                    if (!(title.equals("")) && id != null && owner != null && creation != null)
                        model.add(new ConversationHeader(id, owner, creation, title));
                }
                continue;
            }
            if(jp.getText().equals("messages")) {
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
                    if (jp.getText().equals("content")) {
                        jp.nextToken();
                        body = jp.getText();
                    }
                    jp.nextToken();
                    if (jp.getText().equals("uuid")) {
                        jp.nextToken();
                        try {
                            id = Uuid.parse(jp.getText());
                        } catch (IOException e) {
                            System.out.println("Invalid Uuid");
                        }
                    }
                    jp.nextToken();
                    if (jp.getText().equals("authorUUID")) {
                        jp.nextToken();
                        try {
                            owner = Uuid.parse(jp.getText());
                        } catch (IOException e) {
                            System.out.println("Invalid Uuid");
                        }
                    }
                    jp.nextToken();
                    if (jp.getText().equals("creationTime")) {
                        jp.nextToken();
                        creation = Time.fromMs(jp.getLongValue());
                    }
                    jp.nextToken();
                    if (!(body.equals("")) && id != null && owner != null && creation != null)
                        model.add(new Message(id, Uuid.NULL, Uuid.NULL, creation, owner, body));
                }
            }
           }
           jp.close();
       }
        return model;
    }
}
