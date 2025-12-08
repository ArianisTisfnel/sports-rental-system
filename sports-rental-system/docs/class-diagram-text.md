# 租借系統類別圖文件

**文件說明**
預計使用Java程式語言。
沒有要實作資料庫層、資料庫。
沒有要實作管理員端。
沒有要實作歸還流程。僅專注在會員租借的一個use-case上。

---

## 類別：Member

屬性：
- memberID:String
- rentalHistory:ArrayList<Order>
- accountName: String


方法：
+ addRentalHistory(completedOrders:Arraylist<Order>) : void
+ getRentalHistory():Arraylist<Order>

圖表關係：
Member(1..*)<-"manage"-(1)RentalController
Order(0..*)-"aPartOf"->(1)Member

---

## 類別：Order

屬性：
-orderID: String
-member: Member
-orderTime: LocalDateTime
-status: String (PendingAudit, Approved, Rejected, Completed)
-itemsSet: HashSet<RentalItem> (複製自 RentalList)
-auditReason: String

方法：
+ getOrderID(): String 
+ getStatus(): String 
+ getAuditReason(): String
+ getTotalQuantity(): Integer 
+ getLineItems(): List<RentalItem>

圖表關係：
Order(0..*)-"aPartOf"->(1)Member
RentalController(1)-"confirm"->(1..*)Order
RentalItem(1..*)-"isPartOf"->(1)Order

---

## 類別：RentalPage

屬性：
- currentStatusMessage: String
- auditReasonInput: String

方法：
+ displayEquipmentCatalog(equipmentCatalog: Map<String, Equipment>): void
+ displayCurrentList(rentalList: RentalList): void
+ displayStatusMessage(message: String): void
+ promptForAuditReason(): String

圖表關係：
RentalPage(1)-"isAssociatedWith"->(1)RentalController
RentalController(1)-"isAssociatedWith"->(1)RentalPage

---

## 類別：Equipment

屬性：
- equipmentID: String
- name: String
- totalStock: Integer
- availableStock: Integer
- auditThreshold: Integer

方法：
+ checkAvailability(requestedQuantity: Integer) : Boolean
+ decreaseStock(quantity: Integer) : void
+ getEquipmentID : String
+ getName: String
+ getTotalStock: Integer
+ getAvailableStock: Integer
+ getAuditThreshold: Integer

圖表關係：
Equipment(1..*)<-"manage"-(1)RentalController
Equipment(1)<-"Refers to"-(1)RentalItem

---

## 類別：RentalList

屬性：
- listID: String
- creationDate: LocalDateTime
- itemsSet: HashSet<RentalItem>

方法：
+ addItem(item: RentalItem) : void
+ removeItem(equipmentID: String) : void
+ updateItem(equipmentID: String, quantity: Integer) : void
+ getItems() : List<RentalItem>
+ checkAuditRequirement() : Boolean

圖表關係：
RentalList(1..*)<-"request"-(1)RentalController
RentalList(1..*)<-"isPartOf"-(1)RentalItem

---

## 類別：RentalItem

屬性：
// 屬性在建構後應視為唯讀 (final)。quantity 的修改應透過 RentalList 的 updateItem() 進行。
- equipment: Equipment
- quantity: Integer
方法：
+ equals(Object obj): Boolean
+ hashCode(): int
// `equals()` 和 `hashCode()` 必須僅使用 `equipmentID` 屬性。這是為了確保在集合 (HashSet) 中，同一個器材 ID 僅能存在一個實例。

圖表關係：
RentalItem(1..*)-"isPartOf"->(1)RentalList
RentalItem(1)-"Refers to"->(1)Equipment
RentalItem(1..*)-"isPartOf"->(1)Order
---

## 類別：RentalController

屬性：
+ initSystem(): void
// 負責系統啟動：
// 1. 載入靜態器材庫存資料到 equipmentInventory。
// 2. 建立一個預設 Member 實例 (currentMember)。
// 3. 建立 RentalPage 實例。
- equipmentInventory: Map<String, Equipment>
// 儲存所有器材物件的目錄。以靜態資料儲存，代替資料庫（我們沒有要實作資料庫層）。
// Map 的 Key 應為 Equipment.equipmentID (String)。
- currentMember : Member
// 一個會員對一個Page，一次只服務一個會員。
- currentList: RentalList
// 因為會員會反覆CRUD購物車的內容，因此這裡需要暫存購物車。


方法：
+ addItemRequest(equipmentID: String, quantity: Integer): void
 // 檢查器材於 equipmentInventory 中的狀態，在確認可租借後呼叫 RentalList 的 addItem() 往購物車內新增租借品項。

+ removeItemFromCart(equipmentID: String): void

+ updateItemQuantity(equipmentID: String, newQuantity: Integer): void

+ processCheckout(): void
// 負責檢查 itemSet 的內容是否符合庫存，並在訂單完成後決定是否送出審計理由（一個if-else）。

+ confirmOrder(): void

+ confirmOrder(auditReason: String): void
// 如果有審計理由，亂數生成管理員的允許情形（50%/50%-True/False），並依據結果決定是否建構一個包含 itemSet 跟 auditReason 的 Order（訂單成立），並通知Equipment進行庫存。如果沒有審計理由，直接建構一個沒有auditReason的Order。

圖表關係：
RentalController(1)-"manage"->(1..*)Member
RentalController(1)-"confirm"->(1..*)Order
RentalController(1)-"isAssociatedWith"->(1)RentalPage
RentalController(1)<-"isAssociatedWith"-(1)RentalPage
RentalController(1)<-"manage"-(1..*)Equipment
RentalController(1)-"request"->(1..*)RentalList

---



# 租借系統實作補充文件

**文件說明**
本文件旨在提供類別圖文件（主文件）中未詳述的業務規則、流程邏輯與 Agent 實作指引，以確保 Agent 能順利完成租借系統的核心 use-case 實作。

---
