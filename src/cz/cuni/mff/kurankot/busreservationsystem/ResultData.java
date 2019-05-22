package cz.cuni.mff.kurankot.busreservationsystem;

import java.time.LocalTime;

/**
 * Class represents Result data
 */
public class ResultData {
    /**
     * Array of Objects represents nodes in path
     */
    Object[] path;

    /**
     * Array of LocalTimes represents time departures for all nodes in path
     */
    LocalTime[] timeDepartures;

    /**
     * Array of lines in int representation
     */
    int[] lines;


    /**
     * <p>Constructor of ResultData class</p>
     * @param path is Array of Objects represents nodes in path
     * @param timeDepartures is Array of LocalTimes represents time departures for all nodes in path
     * @param lines is Array of lines in int representation
     */
    public  ResultData(Object[] path, LocalTime[] timeDepartures, int[] lines) {
        this.path = path;
        this.timeDepartures = timeDepartures;
        this.lines = lines;
    }
}
