package com.sportrental;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class RentalController {
    // ⚠️ 確保這些是 public，這樣 RentalPage 才能讀取資料顯示在網頁上
    public Map<String, Equipment> equipmentInventory;
    public Member currentMember;
    public RentalList currentList;

    // 這是 Web 介面 (由 RentalPage 自己注入進來)
    public RentalPage rentalPage;

    public Random random;

    public RentalController() {
        this.random = new Random();
        initSystem();
    }

    public void initSystem() {
        equipmentInventory = new HashMap<>();
        // 載入靜態器材庫存資料
        equipmentInventory.put("E001", new Equipment("E001", "籃球", 20, 10));
        equipmentInventory.put("E002", new Equipment("E002", "排球", 15, 5));
        equipmentInventory.put("E003", new Equipment("E003", "羽毛球拍", 30, 15));
        equipmentInventory.put("E004", new Equipment("E004", "桌球", 10, 5));
        equipmentInventory.put("E005", new Equipment("E005", "網球拍", 25, 10));

        currentMember = new Member("M001", "測試會員");
        currentList = new RentalList();

        // ❌ 移除這行：rentalPage = new RentalPage();
        // 因為現在 RentalPage 是 Spring 的控制器，它會自己把自己塞進來
    }

    public void addItemRequest(String equipmentID, Integer quantity) {
        Equipment equipment = equipmentInventory.get(equipmentID);
        if (equipment == null) {
            rentalPage.displayStatusMessage("錯誤：找不到器材 ID: " + equipmentID);
            return;
        }

        if (Boolean.FALSE.equals(equipment.checkAvailability(quantity))) {
            rentalPage.displayStatusMessage("錯誤：器材 '" + equipment.getName() + "' (ID: " + equipmentID + ") 庫存不足，目前可用: " + equipment.getAvailableStock());
            return;
        }

        RentalItem newItem = new RentalItem(equipment, quantity);
        currentList.addItem(newItem);
        rentalPage.displayStatusMessage("成功：已將 '" + equipment.getName() + "' (數量: " + quantity + ") 加入租借清單。");
    }

    public void removeItemFromCart(String equipmentID) {
        currentList.removeItem(equipmentID);
        rentalPage.displayStatusMessage("已從租借清單中移除器材 ID: " + equipmentID);
    }

    public void updateItemQuantity(String equipmentID, Integer newQuantity) {
        Equipment equipment = equipmentInventory.get(equipmentID);
        if (newQuantity > equipment.getAvailableStock()) {
            rentalPage.displayStatusMessage("錯誤：庫存不足，無法更新為 " + newQuantity + " 個。");
            return;
        }

        currentList.updateItem(equipmentID, newQuantity);
        rentalPage.displayStatusMessage("已更新器材 ID: " + equipmentID + " 的數量為 " + newQuantity);
    }

    public void processCheckout() {
        if (currentList.getItems().isEmpty()) {
            rentalPage.displayStatusMessage("錯誤：您的租借清單是空的，無法結帳。");
            return;
        }

        for (RentalItem item : currentList.getItems()) {
            Equipment equipment = item.getEquipment();
            if (Boolean.FALSE.equals(equipment.checkAvailability(item.getQuantity()))) {
                rentalPage.displayStatusMessage("錯誤：器材 '" + equipment.getName() + "' 庫存不足，無法結帳。");
                return;
            }
        }

        // 這裡會呼叫 RentalPage (Web)，它會回傳 null 來中斷流程並顯示 Modal
        if (Boolean.TRUE.equals(currentList.checkAuditRequirement())) {
            String auditReason = rentalPage.promptForAuditReason();
            // 如果 Web 版回傳 null，代表需要等待使用者輸入，先結束
            if (auditReason == null) return;
            confirmOrder(auditReason);
        } else {
            confirmOrder();
        }
    }

    public void confirmOrder() {
        Order newOrder = new Order(UUID.randomUUID().toString(), currentMember, currentList.getItemsSet(), "Approved", null);
        for (RentalItem item : newOrder.getLineItems()) {
            item.getEquipment().decreaseStock(item.getQuantity());
        }
        currentMember.addRentalHistory(newOrder);
        rentalPage.displayStatusMessage("訂單 (ID: " + newOrder.getOrderID() + ") 已成功核准，無需審計。");
        currentList.clearList();
    }

    public void confirmOrder(String auditReason) {
        boolean approved = random.nextBoolean(); // 50% 機率
        String orderStatus = approved ? "Approved" : "Rejected";

        Order newOrder = new Order(UUID.randomUUID().toString(), currentMember, currentList.getItemsSet(), orderStatus, auditReason);

        if (approved) {
            for (RentalItem item : newOrder.getLineItems()) {
                item.getEquipment().decreaseStock(item.getQuantity());
            }
            rentalPage.displayStatusMessage("訂單 (ID: " + newOrder.getOrderID() + ") 已通過審計並核准。");
            currentList.clearList();
        } else {
            rentalPage.displayStatusMessage("訂單 (ID: " + newOrder.getOrderID() + ") 審計未通過，已拒絕。庫存未扣除。");
        }
        currentMember.addRentalHistory(newOrder);
    }
}