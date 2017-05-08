package securoserve.api.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import securoserve.api.datarepo.database.Database;
import securoserve.api.interfaces.ConfirmationMessage;
import securoserve.api.interfaces.IAlert;
import securoserve.api.logic.AlertLogic;

/**
 * Created by Jandie on 2017-05-01.
 */
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
        return null;
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
        return null;
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
    public ConfirmationMessage addAlert(String token, String name, String description, double lat, double lon, double radius) {
        return null;
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
    public ConfirmationMessage updateAlert(String token, int id, String name, String description, double lat, double lon, double radius) {

        return null;
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
        return null;
    }
}
