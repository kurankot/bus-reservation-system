package cz.cuni.mff.kurankot.busreservationsystem;

/**
 * Class represent json reservation file for line and date
 */
public class ReservationFile {
    /**
     * int representation of line
     */
    int line;

    /**
     * Array of seat in bus
     */
    Seats[] seats = new Seats[50];
}
