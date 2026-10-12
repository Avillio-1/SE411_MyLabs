package edu.psu.se411.lab07_polymorphism.model;

import java.time.LocalDate;
import static edu.psu.se411.lab07_polymorphism.GlobalConfiguration.*;
import edu.psu.se411.lab07_polymorphism.exceptions.InvalidArgumentException;
import edu.psu.se411.lab07_polymorphism.exceptions.MissingInformationException;

public class TrainBooking extends Booking {
    private final SeatClass seatClass;
    private Double distance;

    public TrainBooking(String bookingId, String customerFullName, LocalDate travelDate,
                        String destinationCity, SeatClass seatClass) {
        super(bookingId, customerFullName, travelDate, destinationCity);
        this.seatClass = seatClass;
    }

    public void setDistance(double distance) throws InvalidArgumentException {
        validateRange(distance, MIN_TRAIN_DISTANCE, MAX_TRAIN_DISTANCE, "Distance");
        this.distance = distance;
    }

    @Override
    public double computeTotalPrice() throws MissingInformationException {
        double rate = switch (seatClass) {
            case STANDARD -> TRAIN_STANDARD_RATE;
            case FIRST_CLASS -> TRAIN_FIRST_CLASS_RATE;
        };
        return requireInformation(distance, "Distance") * rate;
    }
}
