package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.awt.Desktop;
import java.net.URI;

@SpringBootApplication
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);

        // 延遲 1 秒後嘗試打開瀏覽器
        new Thread(() -> {
            try {
                Thread.sleep(1000);
                Runtime.getRuntime().exec("open http://localhost:8080/record.html");
            } catch (Exception e) {
                System.err.println("❌ 無法自動開啟瀏覽器：" + e.getMessage());
            }
        }).start();
    }
}
