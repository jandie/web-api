package ui.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lynden.gmapsfx.GoogleMapView;
import com.lynden.gmapsfx.javascript.event.UIEventType;
import com.lynden.gmapsfx.javascript.object.*;
import com.lynden.gmapsfx.shapes.Circle;
import com.lynden.gmapsfx.shapes.CircleOptions;
import interfaces.ConfirmationMessage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import library.Calamity;
import library.SocialPost;
import library.User;
import library.Weather;
import netscape.javascript.JSObject;
import requests.CalamityRequest;
import requests.SocialRequest;
import requests.UserRequest;
import requests.WeatherRequest;
import ui.Main;
import ui.util.ListViewTweetCell;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by guillaimejanssen on 20/03/2017.
 *
 * @author guillaime
 */
public class CalamityListController implements Initializable {

    @FXML
    public TableView<User> userTable;
    public User user;
    /*
    * Connections to the fxml file, every button, label etc.
    * that will get filled are in here
    * */
    @FXML
    private Button refreshButton;
    @FXML
    private Button backButton;
    @FXML
    private Button changeButton;
    @FXML
    private Button planButton;
    @FXML
    private Button askInfoButton;

    @FXML
    private TableView<Calamity> calamityTable;

    @FXML
    private TextField titleTextField;
    @FXML
    private TextField creatorTextField;
    @FXML
    private TextField dateTextField;
    @FXML
    private TextArea informationTextArea;

    @FXML
    private Label weatherLabel;

    @FXML
    private ListView<SocialPost> listViewTweets;

    @FXML
    private GoogleMapView googleMapView;

    private Stage stage;
    private GoogleMap map;
    private Calamity selectedCalamity;
    private Timer timerToRefresh = new Timer();

    private UserRequest userRequest;
    private CalamityRequest calamityRequest;
    private SocialRequest socialRequest;

    public CalamityListController(Stage stage, User user) {
        this.stage = stage;
        this.user = user;
        this.userRequest = new UserRequest();
        this.calamityRequest = new CalamityRequest();
        this.socialRequest = new SocialRequest();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        titleTextField.setEditable(false);
        creatorTextField.setEditable(false);
        dateTextField.setEditable(false);
        informationTextArea.setEditable(false);

        listViewTweets.setCellFactory(param -> new ListViewTweetCell());
        googleMapView.addMapInializedListener(this::mapInitialized);
        refreshButton.setOnAction(this::handleRefreshAction);
        changeButton.setOnAction(this::handleChangeAction);
        planButton.setOnAction(this::handlePlanAction);
        backButton.setOnAction(this::handleBackAction);
        askInfoButton.setOnAction(this::handleAskInfoAction);
        calamityTable.setOnMouseClicked((MouseEvent event) -> {
            if (event.getButton().equals(MouseButton.PRIMARY)) {
                try {
                    fillCalamityDetails(calamityTable.getSelectionModel().getSelectedItem());
                } catch (NullPointerException ex) {
                    fillDefaultCalamityDetails();
                }
            }
        });


        initiateTableColumns();
        refreshCalamityTable();

        // Refreshing the table every 10 seconds
        timerToRefresh.schedule(new RefreshTask(), 10 * 1000);

        // Close handler for timer
        stage.setOnCloseRequest(e -> {
            timerToRefresh.cancel();
        });
    }

    private void refreshSocialPosts() {

        ObjectMapper mapper = new ObjectMapper();

        StringBuilder keywordBuilder = new StringBuilder();

        for(String string : selectedCalamity.getTitleTags()) {
            if (string.length() >= 5) {
                keywordBuilder.append("+").append("+" + string);
            }
        }

        ConfirmationMessage message = socialRequest.getSocialPosts(user.getToken(), keywordBuilder.toString());
        if (message.getStatus() != ConfirmationMessage.StatusType.ERROR) {

            List<SocialPost> socialPosts = mapper.convertValue(message.getReturnObject(), new TypeReference<List<SocialPost>>() {});
            ObservableList<SocialPost> observableList = FXCollections.observableArrayList(socialPosts);

            listViewTweets.setItems(FXCollections.observableArrayList(observableList));
        }
    }

    private void refreshUserTable() {
        List<User> users = this.selectedCalamity.getAssignees();
        ObservableList<User> obsList = FXCollections.observableArrayList(users);
        userTable.setItems(obsList);
    }

