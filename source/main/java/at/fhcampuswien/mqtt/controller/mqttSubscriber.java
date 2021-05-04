package at.fhcampuswien.mqtt.controller;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.sql.Timestamp;
import java.util.concurrent.CountDownLatch;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class mqttSubscriber extends Thread implements KeyListener {
    private MqttClient mqttClient;

    public void run() {

        System.out.println("JUMO-BrokerSubscriber initializing...");

        /*
         * For trying to connect one must change the host:tcp://<host:port>
         *     username if required
         *     password if required
         */
        String host = "tcp://test.mosquitto.org:1883";
        String username = "shabbir";
        String password = "campus09";
        MemoryPersistence persistence = new MemoryPersistence();

        try {
            // Create an Mqtt client
            mqttClient = new MqttClient(host, "Jumo-Broker", persistence);
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            connOpts.setUserName(username);
            connOpts.setPassword(password.toCharArray());

            // Connect the client
            System.out.println("Connecting to JOMO messaging at " + host);
            mqttClient.connect(connOpts);
            System.out.println("Connected");

            // Latch used for synchronizing b/w threads
            final CountDownLatch latch = new CountDownLatch(1);

            // Topic filter the client will subscribe to
            final String subTopic = "shabbirTest";


            // Callback - Anonymous inner-class for receiving messages
            mqttClient.setCallback(new MqttCallback() {


                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    // Called when a message arrives from the server that
                    // matches any subscription made by the client
                    String time = new Timestamp(System.currentTimeMillis()).toString();
                    System.out.println("\nReceived a Message!" +
                            "\n\tTime:    " + time +
                            "\n\tTopic:   " + topic +
                            "\n\tMessage: " + new String(message.getPayload()) +
                            "\n\tQoS:     " + message.getQos() + "\n");
                    latch.countDown(); // unblock main thread
                }

                public void connectionLost(Throwable cause) {
                    System.out.println("Connection to JUMO-Broker messaging lost!" + cause.getMessage());
                    latch.countDown();
                }

                public void deliveryComplete(IMqttDeliveryToken token) {
                }

            });

            // Subscribe client to the topic filter and a QoS level of 0-2
            System.out.println("Subscribing client to topic: " + subTopic);
            mqttClient.subscribe(subTopic, 0);
            System.out.println("Subscribed");

            // Wait for the message to be received
            try {
                latch.await(); // block here until message received, and latch will flip
            } catch (InterruptedException e) {
                System.out.println("I was awoken while waiting");
            }

//---------------------------------------------------------------------------------------------------------------------//
            // After receiving a message it disconnect. But we don't want that. It should listen for other messages.
            //Disconnect the client
            /*mqttClient.disconnect();
            System.out.println("Exiting");
//---------------------------------------------------------------------------------------------------------------------//
            System.exit(0);*/
        } catch (MqttException me) {
            System.out.println("reason " + me.getReasonCode());
            System.out.println("msg " + me.getMessage());
            System.out.println("loc " + me.getLocalizedMessage());
            System.out.println("cause " + me.getCause());
            System.out.println("excep " + me);
            me.printStackTrace();
        }
    }

//---------------------------------------------------------------------------------------------------------------------//
    //Experimental
    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        try {
            if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                mqttClient.disconnect();
                System.out.println("Exiting");
                System.exit(0);
            }
        } catch (MqttException mqttException) {
            mqttException.printStackTrace();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }
//---------------------------------------------------------------------------------------------------------------------//
}

