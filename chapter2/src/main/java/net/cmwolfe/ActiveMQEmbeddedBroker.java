package net.cmwolfe;

import org.apache.activemq.broker.BrokerService;

/**
 * The "Java Messaging Service" book advises readers to download the binary
 * distribution of ActiveMQ and start it up in order to run the examples. At
 * the same time, it suggests including the "activemq-all" library in all sample
 * code. Said library has everything we need to stand up a JMS broker
 * programatically. While more complex examples might benefit from having a
 * standalone broker, the simplest examples won't. It would be better, then to
 * provide a class that can launch a basic broker, so that the examples can be
 * run by simply downloading this project, without the need to grab a binary
 * distribution of ActiveMQ.
 */
public class ActiveMQEmbeddedBroker {

    public static void main(String[] args) throws Exception {
        BrokerService broker = new BrokerService();
        broker.addConnector("tcp://localhost:61616");
        broker.start();
    }
}
