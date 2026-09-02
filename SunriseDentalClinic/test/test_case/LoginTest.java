package test_case;

import dao.UserDAO;
import model.User;
import model.User.UserRole;

import org.junit.Test;
import static org.junit.Assert.*;

public class LoginTest {

    private UserDAO userDAO = new UserDAO();


    // POSITIVE TESTS

    @Test
    public void testValidLoginAdmin() {
        User user = userDAO.authenticate("admin", "admin123");

        assertNotNull("Admin login should succeed", user);
        assertEquals(UserRole.ADMIN, user.getRole());

        System.out.println("PASS: Admin Login");
    }

    @Test
    public void testValidLoginReception() {
        User user = userDAO.authenticate("test13", "test13");

        assertNotNull("Reception login should succeed", user);
        assertEquals(UserRole.RECEPTION, user.getRole());

        System.out.println("PASS: Reception Login");
    }

    @Test
    public void testValidLoginDentist() {
        User user = userDAO.authenticate("test12", "test12");

        assertNotNull("Dentist login should succeed", user);
        assertEquals(UserRole.DENTIST, user.getRole());

        System.out.println("PASS: Dentist Login");
    }

    // NEGATIVE TESTS

    @Test
    public void testInvalidLoginWrongPassword() {
        User user = userDAO.authenticate("admin", "wrong");

        assertNull("Wrong password should fail", user);

        System.out.println("PASS: Wrong Password");
    }

    @Test
    public void testInvalidLoginWrongUsername() {
        User user = userDAO.authenticate("wronguser", "anything");

        assertNull("Wrong username should fail", user);

        System.out.println("PASS: Wrong Username");
    }

    @Test
    public void testInvalidLoginEmptyCredentials() {
        User user1 = userDAO.authenticate("", "");
        User user2 = userDAO.authenticate(null, null);

        assertNull("Empty credentials should fail", user1);
        assertNull("Null credentials should fail", user2);

        System.out.println("PASS: Empty/Null Credentials");
    }
}