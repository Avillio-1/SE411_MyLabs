package edu.spu.se411.lab08;

public class DashboardObserver implements Observer {
    @Override
    public void update(Subject subject) {
        if (subject instanceof Sensor sensor) {
            System.out.printf("Dashboard: %s = %.2f%n", sensor.getName(), sensor.getReading());
        }
    }
}
