package cz.cuni.mff.kurankot.busreservationsystem;

import java.time.Duration;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Class represents searching algorithm for shortest path from start node to end node
 */
public class TimetableGraph {

    /**
     * Graph representation
     */
    Timetable[][] data;

    /**
     * indicate, when travel is through 2 days (when reach 00:00 and continuous next)
     */
    boolean newDay = false;

    /**
     * <p>Constructor of TimetableGraph class</p>
     * @param data
     */
    public TimetableGraph(Timetable[][] data) {
        this.data = data;
    }

    /**
     * <p>algorithm for searching path in graph</p>
     * @param startNode is int representation of starting node
     * @param indexStartCity is int representation of index of starting city
     * @param endNode is int representation of ending node
     * @param indexFinalCity is int representation of index
     * @param startTime is LocalTime representation of starting time
     * @param nodeIndexes is array of node indexes
     * @return ResultData
     */
    public ResultData findPath(int startNode,int indexStartCity, int endNode, int indexFinalCity, LocalTime startTime, int[] nodeIndexes) {

        //array of closed nodes through algorithm runtime
        boolean[] closed = new boolean[data.length];
        //array of distances from starting node to others
        double[] distances = new double[data.length];
        //auxiliar data structure for algorithm
        Set<Integer> set = new HashSet<>();
        //array of predecessors for all nodes in graph
        int[] predecessors = new int[data.length];
        //array of lines between all two nodes after path find
        //int[][] lines = new int[data.length][data.length];

        //array of departures for all pairs of nodes
        LocalTime[][] timeDepartures = new LocalTime[data.length][data.length];

        //INITIALIZE SECTION - START
        set.add(startNode);
        predecessors[startNode] = -1;

        //initialize departure times for all nodes (all nodes have startTime value)
        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data.length; j++) {
                timeDepartures[i][j] = startTime;
            }
        }

        //initialize distances to Integer.MAX_VALUE except startNode node
        for (int i = 0; i < data.length; i++) {
            if (i != startNode) {
                distances[i] = Integer.MAX_VALUE;
            } else {
                distances[i] = 0;
            }
        }
        //INITIALIZE SECTION - END

        //ALGORITHM SSECTION - START
        while (!set.isEmpty()) {
            //find next node with the smallest distance in set
            int actualNode = nextSmallestNodeInSet(set, distances);
            set.remove(actualNode);
            closed[actualNode] = true;

            //main cycle for all columns in actualNode's row in data
            for (int i = 0; i < data.length; i++) {
                if (!data[actualNode][i].isInfDistance) {
                    if (data[actualNode][i].isTransfer) {
                        if (FunctionForTransfer(closed, actualNode, indexFinalCity, i, distances, timeDepartures, nodeIndexes)) {
                            predecessors[i] = actualNode;
                            set.add(i);
                        }

                    } else {
                        if (FunctionForNonTransfer(closed, actualNode, indexStartCity, i, distances, timeDepartures, nodeIndexes, startTime, startNode)) {
                            predecessors[i] = actualNode;
                            set.add(i);
                        }
                    }

                }
            }


        }
        //ALGORITHM SECTION - END

        return  compositeData(startNode, endNode, predecessors, timeDepartures);

    }

    /**
     * <p>Get next smallest node in set for searching algorithm</p>
     * @param set is set of actual nodes in algorithm
     * @param distances is array of double numbers of distances from start node to another nodes
     * @return Integer represents next smallest node in set
     */
    private Integer nextSmallestNodeInSet(Set<Integer> set, double[] distances) {
        //counting next node with the smallest distance
        double minDistance = Integer.MAX_VALUE;
        int actualNode = -1;
        for (Integer i : set) {
            if (distances[i] < minDistance) {
                minDistance = distances[i];
                actualNode = i;
            }
        }
        return actualNode;
    }

    /**
     * <p>Function count next nearest node for transfer edge</p>
     * @param closed is array of closed nodes through algorithm
     * @param actualNode is int representation of actual node
     * @param indexFinalCity is int representation of final city
     * @param i is int representation of cycle from parent calling method
     * @param distances is array of distances from start node to another nodes
     * @param timeDepartures is LocalTime representation of time departures from node
     * @param nodeIndexes is array of ints of node indexes
     * @return indicate, if new node may be added to set and set as predecessor
     */
    private boolean FunctionForTransfer(boolean closed[], int actualNode, int indexFinalCity, int i, double[] distances, LocalTime timeDepartures[][], int[] nodeIndexes) {
        if (!closed[i]) {
            LocalTime nextTime;

            double diff2 = 0;

            //mark all transfer edges neighbours with i
            markMeighbourTransferEdges(i, actualNode, indexFinalCity, timeDepartures, nodeIndexes);

            //return FunctionForNonTransfer(closed, actualNode, i, distances, timeDepartures);

            double duration = data[actualNode][i].duration.getMinute() + data[actualNode][i].duration.getHour()*60;

            //find shorter path to node i
            if (distances[actualNode] + duration < distances[i]) {
                distances[i] = distances[actualNode] + duration;

                //set non-closed nodes's timeDeparture to nextTime
                for (int l = 0; l < data.length; l++) {
                    if (!closed[l]) {
                        timeDepartures[i][l] = timeDepartures[actualNode][i].plus((long)duration, ChronoUnit.MINUTES);
                    }
                }

                return true;

            }

            return false;

        } else {
            return false;
        }
    }

    /**
     * <p>Function count next nearest node for non transfer edge</p>
     * @param closed is array of closed nodes through algorithm
     * @param actualNode is array of closed nodes through algorithm
     * @param indexStartCity is int representation of Start city
     * @param i is int representation of cycle from parent calling method
     * @param distances is array of distances from start node to another nodes
     * @param timeDepartures is LocalTime representation of time departures from node
     * @param nodeIndexes is array of ints of node indexes
     * @param startTime is LocalTime representation of start time
     * @param startNode is int representation of start node
     * @return indicate, if new node may be added to set and set as predecessor
     */
    private boolean FunctionForNonTransfer(boolean closed[], int actualNode, int indexStartCity, int i, double[] distances, LocalTime timeDepartures[][], int[] nodeIndexes, LocalTime startTime, int startNode) {
        if (!closed[i]) {
            //count duration in minute from actualNode to ith node
            double duration = data[actualNode][i].duration.getMinute() + data[actualNode][i].duration.getHour()*60;

            //find shorter path to node i
            if (distances[actualNode] + duration < distances[i]) {

                //find and set nearest next time
                LocalTime nextTime = findImmediateTime(timeDepartures[actualNode][i], actualNode, i, timeDepartures);
                timeDepartures[actualNode][i] = nextTime;



                Duration duration2;
                if (newDay) {
                    LocalTime timeArrive = startTime;
                    Duration toMidnight = Duration.between(timeArrive, LocalTime.parse("23:59")).plusMinutes(1);

                    duration2 = Duration.between(LocalTime.parse("00:00"), nextTime).plusMinutes(toMidnight.toMinutes());
                    newDay = false;
                } else {

                    duration2 = Duration.between(startTime, nextTime).plusMinutes(0);

                }

                //wait time in start node when next edge is not transfer
                double imagDiff = 0;
                if (nodeIndexes[actualNode] == indexStartCity && actualNode==startNode) {
                    imagDiff = duration2.toMinutes();

                }

                distances[i] = distances[actualNode] + duration + imagDiff;

                newDay = false;


                //set non-closed nodes's timeDeparture to nextTime
                for (int l = 0; l < data.length; l++) {
                    if (!closed[l]) {
                        timeDepartures[i][l] = timeDepartures[actualNode][i].plus((long)duration, ChronoUnit.MINUTES);
                    }
                }
                return true;
            }
            return false;
        } else {
            return false;
        }
    }

    /**
     * <p>find nearest next time</p>
     * @param startTime is LocalTime representation of start time
     * @param actualNode is int representation of actual node
     * @param i  is int representation of cycle from parent calling method
     * @param timeDepartures is array of time departures between all two nodes in graph
     * @return next nearest LocalTime to start time
     */
    private LocalTime findImmediateTime(LocalTime startTime, int actualNode, int i, LocalTime timeDepartures[][]) {
        int k = 0;
        LocalTime nextTime = data[actualNode][i].times[k];

        //find in list of departures in day in actualNode nearest actualTIme
        while(!startTime.isBefore(nextTime) && !(startTime.equals(nextTime))) {

            k++;
            //if occured new day, start at begin in list and time departure will set to 00:00
            if (k >= data[actualNode][i].times.length) {
                k = 0;
                //timeDepartures[actualNode][i] = LocalTime.parse("00:00");
                startTime = LocalTime.parse("00:00");
                newDay = true;

            }
            nextTime = data[actualNode][i].times[k];

        }

        return nextTime;
    }

    /**
     * <p>Marking all neighbour tranfer edges in graph fro actualNode</p>
     * @param actualNode is int representation of actual node
     * @param previousNode is int representation of previous node
     * @param indexFinalCity is int representation of index of final city
     * @param timeDepartures is array of time departures for all two nodes in graph
     * @param nodeIndexes is array of ints of node indexes
     */
    private void markMeighbourTransferEdges(int actualNode, int previousNode, int indexFinalCity, LocalTime timeDepartures[][], int[] nodeIndexes) {

        //loop for all neighbours
        for (int j = 0; j < data.length; j++) {
            if (nodeIndexes[actualNode] != indexFinalCity) {
                if ((!data[actualNode][j].isTransfer) && (actualNode != j) && (!data[actualNode][j].isInfDistance) ) {
                    //find next immediate time
                    LocalTime nextImmediateTime = findImmediateTime(timeDepartures[previousNode][actualNode], actualNode, j, timeDepartures);

                    Duration duration;
                    if (newDay) {
                        LocalTime timeArrive = timeDepartures[previousNode][actualNode];
                        Duration toMidnight = Duration.between(timeArrive, LocalTime.parse("23:59")).plusMinutes(1);

                        duration = Duration.between(LocalTime.parse("00:00"), nextImmediateTime).plusMinutes(toMidnight.toMinutes());
                        newDay = false;
                    } else {
                        duration = Duration.between(timeDepartures[previousNode][actualNode], nextImmediateTime).plusMinutes(0);
                    }

                    //initialize for next iteration
                    //--

                    String durationString = String.format("%02d:%02d", duration.toHours(), duration.toMinutes() - duration.toHours()*60);

                    //mark edge
                    data[previousNode][actualNode].duration = LocalTime.parse(durationString);
                }
            } else {
                LocalTime duration = LocalTime.parse("00:00");
                data[previousNode][actualNode].duration = duration;
            }
        }
    }

    /**
     * <p>Composite path from result as path from start node to end node</p>
     * @param startNode is int representation of start node
     * @param endNode is int representation of end node
     * @param predecessors is array of ints of predecessors for all nodes in graph
     * @param timeDepartures is array of time departures of all two nodes in graph
     * @return ResultData
     */
    private ResultData compositeData(int startNode, int endNode, int[] predecessors, LocalTime[][] timeDepartures) {
        ArrayList<Integer> path = new ArrayList<Integer>();

        //compose path from startNode to endNode
        int actualNode = endNode;
        path.add(actualNode);
        while (startNode != actualNode) {
            path.add(predecessors[actualNode]);
            actualNode = predecessors[actualNode];
        }

        //remove from begin and end of path transfer edges
        for (int j = 0; j < 3; j++) {
            boolean atBegin = true;
            for (int i = 0; i < path.size()-1; i++) {
                if (data[path.get(i)][path.get(i+1)].isTransfer && atBegin) {
                    path.remove(i);
                    i=-1;
                } else {
                    atBegin = false;
                }
            }
            Collections.reverse(path);
        }

        //initialize and fill timetable (arrive time and departure time for all node at path)
        LocalTime[] timetable = new LocalTime[path.size()*2];
        for (int i = 0; i < path.size() - 1; i++) {
            timetable[2*i+1] =   timeDepartures[path.get(i)][path.get(i+1)];
            int duration = data[path.get(i)][path.get(i+1)].duration.getHour()*60 + data[path.get(i)][path.get(i+1)].duration.getMinute();
            timetable[2*(i+1)] = timetable[2*i+1].plusMinutes(duration);
        }

        //initialize and find lines for all edges at path
        int[] lines = new int[path.size()-1];
        for (int i = 0; i < lines.length; i++) {
            int index = findLine(path.get(i), path.get(i+1), timetable[2*i+1]);
            if (index != -1){
                lines[i] = data[path.get(i)][path.get(i+1)].lines[index];
            } else {
                lines[i] = index;
            }
        }

        ResultData resultData = new ResultData(path.toArray(), timetable, lines);
        return resultData;
    }

    /**
     * <p>Find line for two neighbour nodes</p>
     * @param firstNode is int representation of firts node
     * @param secondNode is int representation of second node
     * @param time is LocalTime representation of time
     * @return int as index of line
     */
    private int findLine(int firstNode, int secondNode, LocalTime time) {

        if (data[firstNode][secondNode].isTransfer) return -1;

        int index = 0;
        //loop for timetables at concrete edge
        for (int j = 0; j < data[firstNode][secondNode].times.length; j++) {
            if (time.equals(data[firstNode][secondNode].times[j])) {
                index = j;
            }
        }

        return index;
    }
}
