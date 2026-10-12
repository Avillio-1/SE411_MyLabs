package edu.spu.se411.lab08;

import java.util.Random;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class App {
    private static final Logger logger = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) throws CloneNotSupportedException {
        logger.info("Application is starting...");
        Sensor temp = new TemperatureSensor();
        Sensor humidity = new HumiditySensor();
        Observer dashboard = new DashboardObserver();
        Observer logging = new LoggerObserver();
        temp.register(dashboard);
        temp.register(logging);
        humidity.register(dashboard);
        humidity.register(logging);

        Random random = new Random();
        int i = 0;
        while (i < 10) {
            temp.setReading(20 + random.nextDouble() * 15);
            humidity.setReading(40 + random.nextDouble() * 20);
            i++;
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }

        Sensor clone = temp.clone();
        System.out.println("Clone without observers: no notifications expected.");
        clone.setReading(25);
        clone.register(new DashboardObserver());
        System.out.println("Clone with its own dashboard: one notification expected.");
        clone.setReading(26);

        temp.unregister(logging);
        System.out.println("Original after unregistering logger: dashboard notification expected.");
        temp.setReading(27);
    }
}
