package synchrounous;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
import java.util.Random;

public class Client implements MessageListener, Runnable {
    private static int ackMode;
    private static String clientQueueName;
    private static String clientTopicName;

    private boolean transacted = false;
    private MessageProducer producer;

    static {
        clientQueueName = "ClientReq";
        ackMode = Session.AUTO_ACKNOWLEDGE;
    }

    private String createRandomString() {
        Random random = new Random(System.currentTimeMillis());
        long randomLong = random.nextLong();
        return Long.toHexString(randomLong);
    }

    public void onMessage(Message message) {
        String messageText = null;
        try {
            if (message instanceof TextMessage) {
                TextMessage textMessage = (TextMessage) message;
                messageText = textMessage.getText();
                System.out.println("messageText = " + messageText);
            }
        } catch (JMSException e) {
            //Handle the exception appropriately
        }
    }

    public static void main(String[] args) {
        Client client = new Client();
        client.run();
    }

    /**
     * When an object implementing interface <code>Runnable</code> is used
     * to create a thread, starting the thread causes the object's
     * <code>run</code> method to be called in that separately executing
     * thread.
     * <p>
     * The general contract of the method <code>run</code> is that it may
     * take any action whatsoever.
     *
     * @see Thread#run()
     */
    @Override public void run() {
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61616");
        Connection connection;
        try {
            connection = connectionFactory.createConnection();
            connection.start();
            Session session = connection.createSession(transacted, ackMode);
            Destination adminQueue = session.createQueue(clientQueueName);
//            Destination adminTopic = session.createQueue(clientTopicName);

            //Setup a message time_space.producer to send message to the queue the server is consuming from
            this.producer = session.createProducer(adminQueue);
//            this.time_space.producer = session.createProducer(adminTopic);
            this.producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

            //Create a temporary queue that this client will listen for responses on then create a time_space.consumer
            //that consumes message from this temporary queue...for a real application a client should reuse
            //the same temp queue for each message to the server...one temp queue per client
            Destination tempDest = session.createTemporaryQueue();
            MessageConsumer responseConsumer = session.createConsumer(tempDest);

            //This class will handle the messages to the temp queue as well
            responseConsumer.setMessageListener(this);

            //Now create the actual message you want to send
            TextMessage txtMessage = session.createTextMessage();
            txtMessage.setText("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" "
                                       + "xmlns:wsa=\"http://www.w3.org/2005/08/addressing\">\n"
                                       + "    <soapenv:Body>\n"
                                       + "        <m0:getQuote xmlns:m0=\"http://services.samples\">\n"
                                       + "            <m0:request>\n"
                                       + "                <m0:symbol>IBM2</m0:symbol>\n"
                                       + "            </m0:request>\n"
                                       + "        </m0:getQuote>\n"
                                       + "    </soapenv:Body>\n"
                                       + "</soapenv:Envelope>");

            //Set the reply to field to the temp queue you created above, this is the queue the server
            //will respond to
            txtMessage.setJMSReplyTo(tempDest);

            //Set a correlation ID so when you get a response you know which sent message the response is for
            //If there is never more than one outstanding message to the server then the
            //same correlation ID can be used for all the messages...if there is more than one outstanding
            //message to the server you would presumably want to associate the correlation ID with this
            //message somehow...a Map works good
            String correlationId = this.createRandomString();
            txtMessage.setJMSCorrelationID(correlationId);
            this.producer.send(txtMessage);
            System.out.println("Message sent to JMS broker.");
        } catch (JMSException e) {
            //Handle the exception appropriately
        }
    }
}