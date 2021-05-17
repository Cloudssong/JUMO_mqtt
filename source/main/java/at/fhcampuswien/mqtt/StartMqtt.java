package at.fhcampuswien.mqtt;

import at.fhcampuswien.mqtt.controller.MqttSubscriber;

public class StartMqtt {
    public static void main(String[] args) {
        // Check command line arguments
            new MqttSubscriber().run();
    }
}
