package com.sportrental;

import java.util.Map;
import java.util.Scanner;

public class RentalPage {
    private String currentStatusMessage;
    private Scanner scanner;

    /**
     * 建構子：初始化 RentalPage 物件。
     * 設定初始狀態訊息，並建立一個 Scanner 實例用於處理使用者輸入。
     */
    public RentalPage() {
        this.currentStatusMessage = "歡迎來到器材租借系統！";
        this.scanner = new Scanner(System.in);
    }

    /**
     * 顯示器材目錄。
     * 遍歷器材庫存 Map，並將每個器材的 ID、名稱、總庫存和可用庫存輸出到控制台。
     * @param equipmentCatalog 包含所有器材的 Map (Key: equipmentID, Value: Equipment 物件)
     */
    public void displayEquipmentCatalog(Map<String, Equipment> equipmentCatalog) {
        System.out.println("----- 器材目錄 -----");
        if (equipmentCatalog.isEmpty()) {
            System.out.println("目前沒有可用的器材。");
            return;
        }
        equipmentCatalog.forEach((id, equipment) -> {
            System.out.println("ID: " + equipment.getEquipmentID() +
                               ", 名稱: " + equipment.getName() +
                               ", 總庫存: " + equipment.getTotalStock() +
                               ", 可用庫存: " + equipment.getAvailableStock() +
                               ", 審計門檻: " + equipment.getAuditThreshold());
        });
        System.out.println("--------------------");
    }

    /**
     * 顯示目前的租借清單 (購物車內容)。
     * 遍歷 RentalList 中的所有 RentalItem，並將每個品項的器材 ID、名稱和數量輸出到控制台。
     * @param rentalList 當前會員的租借清單
     */
    public void displayCurrentList(RentalList rentalList) {
        System.out.println("----- 我的租借清單 -----");
        if (rentalList.getItems().isEmpty()) {
            System.out.println("您的租借清單是空的。");
            return;
        }
        rentalList.getItems().forEach(item -> {
            Equipment equipment = item.getEquipment();
            System.out.println("ID: " + equipment.getEquipmentID() +
                               ", 名稱: " + equipment.getName() +
                               ", 數量: " + item.getQuantity());
        });
        System.out.println("------------------------");
    }

    /**
     * 顯示系統狀態訊息。
     * 將傳入的訊息輸出到控制台，並更新內部 currentStatusMessage 屬性。
     * @param message 要顯示的狀態訊息
     */
    public void displayStatusMessage(String message) {
        this.currentStatusMessage = message;
        System.out.println(">> 狀態訊息: " + message);
    }

    /**
     * 提示使用者輸入審計理由。
     * 輸出提示訊息，並從控制台讀取一行文字作為審計理由。
     * @return 使用者輸入的審計理由
     */
    public String promptForAuditReason() {
        System.out.print("您租借的數量較多，需要提供審計理由：");
        return scanner.nextLine();
    }
}