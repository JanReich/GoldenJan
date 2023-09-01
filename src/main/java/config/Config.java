package config;

import logger.MyLogger;
import lombok.NonNull;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import util.FileHelper;

import java.io.File;

public abstract class Config {

    protected final Plugin plugin;
    protected final MyLogger logger;
    protected final YamlConfiguration configuration;

    /**
     * The constructor calls the {@link #setupConfig() setup} method. And if there is no config created the default
     * config will created in the {@link #createDefaults(File)} method. After the creation (or if there is already
     * a config created) load the config {@link #loadConfig(File)}
     * @param plugin The {@link minecraft.goldenjan.GoldenJan main class} that extends the {@link Plugin plugin class}
     */
    public Config(final Plugin plugin) {
        this.plugin = plugin;
        this.logger = MyLogger.getLogger();
        this.configuration = new YamlConfiguration();

        setupConfig();
    }

    private void setupConfig() {
        File configFile = getConfigFile();
        logger.info("Die Konfiguration '" + configFile.getPath() + "' wird geladen...");
        FileHelper.createDir(FileHelper.getFile(plugin.getName()));
        if (configFile.exists())
            loadConfig(configFile);
        else {
            logger.info("Eine neue Konfiguration mit Standartwerten wird erstellt...");
            createDefaults(configFile);
        }
    }

    /**
     * When this method is called the config should reload and the inheritance classes handle the loaded data
     */
    public abstract void reloadConfig();

    /**
     * The inheritance classes loading the config from the {@link #getConfigFile() file} and handels the data
     * @param configFile The file that should be loaded
     */
    protected abstract void loadConfig(final @NonNull File configFile);

    /**
     * The inheritance classes create the {@link #getConfigFile() file} and saves the standard values into int and after
     * that calls the {@link #loadConfig(File)} method.
     * @param configFile The file that should be loaded
     */
    protected abstract void createDefaults(final @NonNull File configFile);

    /**
     * This methods returns the {@link File file} that points to the config-file
     * @return The config file
     */
    protected abstract @NonNull File getConfigFile();
}
