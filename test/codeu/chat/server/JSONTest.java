package codeu.chat.server;
import static org.junit.Assert.*;

import codeu.chat.common.ConversationHeader;
import codeu.chat.common.ConversationPayload;
import codeu.chat.common.Message;
import codeu.chat.common.ServerInfo;
import codeu.chat.common.User;
import codeu.chat.util.Uuid;
import org.junit.Ignore;
import org.junit.Test;
import static org.junit.Assert.*;
import java.io.IOException;
import codeu.chat.util.store.Store;
import java.lang.Iterable;
import java.util.Iterator;
import codeu.chat.util.Time;

/**
 * This class tests the JSON class in the server folder
 * The purpose of this test is to ensure that the user is able to
 * access the transaction log and that the transaction log is
 * being saved properly.
 */
public class JSONTest {

    @Test
    public void eachObjectTest() {
        // This function tests if the JSON reader is working if there is a JSON with one type of each Object in the file
        Model model = new Model();
        JSON log = new JSON();
        Model expected = new Model();
        expected.add(new User(createUuid("1.1074501217"), "Krager", Time.fromMs(4309509)));
        expected.add(new ConversationHeader(createUuid("1.3959833157"), createUuid("1.1074501217"),  Time.fromMs(59408509), "Chat1"), new ConversationPayload( createUuid("1.3959833157"), createUuid("1.3350517446"), createUuid("1.1516759239")));
        expected.add(new Message(createUuid("1.3928689216"), createUuid("1.1516759239"), createUuid("0"), Time.fromMs(49584908), createUuid("1.1074501217"),   "I'm so alone"));
        try {
            // Path used by Priyanka:
            model = log.readFromFile("/Users/HMCLoaner/Desktop/CodeU/test/codeu/chat/server/input.json");
            // The path can vary from device, so this may not compile if it's not the correct path for your project
            // set up. A suggested path is as follows
            // model = log.readFromFile("CodeU/test/codeu/chat/server/input.json");
        } catch (IOException e) {
            System.out.println("File not handled properly");
        }
        compareModels(model,expected);
    }

    @Test
    public void oneUserTest() {
        // This function tests if the JSON reader is working if there is a JSON with one type of User in the file
        Model model = new Model();
        JSON log = new JSON();
        Model expected = new Model();
        expected.add(new User(createUuid("1.1074501217"), "Krager", Time.fromMs(12345)));
        try {
            // Path used by Priyanka:
            model = log.readFromFile("/Users/HMCLoaner/Desktop/CodeU/test/codeu/chat/server/input2.json");
            // The path can vary from device, so this may not compile if it's not the correct path for your project
            // set up. A suggested path is as follows
            //model = log.readFromFile("CodeU/test/codeu/chat/server/input2.json");
        }
            catch (IOException e) {
            System.out.println("File not handled properly");
        }
        compareModels(model,expected);
    }

    @Test
    public void emptyFileTest() {
        // This function tests if the JSON reader is working if there is a JSON with no data in it
        JSON log = new JSON();
        Model model = null;
        Model expected = new Model();
        try {
            // Path used by Priyanka:
            model = log.readFromFile("/Users/HMCLoaner/Desktop/CodeU/test/codeu/chat/server/input3.json");
            // The path can vary from device, so this may not compile if it's not the correct path for your project
            // set up. A suggested path is as follows
            //model = log.readFromFile("CodeU/test/codeu/chat/server/input3.json");
        } catch (IOException e) {
            System.out.println("File not handled properly");
        }
        compareModels(model,expected);
    }

