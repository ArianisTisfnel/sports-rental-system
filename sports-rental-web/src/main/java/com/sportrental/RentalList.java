package com.sportrental;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class RentalList {
    private String listID;
    private LocalDateTime creationDate;
    private HashSet<RentalItem> itemsSet; // 改用 HashSet

    public RentalList() {
        this.listID = UUID.randomUUID().toString();
        this.creationDate = LocalDateTime.now();
        this.itemsSet = new HashSet<>();
    }

    // ⭐️ 核心邏輯：加入時若已存在則累加數量
    public void addItem(RentalItem item) {
        boolean found = false;
        for (RentalItem existingItem : itemsSet) {
            if (existingItem.getEquipment().getEquipmentID().equals(item.getEquipment().getEquipmentID())) {
                existingItem.setQuantity(existingItem.getQuantity() + item.getQuantity());
                found = true;
                break;
            }
        }
        if (!found) {
            itemsSet.add(item);
        }
    }

    // 移除項目
    public void removeItem(String equipmentID) {
        itemsSet.removeIf(item -> item.getEquipment().getEquipmentID().equals(equipmentID));
    }

    // 更新項目 (包含數量 <= 0 移除的邏輯)
    public void updateItem(String equipmentID, Integer newQuantity) {
        if (newQuantity <= 0) {
            removeItem(equipmentID);
            return;
        }
        for (RentalItem item : itemsSet) {
            if (item.getEquipment().getEquipmentID().equals(equipmentID)) {
                item.setQuantity(newQuantity);
                return;
            }
        }
    }

    // 為了讓 Thymeleaf 方便顯示，轉成 List
    public List<RentalItem> getItems() {
        return new ArrayList<>(itemsSet);
    }

    // 給 Order 用
    public Set<RentalItem> getItemsSet() {
        return new HashSet<>(itemsSet);
    }

    // 檢查審核
    public Boolean checkAuditRequirement() {
        for (RentalItem item : itemsSet) {
            if (item.getQuantity() > item.getEquipment().getAuditThreshold()) {
                return true;
            }
        }
        return false;
    }

    public void clearList() {
        itemsSet.clear();
    }
}