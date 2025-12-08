package com.sportrental;


public class RentalItem {
    private Equipment equipment;
    private Integer quantity;

    public RentalItem(Equipment equipment, Integer quantity) {
        this.equipment = equipment;
        this.quantity = quantity;
    }

    public Equipment getEquipment() { return equipment; }
    public Integer getQuantity() { return quantity; }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}