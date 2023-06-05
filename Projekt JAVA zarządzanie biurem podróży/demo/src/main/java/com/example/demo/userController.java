package com.example.demo;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.*;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *   Metoda btnInsertAction
 *   Metoda initialize
 *   Metoda getConnection
 *   Metoda getToursList
 *   Metoda showTours
 *   Metoda insertRecord
 *   Metoda showAlert
 *   Metoda updateRecord
 *   Metoda deleteRecord
 *   Metoda executeQuery
 *   Metoda handleMouseAction
 *
 *  @author Piotr Szteinmiller
 *  @version 11.0.13
 */

public class userController implements Initializable {

    @FXML
    private Button btnDelete;

    @FXML
    private Button btnInsert;

    @FXML
    private Button btnUpdate;

    @FXML
    private Button btnSearch;

    @FXML
    private Button btnRefresh;

    @FXML
    private TableColumn<Tours, String> colCity;

    @FXML
    private TableColumn<Tours, String> colEmail;

    @FXML
    private TableColumn<Tours, Integer> colId;

    @FXML
    private TableColumn<Tours, String> colPostalCode;

    @FXML
    private TableColumn<Tours, String> colStreet;

    @FXML
    private TableColumn<Tours, String> colTour;

    @FXML
    private TableColumn<Tours, String> colUsername;

    @FXML
    private TextField tfCity;

    @FXML
    private TextField tfEmail;

    @FXML
    private TextField tfId;

    @FXML
    private TextField tfPostalCode;

    @FXML
    private TextField tfStreet;

    @FXML
    private TextField tfTour;

    @FXML
    private TextField tfUsername;

    @FXML
    private TableView<Tours> tvTable;

    @FXML
    private TextField tfSearch;


