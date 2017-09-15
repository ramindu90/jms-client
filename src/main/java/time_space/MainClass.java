package time_space;

import time_space.consumer.JMSClient;

/**
 * Created by ramindu on 9/12/17.
 */
public class MainClass {
    public static void main(String args[]) throws InterruptedException {
        Thread thread = new Thread(new JMSClient());
        thread.start();
        Thread.sleep(5000);
        Thread thread2 = new Thread(new time_space.producer.JMSClient());
        thread2.start();
    }
}
