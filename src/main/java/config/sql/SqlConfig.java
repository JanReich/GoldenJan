package config.sql;

import config.Config;
import lombok.NonNull;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.plugin.Plugin;
import util.FileHelper;

import java.io.File;
import java.io.IOException;

public class SqlConfig extends Config {

    /**
     * {@inheritDoc}
     * @param plugin The {@link minecraft.goldenjan.GoldenJan main class} that extends the {@link Plugin plugin class}
     */
    public SqlConfig(final Plugin plugin) {
        super(plugin);
    }

    /**
     * {@inheritDoc}
     * Not implemented yet
     */
    @Override
    public void reloadConfig() {

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadConfig(final @NonNull File configFile) {
        try {
            configuration.load(configFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
        SqlValues sqlValues = SqlValues.getSqlValues();
        sqlValues.getValues().put("host", configuration.getString("host"));
        sqlValues.getValues().put("port", configuration.getString("port"));
        sqlValues.getValues().put("database", configuration.getString("database"));
        sqlValues.getValues().put("username", configuration.getString("username"));
        sqlValues.getValues().put("password", configuration.getString("password"));

        logger.info("Die SQL-Config wurde geladen.");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void createDefaults(final @NonNull File configFile) {
        FileHelper.createNewFile(configFile);

        configuration.set("host", "localhost");
        configuration.set("port", "3306");
        configuration.set("database", "goldenjan");
        configuration.set("username", "root");
        configuration.set("password", "");

        try {
            configuration.save(configFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        loadConfig(configFile);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected @NonNull File getConfigFile() {
        return FileHelper.getFile(plugin.getName() + "/sql.yaml");
    }
}