    @FXML
    private void btnInsertAction(ActionEvent event){
        if(event.getSource() == btnInsert){
            insertRecord();
        } else if(event.getSource() == btnUpdate){
            updateRecord();
        } else if(event.getSource() == btnDelete){
            deleteRecord();
        } else if(event.getSource() == btnSearch){
            searchRecord();
        } else if(event.getSource() == btnRefresh){
            refreshRecords();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        showTours();
    }

    public void searchRecord() {
        showTours();
        ObservableList<Tours> filteredList = FXCollections.observableArrayList();
        if(tfSearch == null || tfSearch.getText() == null){
            tvTable.setItems(getToursList());
        }else{
            tfSearch.getText();
            for(Tours tours : tvTable.getItems()){
                String filterID = Integer.toString(tours.getId());
                String filterPostalCode = tours.getPostalCode();
                String filterCity = tours.getCity();
                String filterStreet = tours.getStreet();
                String filterEmail = tours.getEmail();
                String filterTour = tours.getTour();
                String filterUsername = tours.getUsername();
                if(filterID.contains(tfSearch.getText()) || filterUsername.contains(tfSearch.getText()) || filterCity.contains(tfSearch.getText()) ||
                filterStreet.contains(tfSearch.getText()) || filterPostalCode.contains(tfSearch.getText()) ||
                filterEmail.contains(tfSearch.getText()) || filterTour.contains(tfSearch.getText())){
                    filteredList.add(tours);
                }
            }
            tvTable.setItems(filteredList);
        }
    }
    private void refreshRecords() {
        showTours();
    }

    /**
     * Getting connection with our database.
     *
     * @return null or connection
     */
    public Connection getConnection(){
        Connection conn;
        try{
            /* fix error
            * No suitable driver found for jdbc:mysql://localhost:3306/biuropodrozy
            *
            * */Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/biuropodrozy","root","Pietkasz2000");
            return conn;
        }catch(Exception ex){
            System.out.println("Error: " + ex.getMessage());
            return null;
        }
    }

    /**
     * Returning list of the Tours.
     *
     * @return tourList
     */
    public ObservableList<Tours> getToursList(){
        ObservableList<Tours> tourList = FXCollections.observableArrayList();
        Connection conn = getConnection();
        String query = "SELECT * FROM tours";
        Statement st;
        ResultSet rs;

        try{
            st = conn.createStatement();
            rs = st.executeQuery(query);
            Tours tours;
            while(rs.next()){
                tours = new Tours(rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("city"),
                        rs.getString("postalCode"),
                        rs.getString("street"),
                        rs.getString("email"),
                        rs.getString("tour"));
                tourList.add(tours);
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return tourList;
    }

    /**
     * Getting the list of Tours from our database.
     */
    public void showTours(){
        ObservableList<Tours> list = getToursList();

        colId.setCellValueFactory(new PropertyValueFactory<Tours,Integer>("id"));
        colUsername.setCellValueFactory(new PropertyValueFactory<Tours,String>("username"));
        colCity.setCellValueFactory(new PropertyValueFactory<Tours,String>("city"));
        colPostalCode.setCellValueFactory(new PropertyValueFactory<Tours,String>("postalCode"));
        colStreet.setCellValueFactory(new PropertyValueFactory<Tours,String>("street"));
        colEmail.setCellValueFactory(new PropertyValueFactory<Tours,String>("email"));
        colTour.setCellValueFactory(new PropertyValueFactory<Tours,String>("tour"));

        tvTable.setItems(list);
    }

    /**
     *  Funkcja odpowiedzialna za dodawanie rekordów do naszej bazy danych oraz
     *  sprawdzaniu czy dane wprowadzane przez użytkownika są poprawne,
     *  rekordy są dodawane za pomocą SQL query, jeżeli dane nie są poprawne zostanie wyrzucony błąd,
     *  funkcje aktywujemy za pomocą przycisku "insert".
     */
    private void insertRecord(){
        String query = "INSERT INTO tours VALUES ("
                + tfId.getText() + ",'"
                + tfUsername.getText() + "','"
                + tfCity.getText() + "','"
                + tfPostalCode.getText() + "','"
                + tfStreet.getText() + "','"
                + tfEmail.getText() + "','"
                + tfTour.getText() + "')";

        Window owner = (Stage) tfId.getScene().getWindow();
        if(tfId.getText().isEmpty() || tfUsername.getText().isEmpty() ||
                tfCity.getText().isEmpty() || tfPostalCode.getText().isEmpty() ||
                tfStreet.getText().isEmpty() || tfEmail.getText().isEmpty() ||
                tfTour.getText().isEmpty() )
        {
            showAlert(Alert.AlertType.ERROR, owner, "Please enter correct e-mail","form error");
        }else if (!(Pattern.matches("^[a-zA-Z0-9]+[@]{1}+[a-zA-Z0-9]+[.]{1}+[a-zA-Z0-9]+$", tfEmail.getText())))
        {
            showAlert(Alert.AlertType.ERROR, owner, "Please enter valid email.(for example: yourEmail@gmail.com)","email error");
        }else if(!(Pattern.matches("\\d{5}", tfPostalCode.getText()))){
            showAlert(Alert.AlertType.ERROR, owner, "Please enter valid PL postal code.(Postal code: '23-420' Format:'23420')","Postal code error");
        }else if(!(Pattern.matches("^[a-zA-Z0-9._-]{3,}$", tfUsername.getText()))) {
            showAlert(Alert.AlertType.ERROR, owner, "Please enter valid Username.(Username needs to be longer than 3 char Valid characters: a-z,A-Z,0-9,points,dashes and underscores.)", "Username error");
        }else if(!(Pattern.matches("^[a-zA-Z\s-]+$", tfCity.getText()))) {
            showAlert(Alert.AlertType.ERROR, owner, "Please enter valid City.(Valid characters: a-z,A-Z)", "City field text error");
        }else{
            executeQuery(query);
            showTours();
        }

    }

    /**
     * Showing alert window
     *
     * @param alertType Alert type
     * @param owner Window
     * @param message message displayed
     * @param title window title
     */
    public static void showAlert(Alert.AlertType alertType,Window owner, String message,String title){
        Alert alert = new Alert(alertType);
        alert.setContentText(message);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.initOwner(owner);
        alert.showAndWait();
    }

    /**
     * Metoda odpowiedzialna za edycje wprowadzonych przez użytkowanika rekordów,
     * edycja odbywa się za pomocą wyszukiwania id klienta oraz zmieniania przez użytkownika parametrów tego rekordu,
     * funkcje aktywujemy za pomocą przycisku "update".
     */
    private void updateRecord(){
        String query = "UPDATE tours SET " +
                "username = '" + tfUsername.getText() +
                "', city = '" + tfCity.getText() +
                "', postalCode = '" + tfPostalCode.getText() +
                "', street = '" + tfStreet.getText() +
                "', email = '" + tfEmail.getText() +
                "', tour = '" + tfTour.getText() + "' WHERE id =" + tfId.getText() + ";";
        executeQuery(query);
        showTours();

    }

    /**
     * Metoda odpowiedzialna za usuwanie wybranego rekordu,
     * rekord wybieramy za pomocą kliknięcia na dany rekord myszą lub wpisaniu danego id klienta,
     * aby aktywować funkcje należy kliknąć przycisk "delete".
     */
    private void deleteRecord(){
        String query = "DELETE FROM tours WHERE id =" + tfId.getText() + ";";
        executeQuery(query);
        showTours();
    }


    /**
     * Method that allow to execute query
     *
     * @param query query from other methods
     */
    private void executeQuery(String query) {
        Connection conn = getConnection();
        Statement st;
        try{
            st = conn.createStatement();
            st.executeUpdate(query);
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    /**
     *
     * @param mouseEvent SceneBuilder event name
     */

    public void handleMouseAction(MouseEvent mouseEvent) {
        Tours tours = tvTable.getSelectionModel().getSelectedItem();
        tfId.setText(""+tours.getId());
        tfUsername.setText(tours.getUsername());
        tfCity.setText(tours.getCity());
        tfPostalCode.setText(tours.getPostalCode());
        tfStreet.setText(tours.getStreet());
        tfEmail.setText(tours.getStreet());
        tfTour.setText(tours.getTour());
    }


}
