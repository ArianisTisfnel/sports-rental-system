package com.sportrental;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.Scanner; // For simulation input

public class RentalController {
    public Map<String, Equipment> equipmentInventory;
    public Member currentMember;
    public RentalList currentList;
    public RentalPage rentalPage;
    public Random random;

    /**
     * 建構子：初始化 RentalController 物件。
     * 內部會呼叫 initSystem() 進行系統啟動。
     */
    public RentalController() {
        this.random = new Random();
        initSystem();
    }

    /**
     * 負責系統啟動與初始化：
     * 1. 載入靜態器材庫存資料到 equipmentInventory。
     * 2. 建立一個預設 Member 實例 (currentMember)。
     * 3. 建立 RentalPage 實例。
     * 4. 建立 RentalList 實例。
     */
    public void initSystem() {
        equipmentInventory = new HashMap<>();
        // 載入靜態器材庫存資料
        equipmentInventory.put("E001", new Equipment("E001", "籃球", 20, 10));
        equipmentInventory.put("E002", new Equipment("E002", "排球", 15, 5)); // 審計門檻較低
        equipmentInventory.put("E003", new Equipment("E003", "羽毛球拍", 30, 15));
        equipmentInventory.put("E004", new Equipment("E004", "桌球", 10, 5));
        equipmentInventory.put("E005", new Equipment("E005", "網球拍", 25, 10));

        // 建立一個預設 Member 實例
        currentMember = new Member("M001", "測試會員");

        // 建立 RentalList 實例
        currentList = new RentalList();

        // 建立 RentalPage 實例
        rentalPage = new RentalPage();

        rentalPage.displayStatusMessage("系統初始化完成，歡迎 " + currentMember.getAccountName() + "！");
    }

    /**
     * 處理新增租借品項的請求。
     * 檢查器材在庫存中的狀態，確認可租借後呼叫 RentalList 的 addItem() 往購物車內新增租借品項。
     * @param equipmentID 器材ID
     * @param quantity 租借數量
     */
    public void addItemRequest(String equipmentID, Integer quantity) {
        Equipment equipment = equipmentInventory.get(equipmentID);
        if (equipment == null) {
            rentalPage.displayStatusMessage("錯誤：找不到器材 ID: " + equipmentID);
            return;
        }

        if (!equipment.checkAvailability(quantity)) {
            rentalPage.displayStatusMessage("錯誤：器材 '" + equipment.getName() + "' (ID: " + equipmentID + ") 庫存不足，目前可用數量: " + equipment.getAvailableStock());
            return;
        }

        RentalItem newItem = new RentalItem(equipment, quantity);
        currentList.addItem(newItem);
        rentalPage.displayStatusMessage("已將 '" + equipment.getName() + "' (數量: " + quantity + ") 加入租借清單。");
        rentalPage.displayCurrentList(currentList);
    }

    /**
     * 從購物車中移除指定器材 ID 的品項。
     * @param equipmentID 要移除的器材 ID
     */
    public void removeItemFromCart(String equipmentID) {
        currentList.removeItem(equipmentID);
        rentalPage.displayStatusMessage("已從租借清單中移除器材 ID: " + equipmentID + "。");
        rentalPage.displayCurrentList(currentList);
    }

    /**
     * 更新購物車中指定器材 ID 的品項數量。
     * @param equipmentID 要更新的器材 ID
     * @param newQuantity 新的數量
     */
    public void updateItemQuantity(String equipmentID, Integer newQuantity) {
        Equipment equipment = equipmentInventory.get(equipmentID);
        if (equipment == null) {
            rentalPage.displayStatusMessage("錯誤：找不到器材 ID: " + equipmentID);
            return;
        }

        if (newQuantity > equipment.getAvailableStock()) {
            rentalPage.displayStatusMessage("錯誤：器材 '" + equipment.getName() + "' (ID: " + equipmentID + ") 庫存不足，無法更新為 " + newQuantity + " 個。目前可用數量: " + equipment.getAvailableStock());
            return;
        }

        currentList.updateItem(equipmentID, newQuantity);
        rentalPage.displayStatusMessage("已更新器材 ID: " + equipmentID + " 的數量為 " + newQuantity + "。");
        rentalPage.displayCurrentList(currentList);
    }

