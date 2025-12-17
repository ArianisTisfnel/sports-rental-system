package com.sportrental;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext; // 導入 ConfigurableApplicationContext
import java.awt.Desktop; // 導入 Desktop
import java.net.URI;     // 導入 URI
import java.io.IOException;
import java.util.Locale;

@SpringBootApplication
public class SportRentalApplication {
    public static void main(String[] args) {
        // 運行 Spring Boot 應用程式並獲取其上下文
        ConfigurableApplicationContext context = SpringApplication.run(SportRentalApplication.class, args);

        // 在應用程式啟動後，嘗試自動開啟瀏覽器；若不支援則提供回退或提示
        String url = "http://localhost:8080";
        try {
            if (openBrowser(url)) {
                System.out.println("瀏覽器已自動啟動並導向 " + url);
            } else {
                System.out.println("不支援自動開啟瀏覽器，請手動訪問 " + url);
            }
        } catch (Exception e) {
            System.err.println("自動開啟瀏覽器時發生錯誤：" + e.getMessage());
        }
    }

    // 嘗試以多種方式開啟瀏覽器，回傳是否成功
    private static boolean openBrowser(String url) {
        // 1) Java Desktop API
        try {
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(new URI(url));
                return true;
            }
        } catch (Exception ignored) {
        }

        // 2) 作業系統特定的命令回退
        String os = System.getProperty("os.name").toLowerCase(Locale.ENGLISH);
        try {
            if (os.contains("win")) {
                // Windows
                Runtime.getRuntime().exec(new String[]{"rundll32", "url.dll,FileProtocolHandler", url});
                return true;
            } else if (os.contains("mac")) {
                // macOS
                Runtime.getRuntime().exec(new String[]{"open", url});
                return true;
            } else if (os.contains("nix") || os.contains("nux") || os.contains("aix")) {
                // Linux/Unix: 使用 xdg-open（大多數現代發行版可用）
                Runtime.getRuntime().exec(new String[]{"xdg-open", url});
                return true;
            }
        } catch (IOException ignored) {
        }

        // 無法自動開啟
        return false;
    }
}