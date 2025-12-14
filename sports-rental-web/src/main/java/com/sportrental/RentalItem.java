package com.sportrental;

import java.util.Objects;

public class RentalItem {
    // 屬性在建構後應視為唯讀 (final)。quantity 的修改應透過 RentalList 的 updateItem() 進行。
    private final Equipment equipment;
    private Integer quantity;

    public RentalItem(Equipment equipment, Integer quantity) {
        this.equipment = equipment;
        this.quantity = quantity;
    }

    public Equipment getEquipment() { return equipment; }

    public Integer getQuantity() { return quantity; }

    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    /**
     * 判斷兩個 RentalItem 物件是否邏輯相等。僅基於 equipment.getEquipmentID() 判斷。因為 RentalList 使用 HashSet，此方法對於防止重複加入至關重要。
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        RentalItem that = (RentalItem) obj;
        // 只要器材 ID 相同，就視為同一個 RentalItem
        return equipment.getEquipmentID().equals(that.equipment.getEquipmentID());
    }

    /**
     * 計算 RentalItem 物件的雜湊碼。僅基於 equipment.getEquipmentID() 計算。配合 equals 使用，確保 HashSet 運作正常。
     */
    @Override
    public int hashCode() {
        return Objects.hash(equipment.getEquipmentID());
    }
}