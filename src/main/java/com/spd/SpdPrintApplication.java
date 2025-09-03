package com.spd;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class SpdPrintApplication {
    public static void main(String[] args) {
        SpringApplication.run(SpdPrintApplication.class, args);
        System.out.println("                _                  _       _   \n" +
                "  ___ _ __   __| |      _ __  _ __(_)_ __ | |_ \n" +
                " / __| '_ \\ / _` |_____| '_ \\| '__| | '_ \\| __|\n" +
                " \\__ \\ |_) | (_| |_____| |_) | |  | | | | | |_ \n" +
                " |___/ .__/ \\__,_|     | .__/|_|  |_|_| |_|\\__|\n" +
                "     |_|               |_|                     ");
        System.out.println("spd-print启动成功");
    }

}