    private void refreshCalamityTable() {
        Object value = calamityRequest.allCalamity().getReturnObject();

        ObjectMapper mapper = new ObjectMapper();
        List<Calamity> calamities = mapper.convertValue(value, new TypeReference<List<Calamity>>() {
        });

        ObservableList<Calamity> obsList = FXCollections.observableArrayList(calamities);
        calamityTable.setItems(obsList);
    }

    private void handleChangeAction(ActionEvent actionEvent) {
        if (titleTextField.isEditable()) {
            titleTextField.setEditable(false);
            informationTextArea.setEditable(false);
            changeButton.setText("Change values");

            if (!selectedCalamity.getTitle().equals(this.titleTextField.getText()) || !selectedCalamity.getMessage().equals(informationTextArea.getText())) {

                this.selectedCalamity.setTitle(titleTextField.getText());
                this.selectedCalamity.setMessage(informationTextArea.getText());

                CalamityRequest calamityRequest = new CalamityRequest();
                calamityRequest.updateCalamity(user.getToken(),
                        selectedCalamity.getId(),
                        selectedCalamity.getTitle(),
                        selectedCalamity.getMessage(),
                        selectedCalamity.getLocation().getId(),
                        selectedCalamity.getLocation().getLatitude(),
                        selectedCalamity.getLocation().getLongitude(),
                        selectedCalamity.getLocation().getRadius(),
                        selectedCalamity.getConfirmation(),
                        selectedCalamity.getClosed());

            }

        } else {
            titleTextField.setEditable(true);
            informationTextArea.setEditable(true);
            changeButton.setText("Save changes");
        }
    }

