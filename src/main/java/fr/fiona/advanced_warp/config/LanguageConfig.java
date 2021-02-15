package fr.fiona.advanced_warp.config;

import fr.fiona.advanced_warp.Advanced_warp;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class LanguageConfig {
    private File languageConfigFile;
    private FileConfiguration languageConfig;





    public FileConfiguration getLanguageConfig() {
        return this.languageConfig;
    }

    public void createlanguageConfig() {
        languageConfigFile = new File(Advanced_warp.getInstance().getDataFolder(), "language/"+Advanced_warp.getInstance().getConfig().getString("language")+".yml");
        if (!languageConfigFile.exists()) {
            languageConfigFile.getParentFile().mkdirs();
            Advanced_warp.getInstance().saveResource("language/"+Advanced_warp.getInstance().getConfig().getString("language")+".yml", false);
        }

        languageConfig= new YamlConfiguration();
        try {
            languageConfig.load(languageConfigFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }
}
