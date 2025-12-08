package com.sportrental;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class RentalPage {

    // 這裡直接 NEW 出你的舊系統邏輯！保留你的架構
    private RentalController rentalSystem = new RentalController();

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("equipments", rentalSystem.getAllEquipments());
        model.addAttribute("cartItems", rentalSystem.getCurrentList());
        return "index";
    }

    @PostMapping("/add")
    public String addToCart(@RequestParam String id, @RequestParam int quantity, Model model) {
        String msg = rentalSystem.addToCart(id, quantity);
        model.addAttribute("message", msg);
        return home(model);
    }

    @PostMapping("/checkout")
    public String checkout(@RequestParam(required = false) String reason, Model model) {
        
        String status = rentalSystem.checkAuditRequirement();

        if ("EMPTY".equals(status)) {
            model.addAttribute("message", "購物車是空的！");
            return home(model);
        }

        if ("NEED_AUDIT".equals(status) && (reason == null || reason.trim().isEmpty())) {
            model.addAttribute("showAuditModal", true);
            model.addAttribute("auditMsg", "⚠️ 租借數量超過門檻，請填寫審核理由！");
            return home(model);
        }

        rentalSystem.confirmOrder(reason);
        model.addAttribute("message", "✅ 租借成功！" + (reason != null ? "(已送出審核)" : ""));
        
        return home(model);
    }
}