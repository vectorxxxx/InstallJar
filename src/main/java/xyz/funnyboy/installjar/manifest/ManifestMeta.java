package xyz.funnyboy.installjar.manifest;

import java.io.File;
import java.io.IOException;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

/**
 * ManifestUtils
 *
 * @author VectorX
 * @version V1.0
 * @date 2024-01-09 21:15:03
 */
public class ManifestMeta
{
    private static final String IMPLEMENTATION_VENDOR_ID = "Implementation-Vendor-Id";
    private static final String IMPLEMENTATION_TITLE = "Implementation-Title";
    private static final String IMPLEMENTATION_VERSION = "Implementation-Version";
    private Attributes mainAttributes;

    public ManifestMeta(File jarFile) {
        if (jarFile.exists() && jarFile.isFile() && jarFile
                .getName()
                .endsWith(".jar")) {
            try (JarFile jf = new JarFile(jarFile)) {
                Manifest mf = jf.getManifest();
                if (mf != null) {
                    mainAttributes = mf.getMainAttributes();
                }
            }
            catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * 获取组 ID
     *
     * @return {@link String}
     */
    public String getGroupId() {
        if (mainAttributes != null) {
            return mainAttributes.getValue(IMPLEMENTATION_VENDOR_ID);
        }
        return "";
    }

    /**
     * 获取项目 ID
     *
     * @return {@link String}
     */
    public String getArtifactId() {
        if (mainAttributes != null) {
            return mainAttributes.getValue(IMPLEMENTATION_TITLE);
        }
        return "";
    }

    /**
     * 获取版本
     *
     * @return {@link String}
     */
    public String getVersion() {
        if (mainAttributes != null) {
            return mainAttributes.getValue(IMPLEMENTATION_VERSION);
        }
        return "";
    }
}
