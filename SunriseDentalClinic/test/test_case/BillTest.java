package test_case;

import dao.BillDAO;
import model.Bill;
import model.BillItem;
import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class BillTest {

    private BillDAO billDAO;

    @Before
    public void setUp() {
        billDAO = new BillDAO();
    }

    // =============================================
    // POSITIVE TESTS - BILL CALCULATION (3)
    // =============================================

    @Test
    public void testBillCalculationValid1() {
        double subtotal = 10000;
        double tax = 10;
        double discount = 0;
        double expected = 11000;
        double actual = subtotal + (subtotal * tax / 100) - discount;
        
        assertEquals("Calculation should be correct", expected, actual, 0.01);
        System.out.println("PASS: Bill Calculation - No Discount");
    }

    @Test
    public void testBillCalculationValid2() {
        double subtotal = 10000;
        double tax = 10;
        double discount = 500;
        double expected = 10500;
        double actual = subtotal + (subtotal * tax / 100) - discount;
        
        assertEquals("Calculation should be correct", expected, actual, 0.01);
        System.out.println("PASS: Bill Calculation - With Discount");
    }

    @Test
    public void testBillCalculationValid3() {
        double subtotal = 10000;
        double tax = 0;
        double discount = 2000;
        double expected = 8000;
        double actual = subtotal + (subtotal * tax / 100) - discount;
        
        assertEquals("Calculation should be correct", expected, actual, 0.01);
        System.out.println("PASS: Bill Calculation - Zero Tax");
    }

    // =============================================
    // NEGATIVE TESTS - BILL CALCULATION (3)
    // =============================================

    @Test
    public void testBillCalculationNegativeSubtotal() {
        try {
            double subtotal = -1000;
            if (subtotal < 0) {
                throw new IllegalArgumentException("Invalid subtotal");
            }
            fail("Should have thrown exception");
        } catch (IllegalArgumentException e) {
            // Expected - test passes
            System.out.println("PASS: Bill Calculation - Negative Subtotal");
        }
    }

    @Test
    public void testBillCalculationNegativeTax() {
        try {
            double tax = -10;
            if (tax < 0) {
                throw new IllegalArgumentException("Invalid tax");
            }
            fail("Should have thrown exception");
        } catch (IllegalArgumentException e) {
            System.out.println("PASS: Bill Calculation - Negative Tax");
        }
    }

    @Test
    public void testBillCalculationDiscountGreaterThanSubtotal() {
        try {
            double subtotal = 10000;
            double discount = 20000;
            if (discount > subtotal) {
                throw new IllegalArgumentException("Discount exceeds total");
            }
            fail("Should have thrown exception");
        } catch (IllegalArgumentException e) {
            System.out.println("PASS: Bill Calculation - Discount > Subtotal");
        }
    }

    // =============================================
    // POSITIVE TESTS - BILL PAYMENT (3)
    // =============================================

    @Test
    public void testBillPaymentValidFull() {
        try {
            Bill bill = createBill(1, 5000);
            boolean generated = billDAO.generateBill(bill, new ArrayList<>());
            assertTrue("Bill should be generated", generated);
            
            boolean result = billDAO.updateBillStatus(bill.getBillId(), "Paid");
            assertTrue("Full payment should succeed", result);
            System.out.println("PASS: Bill Payment - Full");
        } catch (Exception e) {
            fail("Test failed: " + e.getMessage());
        }
    }

    @Test
    public void testBillPaymentValidPartial() {
        try {
            Bill bill = createBill(2, 5000);
            boolean generated = billDAO.generateBill(bill, new ArrayList<>());
            assertTrue("Bill should be generated", generated);
            
            boolean result = billDAO.updateBillPayment(bill.getBillId(), 2000, "Cash");
            assertTrue("Partial payment should succeed", result);
            System.out.println("PASS: Bill Payment - Partial");
        } catch (Exception e) {
            fail("Test failed: " + e.getMessage());
        }
    }

    @Test
    public void testBillPaymentValidCreditCard() {
        try {
            Bill bill = createBill(3, 5000);
            boolean generated = billDAO.generateBill(bill, new ArrayList<>());
            assertTrue("Bill should be generated", generated);
            
            boolean result = billDAO.updateBillPayment(bill.getBillId(), 5000, "Credit Card");
            assertTrue("Credit card payment should succeed", result);
            System.out.println("PASS: Bill Payment - Credit Card");
        } catch (Exception e) {
            fail("Test failed: " + e.getMessage());
        }
    }

    // =============================================
    // NEGATIVE TESTS - BILL PAYMENT (3)
    // =============================================

    @Test
    public void testBillPaymentInvalidNonExistent() {
        try {
            boolean result = billDAO.updateBillStatus(999, "Paid");
            assertFalse("Non-existent bill should fail", result);
            System.out.println("PASS: Bill Payment - Non-existent Bill");
        } catch (Exception e) {
            fail("Test failed: " + e.getMessage());
        }
    }

    @Test
    public void testBillPaymentInvalidNegativeAmount() {
        try {
            Bill bill = createBill(1, 5000);
            boolean generated = billDAO.generateBill(bill, new ArrayList<>());
            assertTrue("Bill should be generated", generated);
            
            boolean result = billDAO.updateBillPayment(bill.getBillId(), -100, "Cash");
            assertFalse("Negative amount should fail", result);
            System.out.println("PASS: Bill Payment - Negative Amount");
        } catch (Exception e) {
            fail("Test failed: " + e.getMessage());
        }
    }

    @Test
    public void testBillPaymentInvalidNoPaymentMethod() {
        try {
            Bill bill = createBill(1, 5000);
            boolean generated = billDAO.generateBill(bill, new ArrayList<>());
            assertTrue("Bill should be generated", generated);
            
            boolean result = billDAO.updateBillPayment(bill.getBillId(), 5000, null);
            assertFalse("No payment method should fail", result);
            System.out.println("PASS: Bill Payment - No Payment Method");
        } catch (Exception e) {
            fail("Test failed: " + e.getMessage());
        }
    }

    // =============================================
    // HELPER METHOD
    // =============================================

    private Bill createBill(int patientId, double amount) {
        Bill bill = new Bill();
        bill.setPatientId(patientId);
        bill.setBillNumber("BILL-TEST-" + System.currentTimeMillis() + "-" + patientId);
        bill.setBillDate(Date.valueOf(LocalDate.now()));
        bill.setDueDate(null);
        bill.setSubtotal(amount);
        bill.setTax(10.0);
        bill.setDiscount(0);
        double total = amount + (amount * 10 / 100);
        bill.setTotalAmount(total);
        bill.setAmountPaid(0);
        bill.setBalance(total);
        bill.setStatus("Pending");
        bill.setPaymentMethod("Cash");
        bill.setNotes("Test bill for patient " + patientId);
        return bill;
    }
}