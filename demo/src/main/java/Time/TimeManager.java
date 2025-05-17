package Time;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

public class TimeManager {
    // 全局时间变量（volatile 保证多线程可见性）
    public static volatile double time;
    private static final String DATA_FILE = "./time.dat";  // 存储路径
    private static final double INITIAL_TIME = 100.0;         // 首次启动默认时间
    private static Thread updateThread;

    // 静态代码块：程序启动时加载历史时间
    static {
        try {
            loadTimeFromFile();
        } catch (Exception e) {
            System.out.println("加载时间失败，使用初始值：" + INITIAL_TIME);
            time = INITIAL_TIME;
        }
    }

    /**
     * 启动时间更新（无限增长，基于实际流逝时间）
     */
    public static void startUpdating() {
        if (updateThread != null && updateThread.isAlive()) return;

        updateThread = new Thread(() -> {
            final long startMillis = System.currentTimeMillis();
            double lastSavedTime = time;  // 记录上次保存的时间点

            while (true) {
                try {
                    // 计算当前时间（基于程序启动后的累计毫秒数）
                    long elapsedMillis = System.currentTimeMillis() - startMillis;
                    time = lastSavedTime + elapsedMillis / 1000.0;  // 每秒增加 1.0

                    // 控制更新频率（每秒更新 100 次，可调整）
                    TimeUnit.MILLISECONDS.sleep(10);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }, "Time-Daemon-Thread");

        updateThread.setDaemon(true);  // 守护线程，随主线程结束而终止
        updateThread.start();
    }

    /**
     * 保存当前时间到文件（自动创建目录）
     */
    public static void saveTimeToFile() {
        try {
            Files.createDirectories(Paths.get("data"));  // 创建数据目录
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
                oos.writeDouble(time);
                System.out.println("时间已保存：" + time);
            }
        } catch (IOException e) {
            System.err.println("保存时间失败：" + e.getMessage());
        }
    }

    /**
     * 从文件加载时间（存在则读取，否则返回初始值）
     */
    private static void loadTimeFromFile() throws IOException {
        File file = new File(DATA_FILE);
        if (!file.exists()) return;  // 首次启动无文件

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(DATA_FILE))) {
            time = ois.readDouble();
            System.out.println("加载历史时间：" + time);
        }
    }

    /**
     * 注册 JVM 关闭钩子（程序退出时自动保存时间）
     */
    public static void registerShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(TimeManager::saveTimeToFile));
    }
    public static void main(String[] args) {
        // 启动时间更新
        TimeManager.startUpdating();
        TimeManager.registerShutdownHook();
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
    