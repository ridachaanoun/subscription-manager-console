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
                    case "3": generatePayments(); break;
                    case "4": createPaymentRecord(); break;
                    case "5": listPaymentsForSubscription(); break;
                    case "6": markPaymentPaid(); break;
                    case "7": reportsMenu(); break;
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
        System.out.println("3. Generate monthly payments for subscription");
        System.out.println("4. Create payment record");
        System.out.println("5. List payments for subscription");
        System.out.println("6. Mark payment PAID");
        System.out.println("7. Reports");
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