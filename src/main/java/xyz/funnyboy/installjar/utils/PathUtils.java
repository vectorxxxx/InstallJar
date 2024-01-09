package xyz.funnyboy.installjar.utils;

import org.apache.maven.settings.Settings;
import org.apache.maven.settings.building.DefaultSettingsBuilderFactory;
import org.apache.maven.settings.building.DefaultSettingsBuildingRequest;
import org.apache.maven.settings.building.SettingsBuildingException;

import java.io.File;

/**
 * PathUtils
 *
 * @author VectorX
 * @version V1.0
 * @date 2024-01-09 21:06:15
 */
public class PathUtils
{
    /**
     * 获取本地 Maven 仓库路径
     *
     * @return {@link String}
     */
    public static String getLocalRepositoryPath() {
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
}
