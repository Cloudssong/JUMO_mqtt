package at.fhcampuswien.mqtt;
import at.fhcampuswien.mqtt.controller.mqttController;

public class Main {
    public static void main(String[] args){
        mqttController m1 = new mqttController();
        m1.start_broker();
    }
}
