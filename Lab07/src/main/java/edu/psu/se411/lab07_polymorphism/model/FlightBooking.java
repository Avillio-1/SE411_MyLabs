package edu.psu.se411.lab07_polymorphism.model;

import java.time.LocalDate;
import static edu.psu.se411.lab07_polymorphism.GlobalConfiguration.*;
import edu.psu.se411.lab07_polymorphism.exceptions.InvalidArgumentException;
import edu.psu.se411.lab07_polymorphism.exceptions.MissingInformationException;

public class FlightBooking extends Booking {
    private final double baseTicketPrice;
    private Double luggageWeight;

    public FlightBooking(String bookingId, String customerFullName, LocalDate travelDate,
                         String destinationCity, double baseTicketPrice) {
        super(bookingId, customerFullName, travelDate, destinationCity);
        this.baseTicketPrice = baseTicketPrice;
    }

    public void setLuggageWeight(double luggageWeight) throws InvalidArgumentException {
        validateRange(luggageWeight, MIN_LUGGAGE_WEIGHT, MAX_LUGGAGE_WEIGHT, "Luggage weight");
        this.luggageWeight = luggageWeight;
    }

    @Override
    public double computeTotalPrice() throws MissingInformationException {
        return baseTicketPrice + requireInformation(luggageWeight, "Luggage weight")
                * EXTRA_LUGGAGE_RATE;
    }
}
