package xyz.funnyboy.installjar.utils;

import java.awt.*;
import java.awt.datatransfer.*;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Reader;

/**
 * 剪贴板实用程序
 *
 * @author VectorX
 * @version 1.0.0
 * @date 2024/01/10
 */
public class ClipboardUtils
{

    /**
     * 编写 Maven Dependency 至剪贴板
     *
     * @param groupId    组 ID
     * @param artifactId 项目 ID
     * @param version    版本
     */
    public static void writeMavenDependency2Clipboard(String groupId, String artifactId, String version) {
        StringBuilder clipboardText = new StringBuilder();
        clipboardText
                .append("<dependency>\n")
                .append("    <groupId>")
                .append(groupId.trim())
                .append("</groupId>\n")
                .append("    <artifactId>")
                .append(artifactId.trim())
                .append("</artifactId>\n")
                .append("    <version>")
                .append(version.trim())
                .append("</version>\n")
                .append("</dependency>");
        ClipboardUtils.setSysClipboardText(clipboardText.toString());
    }

    /**
     * 1. 从剪切板获得文字。
     */
    public static String getSysClipboardText() {
        String ret = "";
        Clipboard sysClip = Toolkit
                .getDefaultToolkit()
                .getSystemClipboard();
        // 获取剪切板中的内容
        Transferable clipTf = sysClip.getContents(null);

        if (clipTf != null) {
            // 检查内容是否是文本类型
            if (clipTf.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                try {
                    ret = (String) clipTf.getTransferData(DataFlavor.stringFlavor);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return ret;
    }

    /**
     * 2.将字符串复制到剪切板。
     */
    public static void setSysClipboardText(String writeMe) {
        Clipboard clip = Toolkit
                .getDefaultToolkit()
                .getSystemClipboard();
        Transferable tText = new StringSelection(writeMe);
        clip.setContents(tText, null);
    }

    /**
     * 3. 从剪切板获得图片。
     */
    public static Image getImageFromClipboard() throws Exception {
        Clipboard sysc = Toolkit
                .getDefaultToolkit()
                .getSystemClipboard();
        Transferable cc = sysc.getContents(null);
        if (cc == null) {
            return null;
        }
        else if (cc.isDataFlavorSupported(DataFlavor.imageFlavor)) {
            return (Image) cc.getTransferData(DataFlavor.imageFlavor);
        }
        return null;

    }

    /**
     * 4.复制图片到剪切板。
     */
    public static void setClipboardImage(final Image image) throws Exception {
        Transferable trans = new Transferable()
        {
            @Override
            public DataFlavor[] getTransferDataFlavors() {
                return new DataFlavor[] {DataFlavor.imageFlavor};
            }

            @Override
            public boolean isDataFlavorSupported(DataFlavor flavor) {
                return DataFlavor.imageFlavor.equals(flavor);
            }

            @Override
            public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
                if (isDataFlavorSupported(flavor)) {
                    return image;
                }
                throw new UnsupportedFlavorException(flavor);
            }

        };
        Toolkit
                .getDefaultToolkit()
                .getSystemClipboard()
                .setContents(trans, null);
    }

    /**
     * 5.通过流获取，可读取图文混合
     */
    public void getImageAndTextFromClipboard() throws Exception {
        Clipboard sysClip = Toolkit
                .getDefaultToolkit()
                .getSystemClipboard();
        Transferable clipTf = sysClip.getContents(null);
        DataFlavor[] dataList = clipTf.getTransferDataFlavors();
        int wholeLength = 0;
        for (int i = 0; i < dataList.length; i++) {
            DataFlavor data = dataList[i];
            if (data
                    .getSubType()
                    .equals("rtf")) {
                Reader reader = data.getReaderForText(clipTf);
                OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream("d:\\test.rtf"));
                char[] c = new char[1024];
                int leng = -1;
                while ((leng = reader.read(c)) != -1) {
                    osw.write(c, wholeLength, leng);
                }
                osw.flush();
                osw.close();
            }
        }
    }
}
