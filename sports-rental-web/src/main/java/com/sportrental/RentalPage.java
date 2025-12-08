package com.sportrental;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.annotation.PostConstruct;

@Controller
public class RentalPage {

    private RentalController rentalController;

    // 暫存狀態，用來傳遞給網頁
    private String tempMessage = "";
    private boolean showAuditModal = false;
    private boolean showDecisionModal = false;

    @PostConstruct
    public void init() {
        // 1. 建立邏輯控制器
        this.rentalController = new RentalController();
        // 2. ⭐️ 關鍵：把自己注入進去，這樣 RentalController 才能呼叫 displayStatusMessage
        this.rentalController.rentalPage = this;
    }

    // --- [介面實作] 讓 RentalController 呼叫的方法 ---

    public void displayStatusMessage(String message) {
        if (this.tempMessage.isEmpty()) {
            this.tempMessage = message;
        } else {
            this.tempMessage += " | " + message;
        }
    }

    public void displayCurrentList(RentalList list) {
        // 網頁版會自動拉資料，這裡不需要做動作
    }

    public String promptForAuditReason() {
        // 網頁版：設定旗標，讓前端跳出視窗
        this.showAuditModal = true;
        return null; // 回傳 null 中斷 RentalController 的迴圈，等待使用者輸入
    }

    // --- [網頁路由] Spring Boot 部分 ---

    private void resetState() {
        this.tempMessage = "";
        this.showAuditModal = false;
        this.showDecisionModal = false;
    }

    private void setupModel(Model model) {
        // 注意：這裡呼叫了 public 的 equipmentInventory
        model.addAttribute("equipments", rentalController.equipmentInventory.values());
        model.addAttribute("cartItems", rentalController.currentList.getItems());
        model.addAttribute("message", tempMessage);
        model.addAttribute("showAuditModal", showAuditModal);
        model.addAttribute("showDecisionModal", showDecisionModal);
        if (showAuditModal) {
            model.addAttribute("auditMsg", "⚠️ 租借數量超過門檻，請填寫審核理由！");
        }
    }

    @GetMapping("/")
    public String home(Model model) {
        resetState();
        setupModel(model);
        return "index";
    }

    @GetMapping("/cart")
    public String viewCart(Model model) {
        resetState();
        setupModel(model);
        return "cart";
    }

    @PostMapping("/add")
    public String addItemRequest(@RequestParam String id, @RequestParam int quantity, Model model) {
        resetState();
        rentalController.addItemRequest(id, quantity);

        // 判斷是否成功，決定是否顯示「前往租借」詢問視窗
        if (tempMessage.contains("成功")) {
            this.showDecisionModal = true;
        }

        setupModel(model);
        return "index";
    }

    @PostMapping("/update")
    public String updateItemQuantity(@RequestParam String id, @RequestParam int quantity, Model model) {
        resetState();
        rentalController.updateItemQuantity(id, quantity);
        setupModel(model);
        return "cart";
    }

    @PostMapping("/remove")
    public String removeItemFromCart(@RequestParam String id, Model model) {
        resetState();
        rentalController.removeItemFromCart(id);
        setupModel(model);
        return "cart";
    }

    @PostMapping("/checkout")
    public String processCheckout(@RequestParam(required = false) String reason, Model model) {
        resetState();

        if (reason != null && !reason.isEmpty()) {
            // 如果是從 Modal 傳回來的理由，直接確認訂單
            rentalController.confirmOrder(reason);
        } else {
            // 一般結帳流程
            rentalController.processCheckout();
        }

        setupModel(model);
        return "cart";
    }
}