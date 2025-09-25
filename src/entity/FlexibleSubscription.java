package entity;

import java.time.LocalDateTime;

public class FlexibleSubscription extends Subscription {

    public FlexibleSubscription() {
        super();
    }

    public FlexibleSubscription(String id,String serviceName,double price,LocalDateTime startDate,LocalDateTime endDate,Sstatus status) {
        super(id, serviceName, price, startDate, endDate, status);
    }

    @Override
    public String toString() {
        return "FlexibleSubscription{" +
                "id='" + id + '\'' +
                ", serviceName='" + serviceName + '\'' +
                ", price=" + price +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", status=" + status +
                '}';
    }
}