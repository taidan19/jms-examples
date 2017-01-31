package net.cmwolfe;

import java.io.*;
import javax.jms.*;
import javax.naming.*;
import java.util.UUID;

/**
 * A modified version of the Chat application from Chapter 2 of
 * "Java Messsaging Service" 2nd edition, by Richards et al.
 *
 * This version includes the following changes:
 *
 *  - Additional comments to clarify what is happening.
 *  - Code has been simplified and cleaned up in some places.
 *  - Adds some extra whitespace between sections to make it easier to focus on
 *    what's going on in each step of the process.
 *  - Chat application has been modified so that it can still run if no
 *    arguments are applied. This makes it easier to run in an IDE or even
 *    on the command line.
 */
public class Chat implements javax.jms.MessageListener{
    private TopicSession pubSession;
    private TopicPublisher publisher;
    private TopicConnection connection;
    private String username;

    /**
      * Constructor used to Initialize the Chat application.
      */
    public Chat(String topicFactory, String topicName, String username)
        throws Exception {




    	  /*
         * The InitialContext object is used to perform JNDI lookups. The
         * contents of this project's jndi.properties file are used to fuel
         * lookup operations.
         *
         * It is worth noting that the InitialContext could also be created by
         * creating a Properties object containing all the key/value pairs
         * from the jndi.properties file, and passing the Properties object
         * into the constructor.
         */
         InitialContext ctx = new InitialContext();





        // Use the InitialContext to look up a JMS Connection Factory.
        TopicConnectionFactory conFactory = (TopicConnectionFactory) ctx.lookup(topicFactory);






        // Use the JMS Connection Factory to create a JMS Topic Connection. We
        // should only have to create one Connection for a given application,
        // as all other JMS objects can be derived from it.
        TopicConnection connection = conFactory.createTopicConnection();





        // Look up a JMS topic via JNDI. This is necessary in order to create a
        // Topic Subscriber later on.
        Topic chatTopic = (Topic) ctx.lookup(topicName);





        /**
         * In the next few lines, we create separate JMS Session objects for
         * the message Publisher and Subscriber. This is necessary in order to
         * prevent these operations from stepping on each other's toes. Two
         * separate Sessions means they'll run in two separate threads.
         */






        // Create the JMS Publisher, first by creating a JMS Topic Session, and
        // then creating the Topic Publisher from the session.
        TopicSession pubSession = connection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
        TopicPublisher publisher = pubSession.createPublisher(chatTopic);





        // Create the JMS Subscriber, first by creating a (second) JMS Topic
        // Session, and then creating the Topic Subscriber from the session.
        TopicSession subSession = connection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
        TopicSubscriber subscriber = subSession.createSubscriber(chatTopic, null, true);





        // The subscriber needs to have a JMS listener to tell it how to handle
        // incoming messages. This class itself is such a listener, so we'll
        // add it.
        subscriber.setMessageListener(this);





        // Intialize the Chat application variables.
        this.connection = connection;
        this.pubSession = pubSession;
        this.publisher = publisher;
        this.username = username;





        // Start the JMS connection; allows messages to be delivered.
        // Note that the book suggests running start() after everything else
        // is set up.
        connection.start();
    }





    /** Receives mMessages from a topic subscriber */
    public void onMessage(Message message) {

        try {
            TextMessage textMessage = (TextMessage) message;
            String text = textMessage.getText();
            System.out.println(text);
        }
        catch (JMSException jmse) {
          jmse.printStackTrace();
        }
    }







    /** Create and send messages using a topic publisher */
    protected void writeMessage(String text) throws JMSException {

        TextMessage message = pubSession.createTextMessage();
        message.setText(username + ": " + text);
        publisher.publish(message);
    }





    /** Closes the JMS Connection */
    public void close() throws JMSException {
        connection.close();
    }





    /**
      * Runs the Chat Client. Before running this, you should either launch
      * a standalone version of ActiveMQ, or run the ActiveMQEmbeddedBroker
      * class.
      */
    public static void main(String [] args){

        try {

            Chat chat;

            /*
             * The original version of this program required three command line
             * arguments to run, though it did notexit gracefully if the args
             * weren't provided. I have modified it to use defaults pulled from
             * the jndi.properties file, along with a random UUID as the user
             * name.
             */
            if (args.length!=3) {
                String uuid = UUID.randomUUID().toString();
                System.out.println("Factory, Topic, or username missing. Using defaults of:\n" +
                                   "TopicCF, topic1, and " + uuid);
                chat = new Chat("TopicCF", "topic1", uuid);
            }
            else {
                chat = new Chat(args[0],args[1],args[2]);
            }

            // Create an input reader to read chat messages from the command
            // line.
            BufferedReader commandLine = new BufferedReader(new InputStreamReader(System.in));

            // Continue to read text from the command line until "exit" is
            // entered by the user.
            while(true){

                String s = commandLine.readLine();

                // Close the connection and exit the program if "exit" is typed.
                if (s.equalsIgnoreCase("exit")) {
                    chat.close();
                    System.exit(0);
                }
                else {
                    chat.writeMessage(s);
                }
            }
        }
        catch(Exception e) {
           e.printStackTrace();
        }
    }
}
