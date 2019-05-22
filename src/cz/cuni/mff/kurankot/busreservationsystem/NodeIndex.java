package cz.cuni.mff.kurankot.busreservationsystem;

/**
 * Class represents attributes of node
 */
public class NodeIndex {
    /**
     * int representation of index
     */
    int index;

    /**
     * String representationof node, forexample "BA1"
     */
    String name;

    /**
     * String full name of node, forexample "Bratislava". Can be same for multiple nodes
     */
    String city;

    /**
     * int representation of index of city, not equals to index
     */
    int indexCity;
}
