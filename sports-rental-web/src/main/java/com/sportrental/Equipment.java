package com.sportrental;

public class Equipment {
    private String equipmentID;
    private String name;
    private Integer totalStock;
    private Integer availableStock;
    private Integer auditThreshold;

    public Equipment(String equipmentID, String name, Integer totalStock, Integer auditThreshold) {
        this.equipmentID = equipmentID;
        this.name = name;
        this.totalStock = totalStock;
        this.availableStock = totalStock; // 初始可用庫存等於總庫存
        this.auditThreshold = auditThreshold;
    }

    // 核心邏輯：檢查庫存
    public Boolean isQuantityAvailable(Integer requestedQuantity) {
        return availableStock >= requestedQuantity;
    }

    // 核心邏輯：扣除庫存
    public void decreaseStock(Integer quantity) {
        if (Boolean.TRUE.equals(this.isQuantityAvailable(quantity))) {
            this.availableStock -= quantity;
        } else {
            System.err.println("錯誤：器材 " + equipmentID + " 庫存不足，無法減少 " + quantity + " 個。");
        }
    }

    public String getEquipmentID() { return equipmentID; }
    public String getName() { return name; }
    public Integer getTotalStock() { return totalStock; }
    public Integer getAvailableStock() { return availableStock; }
    public Integer getAuditThreshold() { return auditThreshold; }
}