    @Test
    public void multipleObjectTest() {
        // This function tests if the JSON reader is working if there is a JSON with multiple types of each object in the file
        JSON log = new JSON();
        Model model = new Model();
        Model expected = new Model();
        expected.add(new User(createUuid("1.1074501217"), "Krager", Time.fromMs(4098590)));
        expected.add(new User(createUuid("1.3566231147"), "FakeUserName1234", Time.fromMs(48574)));
        expected.add(new ConversationHeader(createUuid("1.3959833157"), createUuid("1.1074501217"),  Time.fromMs(98094), "Chat1"), new ConversationPayload( createUuid("1.3959833157"), createUuid("1.3350517446"), createUuid("1.1516759239")));
        expected.add(new ConversationHeader(createUuid("1.3959833157"), createUuid("1.1074501217"),  Time.fromMs(59485), "Chat2"), new ConversationPayload( createUuid("1.3959833157"), createUuid("1.3350517446"), createUuid("1.1516759239")));
        expected.add(new Message(createUuid("1.3928689216"), createUuid("1.1516759239"), createUuid("0"), Time.fromMs(49589), createUuid("1.1074501217"),   "I'm so alone"));
        expected.add(new Message(createUuid("1.2016074193"), createUuid("1.1516759239"), createUuid("0"), Time.fromMs(9485), createUuid("1.1074501217"),   "I need friends"));
        expected.add(new Message(createUuid("1.384992272"), createUuid("1.1516759239"), createUuid("0"), Time.fromMs(5098), createUuid("1.1074501217"),   "Julia'sFixesAreNotImplementedYetCanYouTell?"));

        try {
            // Path used by Priyanka:
            model = log.readFromFile("/Users/HMCLoaner/Desktop/CodeU/test/codeu/chat/server/input4.json");
            // The path can vary from device, so this may not compile if it's not the correct path for your project
            // set up. A suggested path is as follows
            //model = log.readFromFile("CodeU/test/codeu/chat/server/input4.json");
        } catch (IOException e) {
            System.out.println("File not handled properly");
        }
        compareModels(model,expected);
    }

    @Test
    public void noFile() {
        // When there isn't a file with the inputted name
        // an empty model is made
        JSON log = new JSON();
        Model model = null;
        Model expected = new Model();
        try {
            // Path used by Priyanka:
            model = log.readFromFile("/Users/HMCLoaner/Desktop/CodeU/test/codeu/chat/server/noFile.json");
            // The path can vary from device, so this may not compile if it's not the correct path for your project
            // set up. A suggested path is as follows
            //model = log.readFromFile("CodeU/test/codeu/chat/server/input3.json");
        } catch (IOException e) {
            System.out.println("File not handled properly");
        }
        compareModels(model,expected);
    }

    @Test
    public void objectsUnmatched() {
        // This function tests if the JSON reader is working if there is a JSON with one type of User in the file
        // However, it's being compared to a different user
        Model model = new Model();
        JSON log = new JSON();
        Model expected = new Model();
        expected.add(new User(createUuid("1.1"), "Krager", Time.fromMs(12345)));
        try {
            // Path used by Priyanka:
            model = log.readFromFile("/Users/HMCLoaner/Desktop/CodeU/test/codeu/chat/server/input2.json");
            // The path can vary from device, so this may not compile if it's not the correct path for your project
            // set up. A suggested path is as follows
            //model = log.readFromFile("CodeU/test/codeu/chat/server/input2.json");
        } catch (IOException e) {
            System.out.println("File not handled properly");
        }
        assertEquals(
                compare(model.userById().all().iterator(), expected.userById().all().iterator()), false);
        assertEquals(
                compare(model.userByText().all().iterator(), expected.userByText().all().iterator()), false);
        assertEquals(
                compare(model.userByTime().all().iterator(), expected.userByTime().all().iterator()), false);
    }

    @Test
    public void errorThrown() {
        // This function tests is an error is thrown properly if the file can't be properly read
        JSON log = new JSON();
        boolean errorThrown = false;
        try {
            // Path used by Priyanka:
            Model model = log.readFromFile("/Users/HMCLoaner/Desktop/CodeU/test/codeu/chat/server/input5.json");
            // The path can vary from device, so this may not compile if it's not the correct path for your project
            // set up. A suggested path is as follows
            //model = log.readFromFile("CodeU/test/codeu/chat/server/input2.json");
        }
        catch (IOException e){
            errorThrown = true;
        }
        assertTrue(errorThrown);
    }

