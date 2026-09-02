package test_case;

import dao.TreatmentDAO;
import model.Treatment;
import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;

public class TreatmentTest {

    private TreatmentDAO treatmentDAO;

    @Before
    public void setUp() {
        treatmentDAO = new TreatmentDAO();
    }

    // =============================================
    // POSITIVE TESTS (3)
    // =============================================

    @Test
    public void testAddTreatmentValid1() {
        long timestamp = System.currentTimeMillis();
        Treatment t = new Treatment();
        t.setTreatmentName("Cleaning " + timestamp);
        t.setCategory("Preventive");
        t.setDescription("Professional teeth cleaning");
        t.setCost(5000);
        t.setDuration(30);
        t.setActive(true);
        
        boolean result = treatmentDAO.addTreatment(t);
        
        assertTrue("Valid treatment should be saved", result);
        System.out.println("PASS: Add Treatment - Cleaning");
    }

    @Test
    public void testAddTreatmentValid2() {
        long timestamp = System.currentTimeMillis();
        Treatment t = new Treatment();
        t.setTreatmentName("Root Canal " + timestamp);
        t.setCategory("Restorative");
        t.setDescription("Root canal treatment");
        t.setCost(25000);
        t.setDuration(90);
        t.setActive(true);
        
        boolean result = treatmentDAO.addTreatment(t);
        
        assertTrue("Valid treatment should be saved", result);
        System.out.println("PASS: Add Treatment - Root Canal");
    }

    @Test
    public void testAddTreatmentValid3() {
        long timestamp = System.currentTimeMillis();
        Treatment t = new Treatment();
        t.setTreatmentName("X-Ray " + timestamp);
        t.setCategory("Diagnostic");
        t.setDescription("Dental X-Ray");
        t.setCost(3000);
        t.setDuration(15);
        t.setActive(true);
        
        boolean result = treatmentDAO.addTreatment(t);
        
        assertTrue("Valid treatment should be saved", result);
        System.out.println("PASS: Add Treatment - X-Ray");
    }

    // =============================================
    // NEGATIVE TESTS (3)
    // =============================================

    @Test
    public void testAddTreatmentInvalidEmptyName() {
        Treatment t = new Treatment();
        t.setTreatmentName("");
        t.setCategory("Preventive");
        t.setCost(5000);
        t.setDuration(30);
        t.setActive(true);
        
        boolean result = treatmentDAO.addTreatment(t);
        
        assertFalse("Empty name should fail", result);
        System.out.println("PASS: Invalid Treatment - Empty Name");
    }

    @Test
    public void testAddTreatmentInvalidNegativeCost() {
        Treatment t = new Treatment();
        t.setTreatmentName("Test Treatment");
        t.setCategory("Preventive");
        t.setCost(-1000);
        t.setDuration(30);
        t.setActive(true);
        
        boolean result = treatmentDAO.addTreatment(t);
        
        assertFalse("Negative cost should fail", result);
        System.out.println("PASS: Invalid Treatment - Negative Cost");
    }

    @Test
    public void testAddTreatmentInvalidZeroDuration() {
        Treatment t = new Treatment();
        t.setTreatmentName("Test Treatment");
        t.setCategory("Preventive");
        t.setCost(5000);
        t.setDuration(0);
        t.setActive(true);
        
        boolean result = treatmentDAO.addTreatment(t);
        
        assertFalse("Zero duration should fail", result);
        System.out.println("PASS: Invalid Treatment - Zero Duration");
    }
}