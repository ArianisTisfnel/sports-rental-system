package com.sportrental.sports_rental_web;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Order {
    private String orderID;
    private String memberID;
    private LocalDateTime orderTime;
    private String status;
    private String auditReason;
    private List<RentalItem> items;

    public Order(String orderID, String memberID, List<RentalItem> items, String reason) {
        this.orderID = orderID;
        this.memberID = memberID;
        this.items = new ArrayList<>(items);
        this.orderTime = LocalDateTime.now();
        this.auditReason = reason;

        if (reason != null && !reason.isEmpty()) {
            this.status = "PendingAudit";
        } else {
            this.status = "Approved";
        }
    }
}