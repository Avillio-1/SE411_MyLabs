package edu.psu.se411.lab07_polymorphism;

import java.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import edu.psu.se411.lab07_polymorphism.exceptions.InvalidArgumentException;
import edu.psu.se411.lab07_polymorphism.exceptions.MissingInformationException;
import edu.psu.se411.lab07_polymorphism.model.*;

public class App {
    private static final Logger logger = LoggerFactory.getLogger(App.class);

    public static double computeTotalPrice(Booking booking) throws MissingInformationException {
        return booking.computeTotalPrice();
    }

    public static void main(String[] args) throws Exception {
        logger.info("Application is starting...");
        try {
            LocalDate date = LocalDate.of(2026, 11, 1);
            FlightBooking flight = new FlightBooking("F001", "Ali Ahmed", date, "Jeddah", 500);
            TrainBooking standard = new TrainBooking("T001", "Sara Khalid", date,
                    "Dammam", SeatClass.STANDARD);
            TrainBooking firstClass = new TrainBooking("T002", "Omar Saleh", date,
                    "Riyadh", SeatClass.FIRST_CLASS);
            CarRentalBooking car = new CarRentalBooking("C001", "Noura Abdullah", date,
                    "Abha", 150);
            Booking[] bookings = {flight, standard, firstClass, car};

            for (Booking booking : bookings) {
                expectException(MissingInformationException.class,
                        () -> computeTotalPrice(booking));
            }

            for (double weight : new double[] {-1, 41, Double.NaN, Double.POSITIVE_INFINITY}) {
                expectException(InvalidArgumentException.class, () -> flight.setLuggageWeight(weight));
            }
            for (double distance : new double[] {0, 2001, Double.NaN, Double.POSITIVE_INFINITY}) {
                expectException(InvalidArgumentException.class, () -> standard.setDistance(distance));
            }
            for (int days : new int[] {0, 31}) {
                expectException(InvalidArgumentException.class, () -> car.setRentalDays(days));
            }

            flight.setLuggageWeight(0);
            checkPrice(flight, 500);
            flight.setLuggageWeight(40);
            checkPrice(flight, 900);
            standard.setDistance(1);
            checkPrice(standard, 0.5);
            standard.setDistance(2000);
            checkPrice(standard, 1000);
            firstClass.setDistance(1);
            checkPrice(firstClass, 1);
            firstClass.setDistance(2000);
            checkPrice(firstClass, 2000);
            car.setRentalDays(1);
            checkPrice(car, 150);
            car.setRentalDays(30);
            checkPrice(car, 4500);

            flight.setLuggageWeight(20);
            standard.setDistance(500);
            firstClass.setDistance(500);
            car.setRentalDays(5);
            double[] expectedPrices = {700, 250, 500, 750};
            for (int i = 0; i < bookings.length; i++) {
                Booking booking = bookings[i];
                checkPrice(booking, expectedPrices[i]);
                System.out.printf("%s | %s | %s | %s | Total price: %.2f%n",
                        booking.getBookingId(), booking.getCustomerFullName(),
                        booking.getTravelDate(), booking.getDestinationCity(),
                        computeTotalPrice(booking));
            }
            System.out.println("All booking checks passed.");
        } catch (Exception e) {
            logger.error("Application failed", e);
            throw e;
        } finally {
            logger.info("Application is stopping...");
        }
    }

    private static void checkPrice(Booking booking, double expected) throws MissingInformationException {
        double actual = computeTotalPrice(booking);
        if (Math.abs(actual - expected) > 0.000001) {
            throw new IllegalStateException("Unexpected price for " + booking.getBookingId()
                    + ": expected " + expected + ", got " + actual);
        }
    }

    private static void expectException(Class<? extends Exception> expected, BookingAction action)
            throws Exception {
        try {
            action.run();
        } catch (Exception e) {
            if (!expected.isInstance(e)) {
                throw e;
            }
            logger.error("Expected validation exception: " + e.getMessage(), e);
            return;
        }
        throw new IllegalStateException("Expected " + expected.getSimpleName());
    }

    @FunctionalInterface
    private interface BookingAction {
        void run() throws Exception;
    }
}
