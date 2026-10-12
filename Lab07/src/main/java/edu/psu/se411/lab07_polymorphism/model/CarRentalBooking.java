package edu.psu.se411.lab07_polymorphism.model;

import java.time.LocalDate;
import static edu.psu.se411.lab07_polymorphism.GlobalConfiguration.*;
import edu.psu.se411.lab07_polymorphism.exceptions.InvalidArgumentException;
import edu.psu.se411.lab07_polymorphism.exceptions.MissingInformationException;

public class CarRentalBooking extends Booking {
    private final double dailyRentalRate;
    private Integer rentalDays;

    public CarRentalBooking(String bookingId, String customerFullName, LocalDate travelDate,
                            String destinationCity, double dailyRentalRate) {
        super(bookingId, customerFullName, travelDate, destinationCity);
        this.dailyRentalRate = dailyRentalRate;
    }

    public void setRentalDays(int rentalDays) throws InvalidArgumentException {
        validateRange(rentalDays, MIN_RENTAL_DAYS, MAX_RENTAL_DAYS, "Rental days");
        this.rentalDays = rentalDays;
    }

    @Override
    public double computeTotalPrice() throws MissingInformationException {
        return dailyRentalRate * requireInformation(rentalDays, "Rental days");
    }
}
