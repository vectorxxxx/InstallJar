package xyz.funnyboy.installjar;

import xyz.funnyboy.installjar.frame.GUIFrame;

import javax.swing.*;

public class InstallJar
{
    public static void main(String[] args) {
        SwingUtilities.invokeLater(GUIFrame::new);
    }
}
