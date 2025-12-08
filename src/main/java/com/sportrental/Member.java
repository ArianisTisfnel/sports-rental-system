package com.sportrental;

import java.util.ArrayList;
import java.util.List;

public class Member {
    private String memberID;
    private ArrayList<Order> rentalHistory;
    private String accountName;

    public Member(String memberID, String accountName) {
        this.memberID = memberID;
        this.accountName = accountName;
        this.rentalHistory = new ArrayList<>();
    }

    public String getMemberID() {
        return memberID;
    }

    public String getAccountName() {
        return accountName;
    }

    public void addRentalHistory(Order completedOrder) {
        this.rentalHistory.add(completedOrder);
    }

    public List<Order> getRentalHistory() {
        return rentalHistory;
    }
}