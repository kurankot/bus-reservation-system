package cz.cuni.mff.kurankot.busreservationsystem;

import com.google.gson.Gson;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.stage.Stage;


import java.io.*;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;

/**
 * <p>Main class, that control window application and connect Searching algorithm with other application</p>
 *
 */
public class Controller implements Initializable {

    private Timetable[][] data;
    private Data load;

    private ArrayList<ResultData> resultes = new ArrayList<ResultData>();

    private ArrayList<ChoiceBox> transfers = new ArrayList<>();

    @FXML
    private Pane main;

    @FXML
    private GridPane table;

    @FXML
    private TextField from;
    @FXML
    private TextField to;
    @FXML
    private TextField time;
    @FXML
    private DatePicker date;

    @FXML
    private Line vertLine;
    @FXML
    private Label route;

    /**
     * <p>method after "HLADAT" button in main window</p>
     * @param actionEvent
     * @return void
     */
    @FXML
    public void searchData(javafx.event.ActionEvent actionEvent) {

        try {
            String fromString = from.getText();
            String toString = to.getText();
            String timeString = time.getText();
            String dateString = date.getValue().toString();
            System.out.print(fromString + " " + toString + " " + timeString + " " + dateString + "\n");

            int indexFrom = 0;
            int indexTo = 0;
            for (int i = 0; i < load.nodeIndices.length; i++) {
                if (load.nodeIndices[i].city.toLowerCase().equals(fromString.toLowerCase())) {
                    indexFrom = load.nodeIndices[i].index;
                } else if (load.nodeIndices[i].city.toLowerCase().equals(toString.toLowerCase())) {
                    indexTo = load.nodeIndices[i].index;
                }
            }

            //TESTING DATA - START
            Object[] path = new Object[8];
            path[0] = 0;
            path[1] = 5;
            path[2] = 7;
            path[3] = 9;
            path[4] = 8;
            path[5] = 3;
            path[6] = 2;
            path[7] = 11;

            LocalTime[] depTimes = new LocalTime[16];
            depTimes[1] = LocalTime.parse("07:15");
            depTimes[2] = LocalTime.parse("09:15");
            depTimes[3] = LocalTime.parse("09:15");
            depTimes[4] = LocalTime.parse("09:30");
            depTimes[5] = LocalTime.parse("09:30");
            depTimes[6] = LocalTime.parse("10:00");
            depTimes[7] = LocalTime.parse("10:00");
            depTimes[8] = LocalTime.parse("10:10");
            depTimes[9] = LocalTime.parse("10:10");
            depTimes[10] = LocalTime.parse("11:30");
            depTimes[11] = LocalTime.parse("11:30");
            depTimes[12] = LocalTime.parse("12:05");
            depTimes[13] = LocalTime.parse("12:05");
            depTimes[14] = LocalTime.parse("16:05");

            int[] lines = new int[7];
            lines[0] = 101;
            lines[1] = -1;
            lines[2] = 101;
            lines[3] = -1;
            lines[4] = 101;
            lines[5] = -1;
            lines[6] = 101;



            ResultData testingData = new ResultData(path, depTimes,lines);

            //TESTING DATA - END

            int lastLineY = 180;
            for (int i = 0; i < 3; i++) {
                ResultData result = findResult(indexFrom, indexTo, timeString);
                lastLineY = showResult(fromString, toString, timeString, dateString, customizeResultData(result), lastLineY, i);
                timeString = result.timeDepartures[1].plusMinutes(1).toString();
            }

        } catch (Exception ex) {
            System.out.print("Nespravne zadane vstupne data\n");

            Parent root = null;
            try {
                root = FXMLLoader.load(getClass().getResource("buyTicket.fxml"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            Stage stage = new Stage();
            stage.setTitle("Chyba");
            Scene sc = new Scene(root, 250, 100);
            stage.setScene(sc);
            stage.show();

            Label info = new Label();
            info.setText("Nesprávne zadané vstupné dáta");
            Pane infoWindow = (Pane)sc.lookup("#mainBuyTicket");
            info.setLayoutY(10);
            info.setLayoutX(20);

            infoWindow.getChildren().add(info);

            Button endInfo = new Button();
            endInfo.setText("Ok");
            endInfo.setLayoutX(105);
            endInfo.setLayoutY(40);
            infoWindow.getChildren().add(endInfo);
            endInfo.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    stage.close();
                }
            });

        }




    }

