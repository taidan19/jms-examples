package net.cmwolfe;

import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

/**
 * A modified version of the QBorrower application from Chapter 2 of
 * "Java Messsaging Service" 2nd edition, by Richards et al.
 *
 * This version includes the following changes:
 *
 *  - Additional comments to clarify what is happening.
 *  - Code has been simplified and cleaned up in some places.
 *  - Adds some extra whitespace between sections to make it easier to focus on
 *    what's going on in each step of the process.
 *  - Application has been modified so that it can still run if no
 *    arguments are applied. This makes it easier to run in an IDE or even
 *    on the command line.
 */
public class QBorrower {

    private QueueConnection qConnect = null;
    private QueueSession qSession = null;
    private Queue responseQ = null;
    private Queue requestQ = null;

    public QBorrower(String queuecf, String requestQueue,
                     String responseQueue) {

        try {

            // Unlike in the Chat application from Chapter 2, this class
            // demonstrates that you can assign an InitialContext to an object
            // of type `Context`.
            Context ctx = new InitialContext();




            // The InitialContext is used to look up a Queue(rather than
            // Topic) Connection Factory.
            QueueConnectionFactory qFactory =
                    (QueueConnectionFactory) ctx.lookup(queuecf);




            // Again, we create a Queue Connection rather than a Topic Conn.
            qConnect = qFactory.createQueueConnection();






            // Create a single Queue Session object. Notice that the first
            // param is "false"; this means the Session will not use
            // Transactions.
            qSession =
                qConnect.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);





            // Lookup the request and response queues.
            requestQ = (Queue) ctx.lookup(requestQueue);
            responseQ = (Queue) ctx.lookup(responseQueue);




            // Now that setup is complete, start the Connection.
            qConnect.start();
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    private void sendLoanRequest(double salary, double loanAmt) {

        try {

            // Create JMS message
            MapMessage msg = qSession.createMapMessage();
            msg.setDouble("Salary", salary);
            msg.setDouble("LoanAmount", loanAmt);
            msg.setJMSReplyTo(responseQ);

            // Create the sender and send the message
            QueueSender qSender = qSession.createSender(requestQ);
            qSender.send(msg);

            // Wait to see if the loan request was accepted or declined
            String filter =
                    "JMSCorrelationId = '" + msg.getJMSMessageID() + "'";

            QueueReceiver qReceiver =
                    qSession.createReceiver(responseQ, filter);

            TextMessage tmsg = (TextMessage) qReceiver.receive(30000);

            if(tmsg == null) {
                System.out.println("QLender not responding");
            }
            else {
                System.out.println("Loan request was " + tmsg.getText());
            }
        }
        catch(JMSException jmse) {

            jmse.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Closes the JMS connection and exits the program.
     */
    private void exit() {

        try {
            qConnect.close();
        }
        catch(JMSException jmse) {
            jmse.printStackTrace();
        }
        System.exit(0);
    }

    /**
      * Runs the QBorrower class. Before running this, you should either launch
      * a standalone version of ActiveMQ, or run the ActiveMQEmbeddedBroker
      * class.
      */
    public static void main(String[] args) {

        String queuecf = null;
        String requestq = null;
        String responseq = null;

        /*
         * The original version of this program required exactly three command
         * line arguments to run, and exited gracefully if not provided. I have
         * modified it to instead use defaults pulled from the jndi.properties
         * file.
         */
        if(args.length == 3) {
            queuecf = args[0];
            requestq = args[1];
            responseq = args[2];
        }
        else {
          System.out.println("Invalid arguments. Using defaults of:\n" +
                             "ConnectionFactory: QueueCF,\n" +
                             "Request Queue: LoanRequestQ,\n" +
                             "Response Queue: LoanResponseQ");

            queuecf = "QueueCF";
            requestq = "LoanRequestQ";
            responseq = "LoanResponseQ";
        }




        // Create the Borrower object using the args from above.
        QBorrower borrower = new QBorrower(queuecf, requestq, responseq);




        // Read all standard input and send it as a message
        try {
            BufferedReader stdin = new BufferedReader(
                    new InputStreamReader(System.in));
            System.out.println("QBorrower Application Started");
            System.out.println("Press enter to quit application");
            System.out.println("Enter: Salary, Loan Amount");
            System.out.println("\ne.g. 50000, 120000");

            while(true) {

                System.out.print("> ");
                String loanRequest = stdin.readLine();

                if(loanRequest == null || loanRequest.trim().length() <= 0) {
                    borrower.exit();
                }

                // Parse the deal description
                StringTokenizer st = new StringTokenizer(loanRequest, ",");

                double salary =
                    Double.valueOf(st.nextToken().trim()).doubleValue();
                double loanAmt =
                    Double.valueOf(st.nextToken().trim()).doubleValue();

                borrower.sendLoanRequest(salary, loanAmt);
            }
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }
}
