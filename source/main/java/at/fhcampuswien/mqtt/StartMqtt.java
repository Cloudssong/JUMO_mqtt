package at.fhcampuswien.mqtt;

import at.fhcampuswien.mqtt.controller.MqttSubscriber;
import at.fhcampuswien.mqtt.database.DatabaseConnection;

public class StartMqtt {
    public static void main(String[] args) throws Exception {
        // Check command line arguments
        //new MqttSubscriber().start();
        DatabaseConnection db1 = new DatabaseConnection();
        db1.readDataBase();
    }
}
