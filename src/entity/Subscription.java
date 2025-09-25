package entity;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public abstract class Subscription {
    protected String id;
    protected String serviceName;
    protected double price;
    protected LocalDateTime startDate;
    protected LocalDateTime endDate; // nullable
    protected Sstatus status;

    public Subscription() {
        this.id = UUID.randomUUID().toString();
    }

    public Subscription(String id,
                        String serviceName,
                        double price,
                        LocalDateTime startDate,
                        LocalDateTime endDate,
                        Sstatus status) {
        this.id = (id == null || id.trim().isEmpty()) ? UUID.randomUUID().toString() : id;
        this.serviceName = serviceName;
        this.price = price;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getServiceName() { return serviceName; }
    public void setServiceName(String serviceName) { this.serviceName = serviceName; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public LocalDateTime getStartDate() { return startDate; }
    public void setStartDate(LocalDateTime startDate) { this.startDate = startDate; }

    public LocalDateTime getEndDate() { return endDate; }
    public void setEndDate(LocalDateTime endDate) { this.endDate = endDate; }

    public Sstatus getStatus() { return status; }
    public void setStatus(Sstatus status) { this.status = status; }

    public boolean isActive() {
        return Sstatus.ACTIVE.equals(this.status);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Subscription)) return false;
        Subscription that = (Subscription) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Subscription{" +
                "id='" + id + '\'' +
                ", serviceName='" + serviceName + '\'' +
                ", price=" + price +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", status=" + status +
                '}';
    }
}