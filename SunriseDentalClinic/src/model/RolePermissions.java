package model;

import model.User.UserRole;
import java.util.*;

public class RolePermissions {
    
    // =====================================================
    // PERMISSION MAPS
    // =====================================================
    
    private static final Map<UserRole, Set<String>> PAGE_ACCESS = new HashMap<>();
    private static final Map<UserRole, Set<String>> ACTION_PERMISSIONS = new HashMap<>();
    
    // =====================================================
    // STATIC INITIALIZER - Build permission maps
    // =====================================================
    
    static {
        // =============================================
        // PAGE ACCESS PERMISSIONS
        // =============================================
        
        // ADMIN - Full access to all pages
        Set<String> adminPages = new HashSet<>();
        adminPages.addAll(getAllCardNames());
        PAGE_ACCESS.put(UserRole.ADMIN, adminPages);
        
        // RECEPTION - Front desk access
        Set<String> receptionPages = new HashSet<>();
        receptionPages.add("DASHBOARD");
        receptionPages.add("USER_PROFILE");
        receptionPages.add("PATIENT_LIST");
        receptionPages.add("PATIENT_ADD");
        receptionPages.add("PATIENT_DETAILS");
        receptionPages.add("APPOINTMENT_LIST");
        receptionPages.add("APPOINTMENT_BOOK");
        receptionPages.add("APPOINTMENT_SCHEDULE");
        receptionPages.add("APPOINTMENT_DETAILS");
        receptionPages.add("BILL_LIST");
        receptionPages.add("BILL_GENERATE");
        receptionPages.add("BILL_DETAILS");
        receptionPages.add("REPORT_DASHBOARD");
        receptionPages.add("REPORT_REVENUE");
        receptionPages.add("REPORT_SCHEDULE");
        receptionPages.add("REPORT_PATIENT");
        receptionPages.add("DENTIST_LIST");
        receptionPages.add("DENTIST_DETAILS");
        receptionPages.add("TREATMENT_LIST");
        receptionPages.add("TREATMENT_ADD");
        receptionPages.add("TREATMENT_DETAILS");
        receptionPages.add("HELP");
        PAGE_ACCESS.put(UserRole.RECEPTION, receptionPages);
        
        // DENTIST - Clinical access
        Set<String> dentistPages = new HashSet<>();
        dentistPages.add("DASHBOARD");
        dentistPages.add("USER_PROFILE");
        dentistPages.add("PATIENT_LIST");
        dentistPages.add("PATIENT_DETAILS");
        dentistPages.add("APPOINTMENT_LIST");
        dentistPages.add("APPOINTMENT_SCHEDULE");
        dentistPages.add("APPOINTMENT_DETAILS");
        dentistPages.add("BILL_LIST");
        dentistPages.add("BILL_GENERATE");
        dentistPages.add("BILL_DETAILS");
        dentistPages.add("REPORT_PATIENT");
        dentistPages.add("TREATMENT_LIST");
        dentistPages.add("TREATMENT_DETAILS");
        dentistPages.add("HELP");
        PAGE_ACCESS.put(UserRole.DENTIST, dentistPages);
        
        // PATIENT - Self-service access
        Set<String> patientPages = new HashSet<>();
        patientPages.add("DASHBOARD");
        patientPages.add("USER_PROFILE");
        patientPages.add("APPOINTMENT_LIST");
        patientPages.add("APPOINTMENT_BOOK");
        patientPages.add("APPOINTMENT_DETAILS");
        patientPages.add("BILL_LIST");
        patientPages.add("BILL_DETAILS");
        patientPages.add("DENTIST_LIST");
        patientPages.add("DENTIST_DETAILS");
        patientPages.add("TREATMENT_LIST");
        patientPages.add("TREATMENT_DETAILS");
        patientPages.add("REPORT_PATIENT");
        patientPages.add("HELP");
        PAGE_ACCESS.put(UserRole.PATIENT, patientPages);
        
        // =============================================
        // ACTION PERMISSIONS (CRUD Operations)
        // =============================================
        
        // ADMIN - Full CRUD on everything
        Set<String> adminActions = new HashSet<>();
        adminActions.addAll(getAllActions());
        ACTION_PERMISSIONS.put(UserRole.ADMIN, adminActions);
        
        // RECEPTION - Limited CRUD
        Set<String> receptionActions = new HashSet<>();
        receptionActions.add("VIEW_PATIENTS");
        receptionActions.add("ADD_PATIENTS");
        receptionActions.add("EDIT_PATIENTS");
        receptionActions.add("VIEW_APPOINTMENTS");
        receptionActions.add("ADD_APPOINTMENTS");
        receptionActions.add("EDIT_APPOINTMENTS");
        receptionActions.add("VIEW_BILLS");
        receptionActions.add("ADD_BILLS");
        receptionActions.add("VIEW_TREATMENTS");
        receptionActions.add("ADD_TREATMENTS");
        receptionActions.add("EDIT_TREATMENTS");
        receptionActions.add("VIEW_DENTISTS");
        receptionActions.add("VIEW_REPORTS");
        ACTION_PERMISSIONS.put(UserRole.RECEPTION, receptionActions);
        
        // DENTIST - Limited CRUD
        Set<String> dentistActions = new HashSet<>();
        dentistActions.add("VIEW_PATIENTS");
        dentistActions.add("VIEW_APPOINTMENTS");
        dentistActions.add("VIEW_BILLS");
        dentistActions.add("ADD_BILLS");
        dentistActions.add("VIEW_TREATMENTS");
        dentistActions.add("VIEW_DENTISTS");
        dentistActions.add("VIEW_REPORTS");
        ACTION_PERMISSIONS.put(UserRole.DENTIST, dentistActions);
        
        // PATIENT - View only own data
        Set<String> patientActions = new HashSet<>();
        patientActions.add("VIEW_APPOINTMENTS");
        patientActions.add("ADD_APPOINTMENTS");
        patientActions.add("VIEW_BILLS");
        patientActions.add("VIEW_TREATMENTS");
        patientActions.add("VIEW_DENTISTS");
        patientActions.add("VIEW_REPORTS");
        ACTION_PERMISSIONS.put(UserRole.PATIENT, patientActions);
    }
    
