package com.sportrental;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Order {
    private String orderID;
    private Member member;
    private LocalDateTime orderTime;
    private String status;
    private HashSet<RentalItem> itemsSet;
    private String auditReason;

    public Order(String orderID, Member member, Set<RentalItem> itemsSet, String status, String auditReason) {
        this.orderID = orderID;
        this.member = member;
        this.orderTime = LocalDateTime.now();
        this.itemsSet = new HashSet<>(itemsSet); // 深度複製
        this.status = status;
        this.auditReason = auditReason;
    }

    public String getOrderID() { return orderID; }
    public String getStatus() { return status; }
    public String getAuditReason() { return auditReason; }

    public List<RentalItem> getLineItems() {
        return new ArrayList<>(itemsSet);
    }
}