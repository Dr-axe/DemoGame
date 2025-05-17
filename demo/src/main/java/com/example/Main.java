package com.example;
import java.util.Scanner;

import Time.TimeManager;
import decides.*;
public class Main {
    // 测试调用
    public static void main(String[] args) {
            // 启动时间更新
            TimeManager.startUpdating();
    
            // 模拟其他类读取 time 值（循环打印）
            for (int i = 0; i < 20; i++) {
                System.out.println("当前 time 值: " + TimeManager.time);
                try {
                    Thread.sleep(100); // 每 100ms 打印一次
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
}