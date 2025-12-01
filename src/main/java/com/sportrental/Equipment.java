package com.sportrental;

public class Equipment {
    private String equipmentID;
    private String name;
    private Integer totalStock;
    private Integer availableStock;
    private Integer auditThreshold;

    /**
     * 建構子：用於建立一個新的器材物件。
     * @param equipmentID 器材ID
     * @param name 器材名稱
     * @param totalStock 器材總庫存
     * @param auditThreshold 審計門檻 (當租借數量超過此門檻時需要審計)
     */
    public Equipment(String equipmentID, String name, Integer totalStock, Integer auditThreshold) {
        this.equipmentID = equipmentID;
        this.name = name;
        this.totalStock = totalStock;
        this.availableStock = totalStock; // 初始可用庫存等於總庫存
        this.auditThreshold = auditThreshold;
    }

    /**
     * 檢查 requestedQuantity 是否在 availableStock 的範圍內。
     * @param requestedQuantity 請求的數量
     * @return 如果庫存充足則回傳 true，否則回傳 false
     */
    public Boolean checkAvailability(Integer requestedQuantity) {
        return availableStock >= requestedQuantity;
    }

    /**
     * 減少器材的可用庫存。
     * @param quantity 要減少的數量
     */
    public void decreaseStock(Integer quantity) {
        if (checkAvailability(quantity)) {
            this.availableStock -= quantity;
        } else {
            System.err.println("錯誤：器材 " + equipmentID + " 庫存不足，無法減少 " + quantity + " 個。");
        }
    }

    /**
     * 取得器材ID。
     * @return 器材ID
     */
    public String getEquipmentID() {
        return equipmentID;
    }

    /**
     * 取得器材名稱。
     * @return 器材名稱
     */
    public String getName() {
        return name;
    }

    /**
     * 取得器材總庫存。
     * @return 器材總庫存
     */
    public Integer getTotalStock() {
        return totalStock;
    }

    /**
     * 取得器材可用庫存。
     * @return 器材可用庫存
     */
    public Integer getAvailableStock() {
        return availableStock;
    }

    /**
     * 取得器材審計門檻。
     * @return 器材審計門檻
     */
    public Integer getAuditThreshold() {
        return auditThreshold;
    }
}