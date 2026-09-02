package test_case;

import dao.PatientDAO;
import model.Patient;
import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;
import java.sql.Date;
import java.time.LocalDate;

public class PatientTest {

    private PatientDAO patientDAO;

    @Before
    public void setUp() {
        patientDAO = new PatientDAO();
    }

    // =============================================
    // POSITIVE TESTS (3)
    // =============================================

    @Test
    public void testAddPatientValid1() {
        long timestamp = System.currentTimeMillis();
        String uniqueEmail = "john" + timestamp + "@email.com";
        String uniquePhone = "071" + (timestamp % 10000000);
        
        Patient p = createPatient("John Doe " + timestamp, uniquePhone, uniqueEmail, "1990-01-01");
        boolean result = patientDAO.addPatient(p);
        
        assertTrue("Valid patient should be saved", result);
        System.out.println("PASS: Add Patient Valid 1 - John Doe " + timestamp);
    }

    @Test
    public void testAddPatientValid2() {
        long timestamp = System.currentTimeMillis();
        String uniqueEmail = "jane" + timestamp + "@email.com";
        String uniquePhone = "077" + (timestamp % 10000000);
        
        Patient p = createPatient("Jane Smith " + timestamp, uniquePhone, uniqueEmail, "1985-05-15");
        boolean result = patientDAO.addPatient(p);
        
        assertTrue("Valid patient should be saved", result);
        System.out.println("PASS: Add Patient Valid 2 - Jane Smith " + timestamp);
    }

    @Test
    public void testAddPatientValid3() {
        long timestamp = System.currentTimeMillis();
        String uniqueEmail = "sam" + timestamp + "@email.com";
        String uniquePhone = "076" + (timestamp % 10000000);
        
        Patient p = createPatient("Sam Perera " + timestamp, uniquePhone, uniqueEmail, "2000-10-20");
        boolean result = patientDAO.addPatient(p);
        
        assertTrue("Valid patient should be saved", result);
        System.out.println("PASS: Add Patient Valid 3 - Sam Perera " + timestamp);
    }

    // =============================================
    // NEGATIVE TESTS (3)
    // =============================================

    @Test
    public void testAddPatientInvalidEmptyName() {
        long timestamp = System.currentTimeMillis();
        String uniqueEmail = "test" + timestamp + "@email.com";
        String uniquePhone = "071" + (timestamp % 10000000);
        
        Patient p = createPatient("", uniquePhone, uniqueEmail, "1990-01-01");
        boolean result = patientDAO.addPatient(p);
        
        assertFalse("Empty name should fail", result);
        System.out.println("PASS: Invalid Patient - Empty Name");
    }

    @Test
    public void testAddPatientInvalidPhone() {
        long timestamp = System.currentTimeMillis();
        String uniqueEmail = "phone" + timestamp + "@email.com";
        
        Patient p = createPatient("Test User", "123", uniqueEmail, "1990-01-01");
        boolean result = patientDAO.addPatient(p);
        
        assertFalse("Invalid phone should fail", result);
        System.out.println("PASS: Invalid Patient - Invalid Phone");
    }

    @Test
    public void testAddPatientInvalidEmail() {
        long timestamp = System.currentTimeMillis();
        String uniquePhone = "071" + (timestamp % 10000000);
        
        Patient p = createPatient("Test User", uniquePhone, "invalid-email", "1990-01-01");
        boolean result = patientDAO.addPatient(p);
        
        assertFalse("Invalid email should fail", result);
        System.out.println("PASS: Invalid Patient - Invalid Email");
    }

    // =============================================
    // POSITIVE TESTS - UPDATE PATIENT (3)
    // =============================================

    @Test
    public void testUpdatePatientValid1() {
        long timestamp = System.currentTimeMillis();
        String uniqueEmail = "update1" + timestamp + "@email.com";
        String uniquePhone = "071" + (timestamp % 10000000);
        
        Patient p = createPatient("Update Test", uniquePhone, uniqueEmail, "1990-01-01");
        patientDAO.addPatient(p);
        
        p.setPatientName("Updated Name " + timestamp);
        boolean result = patientDAO.updatePatient(p);
        
        assertTrue("Patient should be updated", result);
        System.out.println("PASS: Update Patient - Name");
    }

    @Test
    public void testUpdatePatientValid2() {
        long timestamp = System.currentTimeMillis();
        String uniqueEmail = "update2" + timestamp + "@email.com";
        String uniquePhone = "077" + (timestamp % 10000000);
        
        Patient p = createPatient("Update Test2", uniquePhone, uniqueEmail, "1990-01-01");
        patientDAO.addPatient(p);
        
        p.setEmail("newemail" + timestamp + "@domain.com");
        boolean result = patientDAO.updatePatient(p);
        
        assertTrue("Patient email should be updated", result);
        System.out.println("PASS: Update Patient - Email");
    }

    @Test
    public void testUpdatePatientValid3() {
        long timestamp = System.currentTimeMillis();
        String uniqueEmail = "update3" + timestamp + "@email.com";
        String uniquePhone = "076" + (timestamp % 10000000);
        
        Patient p = createPatient("Update Test3", uniquePhone, uniqueEmail, "1990-01-01");
        patientDAO.addPatient(p);
        
        p.setAddress("New Address, Colombo - " + timestamp);
        boolean result = patientDAO.updatePatient(p);
        
        assertTrue("Patient address should be updated", result);
        System.out.println("PASS: Update Patient - Address");
    }

    // =============================================
    // NEGATIVE TESTS - UPDATE PATIENT (3)
    // =============================================

    @Test
    public void testUpdatePatientInvalidNonExistent() {
        Patient p = new Patient();
        p.setPatientId(999999);
        p.setPatientName("Non-existent");
        boolean result = patientDAO.updatePatient(p);
        
        assertFalse("Non-existent patient should fail", result);
        System.out.println("PASS: Update Invalid - Non-existent Patient");
    }

    @Test
    public void testUpdatePatientInvalidPhone() {
        long timestamp = System.currentTimeMillis();
        String uniqueEmail = "updatephone" + timestamp + "@email.com";
        String uniquePhone = "071" + (timestamp % 10000000);
        
        Patient p = createPatient("Test User", uniquePhone, uniqueEmail, "1990-01-01");
        patientDAO.addPatient(p);
        
        p.setContactNumber("123");
        boolean result = patientDAO.updatePatient(p);
        
        assertFalse("Invalid phone should fail", result);
        System.out.println("PASS: Update Invalid - Invalid Phone");
    }

    @Test
    public void testUpdatePatientInvalidEmail() {
        long timestamp = System.currentTimeMillis();
        String uniqueEmail = "updateemail" + timestamp + "@email.com";
        String uniquePhone = "077" + (timestamp % 10000000);
        
        Patient p = createPatient("Test User", uniquePhone, uniqueEmail, "1990-01-01");
        patientDAO.addPatient(p);
        
        p.setEmail("invalid@");
        boolean result = patientDAO.updatePatient(p);
        
        assertFalse("Invalid email should fail", result);
        System.out.println("PASS: Update Invalid - Invalid Email");
    }

    // =============================================
    // HELPER METHOD
    // =============================================

    private Patient createPatient(String name, String phone, String email, String dob) {
        Patient p = new Patient();
        p.setPatientName(name);
        p.setContactNumber(phone);
        p.setEmail(email);
        p.setDateOfBirth(Date.valueOf(LocalDate.parse(dob)));
        p.setGender("Male");
        p.setAddress("Test Address");
        return p;
    }
}