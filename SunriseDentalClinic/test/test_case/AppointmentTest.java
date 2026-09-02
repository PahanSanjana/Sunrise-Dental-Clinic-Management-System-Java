package test_case;

import dao.AppointmentDAO;
import model.Appointment;

import org.junit.Test;
import static org.junit.Assert.*;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;

public class AppointmentTest {

    private AppointmentDAO appointmentDAO = new AppointmentDAO();

    // =====================================================
    // POSITIVE TESTS - BOOK APPOINTMENT
    // =====================================================

    @Test
    public void testBookAppointmentValid1() {

        // Unique date and time for this test
        Appointment a = createAppointment(
                1,
                1,
                100,
                "08:00"
        );

        boolean result = appointmentDAO.bookAppointment(a);

        assertTrue(
                "Valid appointment should be booked",
                result
        );

        assertTrue(
                "Generated appointment ID should be greater than 0",
                a.getAppointmentId() > 0
        );

        System.out.println("PASS: Book Appointment - Valid 1");

        // Cleanup
        appointmentDAO.cancelAppointment(a.getAppointmentId());
    }


    @Test
    public void testBookAppointmentValid2() {

        Appointment a = createAppointment(
                2,
                2,
                101,
                "08:30"
        );

        boolean result = appointmentDAO.bookAppointment(a);

        assertTrue(
                "Valid appointment should be booked",
                result
        );

        assertTrue(
                "Generated appointment ID should be greater than 0",
                a.getAppointmentId() > 0
        );

        System.out.println("PASS: Book Appointment - Valid 2");

        // Cleanup
        appointmentDAO.cancelAppointment(a.getAppointmentId());
    }


    @Test
    public void testBookAppointmentValid3() {

        Appointment a = createAppointment(
                3,
                7,
                102,
                "09:00"
        );

        boolean result = appointmentDAO.bookAppointment(a);

        assertTrue(
                "Valid appointment should be booked",
                result
        );

        assertTrue(
                "Generated appointment ID should be greater than 0",
                a.getAppointmentId() > 0
        );

        System.out.println("PASS: Book Appointment - Valid 3");

        // Cleanup
        appointmentDAO.cancelAppointment(a.getAppointmentId());
    }


    // =====================================================
    // NEGATIVE TESTS - BOOK APPOINTMENT
    // =====================================================

    @Test
    public void testBookAppointmentDoubleBooking() {

        // First appointment
        Appointment a1 = createAppointment(
                1,
                1,
                103,
                "10:00"
        );

        boolean firstResult = appointmentDAO.bookAppointment(a1);

        assertTrue(
                "First appointment should be booked",
                firstResult
        );

        // Second appointment
        // Same dentist + same date + same time
        Appointment a2 = createAppointment(
                2,
                1,
                103,
                "10:00"
        );

        boolean secondResult = appointmentDAO.bookAppointment(a2);

        assertFalse(
                "Double booking should fail",
                secondResult
        );

        System.out.println(
                "PASS: Book Appointment - Double Booking"
        );

        // Cleanup first appointment
        appointmentDAO.cancelAppointment(
                a1.getAppointmentId()
        );
    }


    @Test
    public void testBookAppointmentPastDate() {

        Appointment a = createAppointment(
                1,
                1,
                -1,
                "10:00"
        );

        boolean result = appointmentDAO.bookAppointment(a);

        assertFalse(
                "Past date should fail",
                result
        );

        System.out.println(
                "PASS: Book Appointment - Past Date"
        );
    }


    @Test
    public void testBookAppointmentNoDentist() {

        // Dentist 999 does not exist
        Appointment a = createAppointment(
                1,
                999,
                105,
                "10:00"
        );

        boolean result = appointmentDAO.bookAppointment(a);

        assertFalse(
                "Non-existent dentist should fail",
                result
        );

        System.out.println(
                "PASS: Book Appointment - No Dentist"
        );
    }


    // =====================================================
    // POSITIVE TESTS - CANCEL APPOINTMENT
    // =====================================================

