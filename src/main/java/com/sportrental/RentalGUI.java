package com.sportrental;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Swing 版租借系統 GUI，包裝現有的 RentalController。
 * 注意：目前屬於示範用途，主流程仍以 console 互動為主。
 */
public class RentalGUI extends JFrame {
    private RentalController controller;
    private JPanel mainPanel;
    private CardLayout cardLayout;

    private static final String CATEGORY_PANEL = "CATEGORY";
    private static final String EQUIPMENT_LIST_PANEL = "EQUIPMENT_LIST";
    private static final String EQUIPMENT_DETAIL_PANEL = "EQUIPMENT_DETAIL";
    private static final String RENTAL_CART_PANEL = "RENTAL_CART";

    private String currentCategory;
    private Equipment currentEquipment;
    private Map<String, java.util.List<Equipment>> categoryMap;

    public RentalGUI() {
        controller = new RentalController();
        initializeCategoryMap();
        setupGUI();
    }

    private void initializeCategoryMap() {
        categoryMap = new HashMap<>();
        categoryMap.put("球類運動", Arrays.asList(
            controller.equipmentInventory.get("E001"),
            controller.equipmentInventory.get("E002")
        ));
        categoryMap.put("拍類運動", Arrays.asList(
            controller.equipmentInventory.get("E003"),
            controller.equipmentInventory.get("E005")
        ));
        categoryMap.put("桌上運動", Arrays.asList(
            controller.equipmentInventory.get("E004")
        ));
    }

    private void setupGUI() {
        setTitle("租借系統 - " + controller.currentMember.getAccountName());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        createCategoryPanel();
        createEquipmentListPanel();
        createEquipmentDetailPanel();
        createRentalCartPanel();

        add(mainPanel);
        cardLayout.show(mainPanel, CATEGORY_PANEL);
    }

    private void createCategoryPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JLabel titleLabel = new JLabel("選擇運動類別", SwingConstants.CENTER);
        titleLabel.setFont(new Font("微軟正黑體", Font.BOLD, 24));
        panel.add(titleLabel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(50, 100, 50, 100));

        for (String category : categoryMap.keySet()) {
            JButton categoryButton = new JButton(category);
            categoryButton.setFont(new Font("微軟正黑體", Font.PLAIN, 18));
            categoryButton.addActionListener(e -> showEquipmentList(category));
            buttonPanel.add(categoryButton);
        }

