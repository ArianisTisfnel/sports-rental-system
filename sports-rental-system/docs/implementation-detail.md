## 1. 系統啟動與 I/O 模擬 (T-PR-04)

### 1.1 啟動流程
Agent 應在主要執行類別中依序呼叫 `RentalController.initSystem()` 進行初始化。

### 1.2 I/O 模擬機制
由於不實作 UI，所有使用者互動（RentalPage 的方法）必須透過 **標準控制台 (Console) 進行模擬**：
* **輸出 (display...):** 使用 `System.out.println()` 輸出資訊，例如器材目錄、購物車內容和狀態訊息。
* **輸入 (promptForAuditReason):** 必須使用 `java.util.Scanner` 或類似機制從控制台讀取使用者輸入。

---

## 2. 租借清單與審計流程的業務邏輯

### 2.1 審計條件判斷 (T-PR-03)

`RentalList.checkAuditRequirement()` 方法的邏輯定義如下：
* 方法必須遍歷 `itemsSet` 中的每一個 `RentalItem`。
* 審計觸發標準為：若 **任何一個** `RentalItem` 的 `quantity` **大於** 其對應 `Equipment` 物件的 `auditThreshold`，則此方法應立即回傳 `True`。
* 只有所有品項的數量都符合門檻，才回傳 `False`。

### 2.2 結帳與審計流程 (T-PR-01, T-PR-02)

`RentalController.processCheckout()` 負責啟動結帳流程。

| 流程階段 | 說明 |
| :--- | :--- |
| **庫存前置檢查** (T-PR-01) | 在建立訂單前，必須遍歷 `currentList` 中的所有 `RentalItem`，並呼叫對應 `Equipment` 的 `checkAvailability()` 方法。 |
| **檢查失敗回饋** | 如果任何一項檢查失敗（`checkAvailability` 回傳 `False`），`processCheckout()` 應 **立即中止**。系統須透過 `RentalPage.displayStatusMessage()` 顯示錯誤訊息（例如：「器材 [ID] 庫存不足」）。 |
| **檢查成功** | 如果所有品項檢查通過，則進入審計判斷。 |
| **審計判斷** | 呼叫 `currentList.checkAuditRequirement()`。 |
| **若需審計 (True)** | 系統必須透過 `RentalPage.promptForAuditReason()` 請求使用者輸入審計理由，並將結果傳給 `confirmOrder(auditReason: String)`。 |
| **若不需審計 (False)** | 直接呼叫 `confirmOrder()`。 |

---

## 3. 訂單完成邏輯與庫存操作 (T-PR-02)

### 3.1 `confirmOrder(auditReason)` 執行細節 (需審計)
* **模擬管理員審核：** 使用亂數（例如 `java.util.Random`）生成審核結果，比例為 50% 允許 (`True`) / 50% 拒絕 (`False`)。
* **若審核通過 (`True`):**
    * 建立一個新的 `Order` 物件，狀態設為 `Approved`。
    * 將 `Order` 的 `auditReason` 設為傳入的值。
    * **庫存操作：** **必須** 遍歷 `Order` 中的所有 `RentalItem`，並呼叫對應 `Equipment` 的 `decreaseStock(quantity)` 方法來扣除庫存。
    * 將完成的 `Order` 加入 `currentMember` 的 `rentalHistory`。
*