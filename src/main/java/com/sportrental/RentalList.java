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
    private HashSet<RentalItem> itemsSet;

    /**
     * 建構子：初始化 RentalList 物件。
     * 自動生成清單 ID，設定建立日期，並初始化品項集合。
     */
    public RentalList() {
        this.listID = UUID.randomUUID().toString();
        this.creationDate = LocalDateTime.now();
        this.itemsSet = new HashSet<>();
    }

    /**
     * 將租借品項加入清單。
     * 如果品項已存在 (依據 equipmentID 判斷)，則更新其數量。
     * @param item 要加入的 RentalItem 物件
     */
    public void addItem(RentalItem item) {
        // 檢查清單中是否已存在此器材
        boolean found = false;
        for (RentalItem existingItem : itemsSet) {
            if (existingItem.getEquipment().getEquipmentID().equals(item.getEquipment().getEquipmentID())) {
                // 如果存在，更新數量
                existingItem.setQuantity(existingItem.getQuantity() + item.getQuantity());
                found = true;
                break;
            }
        }
        if (!found) {
            itemsSet.add(item);
        }
    }

    /**
     * 從清單中移除指定器材 ID 的租借品項。
     * @param equipmentID 要移除的器材 ID
     */
    public void removeItem(String equipmentID) {
        itemsSet.removeIf(item -> item.getEquipment().getEquipmentID().equals(equipmentID));
    }

    /**
     * 更新清單中指定器材 ID 的租借品項數量。
     * 如果 newQuantity 為 0 或更少，則移除該品項。
     * @param equipmentID 要更新的器材 ID
     * @param newQuantity 新的數量
     */
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
        System.out.println("警告：租借清單中找不到器材 ID 為 " + equipmentID + " 的品項，無法更新。");
    }

    /**
     * 取得清單中的所有租借品項。
     * @return 租借品項的列表
     */
    public List<RentalItem> getItems() {
        return new ArrayList<>(itemsSet);
    }

    /**
     * 檢查租借清單是否需要審計。
     * 如果任何一個品項的數量大於其對應器材的審計門檻，則回傳 true。
     * @return 如果需要審計則回傳 true，否則回傳 false
     */
    public Boolean checkAuditRequirement() {
        for (RentalItem item : itemsSet) {
            if (item.getQuantity() > item.getEquipment().getAuditThreshold()) {
                return true;
            }
        }
        return false;
    }

    /**
     * 取得租借清單的 ID。
     * @return 清單 ID
     */
    public String getListID() {
        return listID;
    }

    /**
     * 取得租借清單的建立日期。
     * @return 建立日期
     */
    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    /**
     * 取得租借清單中的品項集合。
     * @return 品項集合的副本
     */
    public HashSet<RentalItem> getItemsSet() {
        return new HashSet<>(itemsSet); // 回傳副本以保護內部狀態
    }

    /**
     * 清空租借清單。
     */
    public void clearList() {
        itemsSet.clear();
    }
}