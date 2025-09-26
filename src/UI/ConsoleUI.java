package UI;



import entity.*;
import serveses.PaymentService;
import serveses.SubscriptionService;
import serveses.impl.PaymentServiceImpl;
import serveses.impl.SubscriptionServiceImpl;
import util.ValidationUtils;

import java.time.LocalDateTime;
import java.time.YearMonth;
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
                System.out.println("Error: " + e.getMessage());
                e.printStackTrace(System.out);
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
        System.out.print("Price (e.g. 9.99): ");
        double price = Double.parseDouble(sc.nextLine().trim());
        LocalDateTime start = LocalDateTime.now();
        System.out.print("End date (YYYY-MM-DDTHH:MM) or empty: ");
        String endStr = sc.nextLine().trim();
        LocalDateTime end = endStr.isEmpty() ? null : LocalDateTime.parse(endStr);
        System.out.print("Type (1=Fixed, 2=Flexible): ");
        String type = sc.nextLine().trim();
        Subscription s;
        if ("1".equals(type)) {
            System.out.print("Months engaged (int): ");
            int months = Integer.parseInt(sc.nextLine().trim());
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
        System.out.print("Subscription id: ");
        String id = sc.nextLine().trim();
        subscriptionService.generateMonthlyPaymentsForSubscription(id);
        System.out.println("Payments generated (if any).");
    }

    private void createPaymentRecord() throws Exception {
        System.out.print("Subscription id: ");
        String sid = sc.nextLine().trim();
        System.out.print("Due date (YYYY-MM-DDTHH:MM): ");
        LocalDateTime due = LocalDateTime.parse(sc.nextLine().trim());
        Payment p = new Payment(null, due, null, "manual", Pstatus.UNPAID, sid);
        paymentService.recordPayment(p);
        System.out.println("Payment record created: " + p.getId());
    }

    private void listPaymentsForSubscription() throws Exception {
        System.out.print("Subscription id: ");
        String sid = sc.nextLine().trim();
        List<Payment> list = paymentService.findBySubscription(sid);
        if (list.isEmpty()) {
            System.out.println("No payments for subscription " + sid);
            return;
        }
        list.forEach(System.out::println);
    }

    private void markPaymentPaid() throws Exception {
        System.out.print("Payment id: ");
        String pid = sc.nextLine().trim();
        paymentService.markPaymentAsPaid(pid);
        System.out.println("Payment marked as PAID: " + pid);
    }

    private void reportsMenu() throws Exception {
        System.out.println("Reports:");
        System.out.println("1. Total paid for month");
        System.out.println("2. Total paid for year");
        System.out.println("3. Total unpaid for subscription");
        System.out.print("choice: ");
        String c = sc.nextLine().trim();
        switch (c) {
            case "1":
                System.out.print("Month (YYYY-MM): ");
                YearMonth ym = YearMonth.parse(sc.nextLine().trim());
                System.out.println("Total paid: " + paymentService.totalPaidForMonth(ym));
                break;
            case "2":
                System.out.print("Year (YYYY): ");
                int year = Integer.parseInt(sc.nextLine().trim());
                System.out.println("Total paid: " + paymentService.totalPaidForYear(year));
                break;
            case "3":
                System.out.print("Subscription id: ");
                String sid = sc.nextLine().trim();
                System.out.println("Total unpaid: " + paymentService.totalUnpaidForSubscription(sid));
                break;
            default:
                System.out.println("Unknown option");
        }
    }
}