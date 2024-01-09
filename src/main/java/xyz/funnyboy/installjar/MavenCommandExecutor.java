package xyz.funnyboy.installjar;

import org.apache.maven.settings.Settings;
import org.apache.maven.settings.building.DefaultSettingsBuilderFactory;
import org.apache.maven.settings.building.DefaultSettingsBuildingRequest;
import org.apache.maven.settings.building.SettingsBuildingException;
import xyz.funnyboy.installjar.utils.TextBorderUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.File;
import java.io.IOException;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

public class MavenCommandExecutor extends JFrame
{

    private final JTextField fileField;
    private final JTextField groupIdField;
    private final JTextField artifactIdField;
    private final JTextField versionField;

    public MavenCommandExecutor() {
        setTitle("Install Jar");
        setSize(400, 200);
        // 禁止改变大小
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(5, 5, 2, 2);

        constraints.gridx = 0;
        constraints.gridy = 0;
        panel.add(new JLabel("File Path:"), constraints);

        // 文本框边框设统一置
        final TextBorderUtils border = new TextBorderUtils(new Color(192, 192, 192), 1, true);

        fileField = new JTextField(20);
        fileField.setBorder(border);
        fileField.setMaximumSize(new Dimension(Integer.MAX_VALUE, fileField.getPreferredSize().height));  // 设置文本框的最大宽度为最大值
        // 支持粘贴
        fileField.setEditable(true);
        // 为 jarFileField 添加拖放焦点监听器
        fileField.addFocusListener(new FocusListener()
        {
            @Override
            public void focusGained(FocusEvent e) {

            }

            @Override
            public void focusLost(FocusEvent e) {
                fillMavenInfoFromJar(new File(fileField.getText()));
            }
        });
        // 为 jarFileField 添加拖放事件监听器
        fileField.setDropTarget(new DropTarget()
        {
            private static final long serialVersionUID = -7027895700567355000L;

            @Override
            public synchronized void drop(DropTargetDropEvent evt) {
                try {
                    evt.acceptDrop(DnDConstants.ACTION_COPY);
                    Transferable transferable = evt.getTransferable();
                    if (!transferable.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                        return;
                    }
                    java.util.List<File> files = (java.util.List<File>) transferable.getTransferData(DataFlavor.javaFileListFlavor);
                    if (files.size() > 0) {
                        File file = files.get(0);
                        fileField.setText(file.getAbsolutePath());
                        fillMavenInfoFromJar(file);
                    }
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        constraints.gridx = 1;
        constraints.gridy = 0;
        constraints.gridwidth = 2;  // 设置文本框跨越两列
        constraints.fill = GridBagConstraints.HORIZONTAL;  // 让文本框水平填充整个区域
        panel.add(fileField, constraints);

        JButton fileChooserButton = new JButton("...");
        fileChooserButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            int returnValue = fileChooser.showOpenDialog(null);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                fileField.setText(selectedFile.getAbsolutePath());
            }
        });
        constraints.gridx = 3;
        constraints.gridy = 0;
        panel.add(fileChooserButton, constraints);

        constraints.gridx = 0;
        constraints.gridy = 1;
        panel.add(new JLabel("Group ID:"), constraints);

        groupIdField = new JTextField(20);
        groupIdField.setBorder(border);
        // 支持粘贴
        groupIdField.setEditable(true);
        constraints.gridx = 1;
        constraints.gridy = 1;
        panel.add(groupIdField, constraints);

        constraints.gridx = 0;
        constraints.gridy = 2;
        panel.add(new JLabel("Artifact ID:"), constraints);

        artifactIdField = new JTextField(20);
        artifactIdField.setBorder(border);
        // 支持粘贴
        artifactIdField.setEditable(true);
        constraints.gridx = 1;
        constraints.gridy = 2;
        panel.add(artifactIdField, constraints);

        constraints.gridx = 0;
        constraints.gridy = 3;
        panel.add(new JLabel("Version:"), constraints);

        versionField = new JTextField(20);
        versionField.setBorder(border);
        // 支持粘贴
        versionField.setEditable(true);
        constraints.gridx = 1;
        constraints.gridy = 3;
        panel.add(versionField, constraints);

        JButton executeButton = new JButton("Execute Maven Command");
        executeButton.addActionListener(e -> executeMavenCommand());
        constraints.gridx = 2;
        constraints.gridy = 4;
        panel.add(executeButton, constraints);

        add(panel);
    }

    private void fillMavenInfoFromJar(File jarFile) {
        if (!jarFile.exists() || !jarFile.isFile() || !jarFile
                .getName()
                .endsWith(".jar")) {
            groupIdField.setText("");
            artifactIdField.setText("");
            versionField.setText("");
            return;
        }

        try (JarFile jf = new JarFile(jarFile)) {
            Manifest mf = jf.getManifest();
            if (mf == null) {
                return;
            }
            Attributes mainAttributes = mf.getMainAttributes();
            String groupId = mainAttributes.getValue("Implementation-Vendor-Id");
            String artifactId = mainAttributes.getValue("Implementation-Title");
            String version = mainAttributes.getValue("Implementation-Version");

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
        catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error reading Jar file: " + ex.getMessage());
        }
    }

    private void executeMavenCommand() {
        String filePath = fileField.getText();
        String groupId = groupIdField.getText();
        String artifactId = artifactIdField.getText();
        String version = versionField.getText();

        ProcessBuilder processBuilder = new ProcessBuilder("cmd.exe", "/c", "mvn", "install:install-file", "-Dfile=" + filePath, "-DgroupId=" + groupId,
                "-DartifactId=" + artifactId, "-Dversion=" + version, "-Dpackaging=jar");
        processBuilder.directory(new File(System.getProperty("user.dir")));

        try {
            Process process = processBuilder.start();
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                JOptionPane.showMessageDialog(this, "Error executing Maven command. Exit code: " + exitCode);
                return;
            }

            JOptionPane.showMessageDialog(this, "Maven command executed successfully");

            // 获取本地仓库路径
            String localRepositoryPath = getLocalRepositoryPath();
            if (localRepositoryPath == null) {
                JOptionPane.showMessageDialog(this, "Jar file not found in local Maven repository");
                return;
            }

            // 打开安装的 Jar 包文件路径
            File jarFile = new File(localRepositoryPath + "/" + groupId.replace(".", "/") + "/" + artifactId + "/" + version);
            if (jarFile.exists()) {
                Desktop
                        .getDesktop()
                        .open(jarFile);
            }
        }
        catch (IOException | InterruptedException ex) {
            JOptionPane.showMessageDialog(this, "Error executing Maven command: " + ex.getMessage());
        }
    }

    private String getLocalRepositoryPath() {
        final String defaultMavenHome = System.getProperty("user.home") + "/.m2/repository/";

        final String mavenHome = System.getenv("MAVEN_HOME");
        if (mavenHome == null) {
            return defaultMavenHome;
        }
        File settingsFile = new File(mavenHome, "conf/settings.xml");
        if (!settingsFile.exists()) {
            return defaultMavenHome;
        }

        DefaultSettingsBuilderFactory factory = new DefaultSettingsBuilderFactory();
        DefaultSettingsBuildingRequest request = new DefaultSettingsBuildingRequest();
        request.setUserSettingsFile(settingsFile);

        try {
            Settings settings = factory
                    .newInstance()
                    .build(request)
                    .getEffectiveSettings();
            return settings.getLocalRepository();
        }
        catch (SettingsBuildingException e) {
            e.printStackTrace();
            return defaultMavenHome;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MavenCommandExecutor executor = new MavenCommandExecutor();
            executor.setVisible(true);
        });
    }
}