    /**
     * <p>Customize result data for better manipulation for output</p>
     * <p>Forexample for links trace 201 -1 201 -1 203 reduce to 201 -1 203, where numbers except -1 are concrete links between two cities connect with edge</p>
     * @param resultData
     * @return ResultData object
     */
    public ResultData customizeResultData(ResultData resultData) {
        ArrayList<Object> newPath = new ArrayList<>();
        ArrayList<LocalTime> newDeptTimes = new ArrayList<>();
        ArrayList<Integer> newLines = new ArrayList<>();

        //first items
        newPath.add(resultData.path[0]);

        newDeptTimes.add(resultData.timeDepartures[1]);
        newLines.add(resultData.lines[0]);
        for (int i = 1; i < resultData.path.length - 2; i++) {

            if (resultData.lines[i]==-1) {
                if (resultData.lines[i-1]==resultData.lines[i+1]) {
                    i += 1;
                } else {
                    newLines.add(-1);
                    newPath.add(resultData.path[i]);
                    newPath.add(resultData.path[i+1]);
                    newDeptTimes.add(resultData.timeDepartures[2*i]);
                    newDeptTimes.add(resultData.timeDepartures[2*i]);
                    newDeptTimes.add(resultData.timeDepartures[2*i+2]);
                    newDeptTimes.add(resultData.timeDepartures[2*i+2]);

                }
            } else {
                newLines.add(resultData.lines[i]);


            }
        }
        //last values
        newPath.add(resultData.path[resultData.path.length-1]);
        newDeptTimes.add(resultData.timeDepartures[resultData.timeDepartures.length-2]);

        if (newLines.size()!=1) {
            newLines.add(resultData.lines[resultData.lines.length-1]);
        }

        LocalTime[] deptTimesForReturn = new LocalTime[newDeptTimes.size()+2];
        for (int i = 1; i < deptTimesForReturn.length -1 ; i++) {
            deptTimesForReturn[i] = newDeptTimes.get(i-1);
        }

        ResultData finalData = new ResultData(newPath.toArray(),deptTimesForReturn, newLines.stream().mapToInt(i -> i).toArray());
        return  finalData;
    }

    /**
     * <p>Run searching algorithm</p>
     * @param from is integer number of starting node
     * @param dest is integer number of ending node
     * @param timeString is string time in format "hh:mm"
     * @return ResultData from searching algorithm.
     */
    public ResultData findResult(int from, int dest, String timeString) {

        LocalTime time = LocalTime.parse(timeString);
        TimetableGraph ttg = new TimetableGraph(data);
        int indexFinalCity = load.nodeIndices[dest].indexCity;
        int indexStartCity = load.nodeIndices[from].indexCity;
        int[] cityIndexes = new int[load.nodeIndices.length];
        for (int i = 0; i < load.nodeIndices.length; i++) {
            cityIndexes[i] = load.nodeIndices[i].indexCity;
        }
        return ttg.findPath(from, indexStartCity, dest, indexFinalCity, time, cityIndexes);

    }

