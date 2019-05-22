package cz.cuni.mff.kurankot.busreservationsystem;

/**
 * Class represents seat in bus
 */
public class Seats {
    /**
     * Boolean, if seat is available
     */
    Boolean[] availability;

    /**
     * <p>Constructor of class Seats</p>
     * @param length is number of cities on line path
     */
    public Seats(int length) {
        availability = new Boolean[length];
    }
}

