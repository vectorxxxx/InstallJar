package xyz.funnyboy.installjar.frame;

import javax.swing.*;
import java.awt.*;

/**
 * TextField
 *
 * @author VectorX
 * @version V1.0
 * @date 2024-01-09 21:42:10
 */
public class TextField extends JTextField
{
    public TextField() {
        super(20);
        // 设置文本框的最大宽度为最大值
        this.setMaximumSize(new Dimension(Integer.MAX_VALUE, this.getPreferredSize().height));
        // 设置边框
        this.setBorder(new TextBorder(new Color(192, 192, 192), 1, true));
        // 支持粘贴
        this.setEditable(true);
    }
}