    @Test
    public void orderDoesntMatter() {
        // This function tests if the JSON reader is working if there is a JSON with multiple types of each object in the file
        // This also has the fields in a variety of orders to make sure that the order doesn't affect it.
        JSON log = new JSON();
        Model model = new Model();
        Model expected = new Model();
        expected.add(new User(createUuid("1.1074501217"), "Krager", Time.fromMs(4098590)));
        expected.add(new User(createUuid("1.3566231147"), "FakeUserName1234", Time.fromMs(48574)));
        expected.add(new ConversationHeader(createUuid("1.3959833157"), createUuid("1.1074501217"),  Time.fromMs(98094), "Chat1"), new ConversationPayload( createUuid("1.3959833157"), createUuid("1.3350517446"), createUuid("1.1516759239")));
        expected.add(new ConversationHeader(createUuid("1.3959833157"), createUuid("1.1074501217"),  Time.fromMs(59485), "Chat2"), new ConversationPayload( createUuid("1.3959833157"), createUuid("1.3350517446"), createUuid("1.1516759239")));
        expected.add(new Message(createUuid("1.3928689216"), createUuid("1.1516759239"), createUuid("0"), Time.fromMs(49589), createUuid("1.1074501217"),   "I'm so alone"));
        expected.add(new Message(createUuid("1.2016074193"), createUuid("1.1516759239"), createUuid("0"), Time.fromMs(9485), createUuid("1.1074501217"),   "I need friends"));
        expected.add(new Message(createUuid("1.384992272"), createUuid("1.1516759239"), createUuid("0"), Time.fromMs(5098), createUuid("1.1074501217"),   "Julia'sFixesAreNotImplementedYetCanYouTell?"));

        try {
            // Path used by Priyanka:
            model = log.readFromFile("/Users/HMCLoaner/Desktop/CodeU/test/codeu/chat/server/input6.json");
            // The path can vary from device, so this may not compile if it's not the correct path for your project
            // set up. A suggested path is as follows
            //model = log.readFromFile("CodeU/test/codeu/chat/server/input4.json");
        } catch (IOException e) {
            System.out.println("File not handled properly");
        }
        compareModels(model,expected);
    }

    public Uuid createUuid(String id)
    {
        // This function is used to create a Uuid from a string
        // It was created since this code would be used multiple times
        Uuid toReturn = null;
        try {
            toReturn = Uuid.parse(id);
        } catch (IOException e) {
            System.out.println("Invalid Uuid");
        }
        return toReturn;
    }
    public boolean compare(Iterator iterator, Iterator desIterator) {
        // This function is used to check if the various items are the same
        // in value. They call the respective Object types equal functions
        boolean same = true;
        while (iterator.hasNext()) {
           if (!iterator.next().equals(desIterator.next())) {
                return false;
            }
        }
        return same;
    }

    public void compareModels(Model model, Model expected)
    {
        assertEquals(
                compare(model.userById().all().iterator(), expected.userById().all().iterator()), true);
        assertEquals(
                compare(model.userByText().all().iterator(), expected.userByText().all().iterator()), true);
        assertEquals(
                compare(model.userByTime().all().iterator(), expected.userByTime().all().iterator()), true);
        assertEquals(
                compare(model.conversationById().all().iterator(), expected.conversationById().all().iterator()), true);
        assertEquals(
                compare(model.conversationByTime().all().iterator(), expected.conversationByTime().all().iterator()), true);
        assertEquals(
                compare(model.conversationByText().all().iterator(), expected.conversationByText().all().iterator()), true);
        assertEquals(
                compare(model.messageById().all().iterator(), expected.messageById().all().iterator()), true);
        assertEquals(
                compare(model.messageByText().all().iterator(), expected.messageByText().all().iterator()), true);
        assertEquals(
                compare(model.messageByTime().all().iterator(), expected.messageByTime().all().iterator()), true);
    }

}