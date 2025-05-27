package com.example;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.*;

import org.lwjgl.opengl.GL;

public class OpenGLDemo {
    public static void main(String[] args) {
        // 初始化GLFW
        if (!glfwInit()) {
            throw new IllegalStateException("无法初始化GLFW");
        }

        // 配置窗口参数
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // 窗口默认隐藏
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // 允许调整大小

        // 创建窗口
        long window = glfwCreateWindow(800, 600, "OpenGL示例", NULL, NULL);
        if (window == NULL) {
            glfwTerminate();
            throw new IllegalStateException("无法创建窗口");
        }

        // 设置OpenGL上下文
        glfwMakeContextCurrent(window);
        GL.createCapabilities(); // 初始化OpenGL绑定

        // 显示窗口
        glfwShowWindow(window);

        // 设置窗口关闭回调
        glfwSetWindowCloseCallback(window, win -> glfwSetWindowShouldClose(win, true));

        // 渲染循环
        while (!glfwWindowShouldClose(window)) {
            // 清空屏幕
            GL.createCapabilities();
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            // 交换缓冲区并处理事件
            glfwSwapBuffers(window);
            glfwPollEvents();
        }

        // 释放资源
        glfwTerminate();
    }
}