    // =====================================================
    // GET ALL CARD NAMES
    // =====================================================
    
    private static Set<String> getAllCardNames() {
        Set<String> all = new HashSet<>();
        all.add("DASHBOARD");
        all.add("USER_PROFILE");
        all.add("USER_MANAGEMENT");
        all.add("PATIENT_LIST");
        all.add("PATIENT_ADD");
        all.add("PATIENT_DETAILS");
        all.add("APPOINTMENT_LIST");
        all.add("APPOINTMENT_BOOK");
        all.add("APPOINTMENT_SCHEDULE");
        all.add("APPOINTMENT_DETAILS");
        all.add("BILL_LIST");
        all.add("BILL_GENERATE");
        all.add("BILL_DETAILS");
        all.add("REPORT_DASHBOARD");
        all.add("REPORT_REVENUE");
        all.add("REPORT_SCHEDULE");
        all.add("REPORT_PATIENT");
        all.add("STAFF_LIST");
        all.add("STAFF_ADD");
        all.add("STAFF_DETAILS");
        all.add("DENTIST_LIST");
        all.add("DENTIST_ADD");
        all.add("DENTIST_DETAILS");
        all.add("TREATMENT_LIST");
        all.add("TREATMENT_ADD");
        all.add("TREATMENT_DETAILS");
        all.add("HELP");
        return all;
    }
    
    // =====================================================
    // GET ALL ACTIONS
    // =====================================================
    
    private static Set<String> getAllActions() {
        Set<String> all = new HashSet<>();
        all.add("VIEW_PATIENTS");
        all.add("ADD_PATIENTS");
        all.add("EDIT_PATIENTS");
        all.add("DELETE_PATIENTS");
        all.add("VIEW_APPOINTMENTS");
        all.add("ADD_APPOINTMENTS");
        all.add("EDIT_APPOINTMENTS");
        all.add("DELETE_APPOINTMENTS");
        all.add("VIEW_BILLS");
        all.add("ADD_BILLS");
        all.add("EDIT_BILLS");
        all.add("DELETE_BILLS");
        all.add("VIEW_TREATMENTS");
        all.add("ADD_TREATMENTS");
        all.add("EDIT_TREATMENTS");
        all.add("DELETE_TREATMENTS");
        all.add("VIEW_DENTISTS");
        all.add("ADD_DENTISTS");
        all.add("EDIT_DENTISTS");
        all.add("DELETE_DENTISTS");
        all.add("VIEW_STAFF");
        all.add("ADD_STAFF");
        all.add("EDIT_STAFF");
        all.add("DELETE_STAFF");
        all.add("VIEW_USERS");
        all.add("ADD_USERS");
        all.add("EDIT_USERS");
        all.add("DELETE_USERS");
        all.add("VIEW_REPORTS");
        all.add("ADD_REPORTS");
        all.add("MANAGE_SETTINGS");
        return all;
    }
    
    // =====================================================
    // PAGE ACCESS METHODS
    // =====================================================
    
    /**
     * Check if a user role has access to a specific page
     * @param role The user role
     * @param cardName The page/card name
     * @return true if has access, false otherwise
     */
    public static boolean hasPageAccess(UserRole role, String cardName) {
        if (role == null || cardName == null) {
            return false;
        }
        Set<String> pages = PAGE_ACCESS.get(role);
        return pages != null && pages.contains(cardName);
    }
    
