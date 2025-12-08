package com.sportrental.sports_rental_web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
public class RentalController {

    private Map<String, Equipment> equipmentInventory = new HashMap<>();
    private RentalList currentList = new RentalList();
    private Member currentMember;

    public RentalController() {
        equipmentInventory.put("E001", new Equipment("E001", "籃球", 20, 5));
        equipmentInventory.put("E002", new Equipment("E002", "排球", 15, 5));
        equipmentInventory.put("E003", new Equipment("E003", "羽球拍", 50, 10));
        equipmentInventory.put("E004", new Equipment("E004", "桌球拍", 30, 8));
        this.currentMember = new Member("M001", "王小明");
    }

    // 🆕 新增：給首頁顯示用
    public List<Equipment> getAllEquipments() {
        return new ArrayList<>(equipmentInventory.values());
    }

    public List<RentalItem> getCurrentList() { return currentList.getItemsList(); }

    // 🔄 修改：回傳 String 訊息給網頁 Alert
    public String addToCart(String id, int qty) {
        Equipment eq = equipmentInventory.get(id);
        if (eq == null) return "錯誤：找不到器材";
        if (qty <= 0) return "錯誤：數量必須大於 0";
        if (qty > eq.getAvailableStock()) return "錯誤：庫存不足！";

        currentList.addItem(eq, qty);
        return "成功：已加入 " + qty + " 個 " + eq.getName();
    }

    // 🆕 新增：給購物車頁面更新用
    public String updateCartItem(String id, int newQty) {
        if (newQty <= 0) return "錯誤：數量必須大於 0";
        Equipment eq = equipmentInventory.get(id);
        if (newQty > eq.getAvailableStock()) return "錯誤：庫存不足！";

        currentList.updateQuantity(id, newQty);
        return "已更新數量";
    }

    // 🆕 新增：給購物車頁面刪除用
    public String removeCartItem(String id) {
        currentList.removeItem(id);
        return "已移除器材";
    }

    // 🆕 新增：檢查審核狀態
    public String checkAuditRequirement() {
        if (currentList.getItemsList().isEmpty()) return "EMPTY";
        if (currentList.needsAudit()) return "NEED_AUDIT";
        return "OK";
    }

    public void confirmOrder(String auditReason) {
        for (RentalItem item : currentList.getItemsList()) {
            item.getEquipment().decreaseStock(item.getQuantity());
        }
        // 這裡省略建立 Order 物件的細節，重點是邏輯流程
        currentList.clear();
    }
}