    /**
     * <p>Showing result to main window after searching</p>
     * <p>calls createAndAddNewItem() method for adding new item. Item is one result from searching algorithm</p>
     * @param fromString is string representation of starting city
     * @param toString is string representation of ending city
     * @param timeString is string representation of starting time
     * @param dateString is string representation of date in format "yyyy-mm-dd"
     * @param result is ResultData from searching algorithm
     * @param lastLineY is int number represents Y position of last added element to window
     * @param i is int representation of cycle, which represent number of showing results to main window. Default is set to 3
     * @return int number represents Y position of last added element to window
     */
    public int showResult(String fromString, String toString, String timeString, String dateString, ResultData result, int lastLineY, int i) {

        vertLine.setVisible(true);
        route.setText(fromString + " -> " + toString);


        String[] cityThroughTravel = new String[result.path.length];
        for (int k = 0; k < result.path.length; k++) {
            for (int j = 0; j < load.nodeIndices.length; j++) {
                if (Integer.parseInt(result.path[k].toString())==load.nodeIndices[j].index) {
                    cityThroughTravel[k] = load.nodeIndices[j].city;
                }
            }
        }

        return lastLineY = createAndAddNewItem(lastLineY + 20, cityThroughTravel, result, i, dateString, timeString);

    }

    //create node for find result to show, firstLineY is position of next first line (header)

