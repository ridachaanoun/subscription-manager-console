package entity;

import java.time.LocalDateTime;

public class FixedSubscription extends Subscription {
    private int monthsEngaged;

    public FixedSubscription() {
        super();
    }

    public FixedSubscription(String id,String serviceName,double price,LocalDateTime startDate,LocalDateTime endDate,Sstatus status,int monthsEngaged) {
        super(id, serviceName, price, startDate, endDate, status);
        this.monthsEngaged = monthsEngaged;
    }

    public int getMonthsEngaged() { return monthsEngaged; }
    public void setMonthsEngaged(int monthsEngaged) { this.monthsEngaged = monthsEngaged; }

    @Override
    public String toString() {
        return "FixedSubscription{" +
                "id='" + id + '\'' +
                ", serviceName='" + serviceName + '\'' +
                ", price=" + price +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", status=" + status +
                ", monthsEngaged=" + monthsEngaged +
                '}';
    }
}
