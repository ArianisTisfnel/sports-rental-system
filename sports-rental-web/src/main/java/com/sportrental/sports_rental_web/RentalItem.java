package com.sportrental.sports_rental_web;
public class RentalItem {
    private Equipment equipment;
    private int quantity;

    public RentalItem(Equipment equipment, int quantity) {
        this.equipment = equipment;
        this.quantity = quantity;
    }

    public Equipment getEquipment() { return equipment; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}