    @Test
    public void testCancelAppointmentValid1() {

        Appointment a = createAppointment(
                1,
                1,
                106,
                "10:30"
        );

        // First make sure booking succeeds
        boolean bookingResult =
                appointmentDAO.bookAppointment(a);

        assertTrue(
                "Appointment must be booked before cancellation",
                bookingResult
        );

        boolean result =
                appointmentDAO.cancelAppointment(
                        a.getAppointmentId()
                );

        assertTrue(
                "Appointment should be cancelled",
                result
        );

        System.out.println(
                "PASS: Cancel Appointment - Valid 1"
        );
    }


    @Test
    public void testCancelAppointmentValid2() {

        Appointment a = createAppointment(
                2,
                2,
                107,
                "11:00"
        );

        boolean bookingResult =
                appointmentDAO.bookAppointment(a);

        assertTrue(
                "Appointment must be booked before cancellation",
                bookingResult
        );

        boolean result =
                appointmentDAO.cancelAppointment(
                        a.getAppointmentId()
                );

        assertTrue(
                "Appointment should be cancelled",
                result
        );

        System.out.println(
                "PASS: Cancel Appointment - Valid 2"
        );
    }


    @Test
    public void testCancelAppointmentValid3() {

        Appointment a = createAppointment(
                3,
                7,
                108,
                "11:30"
        );

        boolean bookingResult =
                appointmentDAO.bookAppointment(a);

        assertTrue(
                "Appointment must be booked before cancellation",
                bookingResult
        );

        boolean result =
                appointmentDAO.cancelAppointment(
                        a.getAppointmentId()
                );

        assertTrue(
                "Appointment should be cancelled",
                result
        );

        System.out.println(
                "PASS: Cancel Appointment - Valid 3"
        );
    }


    // =====================================================
    // NEGATIVE TESTS - CANCEL APPOINTMENT
    // =====================================================

    @Test
    public void testCancelAppointmentInvalidNonExistent() {

        boolean result =
                appointmentDAO.cancelAppointment(999999);

        assertFalse(
                "Non-existent appointment should fail",
                result
        );

        System.out.println(
                "PASS: Cancel Appointment - Non-existent"
        );
    }


    @Test
    public void testCancelAppointmentAlreadyCancelled() {

        Appointment a = createAppointment(
                1,
                1,
                109,
                "12:00"
        );

        // Book appointment
        boolean bookingResult =
                appointmentDAO.bookAppointment(a);

        assertTrue(
                "Appointment must be booked first",
                bookingResult
        );

        // Cancel first time
        boolean firstCancel =
                appointmentDAO.cancelAppointment(
                        a.getAppointmentId()
                );

        assertTrue(
                "First cancellation should succeed",
                firstCancel
        );

        // Cancel second time
        boolean secondCancel =
                appointmentDAO.cancelAppointment(
                        a.getAppointmentId()
                );

        assertFalse(
                "Already cancelled appointment should fail",
                secondCancel
        );

        System.out.println(
                "PASS: Cancel Appointment - Already Cancelled"
        );
    }


    @Test
    public void testCancelAppointmentInvalidId() {

        boolean result =
                appointmentDAO.cancelAppointment(-1);

        assertFalse(
                "Invalid appointment ID should fail",
                result
        );

        System.out.println(
                "PASS: Cancel Appointment - Invalid ID"
        );
    }


    // =====================================================
    // HELPER METHOD
    // =====================================================

    private Appointment createAppointment(
            int patientId,
            int dentistId,
            int daysFromNow,
            String time) {

        Appointment a = new Appointment();

        a.setPatientId(patientId);

        a.setDentistId(dentistId);

        a.setAppointmentDate(
                Date.valueOf(
                        LocalDate.now()
                                .plusDays(daysFromNow)
                )
        );

        a.setAppointmentTime(
                Time.valueOf(
                        LocalTime.parse(time)
                )
        );

        a.setStatus("Scheduled");

        a.setReason("Test appointment");

        a.setNotes("JUnit automated test");

        return a;
    }
}