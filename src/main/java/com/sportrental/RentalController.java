package com.sportrental;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

/**
 * 管理租借流程的核心控制器。
 */
public class RentalController {
    public Map<String, Equipment> equipmentInventory;
    public Member currentMember;
    public RentalList currentList;
    public RentalPage rentalPage;

    public RentalController() {
        initSystem();
    }

    /**
     * 載入靜態器材庫存並建立預設會員、購物車與頁面。
     */
    public void initSystem() {
        equipmentInventory = new HashMap<>();
        equipmentInventory.put("E001", new Equipment("E001", "籃球", 30, 10));
        equipmentInventory.put("E002", new Equipment("E002", "排球", 25, 5));
        equipmentInventory.put("E003", new Equipment("E003", "羽毛球拍", 40, 15));
        equipmentInventory.put("E004", new Equipment("E004", "桌球", 10, 8));

        currentMember = new Member("M001", "DefaultMember");
        currentList = new RentalList();
        rentalPage = new RentalPage();
        rentalPage.displayStatusMessage("系統初始化完成。");
    }

    /**
     * 處理新增租借品項的請求。
     * [修正版] 增加累加數量檢查，防止分次加入導致總數超過庫存。
     * @param equipmentID 器材ID
     * @param quantity 租借數量
     */
    public void addItemRequest(String equipmentID, Integer quantity) {
        Equipment equipment = equipmentInventory.get(equipmentID);
        if (equipment == null) {
            rentalPage.displayStatusMessage("錯誤：找不到器材 ID: " + equipmentID);
            return;
        }

        // Step 1: 先計算購物車內已經有多少個這個器材
        int currentInCart = 0;
        for (RentalItem item : currentList.getItems()) {
            if (item.getEquipment().getEquipmentID().equals(equipmentID)) {
                currentInCart = item.getQuantity();
                break;
            }
        }

        // Step 2: 檢查 (購物車已有 + 這次想加的) 是否超過總可用庫存
        // equipment.getAvailableStock() 是指總共剩多少，不是指還能再借多少
        if (quantity + currentInCart > equipment.getAvailableStock()) {
            rentalPage.displayStatusMessage(
                "錯誤：庫存不足！器材 '" + equipment.getName() +
                "' 目前可用: " + equipment.getAvailableStock() +
                ", 您的購物車已加入: " + currentInCart +
                ", 無法再加入: " + quantity + " 個。"
            );
            return;
        }

        // Step 3: 通過檢查，執行加入
        RentalItem newItem = new RentalItem(equipment, quantity);
        currentList.addItem(newItem);

        rentalPage.displayStatusMessage("成功加入 '" + equipment.getName() + "' " + quantity + " 個。");
        rentalPage.displayCurrentList(currentList);
    }

    /**
     * 從購物車移除指定器材。
     */
    public void removeItemFromCart(String equipmentID) {
        currentList.removeItem(equipmentID);
        rentalPage.displayStatusMessage("已從購物車移除器材 ID: " + equipmentID);
        rentalPage.displayCurrentList(currentList);
    }

    /**
     * 更新購物車內器材的數量。
     */
    public void updateItemQuantity(String equipmentID, Integer newQuantity) {
        Equipment equipment = equipmentInventory.get(equipmentID);
        if (equipment == null) {
            rentalPage.displayStatusMessage("錯誤：找不到器材 ID: " + equipmentID);
            return;
        }
        if (newQuantity > equipment.getAvailableStock()) {
            rentalPage.displayStatusMessage("錯誤：數量超過可用庫存，更新失敗。");
            return;
        }
        currentList.updateItem(equipmentID, newQuantity);
        rentalPage.displayStatusMessage("已更新器材 " + equipmentID + " 的數量為 " + newQuantity);
        rentalPage.displayCurrentList(currentList);
    }

    /**
     * 結帳流程：庫存檢查後依需求進入審計或直接成立訂單。
     */
    public void processCheckout() {
        if (currentList.getItems().isEmpty()) {
            rentalPage.displayStatusMessage("購物車為空，無法結帳。");
            return;
        }

        // 庫存前置檢查
        for (RentalItem item : currentList.getItems()) {
            Equipment equipment = item.getEquipment();
            if (!equipment.checkAvailability(item.getQuantity())) {
                rentalPage.displayStatusMessage("器材 " + equipment.getEquipmentID() + " 庫存不足，結帳已取消。");
                return;
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
     * 無需審計的訂單成立流程。
     */
    public void confirmOrder() {
        Order order = new Order(UUID.randomUUID().toString(), currentMember, currentList.getItemsSet(), "Approved", null);
        for (RentalItem item : order.getLineItems()) {
            item.getEquipment().decreaseStock(item.getQuantity());
        }
        currentMember.addRentalHistory(order);
        currentList.clearList();
        rentalPage.displayStatusMessage("訂單成立並已扣除庫存。狀態: Approved");
        rentalPage.displayCurrentList(currentList);
    }

    /**
     * 需審計的訂單成立流程。
     */
    public void confirmOrder(String auditReason) {
        boolean approved = new Random().nextBoolean();
        String status = approved ? "Approved" : "Rejected";
        Order order = new Order(UUID.randomUUID().toString(), currentMember, currentList.getItemsSet(), status, auditReason);

        if (approved) {
            for (RentalItem item : order.getLineItems()) {
                item.getEquipment().decreaseStock(item.getQuantity());
            }
            rentalPage.displayStatusMessage("審核通過，訂單成立並已扣庫存。");
        } else {
            rentalPage.displayStatusMessage("審核未通過，訂單被拒絕，庫存未扣除。");
        }

        currentMember.addRentalHistory(order);
        currentList.clearList();
        rentalPage.displayCurrentList(currentList);
    }
}