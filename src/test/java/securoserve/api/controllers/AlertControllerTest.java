package securoserve.api.controllers;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import securoserve.api.TestUtil;
import securoserve.api.datarepo.database.Database;
import securoserve.library.Alert;
import securoserve.library.Calamity;
import securoserve.library.User;

import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by Jandie on 2017-05-01.
 */
public class AlertControllerTest {
    private Database database;
    private AlertController ac;
    private CalamityController cc;
    private User user;

    @Before
    public void setUp() throws Exception {
        database = TestUtil.cleanAndBuildTestDatabase();
        ac = new AlertController(database);
        cc = new CalamityController(database);
        user = TestUtil.createTempUser(database);
    }

    @Test
    public void addAlert() throws Exception {
        Alert alert = (Alert) ac.addAlert(user.getToken(), "testAlert", "testDescription",
                55, 56, 1).getReturnObject();

        assertEquals("testAlert", alert.getName());
        assertEquals("testDescription", alert.getDescription());
        assertEquals(55, alert.getLocation().getLatitude(), 0.01);
        assertEquals(56, alert.getLocation().getLongitude(), 0.01);
        assertEquals(1, alert.getLocation().getRadius(), 0.01);

        alert = ((List<Alert>) ac.getAllAlerts(user.getToken()).getReturnObject()).get(0);

        assertEquals("testAlert", alert.getName());
        assertEquals("testDescription", alert.getDescription());
        assertEquals(55, alert.getLocation().getLatitude(), 0.01);
        assertEquals(56, alert.getLocation().getLongitude(), 0.01);
        assertEquals(1, alert.getLocation().getRadius(), 0.01);
    }

    @Test
    public void removeAlert() throws Exception {
        Alert alert1 = (Alert) ac.addAlert(user.getToken(), "testAlert", "testDescription",
                55, 56, 1).getReturnObject();

        Alert alert2 = (Alert) ac.addAlert(user.getToken(), "testAlert", "testDescription",
                55, 56, 1).getReturnObject();

        ac.removeAlert(user.getToken(), alert1.getId());
        ac.removeAlert(user.getToken(), alert2.getId());

        int size = ((List<Alert>) ac.getAllAlerts(user.getToken()).getReturnObject()).size();

        assertEquals(0, size);
    }

    @Test
    public void proposeCalamity() throws Exception {
        Alert alert1 = (Alert) ac.addAlert(user.getToken(), "testAlert", "testDescription",
                52.369040, 9.748287, 0).getReturnObject();
        Alert alert2 = (Alert) ac.addAlert(user.getToken(), "testAlert", "testDescription",
                52.369105, 9.748333, 0).getReturnObject();
        Alert alert3 = (Alert) ac.addAlert(user.getToken(), "testAlert", "testDescription",
                52.369258, 9.748698, 0).getReturnObject();
        Alert alert4 = (Alert) ac.addAlert(user.getToken(), "testAlert", "testDescription",
                52.369253, 9.748971, 0).getReturnObject();
        Alert alert5 = (Alert) ac.addAlert(user.getToken(), "testAlert", "testDescription",
                52.368909, 9.748703, 0).getReturnObject();
        Alert alert6 = (Alert) ac.addAlert(user.getToken(), "testAlert", "testDescription",
                52.368914, 9.748370, 0).getReturnObject();

        int size = ((List<Calamity>) cc.allCalamity().getReturnObject()).size();

        assertEquals(1, size);
    }

    @After
    public void tearDown() throws Exception {
        TestUtil.deleteTempUser(user, database);
    }
}