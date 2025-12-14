package com.sportrental;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * 租借清單
 * 每個 `RentalList` 實例都有一個唯一的 ID 和創建日期，並包含一個不重複的 `RentalItem` 集合。
 * 它提供了一系列方法來添加、移除、更新租借品項，以及檢查是否需要審計。
 */
public class RentalList {
    private String listID;
    private LocalDateTime creationDate;
    private HashSet<RentalItem> itemsSet;

    public RentalList() {
        this.listID = UUID.randomUUID().toString();
        this.creationDate = LocalDateTime.now();
        this.itemsSet = new HashSet<>();
    }

    public void addItem(RentalItem item) {

        int itemID = item.getEquipment().getEquipmentID();
        boolean isFound = false;
        
        // 檢查清單中是否已存在此器材
        for (RentalItem existingItem : itemsSet) {
            int existingItemID = existingItem.getEquipment().getEquipmentID();
            
            // 如果存在，更新數量
            if (existingItemID == itemID){
                existingItem.setQuantity(existingItem.getQuantity() + item.getQuantity());
                isFound = true;
                break;
            }
        }

        // 如果不存在，添加新品項
        if (!isFound) {
            itemsSet.add(item);
        }
    }

    public void removeItem(String equipmentID) {
        int itemID = item.getEquipment().getEquipmentID();
        itemsSet.removeIf(item -> itemID.equals(equipmentID));
    }

    public void updateItem(String equipmentID, Integer newQuantity) {
        
        // 如果 newQuantity 為 0 或更少，則移除該品項。
        if (newQuantity <= 0) { 
            removeItem(equipmentID);
            return;
        }

        // 如果 newQuantity 大於 0，則更新該品項的數量
        for (RentalItem item : itemsSet) {
            int itemID = item.getEquipment().getEquipmentID();
            
            // 找到對應的品項
            if (itemID == equipmentID) {
                item.setQuantity(newQuantity); // 更新數量
                return;
            }
        }

        System.out.println("警告：租借清單中找不到器材 ID 為 " + equipmentID + " 的品項，無法更新。");
    }

    public List<RentalItem> getItems() { return new ArrayList<>(itemsSet); }

    /**
     * 檢查租借清單是否需要審計。如果任何一個品項的數量大於其對應器材的審計門檻，則回傳 true。
     * @return 如果需要審計則回傳 true，否則回傳 false
     */
    public Boolean isAuditRequired() {

        // 檢查每一個品項
        for (RentalItem item : itemsSet) {
            int quantity = item.getQuantity();
            int auditThreshold = item.getEquipment().getAuditThreshold();

            // 如果品項數量大於對應器材的審計門檻，則回傳 true
            if (quantity > auditThreshold) {
                return true;
            }
        }
        
        // 如果所有品項都小於或等於對應器材的審計門檻，則回傳 false
        return false;
    }

    public String getListID() { return listID; }

    public LocalDateTime getCreationDate() { return creationDate; }

    /**
     * 取得租借清單中的品項集合。
     * @return 品項集合的副本
     */
    public Set<RentalItem> getItemsSet() {  return new HashSet<>(itemsSet); /* 回傳副本以保護內部狀態 */ }

    /**
     * 清空租借清單。
     */
    public void clearList() { itemsSet.clear(); }
}