    /**
     * 負責啟動結帳流程。
     * 進行庫存前置檢查，並在訂單完成後決定是否送出審計理由。
     */
    public void processCheckout() {
        if (currentList.getItems().isEmpty()) {
            rentalPage.displayStatusMessage("錯誤：您的租借清單是空的，無法結帳。");
            return;
        }

        // 庫存前置檢查
        for (RentalItem item : currentList.getItems()) {
            Equipment equipment = item.getEquipment();
            if (!equipment.checkAvailability(item.getQuantity())) {
                rentalPage.displayStatusMessage("錯誤：器材 '" + equipment.getName() + "' (ID: " + equipment.getEquipmentID() + ") 庫存不足，無法結帳。目前可用數量: " + equipment.getAvailableStock());
                return; // 立即中止
            }
        }

        // 審計判斷
        if (currentList.checkAuditRequirement()) {
            String auditReason = rentalPage.promptForAuditReason();
            confirmOrder(auditReason);
        } else {
            confirmOrder();
        }
    }

    /**
     * 如果沒有審計理由，直接建構一個沒有 auditReason 的 Order (訂單成立)，並通知 Equipment 進行庫存扣除。
     */
    public void confirmOrder() {
        // 建立訂單，狀態為 Approved
        Order newOrder = new Order(UUID.randomUUID().toString(), currentMember, currentList.getItemsSet(), "Approved", null);

        // 庫存操作：扣除庫存
        for (RentalItem item : newOrder.getLineItems()) {
            item.getEquipment().decreaseStock(item.getQuantity());
        }

        // 將完成的 Order 加入 currentMember 的 rentalHistory
        currentMember.addRentalHistory(newOrder);
        rentalPage.displayStatusMessage("訂單 (ID: " + newOrder.getOrderID() + ") 已成功核准，無需審計。");
        currentList.clearList(); // 清空購物車
        rentalPage.displayCurrentList(currentList);
    }

    /**
     * 如果有審計理由，亂數生成管理員的允許情形（50%/50%-True/False），
     * 並依據結果決定是否建構一個包含 itemSet 跟 auditReason 的 Order（訂單成立），並通知 Equipment 進行庫存。
     * @param auditReason 審計理由
     */
    public void confirmOrder(String auditReason) {
        // 模擬管理員審核：50% 允許 / 50% 拒絕
        boolean approved = random.nextBoolean(); // true 或 false

        String orderStatus;
        if (approved) {
            orderStatus = "Approved";
            // 建立訂單，狀態為 Approved
            Order newOrder = new Order(UUID.randomUUID().toString(), currentMember, currentList.getItemsSet(), orderStatus, auditReason);
            // 庫存操作：扣除庫存
            for (RentalItem item : newOrder.getLineItems()) {
                item.getEquipment().decreaseStock(item.getQuantity());
            }
            currentMember.addRentalHistory(newOrder);
            rentalPage.displayStatusMessage("訂單 (ID: " + newOrder.getOrderID() + ") 已通過審計並核准。");
            currentList.clearList(); // 清空購物車
        } else {
            orderStatus = "Rejected";
            // 建立訂單，狀態為 Rejected (庫存不扣除)
            Order newOrder = new Order(UUID.randomUUID().toString(), currentMember, currentList.getItemsSet(), orderStatus, auditReason);
            currentMember.addRentalHistory(newOrder);
            rentalPage.displayStatusMessage("訂單 (ID: " + newOrder.getOrderID() + ") 審計未通過，已拒絕。庫存未扣除。");
        }
        rentalPage.displayCurrentList(currentList);
    }
}