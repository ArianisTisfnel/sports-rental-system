package com.sportrental;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpSession;

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
        // 2. 關鍵：把自己注入進去，這樣 RentalController 才能呼叫 displayStatusMessage
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

    // 簡單的CSRF token生成（用於防止跨站請求偽造）
    private String generateToken(HttpSession session) {
        String token = java.util.UUID.randomUUID().toString();
        session.setAttribute("csrfToken", token);
        return token;
    }

    private boolean validateToken(HttpSession session, String token) {
        String sessionToken = (String) session.getAttribute("csrfToken");
        return sessionToken != null && sessionToken.equals(token);
    }

    private void setupModel(Model model, HttpSession session) {
        // 注意：這裡呼叫了 public 的 equipmentInventory
        model.addAttribute("equipments", rentalController.equipmentInventory.values());
        model.addAttribute("cartItems", rentalController.currentList.getItems());
        
        // FlashAttribute 會自動添加到 Model 中，我們只需要設置默認值
        // Spring會在redirect後自動將FlashAttribute添加到Model中
        
        // 如果沒有從FlashAttribute獲得message，使用本地狀態
        Object existingMessage = model.getAttribute("message");
        if (existingMessage == null && !tempMessage.isEmpty()) {
            model.addAttribute("message", tempMessage);
        }
        
        // 如果沒有從FlashAttribute獲得showAuditModal，使用本地狀態
        Object existingAuditModal = model.getAttribute("showAuditModal");
        if (existingAuditModal == null) {
            model.addAttribute("showAuditModal", showAuditModal);
        }
        
        // 如果沒有從FlashAttribute獲得showDecisionModal，使用本地狀態
        Object existingDecisionModal = model.getAttribute("showDecisionModal");
        if (existingDecisionModal == null) {
            model.addAttribute("showDecisionModal", showDecisionModal);
        }
        
        model.addAttribute("csrfToken", generateToken(session));
        
        // 處理audit modal的訊息
        Object auditModalValue = model.getAttribute("showAuditModal");
        if ((Boolean.TRUE.equals(auditModalValue) || showAuditModal) && 
            model.getAttribute("auditMsg") == null) {
            model.addAttribute("auditMsg", "⚠️ 租借數量超過門檻，請填寫審核理由！");
        }
    }

    @GetMapping("/")
    public String home(Model model, HttpSession session) {
        resetState();
        setupModel(model, session);
        return "index";
    }

    @GetMapping("/cart")
    public String viewCart(Model model, HttpSession session) {
        resetState();
        setupModel(model, session);
        return "cart";
    }

    @PostMapping("/add")
    public String addItemRequest(@RequestParam String id, @RequestParam int quantity, 
                               RedirectAttributes redirectAttributes) {
        resetState();
        rentalController.addItemRequest(id, quantity);

        // 調試信息
        System.out.println("DEBUG - tempMessage: " + tempMessage);
        System.out.println("DEBUG - contains 成功: " + tempMessage.contains("成功"));

        // 將狀態傳遞給redirect的頁面
        redirectAttributes.addFlashAttribute("message", tempMessage);
        
        // 判斷是否成功，決定是否顯示「前往租借」詢問視窗
        if (tempMessage.contains("成功")) {
            redirectAttributes.addFlashAttribute("showDecisionModal", true);
            System.out.println("DEBUG - showDecisionModal set to true");
        }

        // 使用redirect避免重複提交
        return "redirect:/";
    }

    @PostMapping("/update")
    public String updateItemQuantity(@RequestParam String id, @RequestParam int quantity, 
                                   RedirectAttributes redirectAttributes) {
        resetState();
        rentalController.updateItemQuantity(id, quantity);
        
        // 將狀態傳遞給redirect的頁面
        redirectAttributes.addFlashAttribute("message", tempMessage);
        
        // 使用redirect避免重複提交
        return "redirect:/cart";
    }

    @PostMapping("/remove")
    public String removeItemFromCart(@RequestParam String id, RedirectAttributes redirectAttributes) {
        resetState();
        rentalController.removeItemFromCart(id);
        
        // 將狀態傳遞給redirect的頁面
        redirectAttributes.addFlashAttribute("message", tempMessage);
        
        // 使用redirect避免重複提交
        return "redirect:/cart";
    }

    @PostMapping("/checkout")
    public String processCheckout(@RequestParam(required = false) String reason, 
                                RedirectAttributes redirectAttributes) {
        resetState();

        if (reason != null && !reason.isEmpty()) {
            // 如果是從 Modal 傳回來的理由，直接確認訂單
            rentalController.confirmOrder(reason);
        } else {
            // 一般結帳流程
            rentalController.processCheckout();
        }

        // 將狀態傳遞給redirect的頁面
        redirectAttributes.addFlashAttribute("message", tempMessage);
        if (showAuditModal) {
            redirectAttributes.addFlashAttribute("showAuditModal", true);
            redirectAttributes.addFlashAttribute("auditMsg", "⚠️ 租借數量超過門檻，請填寫審核理由！");
        }

        // 使用redirect避免重複提交
        return "redirect:/cart";
    }
}