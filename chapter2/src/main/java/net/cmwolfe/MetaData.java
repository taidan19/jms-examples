package net.cmwolfe;

import java.util.Enumeration;
import javax.jms.ConnectionMetaData;
import javax.jms.JMSException;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 * A (mostly) unmodified version of the MetaData class from Chapter 2 of
 * "Java Messsaging Service" 2nd edition, by Richards et al.
 *
 * The purpose of the class is to show you how to retrieve metadata from a
 * JMS Connection object. This metadata largely contains information on the
 * JMS Provider, its version, and what kinds of extensions it supports.
 */
public class MetaData {

  public static void main (String[] args) {

    try {

        // All the usual initialization boilerplate. Consult some of the other
        // classes in this chapter for details on what this all means.
        Context ctx = new InitialContext();
        QueueConnectionFactory qFactory =
            (QueueConnectionFactory) ctx.lookup("QueueCF");
        QueueConnection qConnect = qFactory.createQueueConnection();
        ConnectionMetaData metadata = qConnect.getMetaData();




        // This displays the version number of the provider.
        System.out.println("JMS Version: " +
                           metadata.getJMSMajorVersion() + "." +
                           metadata.getJMSMinorVersion());



        // This shows the name of the current provider.
        System.out.println("JMS Provider: " +
                           metadata.getJMSProviderName());




        // This shows the names of the extension properties supported by the
        // current provider.
        System.out.println("JMSX Properties Supported: ");
        Enumeration e = metadata.getJMSXPropertyNames();
        while(e.hasMoreElements()) {
            System.out.println("  " + e.nextElement());
        }
    }
    catch(Exception ex) {
      ex.printStackTrace();
      System.exit(1);
    }
  }
}