    /**
     * <p>Creates and adds new result from searching algorithm</p>
     * @param firstLineY is int representation of first line in Y dir.
     * @param cityThroughTravel is String array of cities through which the road leads
     * @param result is ResultData from searching algorithm
     * @param i is actual state form cycle from calling method. Cycle represent number of showing results in window
     * @param date is string representation of input date
     * @param startTime is string representation of start time
     * @return int represents Y position of next item which may be added next
     */
    public int createAndAddNewItem(int firstLineY, String[] cityThroughTravel, ResultData result, int i, String date, String startTime) {
        resultes.add(result);

        result.timeDepartures[0] = LocalTime.parse("00:00");
        result.timeDepartures[result.timeDepartures.length-1] = LocalTime.parse("00:00");

        //header
        Label header1 = new Label();
        header1.setLayoutY(firstLineY);
        header1.setLayoutX(10);
        header1.setText("Odkiaľ/Prestup/Kam");
        header1.setStyle("-fx-font-weight: bold;");
        Label header2 = new Label();
        header2.setLayoutY(firstLineY);
        header2.setLayoutX(200);
        header2.setText("Príchod");
        header2.setStyle("-fx-font-weight: bold;");
        Label header3 = new Label();
        header3.setLayoutY(firstLineY);
        header3.setLayoutX(280);
        header3.setText("Odchod");
        header3.setStyle("-fx-font-weight: bold;");
        Label header4 = new Label();
        header4.setLayoutY(firstLineY);
        header4.setLayoutX(360);
        header4.setText("Spoj");
        header4.setStyle("-fx-font-weight: bold;");
        main.getChildren().add(header1);
        main.getChildren().add(header2);
        main.getChildren().add(header3);
        main.getChildren().add(header4);

        int nextLineY = firstLineY + 25;
        //body
        for (int j = 0; j < cityThroughTravel.length; j++) {
            Label label1 = new Label();
            label1.setId("resutlCity" + j);
            label1.setLayoutY(nextLineY);
            label1.setLayoutX(10);
            label1.setText(cityThroughTravel[j]);
            Label label2 = new Label();
            label2.setLayoutY(nextLineY);
            label2.setLayoutX(200);
            if (j%2==0) {
                label2.setText("-");
            } else {
                label2.setText(result.timeDepartures[2*j].toString());
            }

            Label label3 = new Label();
            label3.setLayoutY(nextLineY);
            label3.setLayoutX(280);
            if (j%2==1) {
                label3.setText("-");
            } else {
                label3.setText(result.timeDepartures[2*j+1].toString());
            }
            Label label4 = new Label();
            label4.setLayoutY(nextLineY);
            label4.setLayoutX(360);
            if (j < result.lines.length) {
                if (String.valueOf(result.lines[j]).equals("-1")) {
                    if (String.valueOf(result.lines[j-1]).equals(String.valueOf(result.lines[j+1]))) {
                        label4.setText(String.valueOf(result.lines[j-1]));
                    } else {
                        label4.setText("prestup");
                    }
                } else {
                    label4.setText(String.valueOf(result.lines[j]));
                }

            }
            main.getChildren().add(label1);
            main.getChildren().add(label2);
            main.getChildren().add(label3);
            main.getChildren().add(label4);
            nextLineY += 20;
        }
        main.setPrefHeight(nextLineY + 50);
        Button buyButton = new Button();
        buyButton.setText("Kúpiť");
        buyButton.setId(String.valueOf(i));
        buyButton.setMinWidth(70);
        buyButton.setLayoutX(320);
        buyButton.setLayoutY(nextLineY);
        buyButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                //action after button click

                try {
                    Parent root = FXMLLoader.load(getClass().getResource("buyTicket.fxml"));
                    Stage stage = new Stage();
                    stage.setTitle("Buy ticket");
                    Scene s = new Scene(root, 450, 350);
                    stage.setScene(s);
                    stage.show();
                    System.out.print(buyButton.getId());
                    //Label resultCity = (Label)root.lookup("#resutlCity");

                    Pane mainBuyTicket = (Pane)s.lookup("#mainBuyTicket");
                    int nextLineLableY = 0;
                    transfers = new ArrayList<>();
                    for (int ctt = 0; ctt < cityThroughTravel.length - 1; ctt++) {
                        if (!cityThroughTravel[ctt].toLowerCase().equals(cityThroughTravel[ctt+1].toLowerCase())) {
                            showReservation(mainBuyTicket, nextLineLableY, cityThroughTravel, ctt, date, resultes.get(Integer.parseInt(buyButton.getId())).lines[ctt]);
                            nextLineLableY += 50;
                        }
                    }
                    Button buyTicket = new Button();
                    buyTicket.setLayoutX(321);
                    buyTicket.setLayoutY(307);
                    buyTicket.setText("Kúpiť");
                    buyTicket.setPrefWidth(65);
                    mainBuyTicket.getChildren().add(buyTicket);
                    buyTicket.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent event) {

                            try {
                                //array of selected seats
                                Integer[] selectedSeats = new Integer[transfers.size()];
                                for (int transfer = 0; transfer < transfers.size(); transfer++) {
//                                selectedSeats[transfer] = Integer.parseInt(transfers.get(transfer).getValue().toString());
                                    String route = ((Label)mainBuyTicket.getChildren().get(4*transfer)).getText();
                                    String[] splitedRoute = route.split("->");

                                    String actualDate = ((Label) mainBuyTicket.getChildren().get(4*transfer+2)).getText();

                                    if (result.timeDepartures[4*transfer+1].isBefore(LocalTime.parse(startTime))) {
                                        actualDate = LocalDate.parse(actualDate).plusDays(1).toString();
                                    }

                                    setReservation(Integer.parseInt(transfers.get(transfer).getValue().toString()), Integer.parseInt(((Label)mainBuyTicket.getChildren().get(4*transfer+1)).getText()), actualDate, splitedRoute[0].trim(), splitedRoute[1].trim());
                                }
                                transfers = new ArrayList<>();

                                //close window
                                // get a handle to the stage
                                Stage stage = (Stage) buyTicket.getScene().getWindow();
                                // do what you have to do
                                stage.close();


                            } catch (Exception ex) {
                                System.out.print("Nevybrali ste sedadlá pre všetky autobusy\n");

                                Parent root = null;
                                try {
                                    root = FXMLLoader.load(getClass().getResource("buyTicket.fxml"));
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                Stage stage = new Stage();
                                stage.setTitle("Chyba");
                                Scene sc = new Scene(root, 260, 100);
                                stage.setScene(sc);
                                stage.show();

                                Label info = new Label();
                                info.setText("Nevybrali ste sedadlá pre všetky autobusy");
                                Pane infoWindow = (Pane)sc.lookup("#mainBuyTicket");
                                info.setLayoutY(10);
                                info.setLayoutX(1);

                                infoWindow.getChildren().add(info);

                                Button endInfo = new Button();
                                endInfo.setText("Ok");
                                endInfo.setLayoutX(105);
                                endInfo.setLayoutY(40);
                                infoWindow.getChildren().add(endInfo);
                                endInfo.setOnAction(new EventHandler<ActionEvent>() {
                                    @Override
                                    public void handle(ActionEvent event) {
                                        stage.close();
                                    }
                                });
                            }

                        }
                    });

