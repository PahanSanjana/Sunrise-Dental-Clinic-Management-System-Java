package test_case;

import dao.DentistDAO;
import model.Dentist;
import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;

public class DentistTest {

    private DentistDAO dentistDAO;

    @Before
    public void setUp() {
        dentistDAO = new DentistDAO();
    }

    // =============================================
    // POSITIVE TESTS (3)
    // =============================================

    @Test
    public void testAddDentistValid1() {
        long timestamp = System.currentTimeMillis();
        Dentist d = new Dentist();
        d.setDentistName("Dr. Perera " + timestamp);
        d.setSpecialization("Orthodontist");
        d.setLicenseNumber("LIC-" + timestamp + "-001");
        d.setPhone("0712345678");
        d.setEmail("perera" + timestamp + "@clinic.com");
        d.setWorkingHours("Mon-Fri 9-5");
        d.setYearsOfExperience(10);
        d.setConsultationFee(5000);
        d.setAvailable(true);
        
        boolean result = dentistDAO.addDentist(d);
        
        assertTrue("Valid dentist should be saved", result);
        System.out.println("PASS: Add Dentist - Dr. Perera");
    }

    @Test
    public void testAddDentistValid2() {
        long timestamp = System.currentTimeMillis();
        Dentist d = new Dentist();
        d.setDentistName("Dr. Fernando " + timestamp);
        d.setSpecialization("Surgeon");
        d.setLicenseNumber("LIC-" + timestamp + "-002");
        d.setPhone("0771234567");
        d.setEmail("fernando" + timestamp + "@clinic.com");
        d.setWorkingHours("Mon-Sat 8-4");
        d.setYearsOfExperience(15);
        d.setConsultationFee(7000);
        d.setAvailable(true);
        
        boolean result = dentistDAO.addDentist(d);
        
        assertTrue("Valid dentist should be saved", result);
        System.out.println("PASS: Add Dentist - Dr. Fernando");
    }

    @Test
    public void testToggleAvailability() {
        long timestamp = System.currentTimeMillis();
        Dentist d = new Dentist();
        d.setDentistName("Dr. Silva " + timestamp);
        d.setSpecialization("General");
        d.setLicenseNumber("LIC-" + timestamp + "-003");
        d.setPhone("0762345678");
        d.setEmail("silva" + timestamp + "@clinic.com");
        d.setWorkingHours("Mon-Fri 9-5");
        d.setYearsOfExperience(5);
        d.setConsultationFee(3000);
        d.setAvailable(true);
        
        boolean added = dentistDAO.addDentist(d);
        assertTrue("Dentist should be added first", added);
        
        boolean result = dentistDAO.updateAvailability(d.getDentistId(), false);
        
        assertTrue("Availability should be toggled", result);
        System.out.println("PASS: Toggle Dentist Availability");
    }

    // =============================================
    // NEGATIVE TESTS (3)
    // =============================================

    @Test
    public void testAddDentistInvalidEmptyName() {
        long timestamp = System.currentTimeMillis();
        Dentist d = new Dentist();
        d.setDentistName("");
        d.setSpecialization("General");
        d.setLicenseNumber("LIC-" + timestamp + "-004");
        d.setPhone("0712345678");
        d.setEmail("test" + timestamp + "@clinic.com");
        d.setWorkingHours("Mon-Fri 9-5");
        d.setYearsOfExperience(5);
        d.setConsultationFee(3000);
        d.setAvailable(true);
        
        boolean result = dentistDAO.addDentist(d);
        
        assertFalse("Empty name should fail", result);
        System.out.println("PASS: Invalid Dentist - Empty Name");
    }

    @Test
    public void testAddDentistInvalidEmptySpecialization() {
        long timestamp = System.currentTimeMillis();
        Dentist d = new Dentist();
        d.setDentistName("Dr. Test " + timestamp);
        d.setSpecialization("");
        d.setLicenseNumber("LIC-" + timestamp + "-005");
        d.setPhone("0712345678");
        d.setEmail("test" + timestamp + "@clinic.com");
        d.setWorkingHours("Mon-Fri 9-5");
        d.setYearsOfExperience(5);
        d.setConsultationFee(3000);
        d.setAvailable(true);
        
        boolean result = dentistDAO.addDentist(d);
        
        assertFalse("Empty specialization should fail", result);
        System.out.println("PASS: Invalid Dentist - Empty Specialization");
    }

    @Test
    public void testAddDentistInvalidPhone() {
        long timestamp = System.currentTimeMillis();
        Dentist d = new Dentist();
        d.setDentistName("Dr. Test " + timestamp);
        d.setSpecialization("General");
        d.setLicenseNumber("LIC-" + timestamp + "-006");
        d.setPhone("123");
        d.setEmail("test" + timestamp + "@clinic.com");
        d.setWorkingHours("Mon-Fri 9-5");
        d.setYearsOfExperience(5);
        d.setConsultationFee(3000);
        d.setAvailable(true);
        
        boolean result = dentistDAO.addDentist(d);
        
        assertFalse("Invalid phone should fail", result);
        System.out.println("PASS: Invalid Dentist - Invalid Phone");
    }
}