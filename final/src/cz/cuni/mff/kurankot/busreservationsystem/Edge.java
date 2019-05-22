package cz.cuni.mff.kurankot.busreservationsystem;

/**
 * Class represents edge in input graph of timetable
 */
public class Edge {
    /**
     * int representation of start node
     */
    int from;

    /**
     * int representation of end node
     */
    int to;

    /**
     * indicate, if edge is transfer
     */
    boolean isTransfer;

    /**
     * String representation of duration through edge
     */
    String duration;

    /**
     * Array of departures per day in the edge
     */
    Departure[] timetable;
}
