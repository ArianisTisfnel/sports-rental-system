package com.sportrental;

import java.util.ArrayList;
import java.util.List;

public class RentalList {
    private List<RentalItem> itemsList = new ArrayList<>();

    public void addItem(Equipment eq, int qty) {
        itemsList.add(new RentalItem(eq, qty));
    }

    public List<RentalItem> getItemsList() {
        return itemsList;
    }

    public void clear() {
        itemsList.clear();
    }
    
    public boolean needsAudit() {
        for (RentalItem item : itemsList) {
            if (item.getQuantity() > item.getEquipment().getAuditThreshold()) {
                return true;
            }
        }
        return false;
    }
}