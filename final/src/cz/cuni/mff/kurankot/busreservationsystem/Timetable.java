package cz.cuni.mff.kurankot.busreservationsystem;

import java.sql.Time;
import java.time.LocalTime;

/**
 * Class represents Timetable, item in metrix represenation
 */
public class Timetable {
    /**
     * duration between cities where is edge in input graph
     */
    public LocalTime duration;

    /**
     * array of lines
     */
    public int[] lines;

    /**
     * array of times departure per day
     */
    public LocalTime[] times;

    /**
     * indicate, if there is edge between same node (diagonal in metrix representation of input data)
     */
    public Boolean isNullDistance = false;

    /**
     * indicate, if there is not edge between two nodes (in metrix representation of input data)
     */
    public Boolean isInfDistance = false;

    /**
     * indicate, if there is a transfer edge between two nodes (in metrix representation of input data)
     */
    public Boolean isTransfer = false;


    /**
     * <p>Constructor of Timetable class for two nodes, where is non transfer edge</p>
     * @param duration
     * @param lines
     * @param times
     */
    public Timetable(LocalTime duration, int[] lines, LocalTime[] times) {
        this.duration = duration;
        this.lines = lines;
        this.times = times;
    }


    //if in not null distance, must be inf distance, because in other cause will be use another constructor

    /**
     *  <p>Constructor of Timetable class for same node or two nodes, where non exists edge</p>
     * @param isNullDistance
     */
    public Timetable(Boolean isNullDistance) {
        if (isNullDistance) {
            this.isNullDistance = true;
        } else {
            this.isInfDistance = true;
        }
    }

    /**
     *  <p>Constructor of Timetable class for two nodes, where is transfer edge</p>
     * @param isTransfer
     * @param duration
     */
    public Timetable(Boolean isTransfer, LocalTime duration) {
        this.isTransfer = isTransfer;
        this.duration = duration;
    }
}
