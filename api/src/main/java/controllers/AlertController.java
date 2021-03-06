package controllers;

import datarepo.database.Database;
import interfaces.ConfirmationMessage;
import interfaces.IAlert;
import library.Location;
import logic.AlertLogic;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Jandie on 2017-05-01.
 */
@RestController
public class AlertController implements IAlert {

    private AlertLogic alertLogic;

    public AlertController() {
        this.alertLogic = new AlertLogic();
    }

    public AlertController(Database database) {
        this.alertLogic = new AlertLogic(database);
    }

    /**
     * Returns a list with current alerts.
     *
     * @param token The authentication token.
     * @return A list with current alerts.
     */
    @Override
    @RequestMapping("/getallalerts")
    public ConfirmationMessage getAllAlerts(String token) {
        return alertLogic.allAlert(token);
    }

    /**
     * Returns a single alert that matches the id.
     *
     * @param token The authentication token.
     * @param id    The id of the alert.
     * @return A single alert that matches the id.
     */
    @Override
    @RequestMapping("/getalert")
    public ConfirmationMessage getAlert(String token, int id) {
        return alertLogic.getAlert(token, id);
    }

    /**
     * Returns a list of nearby alerts based on the location.
     *
     * @param token    The authentication token.
     * @param location The location to check.
     * @param radius   The radius to check.
     * @return a list of nearby alerts based on the location.
     */
    @Override
    public ConfirmationMessage getNearbyAlerts(String token, Location location, int radius) {
        return alertLogic.getNearbyAlerts(token, location, radius);
    }

    /**
     * Adds a new alert.
     *
     * @param token       The authentication token.
     * @param name        The name of the alert.
     * @param description The description of the alert.
     * @param lat         The latitude of the alert's location.
     * @param lon         The lontitude of the alert's location.
     * @param radius      The radius of the alert's location.
     * @return Confirmation message with feedback about the addition
     * also containing the new alert.
     */
    @Override
    @RequestMapping("/addalert")
    public ConfirmationMessage addAlert(@RequestParam(value = "token") String token,
                                        @RequestParam(value = "name") String name,
                                        @RequestParam(value = "description") String description,
                                        @RequestParam(value = "urgency") int urgency,
                                        @RequestParam(value = "latitude") double lat,
                                        @RequestParam(value = "longitude") double lon,
                                        @RequestParam(value = "radius") double radius) {
        Location location = new Location(-1, lat, lon, radius);
        return alertLogic.addAlert(token, name, description, location, urgency);
    }


    /**
     * Adds alert to calamity
     *
     * @param token       The authentication token.
     * @param name        The name of the alert.
     * @param description The description of the alert.
     * @param urgency
     * @param lat         The latitude of the alert's location.
     * @param lon         The lontitude of the alert's location.
     * @param radius      The radius of the alert's location.
     * @param calamityId  The calamity it is attached to.
     * @return
     */
    @Override
    @RequestMapping("/addalerttocalamity")
    public ConfirmationMessage addAlertToCalamity(@RequestParam(value = "token") String token,
                                                  @RequestParam(value = "name") String name,
                                                  @RequestParam(value = "description") String description,
                                                  @RequestParam(value = "urgency") int urgency,
                                                  @RequestParam(value = "latitude") double lat,
                                                  @RequestParam(value = "longitude") double lon,
                                                  @RequestParam(value = "radius") double radius,
                                                  @RequestParam(value = "calamityid") int calamityId) {
        Location location = new Location(-1, lat, lon, radius);
        return alertLogic.addAlertToCalamity(token, name, description, location, urgency, calamityId);
    }

    /**
     * Updates an alert.
     *
     * @param token       The authentication token.
     * @param id          The id of the alert.
     * @param name        The name of the alert.
     * @param description The description of the alert.
     * @param lat         The latitude of the alert's location.
     * @param lon         The lontitude of the alert's location.
     * @param radius      The radius of the alert's location.
     * @return Confirmation message with feedback about the update.
     */
    @Override
    @RequestMapping("/updatealert")
    public ConfirmationMessage updateAlert(String token, int id, String name, String description, int urgency, double lat, double lon, double radius) {

        return alertLogic.updateAlert(token, id, name, description, urgency, lat, lon, radius);
    }

    /**
     * Deletes an alert.
     *
     * @param token The authentication token.
     * @param id    The id of the token.
     * @return Confirmation message with feedback about the deletion.
     */
    @Override
    @RequestMapping("/removealert")
    public ConfirmationMessage removeAlert(String token, int id) {
        return alertLogic.removeAlert(token, id);
    }
}
