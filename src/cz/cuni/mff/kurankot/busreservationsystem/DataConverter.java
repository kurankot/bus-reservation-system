package cz.cuni.mff.kurankot.busreservationsystem;

import java.time.LocalTime;

/**
 * Class represents Converter of data from json file to metrix
 */
public class DataConverter {

    /**
     * <p>Convert input data from json file to metrix of length VxV, where V is number of vertices in graph</p>
     * @param load is Data represent from json file
     * @return Timetable[][] array of converted input data
     */
    public static Timetable[][] convert(Data load) {

        Timetable[][] finalData = new Timetable[load.nodeIndices.length][load.nodeIndices.length];

        for (Edge edge: load.edges) {
            if (edge.isTransfer) { //is transfer --> within town
                finalData[edge.from][edge.to] = new Timetable(true, LocalTime.parse("00:00") );
            } else {  //is not transfer --> must be edge between 2 towns
                LocalTime duration = LocalTime.parse(edge.duration);
                int[] lines = new int[edge.timetable.length];
                LocalTime[] times = new LocalTime[edge.timetable.length];

                for (int i = 0; i < edge.timetable.length; i++) {
                    lines[i] = Integer.parseInt(edge.timetable[i].line);
                    times[i] = LocalTime.parse(edge.timetable[i].departure);
                }

                finalData[edge.from][edge.to] = new Timetable(duration, lines, times);
            }
        }

        for (int i = 0; i < load.nodeIndices.length; i++) {
            for (int j = 0; j < load.nodeIndices.length; j++) {
                if (i == j) { //edge from town into that one town --> edge nullDistance set to true
                    finalData[i][j] = new Timetable(true);
                } else if (finalData[i][j] == null) {
                    finalData[i][j] = new Timetable(false);
                }
            }
        }

        return finalData;
    }
}
