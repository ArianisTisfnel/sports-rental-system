package com.sportrental.sports_rental_web;

import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
public class RentalList {
    private List<RentalItem> itemsList = new ArrayList<>();

    public void addItem(Equipment eq, int qty) {
        for (RentalItem item : itemsList) {
            if (item.getEquipment().getEquipmentID().equals(eq.getEquipmentID())) {
                item.setQuantity(item.getQuantity() + qty);
                return;
            }
        }
        itemsList.add(new RentalItem(eq, qty));
    }

    // 🆕 新增：為了支援購物車頁面的「更新」按鈕
    public void updateQuantity(String equipmentID, int newQty) {
        for (RentalItem item : itemsList) {
            if (item.getEquipment().getEquipmentID().equals(equipmentID)) {
                if (newQty > 0) item.setQuantity(newQty);
                return;
            }
        }
    }

    // 🆕 新增：為了支援購物車頁面的「移除」按鈕
    public void removeItem(String equipmentID) {
        Iterator<RentalItem> iterator = itemsList.iterator();
        while (iterator.hasNext()) {
            RentalItem item = iterator.next();
            if (item.getEquipment().getEquipmentID().equals(equipmentID)) {
                iterator.remove();
                return;
            }
        }
    }

    public List<RentalItem> getItemsList() { return itemsList; }
    public void clear() { itemsList.clear(); }

    public boolean needsAudit() {
        for (RentalItem item : itemsList) {
            if (item.getQuantity() > item.getEquipment().getAuditThreshold()) return true;
        }
        return false;
    }
}