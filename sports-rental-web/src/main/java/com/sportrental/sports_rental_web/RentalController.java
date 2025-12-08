package com.sportrental;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class RentalController {
    
    private Map<String, Equipment> equipmentInventory = new HashMap<>();
    private RentalList currentList = new RentalList();
    
    // 新增：模擬一個當前登入的會員 (為了符合 UML 的 Member 關聯)
    private Member currentMember; 

    public RentalController() {
        // 初始化器材
        equipmentInventory.put("E001", new Equipment("E001", "籃球", 20, 5));
        equipmentInventory.put("E002", new Equipment("E002", "排球", 15, 5));
        equipmentInventory.put("E003", new Equipment("E003", "羽球拍", 50, 10)); 
        equipmentInventory.put("E004", new Equipment("E004", "桌球拍", 30, 8)); 
        
        // 初始化一個假會員 (User)
        this.currentMember = new Member("M001", "王小明");
    }

    public List<Equipment> getAllEquipments() {
        return new ArrayList<>(equipmentInventory.values());
    }

    public List<RentalItem> getCurrentList() {
        return currentList.getItemsList();
    }
    
    // 取得當前會員 (讓 UI 顯示名字用)
    public Member getCurrentMember() {
        return currentMember;
    }

    public String addToCart(String id, int qty) {
        Equipment eq = equipmentInventory.get(id);
        if (eq == null) return "找不到器材";
        if (qty > eq.getAvailableStock()) return "庫存不足！目前僅剩 " + eq.getAvailableStock();
        
        currentList.addItem(eq, qty);
        return "加入成功";
    }

    public String checkAuditRequirement() {
        if (currentList.getItemsList().isEmpty()) return "EMPTY";
        if (currentList.needsAudit()) return "NEED_AUDIT";
        return "OK";
    }

    // ⭐️ 更新：結帳時建立 Order 並存入 Member
    public void confirmOrder(String auditReason) {
        // 1. 扣除庫存 (UML Source: 207)
        for (RentalItem item : currentList.getItemsList()) {
            item.getEquipment().decreaseStock(item.getQuantity());
        }
        
        // 2. 建立訂單 (UML Source: 244)
        String newOrderId = UUID.randomUUID().toString().substring(0, 8); // 隨機產生 ID
        Order newOrder = new Order(newOrderId, currentMember.getMemberID(), currentList.getItemsList(), auditReason);
        
        // 3. 存入會員歷史紀錄 (UML Source: 189, 267)
        currentMember.addRentalHistory(newOrder);

        // 4. 清空購物車
        currentList.clear();
    }
}