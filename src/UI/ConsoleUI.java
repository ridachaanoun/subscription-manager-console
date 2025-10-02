package UI;



import entity.*;
import serveses.PaymentService;
import serveses.SubscriptionService;
import serveses.impl.PaymentServiceImpl;
import serveses.impl.SubscriptionServiceImpl;
import util.ValidationUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

/**
 * Console UI that delegates all logic to services in com.example.subscription.serveses.
 * UI contains minimal input parsing and displays results.
 */
public class ConsoleUI {
    private final SubscriptionService subscriptionService = new SubscriptionServiceImpl();
    private final PaymentService paymentService = new PaymentServiceImpl();
    private final Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        new ConsoleUI().run();
    }

    private void run() {
        boolean exit = false;
        while (!exit) {
            printMenu();
            String choice = sc.nextLine().trim();
            try {
                switch (choice) {
                    case "1": createSubscription(); break;
                    case "2": listSubscriptions(); break;
                    case "3": modifySubscription(); break;
                    case "4": deleteSubscription(); break;
                    case "5": generatePayments(); break;
                    case "6": createPaymentRecord(); break;
                    case "7": listPaymentsForSubscription(); break;
                    case "8": modifyPayment(); break;
                    case "9": deletePayment(); break;
                    case "10": markPaymentPaid(); break;
                    case "11": reportsMenu(); break;
                    case "0": exit = true; break;
                    default: System.out.println("Unknown option");
                }
            } catch (Exception e) {
                System.out.println("Error: " + (e.getMessage() == null ? e.getClass().getSimpleName() : e.getMessage()));
            }
        }
        sc.close();
        System.out.println("Bye");
    }

    private void printMenu() {
        System.out.println("\n=== Subscription Manager (service layer) ===");
        System.out.println("1. Create subscription");
        System.out.println("2. List subscriptions");
        System.out.println("3. Modify subscription");
        System.out.println("4. Delete subscription");
        System.out.println("5. Generate monthly payments for subscription");
        System.out.println("6. Create payment record");
        System.out.println("7. List payments for subscription");
        System.out.println("8. Modify payment");
        System.out.println("9. Delete payment");
        System.out.println("10. Mark payment PAID");
        System.out.println("11. Reports");
        System.out.println("0. Exit");
        System.out.print("Choice: ");
    }

    private void createSubscription() throws Exception {
        System.out.print("Service name: ");
        String name = sc.nextLine().trim();
    double price = readDouble("Price (e.g. 9.99): ");
        LocalDateTime start = LocalDateTime.now();
    System.out.print("End date (YYYY-MM-DD or YYYY-MM-DDTHH:MM) or empty: ");
    String endStr = sc.nextLine().trim();
    LocalDateTime end = endStr.isEmpty() ? null : parseFlexibleDateTime(endStr);
    String type = readChoice("Type (1=Fixed, 2=Flexible): ", new String[]{"1","2"});
        Subscription s;
        if ("1".equals(type)) {
            int months = readInt("Months engaged (int): ");
            s = new FixedSubscription(null, name, price, start, end, Sstatus.ACTIVE, months);
        } else {
            s = new FlexibleSubscription(null, name, price, start, end, Sstatus.ACTIVE);
        }
        ValidationUtils.validateSubscription(s);
        subscriptionService.create(s);
        System.out.println("Subscription created: " + s.getId());
    }

    private void listSubscriptions() throws Exception {
        List<Subscription> list = subscriptionService.findAll();
        if (list.isEmpty()) {
            System.out.println("No subscriptions.");
            return;
        }
        list.forEach(System.out::println);
    }

    private void modifySubscription() throws Exception {
        String id = readNonEmpty("Subscription ID to modify: ");
        var optSub = subscriptionService.findById(id);
        if (!optSub.isPresent()) {
            System.out.println("Subscription not found with ID: " + id);
            return;
        }
        
        Subscription existing = optSub.get();
        System.out.println("Current subscription: " + existing);
        
        System.out.print("New service name (or press Enter to keep '" + existing.getServiceName() + "'): ");
        String newName = sc.nextLine().trim();
        if (!newName.isEmpty()) {
            existing.setServiceName(newName);
        }
        
        System.out.print("New price (or press Enter to keep " + existing.getPrice() + "): ");
        String priceStr = sc.nextLine().trim();
        if (!priceStr.isEmpty()) {
            try {
                double newPrice = Double.parseDouble(priceStr);
                existing.setPrice(newPrice);
            } catch (NumberFormatException e) {
                System.out.println("Invalid price format. Keeping existing price.");
            }
        }
        
        System.out.print("New status (ACTIVE/SUSPENDED/CANCELLED, or press Enter to keep " + existing.getStatus() + "): ");
        String statusStr = sc.nextLine().trim();
        if (!statusStr.isEmpty()) {
            try {
                Sstatus newStatus = Sstatus.valueOf(statusStr.toUpperCase());
                existing.setStatus(newStatus);
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid status. Keeping existing status.");
            }
        }
        
        subscriptionService.update(existing);
        System.out.println("Subscription updated successfully!");
    }

    private void deleteSubscription() throws Exception {
        String id = readNonEmpty("Subscription ID to delete: ");
        var optSub = subscriptionService.findById(id);
        if (!optSub.isPresent()) {
            System.out.println("Subscription not found with ID: " + id);
            return;
        }
        
        Subscription sub = optSub.get();
        System.out.println("Subscription to delete: " + sub);
        String confirm = readChoice("Are you sure? (y/n): ", new String[]{"y", "n", "Y", "N"});
        
        if (confirm.equalsIgnoreCase("y")) {
            subscriptionService.delete(id);
            System.out.println("Subscription deleted successfully!");
        } else {
            System.out.println("Deletion cancelled.");
        }
    }

    private void generatePayments() throws Exception {
    String id = readNonEmpty("Subscription id: ");
        subscriptionService.generateMonthlyPaymentsForSubscription(id);
        System.out.println("Payments generated (if any).");
    }

    private void createPaymentRecord() throws Exception {
    String sid = readNonEmpty("Subscription id: ");
        System.out.print("Due date (YYYY-MM-DD or YYYY-MM-DDTHH:MM): ");
        LocalDateTime due = readFlexibleDateTime();
        Payment p = new Payment(null, due, null, "manual", Pstatus.UNPAID, sid);
        paymentService.recordPayment(p);
        System.out.println("Payment record created: " + p.getId());
    }

    private void listPaymentsForSubscription() throws Exception {
    String sid = readNonEmpty("Subscription id: ");
        List<Payment> list = paymentService.findBySubscription(sid);
        if (list.isEmpty()) {
            System.out.println("No payments for subscription " + sid);
            return;
        }
        list.forEach(System.out::println);
    }

    private void markPaymentPaid() throws Exception {
    String pid = readNonEmpty("Payment id: ");
        paymentService.markPaymentAsPaid(pid);
        System.out.println("Payment marked as PAID: " + pid);
    }

    private void modifyPayment() throws Exception {
        String id = readNonEmpty("Payment ID to modify: ");
        var optPayment = paymentService.findById(id);
        if (!optPayment.isPresent()) {
            System.out.println("Payment not found with ID: " + id);
            return;
        }
        
        Payment existing = optPayment.get();
        System.out.println("Current payment: " + existing);
        
        System.out.print("New payment type (or press Enter to keep '" + existing.getPaymentType() + "'): ");
        String newType = sc.nextLine().trim();
        if (!newType.isEmpty()) {
            existing.setPaymentType(newType);
        }
        
        System.out.print("New status (PAID/UNPAID/OVERDUE, or press Enter to keep " + existing.getStatus() + "): ");
        String statusStr = sc.nextLine().trim();
        if (!statusStr.isEmpty()) {
            try {
                Pstatus newStatus = Pstatus.valueOf(statusStr.toUpperCase());
                existing.setStatus(newStatus);
                
                // If marking as PAID and no payment date, set current time
                if (newStatus == Pstatus.PAID && existing.getPaymentDate() == null) {
                    existing.setPaymentDate(LocalDateTime.now());
                }
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid status. Keeping existing status.");
            }
        }
        
        System.out.print("Set new payment date? (y/n): ");
        String setDate = sc.nextLine().trim();
        if (setDate.equalsIgnoreCase("y")) {
            System.out.print("New payment date (YYYY-MM-DD or YYYY-MM-DDTHH:MM, or 'null' to clear): ");
            String dateStr = sc.nextLine().trim();
            if (dateStr.equalsIgnoreCase("null")) {
                existing.setPaymentDate(null);
            } else if (!dateStr.isEmpty()) {
                try {
                    LocalDateTime newDate = parseFlexibleDateTime(dateStr);
                    existing.setPaymentDate(newDate);
                } catch (Exception e) {
                    System.out.println("Invalid date format. Keeping existing payment date.");
                }
            }
        }
        
        paymentService.recordPayment(existing);
        System.out.println("Payment updated successfully!");
    }

    private void deletePayment() throws Exception {
        String id = readNonEmpty("Payment ID to delete: ");
        var optPayment = paymentService.findById(id);
        if (!optPayment.isPresent()) {
            System.out.println("Payment not found with ID: " + id);
            return;
        }
        
        Payment payment = optPayment.get();
        System.out.println("Payment to delete: " + payment);
        String confirm = readChoice("Are you sure? (y/n): ", new String[]{"y", "n", "Y", "N"});
        
        if (confirm.equalsIgnoreCase("y")) {
            paymentService.delete(id);
            System.out.println("Payment deleted successfully!");
        } else {
            System.out.println("Deletion cancelled.");
        }
    }

    private void reportsMenu() throws Exception {
        System.out.println("Reports:");
        System.out.println("1. Total paid for month");
        System.out.println("2. Total paid for year");
        System.out.println("3. Total unpaid for subscription");
    String c = readChoice("choice: ", new String[]{"1","2","3"});
        switch (c) {
            case "1":
                System.out.print("Month (YYYY-MM): ");
                YearMonth ym = YearMonth.parse(sc.nextLine().trim());
                System.out.println("Total paid: " + paymentService.totalPaidForMonth(ym));
                break;
            case "2":
                int year = readInt("Year (YYYY): ");
                System.out.println("Total paid: " + paymentService.totalPaidForYear(year));
                break;
            case "3":
                String sid = readNonEmpty("Subscription id: ");
                System.out.println("Total unpaid: " + paymentService.totalUnpaidForSubscription(sid));
                break;
            default:
                System.out.println("Unknown option");
        }
    }

    // --- Helper methods for date parsing ---
    private LocalDateTime readFlexibleDateTime() {
        while (true) {
            String s = sc.nextLine().trim();
            if (s.isEmpty()) {
                System.out.print("Please enter a date (YYYY-MM-DD or YYYY-MM-DDTHH:MM): ");
                continue;
            }
            try {
                return parseFlexibleDateTime(s);
            } catch (Exception ex) {
                System.out.print("Invalid date. Use YYYY-MM-DD or YYYY-MM-DDTHH:MM: ");
            }
        }
    }

    private LocalDateTime parseFlexibleDateTime(String input) {
        String s = input.trim();
        DateTimeFormatter[] fmts = new DateTimeFormatter[] {
                DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"),
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
        };
        for (DateTimeFormatter fmt : fmts) {
            try {
                return LocalDateTime.parse(s, fmt);
            } catch (DateTimeParseException ignored) { }
        }
        // Try date-only -> default time 00:00
        try {
            LocalDate d = LocalDate.parse(s, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            return d.atStartOfDay();
        } catch (DateTimeParseException ex) {
            throw ex;
        }
    }

    // --- General input helpers ---
    private String readNonEmpty(String prompt) {
        while (true) {
            System.out.print(prompt);
            String s = sc.nextLine().trim();
            if (!s.isEmpty()) return s;
            System.out.println("Value cannot be empty.");
        }
    }

    private int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String s = sc.nextLine().trim();
            try {
                return Integer.parseInt(s);
            } catch (NumberFormatException e) {
                System.out.println("Invalid number. Please enter an integer.");
            }
        }
    }

    private double readDouble(String prompt) {
        while (true) {
            System.out.print(prompt);
            String s = sc.nextLine().trim();
            try {
                return Double.parseDouble(s);
            } catch (NumberFormatException e) {
                System.out.println("Invalid number. Please enter a decimal value like 9.99.");
            }
        }
    }

    private String readChoice(String prompt, String[] allowed) {
        while (true) {
            System.out.print(prompt);
            String s = sc.nextLine().trim();
            for (String a : allowed) {
                if (a.equalsIgnoreCase(s)) return s;
            }
            System.out.println("Invalid choice. Allowed: " + String.join(", ", allowed));
        }
    }
}