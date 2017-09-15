package rabbitmq_client;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Client {
    private final static String QUEUE_NAME = "hello";
    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        factory.setUsername("guest");
        factory.setPassword("guest");
        factory.setPort(5672);
        Channel channel = null;
        Connection connection = factory.newConnection();
        channel = connection.createChannel();
        channel.queueDeclare("queue", false, false, false, null);
        channel.exchangeDeclare("exchange", "direct", true);
        channel.queueBind("queue", "exchange", "route");

// The message to be sent
        String message = "<m:placeOrder xmlns:m=\"http://services.samples\">" +
                "<m:order>" +
                "<m:price>100</m:price>" +
                "<m:quantity>20</m:quantity>" +
                "<m:symbol>RMQ</m:symbol>" +
                "</m:order>" +
                "</m:placeOrder>";

// Populate the AMQP message properties
        AMQP.BasicProperties.Builder builder = new AMQP.BasicProperties().builder();
        builder.contentType("application/xml");

// Publish the message to exchange
        channel.basicPublish("exchange", "queue", builder.build(), message.getBytes());
    }
}
