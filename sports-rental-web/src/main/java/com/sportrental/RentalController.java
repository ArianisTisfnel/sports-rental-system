package com.sportrental;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class RentalController {
    // 確保這些是 public，這樣 RentalPage 才能讀取資料顯示在網頁上
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
        // 使用 LinkedHashMap 保留插入順序，讓前端導航順序穩定
        equipmentInventory = new LinkedHashMap<>();
        // 載入靜態器材庫存資料：將 emoji 加入 name，模板只需使用 name 即可
        equipmentInventory.put("E001", new Equipment("E001", "🏀 籃球", 20, 10));
        equipmentInventory.put("E002", new Equipment("E002", "🏐 排球", 15, 5));
        equipmentInventory.put("E003", new Equipment("E003", "🏸 羽毛球拍", 30, 15));
        equipmentInventory.put("E004", new Equipment("E004", "♟️ 羽毛球", 10, 5));
        equipmentInventory.put("E005", new Equipment("E005", "🏓 桌球拍", 30, 15));
        equipmentInventory.put("E006", new Equipment("E006", "🟠 桌球", 10, 5));
        equipmentInventory.put("E007", new Equipment("E007", "🎾 網球拍", 25, 10));

        currentMember = new Member("M001", "測試會員");
        currentList = new RentalList();

        // 移除這行：rentalPage = new RentalPage();
        // 因為現在 RentalPage 是 Spring 的控制器，它會自己把自己塞進來
    }

    /**
     * 將加器材到租借清單中
     * @param equipmentID 器材ID
     * @param quantity 需借數量
     * @throws NullPointerException 如果器材ID不存在
     * @throws IllegalArgumentException 如果器材庫存不足
     */
    public void addItemRequest(String equipmentID, Integer quantity) throws NullPointerException, IllegalArgumentException
    { 
        Equipment equipment = equipmentInventory.get(equipmentID);
        if (equipment == null) {
            final String STATUS_MESSAGE = "錯誤：找不到器材 ID: " + equipmentID;
            rentalPage.displayStatusMessage(STATUS_MESSAGE);
            throw new NullPointerException(STATUS_MESSAGE);
        }

        if (Boolean.FALSE.equals(equipment.isQuantityAvailable(quantity))) {
            final String STATUS_MESSAGE = "錯誤：器材 '" + equipment.getName() + "' (ID: " + equipmentID + ") 庫存不足";
            rentalPage.displayStatusMessage(STATUS_MESSAGE);
            throw new IllegalArgumentException(STATUS_MESSAGE);
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

    /**
     * 處理結帳流程。
     * 如果租借清單是空的：顯示錯誤訊息。
     * 如果任何一個品項的數量大於其對應器材的審計門檻：則顯示錯誤訊息；
     * 否則，顯示 Audit Modal 讓使用者輸入審核理由。
     * 如果 Web 版回傳 null，代表需要等待使用者輸入，先結束；
     * 否則，核准訂單。
     */
    public void processCheckout() {
        if (currentList.getItems().isEmpty()) {
            rentalPage.displayStatusMessage("錯誤：您的租借清單是空的，無法結帳。");
            return;
        }

        for (RentalItem item : currentList.getItems()) {
            Equipment equipment = item.getEquipment();
            if (Boolean.FALSE.equals(equipment.isQuantityAvailable(item.getQuantity()))) {
                rentalPage.displayStatusMessage("錯誤：器材 '" + equipment.getName() + "' 庫存不足，無法結帳。");
                return;
            }
        }

        // 這裡會呼叫 RentalPage (Web)，它會回傳 null 來中斷流程並顯示 Modal
        if (Boolean.TRUE.equals(currentList.isAuditRequired())) {
            String auditReason = rentalPage.promptForAuditReason();
            // 如果 Web 版回傳 null，代表需要等待使用者輸入，先結束
            if (auditReason == null) return;
            confirmOrder(auditReason);
        } else {
            confirmOrder();
        }
    }

    /**
     * 核核訂單。
     * 會將目前的租借清單轉換為訂單，並將其加入會員的歷史紀錄中。
     * 並將器材的庫存減少對應的數量。
     * 最後，清空目前的租借清單。
     */
    public void confirmOrder() {
        Order newOrder = new Order(UUID.randomUUID().toString(), currentMember, currentList.getItemsSet(), "Approved", null);
        for (RentalItem item : newOrder.getLineItems()) {
            item.getEquipment().decreaseStock(item.getQuantity());
        }
        currentMember.addRentalHistory(newOrder);
        rentalPage.displayStatusMessage("訂單 (ID: " + newOrder.getOrderID() + ") 已成功核准，無需審計。");
        currentList.clearList();
    }

    /**
     * 核單核准
     * @param auditReason 審計理由
     * @see #confirmOrder()
     */
    public void confirmOrder(String auditReason) {
        boolean approved = random.nextBoolean(); // 以亂數模擬管理員審核結果：50% 的機率通過；50% 的機率拒絕。
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