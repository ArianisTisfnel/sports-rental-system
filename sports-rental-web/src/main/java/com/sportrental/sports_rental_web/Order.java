package com.sportrental;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Order {
    private String orderID;
    private String memberID;
    private LocalDateTime orderTime;
    private String status; // PendingAudit, Approved
    private String auditReason;
    private List<RentalItem> items; // 訂單內容

    public Order(String orderID, String memberID, List<RentalItem> items, String reason) {
        this.orderID = orderID;
        this.memberID = memberID;
        this.items = new ArrayList<>(items); // 複製一份清單，避免被清空影響
        this.orderTime = LocalDateTime.now();
        this.auditReason = reason;
        
        // 設定初始狀態 (UML Source: 245)
        if (reason != null && !reason.isEmpty()) {
            this.status = "PendingAudit";
        } else {
            this.status = "Approved";
        }
    }

    // Getters
    public String getOrderID() { return orderID; }
    public String getStatus() { return status; }
    public LocalDateTime getOrderTime() { return orderTime; }
    public List<RentalItem> getItems() { return items; }
}