                    System.out.print(" ");
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
        });
        main.getChildren().add(buyButton);
        nextLineY += 30;
        return nextLineY;
    }

    /**
     * <p>Setting and writting new reservation to file </p>
     * @param selectedSeat is int number of selected seat by user
     * @param line is int representation of line
     * @param date is String representation of date
     * @param startCity is String representation of starting city
     * @param endCity is String representation of ending city
     */
    public void setReservation(Integer selectedSeat, int line, String date, String startCity, String endCity) {
        //LOAD DATA FROM LINESDETAIL - START
        LinesDetail lineDetail = null;
        String lineDetailFile = "LinesDetail/" + (line) + ".json";
        try {
            final BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(lineDetailFile)));
            lineDetail = new Gson().fromJson(reader, LinesDetail.class);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        //LOAD DATA FROM LINESDETAIL - END

        int startNode = Arrays.asList(lineDetail.route).indexOf(startCity);
        int endNode = Arrays.asList(lineDetail.route).indexOf(endCity);

        //LOAD DATA FROM RESERVATION FILES - START
        String pathname = "ReservationData/" + line + "_" + date + ".json";

        File file = new File(pathname);
        ArrayList emptySeats = new ArrayList();
        if (!file.exists()) {
            ReservationFile newContent = new ReservationFile();
            newContent.line = line;
            for (int seat = 0; seat < 50; seat++) {
                Seats s = new Seats(lineDetail.route.length-1);
                for (int t = 0; t < lineDetail.route.length-1; t++) {
                    s.availability[t] = true;
                }
                newContent.seats[seat] = s;
            }

            Gson gson = new Gson();

            //write content to file
            FileOutputStream fos;
            try {
                fos = new FileOutputStream(pathname);
                fos.write(gson.toJson(newContent).getBytes());
                fos.close();
                System.out.print("");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //file exists now
        ReservationFile content = null;
        try {
            final BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(pathname)));
            content = new Gson().fromJson(reader, ReservationFile.class);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        //fill new seats
        for (int seat = startNode; seat < endNode; seat++) {
            content.seats[selectedSeat].availability[seat] = false;
        }


        Gson gson = new Gson();
        //write content to file
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(pathname);
            fos.write(gson.toJson(content).getBytes());
            fos.close();
            System.out.print("");
        } catch (IOException e) {
            e.printStackTrace();
        }



    }

    /**
     * <p>Showing reservation and buying dialog</p>
     * @param mainBuyTicket is pane representation of new window
     * @param nextLineLableY is Y number of next element which may be added to pane
     * @param cityThroughTravel is String array of cities through which the road leads
     * @param ctt is int number of actual state in cycle from calling method
     * @param date is String representation of date
     * @param line is int representation of line
     */
    public void showReservation(Pane mainBuyTicket, int nextLineLableY, String[] cityThroughTravel, int ctt, String date, int line) {
        Label label1 = new Label();
        label1.setLayoutY(nextLineLableY);
        label1.setLayoutX(10);
        label1.setText(cityThroughTravel[ctt] + " -> " + cityThroughTravel[ctt+1]);
        mainBuyTicket.getChildren().add(label1);

        Label lineLabel = new Label();
        lineLabel.setText(String.valueOf(line));
        lineLabel.setLayoutY(nextLineLableY);
        lineLabel.setLayoutX(label1.getLayoutX() + label1.getText().length()*5 + 20);
        lineLabel.setVisible(false);
        mainBuyTicket.getChildren().add(lineLabel);

        Label dateLabel = new Label();
        dateLabel.setText(String.valueOf(date));
        dateLabel.setVisible(false);
        mainBuyTicket.getChildren().add(dateLabel);

        //LOAD DATA FROM LINESDETAIL - START
        LinesDetail lineDetail = null;
        String lineDetailFile = "LinesDetail/" + (line) + ".json";
        try {
            final BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(lineDetailFile)));
            lineDetail = new Gson().fromJson(reader, LinesDetail.class);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        //LOAD DATA FROM LINESDETAIL - END

        //LOAD DATA FROM RESERVATION FILES - START
        String pathname = "ReservationData/" + line + "_" + date + ".json";

        File file = new File(pathname);
        ArrayList emptySeats = new ArrayList();
        if (!file.exists()) {
            //create content
            ReservationFile newContent = new ReservationFile();
            newContent.line = line;
            for (int seat = 0; seat < 50; seat++) {
                Seats s = new Seats(lineDetail.route.length-1);
                for (int t = 0; t < lineDetail.route.length-1; t++) {
                    s.availability[t] = true;
                }
                newContent.seats[seat] = s;
            }
            Gson gson = new Gson();

            //write content to file
            FileOutputStream fos;
            try {
                fos = new FileOutputStream(pathname);
                fos.write(gson.toJson(newContent).getBytes());
                fos.close();
                System.out.print("");
            } catch (IOException e) {
                e.printStackTrace();
            }
            emptySeats = findEmptySeats(newContent, cityThroughTravel[ctt], cityThroughTravel[ctt+1], lineDetail.route);
        } else { //file exists
            ReservationFile content = null;
            try {
                final BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(pathname)));
                content = new Gson().fromJson(reader, ReservationFile.class);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            emptySeats = findEmptySeats(content, cityThroughTravel[ctt], cityThroughTravel[ctt+1], lineDetail.route);
        }

        ChoiceBox<String> seats = new ChoiceBox<>();
        seats.setItems(FXCollections.observableArrayList(emptySeats));
        seats.setLayoutX(10);
        nextLineLableY += 20;
        seats.maxHeight(100);
        seats.setLayoutY(nextLineLableY);

        transfers.add(seats);
        mainBuyTicket.getChildren().add(seats);


        //LOAD DATA FROM RESERVATION FILES - END
    }

    /**
     * <p>Finding of empty seats for concreate bus</p>
     * @param content is ReservationFile representation of json file where is info about seats availability
     * @param startCity is String representation of starting city
     * @param endCity is String representation of ending city
     * @param lineDetailRoute is array of String represents detail of actual route
     * @return ArrayList<Integer> represents empty seats
     */
    public ArrayList<Integer> findEmptySeats(ReservationFile content, String startCity, String endCity, String[] lineDetailRoute) {
        int startNode = Arrays.asList(lineDetailRoute).indexOf(startCity);
        int endNode = Arrays.asList(lineDetailRoute).indexOf(endCity);

        ArrayList<Integer> emptySeats = new ArrayList<>();

        for (int seat = 0; seat < content.seats.length; seat++) {
            boolean isEmpty = true;
            for (int e = startNode; e < endNode; e++) {
                if (!content.seats[seat].availability[e]) {
                    isEmpty = false;
                }
            }

            if (isEmpty) {
                emptySeats.add(seat);
            }
        }
        return  emptySeats;
    }

    /**
     * <p>Initialize of data</p>
     * <p>Reads data from jsom data</p>
     */
    public void initData() {
        //DATA LOADING - START
        load = null;
        try {
            final BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream("data01.json")));
            load = new Gson().fromJson(reader, Data.class);
            System.out.print("");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        //DATA LOADING - END

        //DATA CONVERT - START

        data = DataConverter.convert(load);

        //DATA CONVERT - END
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initData();
    }
}


