package com.sportrental.sports_rental_web;

import java.util.ArrayList;
import java.util.List;

public class Member {
    private String memberID;
    private String accountName;
    private List<Order> rentalHistory = new ArrayList<>(); // 儲存歷史訂單

    public Member(String memberID, String accountName) {
        this.memberID = memberID;
        this.accountName = accountName;
    }

    public String getMemberID() { return memberID; }
    public String getAccountName() { return accountName; }
    
    // 新增歷史紀錄 (UML Source: 267)
    public void addRentalHistory(Order order) {
        rentalHistory.add(order);
    }
    
    public List<Order> getRentalHistory() {
        return rentalHistory;
    }
}
