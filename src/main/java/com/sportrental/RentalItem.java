package com.sportrental;

import java.util.Objects;

public class RentalItem {
    // 屬性在建構後應視為唯讀 (final)。quantity 的修改應透過 RentalList 的 updateItem() 進行。
    private final Equipment equipment;
    private Integer quantity;

    /**
     * 建構子：初始化 RentalItem 物件。
     * @param equipment 租借的器材物件
     * @param quantity 租借數量
     */
    public RentalItem(Equipment equipment, Integer quantity) {
        this.equipment = equipment;
        this.quantity = quantity;
    }

    /**
     * 取得租借的器材物件。
     * @return 器材物件
     */
    public Equipment getEquipment() {
        return equipment;
    }

    /**
     * 取得租借數量。
     * @return 租借數量
     */
    public Integer getQuantity() {
        return quantity;
    }

    /**
     * 設定租借數量 (供 RentalList 內部調用)。
     * @param quantity 新的租借數量
     */
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    /**
     * 判斷兩個 RentalItem 物件是否邏輯相等。
     * 僅基於 equipment.getEquipmentID() 判斷。
     * @param obj 要比較的物件
     * @return 如果邏輯相等則回傳 true，否則回傳 false
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        RentalItem that = (RentalItem) obj;
        return equipment.getEquipmentID().equals(that.equipment.getEquipmentID());
    }

    /**
     * 計算 RentalItem 物件的雜湊碼。
     * 僅基於 equipment.getEquipmentID() 計算。
     * @return 雜湊碼
     */
    @Override
    public int hashCode() {
        return Objects.hash(equipment.getEquipmentID());
    }
}