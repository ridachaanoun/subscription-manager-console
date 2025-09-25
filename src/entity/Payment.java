package entity;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;


public class Payment {
    private String id;
    private LocalDateTime dueDate;
    private LocalDateTime paymentDate; // nullable
    private String paymentType;
    private Pstatus status;
    private String subscriptionId; // link to Subscription (1 side)

    public Payment() {
        this.id = UUID.randomUUID().toString();
    }

    public Payment(String id,LocalDateTime dueDate,LocalDateTime paymentDate,String paymentType,Pstatus status,String subscriptionId) {
        this.id = (id == null || id.trim().isEmpty()) ? UUID.randomUUID().toString() : id;
        this.dueDate = dueDate;
        this.paymentDate = paymentDate;
        this.paymentType = paymentType;
        this.status = status;
        this.subscriptionId = subscriptionId;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public LocalDateTime getDueDate() { return dueDate; }
    public void setDueDate(LocalDateTime dueDate) { this.dueDate = dueDate; }

    public LocalDateTime getPaymentDate() { return paymentDate; }
    public void setPaymentDate(LocalDateTime paymentDate) { this.paymentDate = paymentDate; }

    public String getPaymentType() { return paymentType; }
    public void setPaymentType(String paymentType) { this.paymentType = paymentType; }

    public Pstatus getStatus() { return status; }
    public void setStatus(Pstatus status) { this.status = status; }

    public String getSubscriptionId() { return subscriptionId; }
    public void setSubscriptionId(String subscriptionId) { this.subscriptionId = subscriptionId; }

    public boolean isPaid() {
        return Pstatus.PAID.equals(this.status);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Payment)) return false;
        Payment payment = (Payment) o;
        return Objects.equals(id, payment.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Payment{" +
                "id='" + id + '\'' +
                ", dueDate=" + dueDate +
                ", paymentDate=" + paymentDate +
                ", paymentType='" + paymentType + '\'' +
                ", status=" + status +
                ", subscriptionId='" + subscriptionId + '\'' +
                '}';
    }
}