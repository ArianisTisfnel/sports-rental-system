package com.sportrental;

public class RentalItem {
    private Equipment equipment;
    private int quantity;

    public RentalItem(Equipment equipment, int quantity) {
        this.equipment = equipment;
        this.quantity = quantity;
    }

    public Equipment getEquipment() { return equipment; }
    public int getQuantity() { return quantity; }
}
