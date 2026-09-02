package test_case;

import dao.StaffDAO;
import model.Staff;
import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;
import java.sql.Date;
import java.time.LocalDate;

public class StaffTest {

    private StaffDAO staffDAO;

    @Before
    public void setUp() {
        staffDAO = new StaffDAO();
    }

    // =============================================
    // POSITIVE TESTS (3)
    // =============================================

    @Test
    public void testAddStaffValid1() {
        Staff s = new Staff();
        s.setFirstName("John");
        s.setLastName("Doe");
        s.setPosition("Receptionist");
        s.setDepartment("Front Desk");
        s.setPhone("0712345678");
        s.setEmail("john@clinic.com");
        s.setHireDate(Date.valueOf(LocalDate.now()));
        s.setSalary(50000);
        s.setActive(true);
        
        boolean result = staffDAO.addStaff(s);
        
        assertTrue("Valid staff should be saved", result);
        System.out.println("PASS: Add Staff - John Doe");
    }

    @Test
    public void testAddStaffValid2() {
        Staff s = new Staff();
        s.setFirstName("Mary");
        s.setLastName("Smith");
        s.setPosition("Assistant");
        s.setDepartment("Clinical");
        s.setPhone("0771234567");
        s.setEmail("mary@clinic.com");
        s.setHireDate(Date.valueOf(LocalDate.now()));
        s.setSalary(45000);
        s.setActive(true);
        
        boolean result = staffDAO.addStaff(s);
        
        assertTrue("Valid staff should be saved", result);
        System.out.println("PASS: Add Staff - Mary Smith");
    }

    @Test
    public void testUpdateStaffSalary() {
        Staff s = new Staff();
        s.setFirstName("Test");
        s.setLastName("User");
        s.setPosition("Assistant");
        s.setDepartment("Clinical");
        s.setPhone("0712345678");
        s.setEmail("test@clinic.com");
        s.setHireDate(Date.valueOf(LocalDate.now()));
        s.setSalary(40000);
        s.setActive(true);
        staffDAO.addStaff(s);
        
        s.setSalary(55000);
        boolean result = staffDAO.updateStaff(s);
        
        assertTrue("Staff salary should be updated", result);
        System.out.println("PASS: Update Staff Salary");
    }

    // =============================================
    // NEGATIVE TESTS (3)
    // =============================================

    @Test
    public void testAddStaffInvalidEmptyFirstName() {
        Staff s = new Staff();
        s.setFirstName("");
        s.setLastName("Doe");
        s.setPosition("Receptionist");
        
        boolean result = staffDAO.addStaff(s);
        
        assertFalse("Empty first name should fail", result);
        System.out.println("PASS: Invalid Staff - Empty First Name");
    }

    @Test
    public void testAddStaffInvalidEmptyPosition() {
        Staff s = new Staff();
        s.setFirstName("John");
        s.setLastName("Doe");
        s.setPosition("");
        
        boolean result = staffDAO.addStaff(s);
        
        assertFalse("Empty position should fail", result);
        System.out.println("PASS: Invalid Staff - Empty Position");
    }

    @Test
    public void testAddStaffInvalidNegativeSalary() {
        Staff s = new Staff();
        s.setFirstName("John");
        s.setLastName("Doe");
        s.setPosition("Receptionist");
        s.setSalary(-10000);
        
        boolean result = staffDAO.addStaff(s);
        
        assertFalse("Negative salary should fail", result);
        System.out.println("PASS: Invalid Staff - Negative Salary");
    }
}