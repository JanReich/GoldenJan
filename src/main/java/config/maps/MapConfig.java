package config.maps;

import config.Config;
import lombok.NonNull;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.plugin.Plugin;
import util.FileHelper;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public class MapConfig extends Config {

    /**
     * {@inheritDoc}
     * @param plugin The {@link minecraft.goldenjan.GoldenJan main class} that extends the {@link Plugin plugin class}
     */
    public MapConfig(final @NonNull Plugin plugin) {
        super(plugin);
    }

    /**
     * This method saves the map data into the {@link #getConfigFile() file}
     * @param imageUrl The url the image is loaded from
     * @param centered The boolean if the image should be created centered or should it rendered in the upper left corner
     * @param mapIds The ids the images is rendered in an integer array
     */
    public void saveMapImage(final @NonNull String imageUrl, final boolean centered,
                             final int... mapIds) {
        UUID uuid = UUID.randomUUID();
        configuration.set("maps." + uuid + ".url", imageUrl);
        configuration.set("maps." + uuid + ".centered", centered);
        configuration.set("maps." + uuid + ".ids", mapIds);
        saveConfiguration(getConfigFile());
        logger.info("Map with url: " + imageUrl + " was successfully saved in the config");
    }

    /**
     * {@inheritDoc}
     * Not implemented yet
     */
    @Override
    public void reloadConfig() {
        logger.info("Die Map Konfiguration wird neu geladen...");
        loadConfig(getConfigFile());
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

        //Get default map values
        CachedMaps.getCachedMaps().setMAX_IMAGE_WIDTH(configuration.getInt("IMAGE_MAX_WIDTH"));
        CachedMaps.getCachedMaps().setMAX_IMAGE_HEIGHT(configuration.getInt("IMAGE_MAX_HEIGHT"));
        CachedMaps.getCachedMaps().setMAP_SCALE(configuration.getInt("MAP_SCALE"));

        //Loading Maps
        if (configuration.getConfigurationSection("maps.") != null) {
            Set<String> mapIndexes = Objects.requireNonNull(configuration.getConfigurationSection("maps."))
                    .getKeys(false);
            for (String mapIndex : mapIndexes) {
                String url = configuration.getString("maps." + mapIndex + ".url");
                boolean centered = configuration.getBoolean("maps." + mapIndex + ".centered");
                int[] mapIds = configuration.getIntegerList("maps." + mapIndex + ".ids").stream()
                        .mapToInt(i -> i)
                        .toArray();
                CachedMaps.getCachedMaps().loadCachedMaps(new MapConfigValue(url != null ? url : "", centered, mapIds));
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void createDefaults(final @NonNull File configFile) {
        FileHelper.createNewFile(configFile);

        configuration.set("IMAGE_MAX_WIDTH", 640);
        configuration.set("IMAGE_MAX_HEIGHT", 384);
        configuration.set("MAP_SCALE", 128);

        saveConfiguration(configFile);
        loadConfig(configFile);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected @NonNull File getConfigFile() {
        return FileHelper.getFile(plugin.getName() + "/maps.yaml");
    }

    private void saveConfiguration(final @NonNull File configFile) {
        try {
            configuration.save(configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