        panel.add(buttonPanel, BorderLayout.CENTER);
        panel.setName(CATEGORY_PANEL);
        mainPanel.add(panel, CATEGORY_PANEL);
    }

    private void createEquipmentListPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JPanel topPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("", SwingConstants.CENTER);
        titleLabel.setFont(new Font("微軟正黑體", Font.BOLD, 20));

        JButton backButton = new JButton("← 返回類別選擇");
        backButton.addActionListener(e -> cardLayout.show(mainPanel, CATEGORY_PANEL));

        topPanel.add(backButton, BorderLayout.WEST);
        topPanel.add(titleLabel, BorderLayout.CENTER);
        panel.add(topPanel, BorderLayout.NORTH);

        JPanel equipmentPanel = new JPanel();
        equipmentPanel.setLayout(new BoxLayout(equipmentPanel, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(equipmentPanel);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel();
        JButton cartButton = new JButton("前往租借清單 (" + controller.currentList.getItems().size() + " 項)");
        cartButton.addActionListener(e -> showRentalCart());
        bottomPanel.add(cartButton);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        panel.putClientProperty("titleLabel", titleLabel);
        panel.putClientProperty("equipmentPanel", equipmentPanel);
        panel.putClientProperty("cartButton", cartButton);
        panel.setName(EQUIPMENT_LIST_PANEL);

        mainPanel.add(panel, EQUIPMENT_LIST_PANEL);
    }

    private void createEquipmentDetailPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JPanel topPanel = new JPanel(new BorderLayout());
        JButton backButton = new JButton("← 返回器材列表");
        backButton.addActionListener(e -> showEquipmentList(currentCategory));
        topPanel.add(backButton, BorderLayout.WEST);
        panel.add(topPanel, BorderLayout.NORTH);

        JPanel infoPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        JLabel nameLabel = new JLabel();
        JLabel idLabel = new JLabel();
        JLabel stockLabel = new JLabel();

        gbc.gridx = 0; gbc.gridy = 0;
        infoPanel.add(new JLabel("器材名稱:"), gbc);
        gbc.gridx = 1;
        infoPanel.add(nameLabel, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        infoPanel.add(new JLabel("器材編號:"), gbc);
        gbc.gridx = 1;
        infoPanel.add(idLabel, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        infoPanel.add(new JLabel("可用庫存:"), gbc);
        gbc.gridx = 1;
        infoPanel.add(stockLabel, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        infoPanel.add(new JLabel("租借數量:"), gbc);
        gbc.gridx = 1;
        JSpinner quantitySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));
        infoPanel.add(quantitySpinner, gbc);

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        JButton addToCartButton = new JButton("加入租借清單");
        addToCartButton.addActionListener(e -> addToCart(quantitySpinner));
        infoPanel.add(addToCartButton, gbc);

        panel.add(infoPanel, BorderLayout.CENTER);

        panel.putClientProperty("nameLabel", nameLabel);
        panel.putClientProperty("idLabel", idLabel);
        panel.putClientProperty("stockLabel", stockLabel);
        panel.putClientProperty("quantitySpinner", quantitySpinner);
        panel.setName(EQUIPMENT_DETAIL_PANEL);

        mainPanel.add(panel, EQUIPMENT_DETAIL_PANEL);
    }

    private void createRentalCartPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JPanel topPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("租借清單", SwingConstants.CENTER);
        titleLabel.setFont(new Font("微軟正黑體", Font.BOLD, 20));

        JButton backButton = new JButton("← 繼續瀏覽");
        backButton.addActionListener(e -> cardLayout.show(mainPanel, CATEGORY_PANEL));

        topPanel.add(backButton, BorderLayout.WEST);
        topPanel.add(titleLabel, BorderLayout.CENTER);
        panel.add(topPanel, BorderLayout.NORTH);

        JPanel cartPanel = new JPanel();
        cartPanel.setLayout(new BoxLayout(cartPanel, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(cartPanel);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel();
        JButton confirmButton = new JButton("確認租借");
        confirmButton.addActionListener(e -> confirmRental());
        bottomPanel.add(confirmButton);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        panel.putClientProperty("cartPanel", cartPanel);
        panel.putClientProperty("confirmButton", confirmButton);
        panel.setName(RENTAL_CART_PANEL);

        mainPanel.add(panel, RENTAL_CART_PANEL);
    }

    private void showEquipmentList(String category) {
        currentCategory = category;
        JPanel panel = (JPanel) mainPanel.getComponent(getComponentIndex(EQUIPMENT_LIST_PANEL));

        JLabel titleLabel = (JLabel) panel.getClientProperty("titleLabel");
        titleLabel.setText(category + " - 器材列表");

        JPanel equipmentPanel = (JPanel) panel.getClientProperty("equipmentPanel");
        equipmentPanel.removeAll();

        List<Equipment> equipments = categoryMap.get(category);
        for (Equipment equipment : equipments) {
            JPanel itemPanel = new JPanel(new BorderLayout());
            itemPanel.setBorder(BorderFactory.createEtchedBorder());

            JLabel infoLabel = new JLabel(String.format("%s (ID: %s) - 庫存: %d",
                equipment.getName(), equipment.getEquipmentID(), equipment.getAvailableStock()));
            infoLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            JButton selectButton = new JButton("查看詳細");
            selectButton.addActionListener(e -> showEquipmentDetail(equipment));

            itemPanel.add(infoLabel, BorderLayout.CENTER);
            itemPanel.add(selectButton, BorderLayout.EAST);
            equipmentPanel.add(itemPanel);
        }

        updateCartButton();
        equipmentPanel.revalidate();
        equipmentPanel.repaint();

        cardLayout.show(mainPanel, EQUIPMENT_LIST_PANEL);
    }

    private void showEquipmentDetail(Equipment equipment) {
        currentEquipment = equipment;
        JPanel panel = (JPanel) mainPanel.getComponent(getComponentIndex(EQUIPMENT_DETAIL_PANEL));

        JLabel nameLabel = (JLabel) panel.getClientProperty("nameLabel");
        JLabel idLabel = (JLabel) panel.getClientProperty("idLabel");
        JLabel stockLabel = (JLabel) panel.getClientProperty("stockLabel");
        JSpinner quantitySpinner = (JSpinner) panel.getClientProperty("quantitySpinner");

        nameLabel.setText(equipment.getName());
        idLabel.setText(equipment.getEquipmentID());
        stockLabel.setText(String.valueOf(equipment.getAvailableStock()));

        SpinnerNumberModel model = new SpinnerNumberModel(1, 1,
            Math.max(1, equipment.getAvailableStock()), 1);
        quantitySpinner.setModel(model);

        cardLayout.show(mainPanel, EQUIPMENT_DETAIL_PANEL);
    }

    private void addToCart(JSpinner quantitySpinner) {
        int quantity = (Integer) quantitySpinner.getValue();

        if (!currentEquipment.checkAvailability(quantity)) {
            JOptionPane.showMessageDialog(this,
                "庫存不足！目前可用數量: " + currentEquipment.getAvailableStock(),
                "錯誤", JOptionPane.ERROR_MESSAGE);
            return;
        }

        controller.addItemRequest(currentEquipment.getEquipmentID(), quantity);

        JOptionPane.showMessageDialog(this,
            "已將 " + currentEquipment.getName() + " (數量: " + quantity + ") 加入租借清單",
            "成功", JOptionPane.INFORMATION_MESSAGE);

        String[] options = {"繼續瀏覽", "前往租借"};
        int choice = JOptionPane.showOptionDialog(this,
            "請選擇下一步動作", "選擇",
            JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE,
            null, options, options[0]);

        if (choice == 1) {
            showRentalCart();
        } else {
            cardLayout.show(mainPanel, CATEGORY_PANEL);
        }
    }

    private void showRentalCart() {
        JPanel panel = (JPanel) mainPanel.getComponent(getComponentIndex(RENTAL_CART_PANEL));
        JPanel cartPanel = (JPanel) panel.getClientProperty("cartPanel");
        JButton confirmButton = (JButton) panel.getClientProperty("confirmButton");

        cartPanel.removeAll();

        if (controller.currentList.getItems().isEmpty()) {
            JLabel emptyLabel = new JLabel("租借清單是空的", SwingConstants.CENTER);
            cartPanel.add(emptyLabel);
            confirmButton.setEnabled(false);
        } else {
            confirmButton.setEnabled(true);
            boolean hasStockIssue = false;

            for (RentalItem item : controller.currentList.getItems()) {
                JPanel itemPanel = createCartItemPanel(item);

                if (!item.getEquipment().checkAvailability(item.getQuantity())) {
                    itemPanel.setBackground(Color.PINK);
                    hasStockIssue = true;
                }

                cartPanel.add(itemPanel);
            }

            if (hasStockIssue) {
                confirmButton.setEnabled(false);
                JLabel warningLabel = new JLabel(
                    "<html><font color='red'>哎呀！部分器材庫存不足><，請修改數量或將其刪除~</font></html>",
                    SwingConstants.CENTER);
                cartPanel.add(warningLabel);
            }
        }

        cartPanel.revalidate();
        cartPanel.repaint();
        cardLayout.show(mainPanel, RENTAL_CART_PANEL);
    }

    private JPanel createCartItemPanel(RentalItem item) {
        JPanel itemPanel = new JPanel(new BorderLayout());
        itemPanel.setBorder(BorderFactory.createEtchedBorder());

        Equipment equipment = item.getEquipment();
        String infoText = String.format("%s (ID: %s) - 數量: %d - 可用庫存: %d",
            equipment.getName(), equipment.getEquipmentID(),
            item.getQuantity(), equipment.getAvailableStock());

        JLabel infoLabel = new JLabel(infoText);
        infoLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel buttonPanel = new JPanel();

        JButton editButton = new JButton("修改數量");
        editButton.addActionListener(e -> editCartItemQuantity(item));

        JButton deleteButton = new JButton("刪除");
        deleteButton.addActionListener(e -> deleteCartItem(item));

        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);

        itemPanel.add(infoLabel, BorderLayout.CENTER);
        itemPanel.add(buttonPanel, BorderLayout.EAST);

        return itemPanel;
    }

    private void editCartItemQuantity(RentalItem item) {
        String input = JOptionPane.showInputDialog(this,
            "輸入新的數量 (可用庫存: " + item.getEquipment().getAvailableStock() + "):",
            item.getQuantity());

        if (input != null) {
            try {
                int newQuantity = Integer.parseInt(input);
                if (newQuantity > 0 && newQuantity <= item.getEquipment().getAvailableStock()) {
                    controller.updateItemQuantity(item.getEquipment().getEquipmentID(), newQuantity);
                    showRentalCart();
                } else {
                    JOptionPane.showMessageDialog(this, "數量必須大於 0 且不超過可用庫存！",
                        "錯誤", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "請輸入有效的數字！",
                    "錯誤", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteCartItem(RentalItem item) {
        int choice = JOptionPane.showConfirmDialog(this,
            "確定要刪除 " + item.getEquipment().getName() + " 嗎？",
            "確認刪除", JOptionPane.YES_NO_OPTION);

        if (choice == JOptionPane.YES_OPTION) {
            controller.removeItemFromCart(item.getEquipment().getEquipmentID());
            showRentalCart();
        }
    }

    private void confirmRental() {
        boolean needsAudit = false;
        StringBuilder auditItems = new StringBuilder();

        for (RentalItem item : controller.currentList.getItems()) {
            if (item.getQuantity() > item.getEquipment().getAuditThreshold()) {
                needsAudit = true;
                auditItems.append("• ").append(item.getEquipment().getName())
                         .append(" (數量: ").append(item.getQuantity())
                         .append(", 門檻: ").append(item.getEquipment().getAuditThreshold())
                         .append(")\n");
            }
        }

        if (needsAudit) {
            String message = "租借數量已超過一般門檻，若要繼續租借，請填寫申請理由供管理員審核。\n\n" +
                           "超過門檻的器材：\n" + auditItems.toString();

            String reason = JOptionPane.showInputDialog(this, message, "需要審核");

            if (reason == null || reason.trim().isEmpty()) {
                return;
            }

            controller.confirmOrder(reason);
            JOptionPane.showMessageDialog(this,
                "您的申請已送出！管理員將根據您填寫的理由進行審核，請靜待通知>w<",
                "申請已送出", JOptionPane.INFORMATION_MESSAGE);
        } else {
            controller.confirmOrder();
            JOptionPane.showMessageDialog(this, "租借成功！", "成功",
                JOptionPane.INFORMATION_MESSAGE);
        }

        updateCartButton();
        cardLayout.show(mainPanel, CATEGORY_PANEL);
    }

    private void updateCartButton() {
        JPanel panel = (JPanel) mainPanel.getComponent(getComponentIndex(EQUIPMENT_LIST_PANEL));
        JButton cartButton = (JButton) panel.getClientProperty("cartButton");
        if (cartButton != null) {
            cartButton.setText("前往租借清單 (" + controller.currentList.getItems().size() + " 項)");
        }
    }

    private int getComponentIndex(String panelName) {
        for (int i = 0; i < mainPanel.getComponentCount(); i++) {
            Component comp = mainPanel.getComponent(i);
            if (panelName.equals(comp.getName())) {
                return i;
            }
        }

        switch (panelName) {
            case CATEGORY_PANEL: return 0;
            case EQUIPMENT_LIST_PANEL: return 1;
            case EQUIPMENT_DETAIL_PANEL: return 2;
            case RENTAL_CART_PANEL: return 3;
            default: return 0;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new RentalGUI().setVisible(true);
        });
    }
}

