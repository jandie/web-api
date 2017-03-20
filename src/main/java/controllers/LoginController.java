package controllers;

import api.ILogin;
import dataRepo.UserRepo;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Manages user login
 *
 * Created by Jandie on 13-3-2017.
 */
@RestController
public class LoginController implements ILogin{

    /**
     * Creates and returns a new token for a user if username and password
     * are correct.
     * @param username The username of the user.
     * @param password The password of the user.
     * @return The token of the user.
     */
    @RequestMapping("/login")
    public String login(@RequestParam(value = "username", defaultValue="") String username,
                        @RequestParam(value = "password", defaultValue="") String password) {
        try {
            return new UserRepo().login(username, password);
        } catch (SQLException | ParseException | NoSuchAlgorithmException e) {
            Logger.getLogger(LoginController.class.getName()).log(Level.SEVERE,
                    "Login failed!", e);

            return null;
        }
    }
}
