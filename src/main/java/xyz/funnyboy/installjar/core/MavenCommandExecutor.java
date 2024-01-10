package xyz.funnyboy.installjar.core;

import xyz.funnyboy.installjar.utils.ClipboardUtils;
import xyz.funnyboy.installjar.utils.PathUtils;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * InstallJar
 *
 * @author VectorX
 * @version V1.0
 * @date 2024-01-09 21:04:15
 */
public class MavenCommandExecutor
{
    /**
     * 执行 Maven 命令
     *
     * @param parentComponent 父组件
     * @param filePath        文件路径
     * @param groupId         组 ID
     * @param artifactId      项目 ID
     * @param version         版本
     */
    public void executeMavenCommand(Component parentComponent, String filePath, String groupId, String artifactId, String version) {
        ProcessBuilder processBuilder = new ProcessBuilder("cmd.exe", "/c", "mvn", "install:install-file", "-Dfile=" + filePath, "-DgroupId=" + groupId,
                "-DartifactId=" + artifactId, "-Dversion=" + version, "-Dpackaging=jar");
        processBuilder.directory(new File(System.getProperty("user.dir")));

        try {
            Process process = processBuilder.start();
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                JOptionPane.showMessageDialog(parentComponent, "Error executing Maven command: " + getCommandResult(process));
                return;
            }

            JOptionPane.showMessageDialog(parentComponent, "Maven command executed successfully");

            // 获取本地仓库路径
            String localRepositoryPath = PathUtils.getLocalRepositoryPath();
            if (localRepositoryPath == null) {
                JOptionPane.showMessageDialog(parentComponent, "Jar file not found in local Maven repository");
                return;
            }

            // Maven依赖写入剪贴板
            ClipboardUtils.writeMavenDependency2Clipboard(groupId, artifactId, version);
            
            // 打开安装的 Jar 包文件路径
            File jarFile = new File(localRepositoryPath + "/" + groupId.replace(".", "/") + "/" + artifactId + "/" + version);
            if (jarFile.exists()) {
                Desktop
                        .getDesktop()
                        .open(jarFile);
            }
        }
        catch (IOException | InterruptedException ex) {
            JOptionPane.showMessageDialog(parentComponent, "Error executing Maven command: " + ex.getMessage());
        }
    }

    /**
     * 获取j命令结果
     *
     * @param process 过程
     * @return {@link StringBuilder}
     * @throws IOException ioexception
     */
    private String getCommandResult(Process process) throws IOException {
        // 获取错误信息
        StringBuilder result = new StringBuilder();
        // 获取命令执行结果, 有两个结果: 正常的输出 和 错误的输出（PS: 子进程的输出就是主进程的输入）
        try (BufferedReader bufrIn = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8));
                BufferedReader bufrError = new BufferedReader(new InputStreamReader(process.getErrorStream(), StandardCharsets.UTF_8))) {
            // 读取输出
            String line;
            while ((line = bufrIn.readLine()) != null) {
                result
                        .append(line)
                        .append('\n');
            }
            while ((line = bufrError.readLine()) != null) {
                result
                        .append(line)
                        .append('\n');
            }
        }
        return result.toString();
    }

}
