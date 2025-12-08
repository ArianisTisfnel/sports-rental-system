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
        // 初始化：籃球20顆, 羽球拍50支(門檻10)
        equipmentInventory.put("E001", new Equipment("E001", "籃球", 20, 5));
        equipmentInventory.put("E002", new Equipment("E002", "排球", 15, 5));
        equipmentInventory.put("E003", new Equipment("E003", "羽球拍", 50, 10));
        equipmentInventory.put("E004", new Equipment("E004", "桌球拍", 30, 8));

        this.currentMember = new Member("M001", "王小明");
    }

    public List<Equipment> getAllEquipments() {
        return new ArrayList<>(equipmentInventory.values());
    }

    public List<RentalItem> getCurrentList() {
        return currentList.getItemsList();
    }

    // 加入購物車 (邏輯不變)
    public String addToCart(String id, int qty) {
        Equipment eq = equipmentInventory.get(id);
        if (eq == null) return "錯誤：找不到器材";
        if (qty <= 0) return "錯誤：數量必須大於 0";
        // 注意：這裡先做簡單檢查，嚴謹的話要檢查 (現有+新增) 是否超過庫存
        if (qty > eq.getAvailableStock()) {
            return "錯誤：庫存不足！" + eq.getName() + " 目前僅剩 " + eq.getAvailableStock() + " 個。";
        }
        currentList.addItem(eq, qty);
        return "成功：已加入 " + qty + " 個 " + eq.getName();
    }

    // 新增：更新購物車數量 (S-2)
    public String updateCartItem(String id, int newQty) {
        if (newQty <= 0) return "錯誤：數量必須大於 0";
        Equipment eq = equipmentInventory.get(id);

        // 檢查庫存 (修改後的數量不能超過庫存)
        if (newQty > eq.getAvailableStock()) {
            return "錯誤：庫存不足！無法修改為 " + newQty + " 個。";
        }

        currentList.updateQuantity(id, newQty);
        return "已更新數量";
    }

    // 新增：移除購物車項目 (S-3)
    public String removeCartItem(String id) {
        currentList.removeItem(id);
        return "已移除器材";
    }

    // 檢查是否需要審核
    public String checkAuditRequirement() {
        if (currentList.getItemsList().isEmpty()) return "EMPTY";
        if (currentList.needsAudit()) return "NEED_AUDIT";
        return "OK";
    }

    // 結帳並存檔
    public void confirmOrder(String auditReason) {
        for (RentalItem item : currentList.getItemsList()) {
            item.getEquipment().decreaseStock(item.getQuantity());
        }
        String newOrderId = UUID.randomUUID().toString().substring(0, 8);
        Order newOrder = new Order(newOrderId, currentMember.getMemberID(), currentList.getItemsList(), auditReason);
        currentMember.addRentalHistory(newOrder);
        currentList.clear();
    }
}