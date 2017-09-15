/*
 * Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package time_space.consumer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.jms.QueueConnectionFactory;
import javax.jms.TopicConnectionFactory;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.io.IOException;
import java.util.Properties;

public class JMSClient implements Runnable{

    private static Log log = LogFactory.getLog(JMSClient.class);

    public static void main(String[] args) throws InterruptedException {
        JMSClient jmsClient = new JMSClient();
        jmsClient.run();
    }

    public static void runPublishingClient() throws InterruptedException {

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
        //        String topic = "ei_topic";
        String topic = null;
        String queue = "TimeSpaceReqPublish";
//        String queue = null;
        Properties properties = new Properties();

        try {
            boolean validBroker = true;
            properties.load(ClassLoader.getSystemClassLoader().getResourceAsStream("activemq.properties"));

            if (topic == null && queue == null) {
                log.error("Enter topic value or queue value! ");
            } else if (topic != null) {
                Context context = new InitialContext(properties);
                TopicConnectionFactory topicConnectionFactory = (TopicConnectionFactory) context.lookup("ConnectionFactory");
                TopicConsumer topicConsumer = new TopicConsumer(topicConnectionFactory, topic);
                Thread consumerThread = new Thread(topicConsumer);
                log.info("Starting ActiveMQ consumerTopic thread...");
                consumerThread.start();
                Thread.sleep(600 * 1000);
                log.info("Shutting down ActiveMQ consumerTopic...");
                topicConsumer.shutdown();
            } else {
                Context context = new InitialContext(properties);
                QueueConnectionFactory queueConnectionFactory = (QueueConnectionFactory) context.lookup("ConnectionFactory");
                QueueConsumer queueConsumer = new QueueConsumer(queueConnectionFactory, queue);
                Thread consumerThread = new Thread(queueConsumer);
                log.info("Starting ActiveMQ consumerQueue thread...");
                consumerThread.start();
                Thread.sleep(600 * 1000);
                log.info("Shutting down ActiveMQ consumerQueue...");
                queueConsumer.shutdown();
            }
        } catch (IOException e) {
            log.error("Cannot read properties file from resources. " + e.getMessage(), e);
        } catch (NamingException e) {
            log.error("Invalid properties in the properties " + e.getMessage(), e);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
