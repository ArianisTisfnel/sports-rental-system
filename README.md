# 體育器材租借系統 (Sports Equipment Rental System)

一個基於Spring Boot開發的校園體育器材租借管理系統，提供直觀的Web界面來管理器材庫存和租借流程。

## 目錄

- [系統需求](#系統需求)
- [安裝步驟](#安裝步驟)
- [運行專案](#運行專案)
- [訪問應用程序](#訪問應用程序)
- [故障排除](#故障排除)

## 系統需求

### 必要環境
- **Java**: JDK 17 或更高版本
- **Git**: 用於下載專案代碼
- **瀏覽器**: Chrome, Firefox, Safari, Edge

### 環境變數設置
確認已安裝Java 17，並將JDK路徑加入到環境變數：

**Windows:**
```cmd
# 設置 JAVA_HOME
set JAVA_HOME=C:\Program Files\Java\jdk-17

# 驗證安裝
java -version
javac -version
```

**macOS/Linux:**
```bash
# 設置 JAVA_HOME (加入 ~/.bashrc 或 ~/.zshrc)
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk
export PATH=$JAVA_HOME/bin:$PATH

# 驗證安裝
java -version
javac -version
```

## 安裝步驟

### 步驟 1: 下載專案；解壓縮後選擇資料夾；打開資料夾

或使用 Git 下載專案代碼：

```bash
git clone https://github.com/ArianisTisfnel/sports-rental-system.git
```

### 步驟 2: 進入專案目錄

```bash
cd sports-rental-system
```

### 步驟 3: 切換到正確分支

```bash
git checkout web-new
```

### 步驟 4: 進入 Spring Boot 應用目錄

```bash
cd sports-rental-web
```

### 步驟 5: 驗證專案結構

確認目錄中包含以下重要文件：
- `pom.xml` - Maven 配置檔
- `mvnw.cmd` (Windows) 或 `mvnw` (macOS/Linux) - Maven Wrapper
- `src/` - 源代碼目錄

## 運行專案

### Windows 系統

在 `sports-rental-web` 目錄下執行：

```cmd
mvnw.cmd spring-boot:run
```

或使用 PowerShell：
```powershell
.\mvnw.cmd spring-boot:run
```

### macOS/Linux 系統

在 `sports-rental-web` 目錄下執行：

```bash
./mvnw spring-boot:run
```

### 等待啟動完成

當您看到類似以下訊息時，表示應用程序已成功啟動：
```
Started SportsRentalWebApplication in X.XXX seconds
```

## 訪問應用程序

應用程序啟動後，打開瀏覽器並訪問以下網址：

```
http://localhost:8080
```

您將看到體育器材租借系統的主頁面，包含：
- 器材目錄瀏覽
- 購物車功能
- 租借管理介面

## 故障排除

### 常見問題與解決方法

#### 1. Java 版本問題
**錯誤**: `UnsupportedClassVersionError`

**解決方法**:
```bash
# 檢查 Java 版本（需要 JDK 17+）
java -version

# 檢查環境變數
echo $JAVA_HOME    # macOS/Linux
echo %JAVA_HOME%   # Windows
```

#### 2. 端口被佔用
**錯誤**: `Port 8080 was already in use`

**解決方法**:
```bash
# Windows - 終止佔用端口的進程
netstat -ano | findstr :8080
taskkill /PID <PID> /F

# 使用其他端口啟動
mvnw.cmd spring-boot:run -Dserver.port=8081
```

#### 3. 編譯失敗

**解決方法**:
```bash
# 清理並重新編譯
mvnw.cmd clean compile
```

#### 4. 網頁顯示異常

**解決方法**:
- 重新整理頁面 (Ctrl+F5)
- 檢查瀏覽器控制台是否有錯誤訊息

---

**專案版本**: v0.0.1-SNAPSHOT  
**更新日期**: 2025年12月14日  
**Repository**: [GitHub - ArianisTisfnel/sports-rental-system](https://github.com/ArianisTisfnel/sports-rental-system)
