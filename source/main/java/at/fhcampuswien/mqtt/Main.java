package at.fhcampuswien.mqtt;

import at.fhcampuswien.mqtt.controller.mqttSubscriber;

public class Main{
    public static void main(String[] args) {
        // Check command line arguments
            new mqttSubscriber().run();
    }
}
