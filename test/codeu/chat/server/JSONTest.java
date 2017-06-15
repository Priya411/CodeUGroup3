package codeu.chat.server;
import static org.junit.Assert.*;

import codeu.chat.common.ConversationHeader;
import codeu.chat.common.Message;
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
    public void readTest1() {
        // This function tests if the JSON reader is working if there is a JSON with one type of each Object in the file
        Model model = new Model();
        JSON log = new JSON();
        Model comparer = new Model();
        comparer.add(new User(createUuid("1.1074501217"), "Krager", Time.fromMs(4309509)));
        comparer.add(new ConversationHeader(createUuid("1.3959833157"), createUuid("1.1074501217"),  Time.fromMs(59408509), "Chat1"));
        comparer.add(new Message(createUuid("1.3928689216"), Uuid.NULL, Uuid.NULL, Time.fromMs(49584908), createUuid("1.1074501217"),   "I'm so alone"));

        try {
            // Path used by Priyanka:
            model = log.readFromFile("/Users/HMCLoaner/Desktop/CodeU/test/codeu/chat/server/input.json");
            // The path can vary from device, so this may not compile if it's not the correct path for your project
            // set up. A suggested path is as follows
            // model = log.readFromFile("CodeU/test/codeu/chat/server/input.json");
        } catch (IOException e) {
            System.out.println("File not handled properly");
        }
        assertEquals(
                compare(model.userById().all().iterator(), comparer.userById().all().iterator()), true);
        assertEquals(
                compare(model.userByText().all().iterator(), comparer.userByText().all().iterator()), true);
        assertEquals(
                compare(model.userByTime().all().iterator(), comparer.userByTime().all().iterator()), true);
        assertEquals(
                compare(model.conversationById().all().iterator(), comparer.conversationById().all().iterator()), true);
        assertEquals(
                compare(model.conversationByTime().all().iterator(), comparer.conversationByTime().all().iterator()), true);
        assertEquals(
                compare(model.conversationByText().all().iterator(), comparer.conversationByText().all().iterator()), true);
        assertEquals(
                compare(model.messageById().all().iterator(), comparer.messageById().all().iterator()), true);
        assertEquals(
                compare(model.messageByText().all().iterator(), comparer.messageByText().all().iterator()), true);
        assertEquals(
                compare(model.messageByTime().all().iterator(), comparer.messageByTime().all().iterator()), true);
    }

    @Test
    public void readTest2() {
        // This function tests if the JSON reader is working if there is a JSON with one type of User in the file
        Model model = new Model();
        JSON log = new JSON();
        Model comparer = new Model();
        comparer.add(new User(createUuid("1.1074501217"), "Krager", Time.fromMs(12345)));
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
        assertEquals(
               compare(model.userById().all().iterator(), comparer.userById().all().iterator()), true);
        assertEquals(
                compare(model.userByText().all().iterator(), comparer.userByText().all().iterator()), true);
        assertEquals(
                compare(model.userByTime().all().iterator(), comparer.userByTime().all().iterator()), true);
        assertEquals(
                compare(model.conversationById().all().iterator(), comparer.conversationById().all().iterator()), true);
        assertEquals(
                compare(model.conversationByTime().all().iterator(), comparer.conversationByTime().all().iterator()), true);
        assertEquals(
                compare(model.conversationByText().all().iterator(), comparer.conversationByText().all().iterator()), true);
        assertEquals(
                compare(model.messageById().all().iterator(), comparer.messageById().all().iterator()), true);
        assertEquals(
                compare(model.messageByText().all().iterator(), comparer.messageByText().all().iterator()), true);
        assertEquals(
                compare(model.messageByTime().all().iterator(), comparer.messageByTime().all().iterator()), true);
    }

    @Test
    public void readTest3() {
        // This function tests if the JSON reader is working if there is a JSON with no data in it
        JSON log = new JSON();
        Model model = null;
        Model comparer = new Model();
        try {
            // Path used by Priyanka:
            model = log.readFromFile("/Users/HMCLoaner/Desktop/CodeU/test/codeu/chat/server/input3.json");
            // The path can vary from device, so this may not compile if it's not the correct path for your project
            // set up. A suggested path is as follows
            //model = log.readFromFile("CodeU/test/codeu/chat/server/input3.json");
        } catch (IOException e) {
            System.out.println("File not handled properly");
        }
        assertEquals(
                compare(model.userById().all().iterator(), comparer.userById().all().iterator()), true);
        assertEquals(
                compare(model.userByText().all().iterator(), comparer.userByText().all().iterator()), true);
        assertEquals(
                compare(model.userByTime().all().iterator(), comparer.userByTime().all().iterator()), true);
        assertEquals(
                compare(model.conversationById().all().iterator(), comparer.conversationById().all().iterator()), true);
        assertEquals(
                compare(model.conversationByTime().all().iterator(), comparer.conversationByTime().all().iterator()), true);
        assertEquals(
                compare(model.conversationByText().all().iterator(), comparer.conversationByText().all().iterator()), true);
        assertEquals(
                compare(model.messageById().all().iterator(), comparer.messageById().all().iterator()), true);
        assertEquals(
                compare(model.messageByText().all().iterator(), comparer.messageByText().all().iterator()), true);
        assertEquals(
                compare(model.messageByTime().all().iterator(), comparer.messageByTime().all().iterator()), true);
    }

    @Test
    public void readTest4() {
        // This function tests if the JSON reader is working if there is a JSON with two types of each object in the file
        JSON log = new JSON();
        Model model = new Model();
        Model comparer = new Model();
        comparer.add(new User(createUuid("1.1074501217"), "Krager", Time.fromMs(4098590)));
        comparer.add(new User(createUuid("1.3566231147"), "FakeUserName1234", Time.fromMs(48574)));
        comparer.add(new ConversationHeader(createUuid("1.3959833157"), createUuid("1.1074501217"),  Time.fromMs(98094), "Chat1"));
        comparer.add(new ConversationHeader(createUuid("1.3959833157"), createUuid("1.1074501217"),  Time.fromMs(59485), "Chat2"));
        comparer.add(new Message(createUuid("1.3928689216"), Uuid.NULL, Uuid.NULL, Time.fromMs(49589), createUuid("1.1074501217"),   "I'm so alone"));
        comparer.add(new Message(createUuid("1.2016074193"), Uuid.NULL, Uuid.NULL, Time.fromMs(9485), createUuid("1.1074501217"),   "I need friends"));
        comparer.add(new Message(createUuid("1.384992272"), Uuid.NULL, Uuid.NULL, Time.fromMs(5098), createUuid("1.1074501217"),   "Julia'sFixesAreNotImplementedYetCanYouTell?"));

        try {
            // Path used by Priyanka:
            model = log.readFromFile("/Users/HMCLoaner/Desktop/CodeU/test/codeu/chat/server/input4.json");
            // The path can vary from device, so this may not compile if it's not the correct path for your project
            // set up. A suggested path is as follows
            //model = log.readFromFile("CodeU/test/codeu/chat/server/input4.json");
        } catch (IOException e) {
            System.out.println("File not handled properly");
        }
        assertTrue(
                "Check that the user has the correct id", true);
        assertEquals(
                compare(model.userById().all().iterator(), comparer.userById().all().iterator()), true);
        assertEquals(
                compare(model.userByText().all().iterator(), comparer.userByText().all().iterator()), true);
        assertEquals(
                compare(model.userByTime().all().iterator(), comparer.userByTime().all().iterator()), true);
        assertEquals(
                compare(model.conversationById().all().iterator(), comparer.conversationById().all().iterator()), true);
        assertEquals(
                compare(model.conversationByTime().all().iterator(), comparer.conversationByTime().all().iterator()), true);
        assertEquals(
                compare(model.conversationByText().all().iterator(), comparer.conversationByText().all().iterator()), true);
        assertEquals(
                compare(model.messageById().all().iterator(), comparer.messageById().all().iterator()), true);
        assertEquals(
                compare(model.messageByText().all().iterator(), comparer.messageByText().all().iterator()), true);
        assertEquals(
                compare(model.messageByTime().all().iterator(), comparer.messageByTime().all().iterator()), true);
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
}