    private void handlePlanAction(ActionEvent actionEvent) {

        if (selectedCalamity == null) {
            return;
        }

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/CreatePlan.fxml"));
        PlanController controller = new PlanController(user, selectedCalamity);
        fxmlLoader.setController(controller);

        try {
            Main.setStage(fxmlLoader.load(), new Stage(), "Create Plan");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleBackAction(ActionEvent actionEvent) {
        Stage stage = (Stage) backButton.getScene().getWindow();
        stage.close();
    }

    private void handleRefreshAction(ActionEvent actionEvent) {
        int index = calamityTable.getSelectionModel().getSelectedIndex();
        refreshCalamityTable();
        calamityTable.requestFocus();
        calamityTable.getSelectionModel().select(index);
        fillCalamityDetails(calamityTable.getSelectionModel().getSelectedItem());
    }

    private void handleAskInfoAction(ActionEvent actionEvent) {
        if (userTable.getSelectionModel().getSelectedIndex() < 0) {
            showMessage("Nothing selected", "Please select a user to ask information!");
        } else {
            ConfirmationMessage message = userRequest.askInformation(user.getToken(), userTable.getSelectionModel().getSelectedItem().getId());

            if (message.getStatus().equals(ConfirmationMessage.StatusType.SUCCES)) {
                showMessage("SUCCESS", "Assignee has been notified");
            } else {
                showMessage("ERROR", "Assignee has not been notified");
            }
        }
    }

    private void initiateTableColumns() {

        calamityTable.setColumnResizePolicy((param) -> true);

        TableColumn calamityName = new TableColumn("Calamity");
        calamityName.getStyleClass().add("foo");
        calamityName.setMinWidth(397);
        calamityName.setCellValueFactory(new PropertyValueFactory<Calamity, String>("title"));
        calamityTable.getColumns().add(calamityName);

        userTable.setColumnResizePolicy((param) -> true);

        TableColumn userName = new TableColumn("Name");
        userName.getStyleClass().add("foo");
        userName.setMinWidth(150);
        userName.setCellValueFactory(new PropertyValueFactory<User, String>("username"));
        userTable.getColumns().add(userName);

        TableColumn userCity = new TableColumn("City");
        userCity.getStyleClass().add("foo");
        userCity.setMinWidth(247);
        userCity.setCellValueFactory(new PropertyValueFactory<User, String>("city"));
        userTable.getColumns().add(userCity);
    }

    private void fillDefaultCalamityDetails() {
        titleTextField.setText("Calamity title");
        creatorTextField.setText("Calamity creator");
        dateTextField.setText("Calamity date");
        informationTextArea.setText("Calamity information");
    }

    private void fillCalamityDetails(Calamity calamity) throws NullPointerException {
        this.selectedCalamity = calamity;

        updateMap(calamity);
        getWeatherData(calamity);
        refreshUserTable();

        titleTextField.setText(calamity.getTitle());
        creatorTextField.setText(calamity.getUser().toString());
        dateTextField.setText(calamity.getDate().toString());
        informationTextArea.setText(calamity.getMessage());
        informationTextArea.setWrapText(true);

        refreshSocialPosts();
    }

    public void mapInitialized() {
        LatLong fontys = new LatLong(51.4520890, 5.4819830);

        //Set the initial properties of the map.
        MapOptions mapOptions = new MapOptions();

        mapOptions.center(fontys)
                .mapType(MapTypeIdEnum.ROADMAP)
                .overviewMapControl(false)
                .panControl(false)
                .rotateControl(false)
                .scaleControl(false)
                .streetViewControl(false)
                .zoomControl(true)
                .zoom(15);

        map = googleMapView.createMap(mapOptions);

        //Add markers to the map
        MarkerOptions markerOptions1 = new MarkerOptions();
        markerOptions1.position(fontys);

        Marker fontysM = new Marker(markerOptions1);
        map.addMarker(fontysM);
    }

    private void updateMap(Calamity calamity) throws NullPointerException {
        LatLong location = new LatLong(calamity.getLocation().getLatitude(), calamity.getLocation().getLongitude());
        MapOptions mapOptions = new MapOptions();
        mapOptions.center(location)
                .mapType(MapTypeIdEnum.ROADMAP)
                .overviewMapControl(false)
                .panControl(false)
                .rotateControl(false)
                .scaleControl(false)
                .streetViewControl(false)
                .zoomControl(true)
                .zoom(15);

        map = googleMapView.createMap(mapOptions);
        map.addUIEventHandler(UIEventType.click, (JSObject obj) -> {
            LatLong ll = new LatLong((JSObject) obj.getMember("latLng"));
            if (changeButton.getText().equalsIgnoreCase("Save changes")) {
                System.out.println("LatLong: lat: " + ll.getLatitude() + " lng: " + ll.getLongitude());
                this.selectedCalamity.getLocation().setLatitude(ll.getLatitude());
                this.selectedCalamity.getLocation().setLongitude(ll.getLongitude());

                updateMap(selectedCalamity);
            }
        });

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(location);

        Marker marker = new Marker(markerOptions);
        map.addMarker(marker);

        CircleOptions circleO = new CircleOptions();
        circleO.center(location);
        circleO.radius(calamity.getLocation().getRadius());

        Circle circle = new Circle(circleO);
        map.addMapShape(circle);

        InfoWindowOptions iWO = new InfoWindowOptions();
        iWO.content(calamity.getTitle());

        InfoWindow iw = new InfoWindow(iWO);
        iw.open(map, marker);
    }

    private void getWeatherData(Calamity calamity) {
        WeatherRequest request = new WeatherRequest();
        ObjectMapper mapper = new ObjectMapper();

        ConfirmationMessage message = mapper.convertValue(request.getCurrentWeather(user.getToken(),
                calamity.getLocation().getLongitude(),
                calamity.getLocation().getLatitude()), ConfirmationMessage.class);

        if (message.getStatus().equals(ConfirmationMessage.StatusType.ERROR)) {
            weatherLabel.setText("There is no weather data available\n\n" +
                    "Message: " + message.getMessage());
        } else {

            Weather weather = mapper.convertValue(message.getReturnObject(), Weather.class);
            weatherLabel.setText(
                    "Location: " + weather.getCityName() + "(" + weather.getCountryCode() + ")   (" + weather.getLatLong().getLatitude() + "/" + weather.getLatLong().getLongitude() + ")\n\n" +
                            "Temperature (°C):" + weather.getTemperature().getTemperature() + "\n" +
                            " (Min: " + weather.getTemperature().getMinTemperature() + " / Max: " + weather.getTemperature().getMaxTemperature() + ")\n\n" +
                            "Extra information: \n" +
                            "Clouds: " + weather.getClouds() + "%\n" +
                            "Humidity: " + weather.getTemperature().getHumidity() + "%\n" +
                            "Wind Speed: " + weather.getWindSpeed() + "");
        }
    }

    private void showMessage(String title, String text) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(text);
        alert.showAndWait();
    }

    class RefreshTask extends TimerTask {
        @Override
        public void run() {
            refreshCalamityTable();
        }
    }
}

