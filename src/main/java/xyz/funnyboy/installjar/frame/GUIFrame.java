package xyz.funnyboy.installjar.frame;

import xyz.funnyboy.installjar.core.MavenCommandExecutor;
import xyz.funnyboy.installjar.manifest.ManifestMeta;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * InstallJar
 *
 * @author VectorX
 * @version V1.0
 * @date 2024-01-09 21:04:15
 */
public class GUIFrame extends JFrame
{

    private final TextField fileField;
    private final TextField groupIdField;
    private final TextField artifactIdField;
    private final TextField versionField;

    public GUIFrame() {
        this.setVisible(true);
        this.setTitle("Install Jar");
        this.setSize(400, 200);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // 禁止改变大小
        this.setResizable(false);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(5, 5, 2, 2);

        /*
         *  File Path
         */
        constraints.gridx = 0;
        constraints.gridy = 0;
        panel.add(new JLabel("File Path:"), constraints);

        fileField = new TextField();
        // 为 fileField 添加焦点监听器
        fileField.addFocusListener(new TextFocusListener());
        // 为 fileField 添加拖放监听器
        // fileField.setDragEnabled(true);
        // fileField.setDropTarget(new FileDropTarget());
        fileField.setTransferHandler(new FileTransferHandler());
        constraints.gridx = 1;
        constraints.gridy = 0;
        constraints.gridwidth = 2;  // 设置文本框跨越两列
        constraints.fill = GridBagConstraints.HORIZONTAL;  // 让文本框水平填充整个区域
        panel.add(fileField, constraints);

        // 选择文件按钮
        JButton fileChooserButton = new JButton("...");
        fileChooserButton.addActionListener(new FileActionListener());
        constraints.gridx = 3;
        constraints.gridy = 0;
        panel.add(fileChooserButton, constraints);

        /*
         * Group ID
         */
        constraints.gridx = 0;
        constraints.gridy = 1;
        panel.add(new JLabel("Group ID:"), constraints);
        groupIdField = new TextField();
        constraints.gridx = 1;
        constraints.gridy = 1;
        panel.add(groupIdField, constraints);

        /*
         * Artifact ID
         */
        constraints.gridx = 0;
        constraints.gridy = 2;
        panel.add(new JLabel("Artifact ID:"), constraints);
        artifactIdField = new TextField();
        constraints.gridx = 1;
        constraints.gridy = 2;
        panel.add(artifactIdField, constraints);

        /*
         * Version
         */
        constraints.gridx = 0;
        constraints.gridy = 3;
        panel.add(new JLabel("Version:"), constraints);
        versionField = new TextField();
        constraints.gridx = 1;
        constraints.gridy = 3;
        panel.add(versionField, constraints);

        /*
         * Execute Maven Command
         */
        JButton executeButton = new JButton("Execute Maven Command");
        executeButton.addActionListener(new ButtonActionListener(this));
        constraints.gridx = 2;
        constraints.gridy = 4;
        panel.add(executeButton, constraints);

        add(panel);
    }

    private void fillMavenInfoFromJar(File jarFile) {
        final ManifestMeta manifestMeta = new ManifestMeta(jarFile);

        String groupId = manifestMeta.getGroupId();
        String artifactId = manifestMeta.getArtifactId();
        String version = manifestMeta.getVersion();

        if (groupId != null) {
            groupIdField.setText(groupId);
        }
        else {
            groupIdField.setText("");
        }

        if (artifactId != null) {
            artifactIdField.setText(artifactId);
        }
        else {
            artifactIdField.setText("");
        }

        if (version != null) {
            versionField.setText(version);
        }
        else {
            versionField.setText("");
        }
    }

    /**
     * 文件传输处理程序
     *
     * @author VectorX
     * @version 1.0.0
     * @date 2024/01/09
     * @see TransferHandler
     */
    private class FileTransferHandler extends TransferHandler
    {
        @Override
        public int getSourceActions(JComponent c) {
            // 拖放操作只能是拖放
            return TransferHandler.COPY;
        }

        @Override
        public boolean canImport(JComponent comp, DataFlavor[] flavors) {
            // 允许拖放的类型为String
            for (DataFlavor flavor : flavors) {
                if (flavor.isMimeTypeEqual(DataFlavor.javaFileListFlavor)) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public boolean importData(JComponent c, Transferable th) {
            // 处理拖放的数据
            try {
                List<File> files = (List<File>) th.getTransferData(DataFlavor.javaFileListFlavor);
                if (!files.isEmpty()) {
                    File file = files.get(0);
                    fileField.setText(file.getAbsolutePath());
                    fillMavenInfoFromJar(file);
                }
            }
            catch (UnsupportedFlavorException | IOException e) {
                e.printStackTrace();
            }
            return true;
        }
    }

    /**
     * 文件文本放置目标
     *
     * @author VectorX
     * @version 1.0.0
     * @date 2024/01/09
     * @see DropTarget
     */
    private class FileDropTarget extends DropTarget
    {
        private static final long serialVersionUID = 1704126321003264881L;

        @Override
        public synchronized void drop(DropTargetDropEvent evt) {
            try {
                evt.acceptDrop(DnDConstants.ACTION_COPY);
                List<File> files = (List<File>) evt
                        .getTransferable()
                        .getTransferData(DataFlavor.javaFileListFlavor);
                if (!files.isEmpty()) {
                    File file = files.get(0);
                    fileField.setText(file.getAbsolutePath());
                    fillMavenInfoFromJar(file);
                }
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * 文本焦点侦听器
     *
     * @author VectorX
     * @version 1.0.0
     * @date 2024/01/09
     * @see FocusListener
     */
    private class TextFocusListener implements FocusListener
    {
        @Override
        public void focusGained(FocusEvent e) {
        }

        @Override
        public void focusLost(FocusEvent e) {
            fillMavenInfoFromJar(new File(fileField.getText()));
        }
    }

    /**
     * 文件操作侦听器
     *
     * @author VectorX
     * @version 1.0.0
     * @date 2024/01/09
     * @see ActionListener
     */
    private class FileActionListener implements ActionListener
    {
        @Override
        public void actionPerformed(ActionEvent e) {
            JFileChooser fileChooser = new JFileChooser();
            int returnValue = fileChooser.showOpenDialog(null);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                java.io.File selectedFile = fileChooser.getSelectedFile();
                fileField.setText(selectedFile.getAbsolutePath());
                fillMavenInfoFromJar(selectedFile);
            }
        }
    }

    /**
     * 按钮操作侦听器
     *
     * @author VectorX
     * @version 1.0.0
     * @date 2024/01/09
     * @see ActionListener
     */
    private class ButtonActionListener implements ActionListener
    {
        private Component parentComponent;

        public ButtonActionListener(Component parentComponent) {
            this.parentComponent = parentComponent;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            final String filePath = fileField.getText();
            final String groupId = groupIdField.getText();
            final String artifactId = artifactIdField.getText();
            final String version = versionField.getText();
            new MavenCommandExecutor().executeMavenCommand(parentComponent, filePath, groupId, artifactId, version);
        }
    }

}
