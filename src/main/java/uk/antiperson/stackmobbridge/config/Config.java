package uk.antiperson.stackmobbridge.config;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import uk.antiperson.stackmobbridge.StackMobBridge;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

public class Config {

    private File file;
    private File folder;
    private String fileName;
    private FileConfiguration fileCon;
    private StackMobBridge smb;
    public Config(StackMobBridge smb, File folder, String fileName) {
        this.smb = smb;
        this.folder = folder;
        this.fileName = fileName;
    }

    public void reloadConfig() {
        file = new File(folder, fileName);
        fileCon = YamlConfiguration.loadConfiguration(getFile());
    }

    public File getFile() {
        if (file == null) {
            file = new File(folder, fileName);
        }
        return file;
    }

    public FileConfiguration getConfig() {
        if (fileCon == null) {
            reloadConfig();
        }
        return fileCon;
    }

    /**
     * Copies the default config from the jar to the plugin folder.
     * @throws IOException when an I/O error occurs reading or writing.
     */
    public void createFile() throws IOException {
        // The files copy below will throw an error if the directories are not pre existing.
        File parentFile = getFile().getParentFile();
        if(!parentFile.exists()){
            Files.createDirectories(parentFile.toPath());
        }
        // Open the file and copy it to the plugin folder.
        InputStream is = smb.getResource(getFile().getName());
        Files.copy(is, getFile().toPath());
        reloadConfig();
    }

}
