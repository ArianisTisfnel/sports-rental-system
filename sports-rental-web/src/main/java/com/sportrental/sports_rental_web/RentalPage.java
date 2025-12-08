package com.sportrental.sports_rental_web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class RentalPage {
    private RentalController rentalSystem = new RentalController();

    // 首頁：只顯示器材目錄
    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("equipments", rentalSystem.getAllEquipments());
        // 這裡不需要傳 cartItems 了，因為首頁看不到
        return "index";
    }

    // ⭐️ 新增：購物車/結帳頁面
    @GetMapping("/cart")
    public String viewCart(Model model) {
        model.addAttribute("cartItems", rentalSystem.getCurrentList());
        return "cart"; // 對應 cart.html
    }

    // 加入購物車 (在首頁執行)
    @PostMapping("/add")
    public String addToCart(@RequestParam String id, @RequestParam int quantity, Model model) {
        String msg = rentalSystem.addToCart(id, quantity);
        model.addAttribute("message", msg);

        // 如果成功，設定 showDecisionModal 讓前端跳出視窗
        if (msg.startsWith("成功")) {
            model.addAttribute("showDecisionModal", true);
        }

        // 加入後停留在首頁
        return home(model);
    }

    // 更新數量 (在購物車頁面執行)
    @PostMapping("/update")
    public String updateCart(@RequestParam String id, @RequestParam int quantity, Model model) {
        String msg = rentalSystem.updateCartItem(id, quantity);
        // 重導回購物車頁面，避免重新整理表單重複送出
        return "redirect:/cart";
    }

    // 刪除項目 (在購物車頁面執行)
    @PostMapping("/remove")
    public String removeFromCart(@RequestParam String id, Model model) {
        rentalSystem.removeCartItem(id);
        return "redirect:/cart";
    }

    // 結帳 (在購物車頁面執行)
    @PostMapping("/checkout")
    public String checkout(@RequestParam(required = false) String reason, Model model) {
        String status = rentalSystem.checkAuditRequirement();

        // 必須重新把 cartItems 塞回去，因為我们要返回 cart 頁面
        model.addAttribute("cartItems", rentalSystem.getCurrentList());

        if ("EMPTY".equals(status)) {
            model.addAttribute("message", "錯誤：購物車是空的！");
            return "cart";
        }

        if ("NEED_AUDIT".equals(status) && (reason == null || reason.trim().isEmpty())) {
            model.addAttribute("showAuditModal", true);
            model.addAttribute("auditMsg", "⚠️ 租借數量超過門檻，請填寫審核理由！");
            return "cart";
        }

        rentalSystem.confirmOrder(reason);
        model.addAttribute("message", "✅ 租借成功！" + (reason != null ? "(已送出審核)" : ""));

        // 結帳成功後，購物車已被清空，顯示成功訊息
        return "cart";
    }
}