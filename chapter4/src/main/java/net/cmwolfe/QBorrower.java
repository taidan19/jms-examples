package net.cmwolfe;

import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

/**
 * Created by christianwolfe on 1/30/17.
 */
public class QBorrower {

    private QueueConnection qConnect = null;
    private QueueSession qSession = null;
    private Queue responseQ = null;
    private Queue requestQ = null;


    public QBorrower(String queuecf, String requestQueue,
                     String responseQueue) {

        try {

            // Connect to the provider and get the JMS connection
            Context ctx = new InitialContext();
            QueueConnectionFactory qFactory =
                    (QueueConnectionFactory) ctx.lookup(queuecf);
            qConnect = qFactory.createQueueConnection();

            // Create the JMS Session
            qSession = qConnect.createQueueSession(
                false, Session.AUTO_ACKNOWLEDGE);

            // Lookup the request and response queues
            requestQ = (Queue) ctx.lookup(requestQueue);
            responseQ = (Queue) ctx.lookup(responseQueue);

            // Now that setup is complete, start the Connection
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

    private void exit() {

        try {
            qConnect.close();
        }
        catch(JMSException jmse) {
            jmse.printStackTrace();
        }
        System.exit(0);
    }

    public static void main(String[] args) {

        String queuecf = null;
        String requestq = null;
        String responseq = null;

        if(args.length == 3) {
            queuecf = args[0];
            requestq = args[1];
            responseq = args[2];
        }
        else {
            System.out.println("Invalid arguments. Should be: ");
            System.exit(0);
        }

        QBorrower borrower = new QBorrower(queuecf, requestq, responseq);

        try {

            // Read all standard input and send it as a message
            BufferedReader stdin = new BufferedReader(
                    new InputStreamReader(System.in));
            System.out.println("QBorrower Application Started");
            System.out.println("Press enter to quit application");
            System.out.println("Enter: Salary, Loan Amount");
            System.out.println("\ne.g. 50000, 120000");

            while(true) {

                System.out.print("> ");
                String loanRequest = stdin.readLine();

                if(loanRequest == null ||
                   loanRequest.trim().length() <= 0) {

                    borrower.exit();
                }

                // Parse the deal description
                StringTokenizer st = new StringTokenizer(loanRequest, ",");

                double salary = Double.valueOf(st.nextToken().trim()).doubleValue();
                double loanAmt = Double.valueOf(st.nextToken().trim()).doubleValue();
            }
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }
}
