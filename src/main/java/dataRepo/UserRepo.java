package dataRepo;

import enums.QueryType;
import library.User;
import utils.HashUtil;

import java.security.NoSuchAlgorithmException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Manager user in the database.
 * <p>
 * Created by Jandie on 20-3-2017.
 */
public class UserRepo {

    /**
     * Contains the intance of the database.
     */
    private Database database;

    /**
     * Defines the default dateformat for user.
     */
    private static final DateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

    /**
     * Creates new instance of database.
     */
    public UserRepo(Database database) {
        this.database = database;
    }

    /**
     * Checks username and password then return a valid token.
     *
     * @param username The username of the user.
     * @param password The password of the user.
     * @return The valid token.
     * @throws SQLException
     * @throws ParseException
     * @throws NoSuchAlgorithmException
     */
    public String login(String username, String password) throws SQLException,
            ParseException, NoSuchAlgorithmException {

        String salt = getUserSalt(username);
        password = HashUtil.hashPassword(password, salt, "SHA-256", "UTF-8");
        int userId;
        String tokenExpiration;
        String token = null;

        String query = "SELECT `id`, `token`, `tokenExpiration` FROM `securoserve`.`User` WHERE `username` = ? AND `passwordhash` = ?";

        List<Object> parameters = new ArrayList<>();
        parameters.add(username);
        parameters.add(password);

        try (ResultSet rs = database.executeQuery(query, parameters, QueryType.QUERY)) {
            if (rs != null && rs.next()) {
                token = rs.getString(2);
                tokenExpiration = rs.getString(3);
                token = getToken(token, tokenExpiration);
            }
        }

        return token;
    }

    /**
     * Decides whether the token needs to be renewed or not and handles
     * accordingly.
     *
     * @param token           The token of the user.
     * @param tokenExpiration The expiration date of the user token.
     * @return The valid token.
     * @throws SQLException
     * @throws ParseException
     * @throws NoSuchAlgorithmException
     */
    private String getToken(String token, String tokenExpiration)
            throws SQLException, ParseException, NoSuchAlgorithmException {

        if (sdf.parse(tokenExpiration).getTime() > new Date().getTime()) {
            return token;
        }

        return updateToken(token, true);
    }

    /**
     * Updates and generates a new token in the database and renews the expiration date.
     *
     * @param authToken The user id
     * @return The new valid token.
     * @throws NoSuchAlgorithmException
     * @throws SQLException
     */
    private String updateToken(String authToken, boolean regenerate) throws NoSuchAlgorithmException, SQLException {
        String token;

        if (regenerate) {
            token = HashUtil.generateSalt();
        } else {
            token = authToken;
        }

        Calendar date = Calendar.getInstance();
        date.add(Calendar.MINUTE, 15);

        String tokenExpiration = sdf.format(date.getTime());
        String query = "UPDATE `securoserve`.`User` SET `token` = ?, `tokenexpiration` = ? WHERE `token` = ?";

        List<Object> parameters = new ArrayList<>();
        parameters.add(token);
        parameters.add(tokenExpiration);
        parameters.add(authToken);

        database.executeQuery(query, parameters, QueryType.NON_QUERY);

        return token;
    }

    /**
     * Fetches the user salt from the database.
     *
     * @param username The username of the user.
     * @return The salt of the user.
     * @throws SQLException
     */
    private String getUserSalt(String username) throws SQLException {
        String query = "SELECT `Salt` FROM `securoserve`.`User` WHERE `Username` = ?";

        List<Object> parameters = new ArrayList<>();
        parameters.add(username);

        String salt = null;

        try (ResultSet rs = database.executeQuery(query, parameters, QueryType.QUERY)) {
            if (rs != null && rs.next()) {
                salt = rs.getString(1);
            }
        }

        return salt;
    }

    /**
     * Inserts a new user to the database.
     *
     * @param userTypeId         The id of the usertype.
     * @param calamityAssigneeId The calamityAssigneeId.
     * @param buildingId         The id of the building.
     * @param username           The username of the user.
     * @param password           The password of the user.
     * @param email              The email of the user.
     * @param city               The city of the user.
     * @return The newly created user.
     * @throws NoSuchAlgorithmException
     * @throws SQLException
     */
    public User register(int userTypeId, int calamityAssigneeId, int buildingId, String username,
                         String password, String email, String city)
            throws NoSuchAlgorithmException, SQLException {
        User user = null;

        Calendar date = Calendar.getInstance();
        date.add(Calendar.MINUTE, 15);

        String salt = HashUtil.generateSalt();
        password = HashUtil.hashPassword(password, salt, "SHA-256", "UTF-8");
        String token = HashUtil.generateSalt();
        String tokenExpiration = sdf.format(date.getTime());
        String query = "INSERT INTO `securoserve`.`User` " +
                "(`UserTypeID`, `CalamityAssigneeID`, `BuildingID`, `Username`, " +
                "`PasswordHash`, `Salt`, `Email`, `City`, `Token`, `TokenExpiration`) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        List<Object> parameters = new ArrayList<>();
        parameters.add(userTypeId);
        parameters.add(calamityAssigneeId);
        parameters.add(buildingId);
        parameters.add(username);
        parameters.add(password);
        parameters.add(salt);
        parameters.add(email);
        parameters.add(city);
        parameters.add(token);
        parameters.add(tokenExpiration);

        try (ResultSet rs = database.executeQuery(query, parameters, QueryType.INSERT)){
            if (rs.next()) {
                int id = rs.getInt(1);

                user = new User(id, null, null, null,
                        username, email, city, token);
            }
        }

        return user;
    }

    /**
     * Deletes a user from the database by id.
     *
     * @param userId The id of the user to delete.
     */
    public void deleteUser(int userId) throws SQLException {
        String query = "DELETE FROM `securoserve`.`User` WHERE `id` = ?";

        List<Object> parameters = new ArrayList<>();
        parameters.add(userId);

        database.executeQuery(query, parameters, QueryType.NON_QUERY);
    }

    public User getUser(String token) throws SQLException, NoSuchAlgorithmException, ParseException {
        User user = null;
        String query = "SELECT `ID`, `UserTypeID`, `CalamityAssigneeID`, `BuildingID`, `Username`, " +
                "`Email`,  `City`, `TokenExpiration` FROM `securoserve`.`User` WHERE `Token` = ?";

        List<Object> parameters =  new ArrayList<>();
        parameters.add(token);

        try (ResultSet rs = database.executeQuery(query, parameters, QueryType.QUERY)) {
            if (rs.next()) {
                int id = rs.getInt(1);
                int userTypeId = rs.getInt(2);
                int calamityId = rs.getInt(3);
                int buildingId = rs.getInt(4);
                String username = rs.getString(5);
                String email = rs.getString(6);
                String city = rs.getString(7);

                if (new Date().after(sdf.parse(rs.getString(8)))) {
                    return null;
                }

                user = new User(id, null, null, null, username, email, city, token);
            }
        }

        if (user != null) {
            updateToken(token, false);
        }

        return user;
    }
}
