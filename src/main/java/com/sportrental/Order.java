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
    private String status; // PendingAudit, Approved, Rejected, Completed
    private HashSet<RentalItem> itemsSet;
    private String auditReason;

    /**
     * 建構子：用於建立一個新的訂單物件。
     * @param orderID 訂單ID
     * @param member 訂單所屬會員
     * @param itemsSet 租借品項集合 (從 RentalList 複製而來)
     * @param status 訂單狀態
     * @param auditReason 審計理由 (若無審計則為 null)
     */
    public Order(String orderID, Member member, Set<RentalItem> itemsSet, String status, String auditReason) {
        this.orderID = orderID;
        this.member = member;
        this.orderTime = LocalDateTime.now();
        this.itemsSet = new HashSet<>(itemsSet); // 深度複製，避免引用問題
        this.status = status;
        this.auditReason = auditReason;
    }

    /**
     * 取得訂單ID。
     * @return 訂單ID
     */
    public String getOrderID() {
        return orderID;
    }

    /**
     * 取得訂單狀態。
     * @return 訂單狀態
     */
    public String getStatus() {
        return status;
    }

    /**
     * 取得審計理由。
     * @return 審計理由，如果沒有則為 null
     */
    public String getAuditReason() {
        return auditReason;
    }

    /**
     * 取得訂單中所有租借品項的總數量。
     * @return 所有租借品項的總數量
     */
    public Integer getTotalQuantity() {
        return itemsSet.stream().mapToInt(RentalItem::getQuantity).sum();
    }

    /**
     * 取得訂單中的租借品項列表。
     * @return 租借品項列表
     */
    public List<RentalItem> getLineItems() {
        return new ArrayList<>(itemsSet);
    }
}