    /**
     * Check if a user has access to a specific page
     * @param user The user
     * @param cardName The page/card name
     * @return true if has access, false otherwise
     */
    public static boolean hasPageAccess(User user, String cardName) {
        if (user == null || cardName == null) {
            return false;
        }
        return hasPageAccess(user.getRole(), cardName);
    }
    
    /**
     * Get all pages accessible by a role
     * @param role The user role
     * @return Set of page names
     */
    public static Set<String> getAccessiblePages(UserRole role) {
        if (role == null) {
            return Collections.emptySet();
        }
        Set<String> pages = PAGE_ACCESS.get(role);
        return pages != null ? Collections.unmodifiableSet(pages) : Collections.emptySet();
    }
    
    // =====================================================
    // ACTION PERMISSION METHODS
    // =====================================================
    
    /**
     * Check if a user role has a specific action permission
     * @param role The user role
     * @param action The action name
     * @return true if has permission, false otherwise
     */
    public static boolean hasActionPermission(UserRole role, String action) {
        if (role == null || action == null) {
            return false;
        }
        Set<String> actions = ACTION_PERMISSIONS.get(role);
        return actions != null && actions.contains(action);
    }
    
    /**
     * Check if a user has a specific action permission
     * @param user The user
     * @param action The action name
     * @return true if has permission, false otherwise
     */
    public static boolean hasActionPermission(User user, String action) {
        if (user == null || action == null) {
            return false;
        }
        return hasActionPermission(user.getRole(), action);
    }
    
    /**
     * Get all actions permitted for a role
     * @param role The user role
     * @return Set of action names
     */
    public static Set<String> getPermittedActions(UserRole role) {
        if (role == null) {
            return Collections.emptySet();
        }
        Set<String> actions = ACTION_PERMISSIONS.get(role);
        return actions != null ? Collections.unmodifiableSet(actions) : Collections.emptySet();
    }
    
    // =====================================================
    // CONVENIENCE PERMISSION METHODS
    // =====================================================
    
    /**
     * Check if user can view patients
     */
    public static boolean canViewPatients(UserRole role) {
        return hasActionPermission(role, "VIEW_PATIENTS");
    }
    
    /**
     * Check if user can add patients
     */
    public static boolean canAddPatients(UserRole role) {
        return hasActionPermission(role, "ADD_PATIENTS");
    }
    
    /**
     * Check if user can edit patients
     */
    public static boolean canEditPatients(UserRole role) {
        return hasActionPermission(role, "EDIT_PATIENTS");
    }
    
    /**
     * Check if user can delete patients
     */
    public static boolean canDeletePatients(UserRole role) {
        return hasActionPermission(role, "DELETE_PATIENTS");
    }
    
    /**
     * Check if user can view appointments
     */
    public static boolean canViewAppointments(UserRole role) {
        return hasActionPermission(role, "VIEW_APPOINTMENTS");
    }
    
    /**
     * Check if user can add appointments
     */
    public static boolean canAddAppointments(UserRole role) {
        return hasActionPermission(role, "ADD_APPOINTMENTS");
    }
    
    /**
     * Check if user can edit appointments
     */
    public static boolean canEditAppointments(UserRole role) {
        return hasActionPermission(role, "EDIT_APPOINTMENTS");
    }
    
    /**
     * Check if user can delete appointments
     */
    public static boolean canDeleteAppointments(UserRole role) {
        return hasActionPermission(role, "DELETE_APPOINTMENTS");
    }
    
    /**
     * Check if user can view bills
     */
    public static boolean canViewBills(UserRole role) {
        return hasActionPermission(role, "VIEW_BILLS");
    }
    
    /**
     * Check if user can add bills
     */
    public static boolean canAddBills(UserRole role) {
        return hasActionPermission(role, "ADD_BILLS");
    }
    
    /**
     * Check if user can edit bills
     */
    public static boolean canEditBills(UserRole role) {
        return hasActionPermission(role, "EDIT_BILLS");
    }
    
    /**
     * Check if user can delete bills
     */
    public static boolean canDeleteBills(UserRole role) {
        return hasActionPermission(role, "DELETE_BILLS");
    }
    
    /**
     * Check if user can view treatments
     */
    public static boolean canViewTreatments(UserRole role) {
        return hasActionPermission(role, "VIEW_TREATMENTS");
    }
    
    /**
     * Check if user can add treatments
     */
    public static boolean canAddTreatments(UserRole role) {
        return hasActionPermission(role, "ADD_TREATMENTS");
    }
    
