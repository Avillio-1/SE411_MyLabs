package edu.psu.se411.lab07_polymorphism.model;

import java.time.LocalDate;
import edu.psu.se411.lab07_polymorphism.exceptions.InvalidArgumentException;
import edu.psu.se411.lab07_polymorphism.exceptions.MissingInformationException;

public abstract class Booking {
    private final String bookingId;
    private final String customerFullName;
    private final LocalDate travelDate;
    private final String destinationCity;

    protected Booking(String bookingId, String customerFullName,
                      LocalDate travelDate, String destinationCity) {
        this.bookingId = bookingId;
        this.customerFullName = customerFullName;
        this.travelDate = travelDate;
        this.destinationCity = destinationCity;
    }

    public String getBookingId() { return bookingId; }
    public String getCustomerFullName() { return customerFullName; }
    public LocalDate getTravelDate() { return travelDate; }
    public String getDestinationCity() { return destinationCity; }

    protected static void validateRange(double value, double minimum, double maximum,
                                        String field) throws InvalidArgumentException {
        if (!Double.isFinite(value) || value < minimum || value > maximum) {
            throw new InvalidArgumentException(field + " must be between "
                    + minimum + " and " + maximum + ".");
        }
    }

    protected static double requireInformation(Number value, String field)
            throws MissingInformationException {
        if (value == null) {
            throw new MissingInformationException(field + " has not been provided.");
        }
        return value.doubleValue();
    }

    public abstract double computeTotalPrice() throws MissingInformationException;
}
