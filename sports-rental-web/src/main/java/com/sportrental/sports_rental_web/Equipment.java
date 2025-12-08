package com.sportrental;

public class Equipment {
    private String equipmentID;
    private String name;
    private int availableStock;
    private int auditThreshold; // 審核門檻

    public Equipment(String id, String name, int stock, int threshold) {
        this.equipmentID = id;
        this.name = name;
        this.availableStock = stock;
        this.auditThreshold = threshold;
    }

    public String getEquipmentID() { return equipmentID; }
    public String getName() { return name; }
    public int getAvailableStock() { return availableStock; }
    public int getAuditThreshold() { return auditThreshold; }
    
    public void decreaseStock(int qty) {
        this.availableStock -= qty;
    }
}