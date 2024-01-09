package xyz.funnyboy.installjar;

import xyz.funnyboy.installjar.frame.GUIFrame;

import javax.swing.*;

public class InstallJar
{
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GUIFrame frame = new GUIFrame();
            frame.setVisible(true);
            frame.setTitle("Install Jar");
            frame.setSize(400, 200);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            // 禁止改变大小
            frame.setResizable(false);

        });
    }
}