    /**
     * Check if user can edit treatments
     */
    public static boolean canEditTreatments(UserRole role) {
        return hasActionPermission(role, "EDIT_TREATMENTS");
    }
    
    /**
     * Check if user can delete treatments
     */
    public static boolean canDeleteTreatments(UserRole role) {
        return hasActionPermission(role, "DELETE_TREATMENTS");
    }
    
    /**
     * Check if user can view dentists
     */
    public static boolean canViewDentists(UserRole role) {
        return hasActionPermission(role, "VIEW_DENTISTS");
    }
    
    /**
     * Check if user can add dentists
     */
    public static boolean canAddDentists(UserRole role) {
        return hasActionPermission(role, "ADD_DENTISTS");
    }
    
    /**
     * Check if user can edit dentists
     */
    public static boolean canEditDentists(UserRole role) {
        return hasActionPermission(role, "EDIT_DENTISTS");
    }
    
    /**
     * Check if user can delete dentists
     */
    public static boolean canDeleteDentists(UserRole role) {
        return hasActionPermission(role, "DELETE_DENTISTS");
    }
    
    /**
     * Check if user can view staff
     */
    public static boolean canViewStaff(UserRole role) {
        return hasActionPermission(role, "VIEW_STAFF");
    }
    
    /**
     * Check if user can add staff
     */
    public static boolean canAddStaff(UserRole role) {
        return hasActionPermission(role, "ADD_STAFF");
    }
    
    /**
     * Check if user can edit staff
     */
    public static boolean canEditStaff(UserRole role) {
        return hasActionPermission(role, "EDIT_STAFF");
    }
    
    /**
     * Check if user can delete staff
     */
    public static boolean canDeleteStaff(UserRole role) {
        return hasActionPermission(role, "DELETE_STAFF");
    }
    
    /**
     * Check if user can view reports
     */
    public static boolean canViewReports(UserRole role) {
        return hasActionPermission(role, "VIEW_REPORTS");
    }
    
    /**
     * Check if user can manage users
     */
    public static boolean canManageUsers(UserRole role) {
        return hasActionPermission(role, "VIEW_USERS") || 
               hasActionPermission(role, "ADD_USERS") ||
               hasActionPermission(role, "EDIT_USERS") ||
               hasActionPermission(role, "DELETE_USERS");
    }
    
    /**
     * Check if user can manage settings
     */
    public static boolean canManageSettings(UserRole role) {
        return hasActionPermission(role, "MANAGE_SETTINGS");
    }
    
    // =====================================================
    // ROLE-BASED DATA FILTERING HELPERS
    // =====================================================
    
    /**
     * Get the filter type for appointment data based on role
     * @param role The user role
     * @return Filter type: "ALL", "DENTIST", "PATIENT"
     */
    public static String getAppointmentFilterType(UserRole role) {
        if (role == null) {
            return "NONE";
        }
        switch (role) {
            case ADMIN:
            case RECEPTION:
                return "ALL";
            case DENTIST:
                return "DENTIST";
            case PATIENT:
                return "PATIENT";
            default:
                return "NONE";
        }
    }
    
    /**
     * Get the filter type for bill data based on role
     * @param role The user role
     * @return Filter type: "ALL", "DENTIST", "PATIENT"
     */
    public static String getBillFilterType(UserRole role) {
        if (role == null) {
            return "NONE";
        }
        switch (role) {
            case ADMIN:
            case RECEPTION:
                return "ALL";
            case DENTIST:
                return "DENTIST";
            case PATIENT:
                return "PATIENT";
            default:
                return "NONE";
        }
    }
    
    // =====================================================
    // ROLE INFORMATION METHODS
    // =====================================================
    
    /**
     * Get the display name for a role
     */
    public static String getRoleDisplayName(UserRole role) {
        if (role == null) {
            return "Unknown";
        }
        switch (role) {
            case ADMIN:
                return "Administrator";
            case RECEPTION:
                return "Receptionist";
            case DENTIST:
                return "Dentist";
            case PATIENT:
                return "Patient";
            default:
                return role.name();
        }
    }
    
    /**
     * Get the dashboard card name for a role
     */
    public static String getDashboardCard(UserRole role) {
        if (role == null) {
            return "DASHBOARD";
        }
        return "DASHBOARD";
    }
    
    /**
     * Get all roles
     */
    public static List<UserRole> getAllRoles() {
        return Arrays.asList(UserRole.values());
    }
    
    /**
     * Get roles that can manage a specific module
     */
    public static List<UserRole> getRolesWithPermission(String action) {
        List<UserRole> roles = new ArrayList<>();
        for (UserRole role : UserRole.values()) {
            if (hasActionPermission(role, action)) {
                roles.add(role);
            }
        }
        return roles;
    }
}