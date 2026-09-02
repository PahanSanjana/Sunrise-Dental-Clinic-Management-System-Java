package test_case;

import model.User;
import model.User.UserRole;
import model.RolePermissions;
import org.junit.Test;
import static org.junit.Assert.*;

public class RoleAccessTest {

    // =============================================
    // POSITIVE TESTS (3)
    // =============================================

    @Test
    public void testAdminFullAccess() {
        User admin = new User();
        admin.setRole(UserRole.ADMIN);
        
        boolean canDeletePatients = RolePermissions.hasActionPermission(admin.getRole(), "DELETE_PATIENTS");
        boolean canDeleteBills = RolePermissions.hasActionPermission(admin.getRole(), "DELETE_BILLS");
        boolean canAddAppointments = RolePermissions.hasActionPermission(admin.getRole(), "ADD_APPOINTMENTS");
        
        assertTrue("Admin should delete patients", canDeletePatients);
        assertTrue("Admin should delete bills", canDeleteBills);
        assertTrue("Admin should book appointments", canAddAppointments);
        
        System.out.println("PASS: Admin Full Access");
    }

    @Test
    public void testReceptionAccess() {
        User reception = new User();
        reception.setRole(UserRole.RECEPTION);
        
        boolean canViewPatients = RolePermissions.hasActionPermission(reception.getRole(), "VIEW_PATIENTS");
        boolean canAddPatients = RolePermissions.hasActionPermission(reception.getRole(), "ADD_PATIENTS");
        boolean canEditPatients = RolePermissions.hasActionPermission(reception.getRole(), "EDIT_PATIENTS");
        
        assertTrue("Reception should view patients", canViewPatients);
        assertTrue("Reception should add patients", canAddPatients);
        assertTrue("Reception should edit patients", canEditPatients);
        
        System.out.println("PASS: Reception Access");
    }

    @Test
    public void testDentistAccess() {
        User dentist = new User();
        dentist.setRole(UserRole.DENTIST);

        boolean canViewPatients = RolePermissions.hasActionPermission(dentist.getRole(), "VIEW_PATIENTS");
        boolean canViewPatients2 = RolePermissions.hasActionPermission(dentist.getRole(), "VIEW_PATIENTS");
        boolean canViewPatients3 = RolePermissions.hasActionPermission(dentist.getRole(), "VIEW_PATIENTS");

        assertTrue("Dentist should view patients", canViewPatients);
        assertTrue("Dentist should view patients", canViewPatients2);
        assertTrue("Dentist should view patients", canViewPatients3);

        System.out.println("PASS: Dentist Access");
    }

    // =============================================
    // NEGATIVE TESTS (3)
    // =============================================

    @Test
    public void testPatientNoDeleteAccess() {
        User patient = new User();
        patient.setRole(UserRole.PATIENT);
        
        boolean canDeletePatients = RolePermissions.hasActionPermission(patient.getRole(), "DELETE_PATIENTS");
        boolean canDeleteBills = RolePermissions.hasActionPermission(patient.getRole(), "DELETE_BILLS");
        boolean canEditPatients = RolePermissions.hasActionPermission(patient.getRole(), "EDIT_PATIENTS");
        
        assertFalse("Patient should not delete patients", canDeletePatients);
        assertFalse("Patient should not delete bills", canDeleteBills);
        assertFalse("Patient should not edit patients", canEditPatients);
        
        System.out.println("PASS: Patient No Delete Access");
    }

    @Test
    public void testPatientNoBillAccess() {
        User patient = new User();
        patient.setRole(UserRole.PATIENT);
        
        boolean canAddBills = RolePermissions.hasActionPermission(patient.getRole(), "ADD_BILLS");
        boolean canEditBills = RolePermissions.hasActionPermission(patient.getRole(), "EDIT_BILLS");
        
        assertFalse("Patient should not add bills", canAddBills);
        assertFalse("Patient should not edit bills", canEditBills);
        
        System.out.println("PASS: Patient No Bill Access");
    }

    @Test
    public void testReceptionNoDeleteAccess() {
        User reception = new User();
        reception.setRole(UserRole.RECEPTION);
        
        boolean canDeletePatients = RolePermissions.hasActionPermission(reception.getRole(), "DELETE_PATIENTS");
        boolean canDeleteBills = RolePermissions.hasActionPermission(reception.getRole(), "DELETE_BILLS");
        
        assertFalse("Reception should not delete patients", canDeletePatients);
        assertFalse("Reception should not delete bills", canDeleteBills);
        
        System.out.println("PASS: Reception No Delete Access");
    }
}