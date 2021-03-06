package at.fhcampuswien.mqtt.controller;

import at.fhcampuswien.mqtt.database.DatabaseConnection;

import java.sql.Array;
import java.sql.Timestamp;
import java.util.concurrent.CountDownLatch;
import java.io.*;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class MqttSubscriber extends Thread {

    private MqttClient mqttClient;

    //array initialization
    int i = 0;
    float[] arr = new float[16];

    public void run() {

        System.out.println("JUMO-BrokerSubscriber initializing...");

        /*
         *  For trying to connect one must change the host:tcp://<host:port>
         *  username if required
         *  password if required
         */
        //TODO Change the parameters
        String host = "tcp://195.201.96.148:1883";
        String username = "developer";
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
            final String subTopic = "/#";


            // Callback - Anonymous inner-class for receiving messages
            mqttClient.setCallback(new MqttCallback() {

                //TODO Method needs to be changed!
                //FIXME messages must go to the database

                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    // Called when a message arrives from the server that
                    // matches any subscription made by the client
                    String time = new Timestamp(System.currentTimeMillis()).toString();
                    System.out.println("\nReceived a Message!" +
                            "\n\tTime:    " + time +
                            "\n\tTopic:   " + topic +
                            "\n\tMessage: " + new String(message.getPayload()) +
                            "\n\tQoS:     " + message.getQos() + "\n");
                    mqttMessageStorage(time + " " + new String(message.getPayload()));

                    //start database connection
                    arr[i] = Float.parseFloat(new String(message.getPayload()));
                    System.out.println("Array VALUE: " +arr[i]);

                    i++;    //increment counter value

                    System.out.println("Counter: " +i);

                    if (i == 15) {
                        //pass the values to the database
                        DatabaseConnection db1 = new DatabaseConnection();
                        arr = convertArray(arr);    //convert values to correct comma
                        db1.readDataBase(arr[0], arr[1], arr[2], arr[3], arr[4], arr[5], arr[6], arr[7], arr[8], arr[9], arr[10], arr[11], arr[12], arr[13], arr[14], arr[15]);
                        i = 0;  //reset counter
                    }

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

            //TODO Optimization
//---------------------------------------------------------------------------------------------------------------------//
            // After receiving a message it disconnect. But we don't want that. It should listen for other messages.
            //Disconnect the client
            /*mqttClient.disconnect();
            System.out.println("Exiting");
            System.exit(0);*/
//---------------------------------------------------------------------------------------------------------------------//
        } catch (MqttException me) {
            System.out.println("reason " + me.getReasonCode());
            System.out.println("msg " + me.getMessage());
            System.out.println("loc " + me.getLocalizedMessage());
            System.out.println("cause " + me.getCause());
            System.out.println("excep " + me);
            me.printStackTrace();
        }
    }

    //TODO Delete it later
//---------------------------------------------------------------------------------------------------------------------//
    //Add the messages and timestamp into a file
    private void mqttMessageStorage(String message) {
        try {
            FileWriter fileWriter = new FileWriter("mqttMessages.txt", true);

            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(message);
            bufferedWriter.newLine();
            bufferedWriter.flush();
            bufferedWriter.close();
        } catch (IOException ioe) {
            ioe.fillInStackTrace();
        }
    }
//---------------------------------------------------------------------------------------------------------------------//

    private float[] convertArray (float[] oldArray)
    {
        oldArray[0] = oldArray[0]/10;
        oldArray[1] = oldArray[1]/10;
        oldArray[2] = oldArray[2]/10;
        oldArray[3] = oldArray[3]/10;
        oldArray[4] = oldArray[4]/10;
        oldArray[5] = oldArray[5]/10;
        oldArray[6] = oldArray[6]/10;
        oldArray[7] = oldArray[7]/10;
        oldArray[8] = oldArray[8]/10;
        oldArray[9] = oldArray[9]/10;
        oldArray[10] = oldArray[10]/10;

        return oldArray;
    }

}

