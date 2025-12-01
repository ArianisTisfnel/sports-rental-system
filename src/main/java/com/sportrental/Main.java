package com.sportrental;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        // 初始化 RentalController，它會自動呼叫 initSystem()
        RentalController controller = new RentalController();
        Scanner scenarioScanner = new Scanner(System.in);

        System.out.println("----- 租借系統測試場景 -----");
        controller.rentalPage.displayEquipmentCatalog(controller.equipmentInventory);

        // 場景 A (無審計，成功)：租借數量低於 auditThreshold，且庫存充足，最終訂單狀態為 Approved。
        System.out.println("\n--- 場景 A: 無審計，成功 ---");
        System.out.println("1. 將 '籃球' (ID: E001) 租借 5 個 (auditThreshold: 10)");
        controller.addItemRequest("E001", 5); // 5 < 10, 無需審計
        controller.processCheckout();
        System.out.println("場景 A 執行完畢。目前會員租借歷史訂單數量：" + controller.currentMember.getRentalHistory().size());
        if (!controller.currentMember.getRentalHistory().isEmpty()) {
            Order lastOrder = controller.currentMember.getRentalHistory().get(controller.currentMember.getRentalHistory().size() - 1);
            System.out.println("最後一筆訂單狀態: " + lastOrder.getStatus());
            System.out.println("E001 籃球目前可用庫存: " + controller.equipmentInventory.get("E001").getAvailableStock());
        }

        System.out.println("\n按下 Enter 鍵繼續到下一個場景...");
        scenarioScanner.nextLine();

        // 場景 B (需審計，通過)：租借數量高於 auditThreshold，使用者提供理由，亂數審核通過，最終訂單狀態為 Approved，庫存扣除。
        // 注意：審核是亂數 50/50，此場景可能需要多次執行才能看到 Approved 結果。
        System.out.println("\n--- 場景 B: 需審計，通過 (可能需多次嘗試) ---");
        System.out.println("1. 將 '排球' (ID: E002) 租借 8 個 (auditThreshold: 5)");
        System.out.println("請輸入審計理由 (例如: 社團活動需要):");
        controller.addItemRequest("E002", 8); // 8 > 5, 需審計
        controller.processCheckout(); // 這裡會提示輸入審計理由
        System.out.println("場景 B 執行完畢。目前會員租借歷史訂單數量：" + controller.currentMember.getRentalHistory().size());
        if (!controller.currentMember.getRentalHistory().isEmpty()) {
            Order lastOrder = controller.currentMember.getRentalHistory().get(controller.currentMember.getRentalHistory().size() - 1);
            System.out.println("最後一筆訂單狀態: " + lastOrder.getStatus());
            if ("Approved".equals(lastOrder.getStatus())) {
                System.out.println("E002 排球目前可用庫存: " + controller.equipmentInventory.get("E002").getAvailableStock());
            } else {
                System.out.println("審計未通過，排球庫存未扣除。");
            }
        }

        System.out.println("\n按下 Enter 鍵繼續到下一個場景...");
        scenarioScanner.nextLine();

        // 場景 C (需審計，拒絕)：租借數量高於 auditThreshold，使用者提供理由，亂數審核拒絕，最終訂單狀態為 Rejected，庫存不扣除。
        // 注意：審核是亂數 50/50，此場景可能需要多次執行才能看到 Rejected 結果。
        System.out.println("\n--- 場景 C: 需審計，拒絕 (可能需多次嘗試) ---");
        System.out.println("1. 將 '羽毛球拍' (ID: E003) 租借 20 個 (auditThreshold: 15)");
        System.out.println("請輸入審計理由 (例如: 舉辦大型比賽):");
        controller.addItemRequest("E003", 20); // 20 > 15, 需審計
        controller.processCheckout(); // 這裡會提示輸入審計理由
        System.out.println("場景 C 執行完畢。目前會員租借歷史訂單數量：" + controller.currentMember.getRentalHistory().size());
        if (!controller.currentMember.getRentalHistory().isEmpty()) {
            Order lastOrder = controller.currentMember.getRentalHistory().get(controller.currentMember.getRentalHistory().size() - 1);
            System.out.println("最後一筆訂單狀態: " + lastOrder.getStatus());
            if ("Rejected".equals(lastOrder.getStatus())) {
                System.out.println("E003 羽毛球拍目前可用庫存: " + controller.equipmentInventory.get("E003").getAvailableStock());
            } else {
                System.out.println("審計通過，羽毛球拍庫存已扣除。");
            }
        }

        System.out.println("\n按下 Enter 鍵繼續到下一個場景...");
        scenarioScanner.nextLine();

        // 場景 D (庫存不足，失敗)：嘗試租借超過 availableStock 的數量，processCheckout() 終止並顯示錯誤訊息。
        System.out.println("\n--- 場景 D: 庫存不足，失敗 ---");
        System.out.println("1. 將 '桌球' (ID: E004) 租借 15 個 (可用庫存: 10)");
        controller.addItemRequest("E004", 15); // 嘗試租借超過可用庫存
        // 由於 addItemRequest 會檢查庫存，這裡的 processCheckout 不會執行到有錯誤訊息
        // 為了更貼近場景 D 的描述，我們手動清空購物車，然後直接嘗試加入超量品項
        System.out.println("重置購物車並嘗試再次添加超量器材以觸發錯誤訊息...");
        controller.currentList.clearList();
        controller.addItemRequest("E004", 15); // 這次應該會直接顯示庫存不足的錯誤訊息

        System.out.println("\n場景 D 執行完畢。");

        scenarioScanner.close();
        System.out.println("\n----- 所有測試場景執行完畢！ -----");
    }
}