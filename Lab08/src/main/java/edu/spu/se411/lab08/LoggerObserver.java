package edu.spu.se411.lab08;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggerObserver implements Observer {
    private static final Logger logger = LoggerFactory.getLogger(LoggerObserver.class);

    @Override
    public void update(Subject subject) {
        if (subject instanceof Sensor sensor) {
            System.out.printf("Logger: %s = %.2f%n", sensor.getName(), sensor.getReading());
            logger.info("{} = {}", sensor.getName(), sensor.getReading());
        }
    }
}
