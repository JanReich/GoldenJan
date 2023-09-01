package config.maps;

import logger.MyLogger;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import maps.DisplayData;
import maps.MapHelper;
import maps.MapTileData;
import util.ImageHelper;

import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * This class hold tracks of all custom painted Maps, that when the {@link org.bukkit.event.server.MapInitializeEvent MapInitializeEvent}
 * is called the image can be rendered to this Map.
 */
@Getter
@Setter
public class CachedMaps {

    private int MAX_IMAGE_WIDTH;
    private int MAX_IMAGE_HEIGHT;
    private int MAP_SCALE;

    private final MyLogger logger;
    private final Map<Integer, MapTileData> mapData;

    private static CachedMaps instance;

    private CachedMaps() {
        mapData = new HashMap<>();
        logger = MyLogger.getLogger();
    }

    /**
     * Adds an image that was loaded in the runtime to the cache
     * @param id The id of the Map that was loaded
     * @param mapImage The image that is rendered on the map
     * @param offsetX The offset in the row, when the image isen't centered the offset is always 0
     * @param offsetY The offset int the colum, when the image isen't centered the offset is always 0
     */
    public void addCachedMap(final int id, final BufferedImage mapImage, final int offsetX, final int offsetY) {
        mapData.put(id, new MapTileData(mapImage, offsetX, offsetY));
    }

    /**
     * This Method get the config data and splits the image into maps and load the images that should be loaded to the
     * maps.
     * @see MapConfigValue
     * @param mapConfigValue An object with the values out of the config
     */
    public void loadCachedMaps(final @NonNull MapConfigValue mapConfigValue) {
        logger.info("Eine neue Map wird aus der Konfiguration geladen. Mit den IDs: " +
                Arrays.toString(mapConfigValue.mapIds()));

        ImageHelper imageHelper = new ImageHelper();
        if (!imageHelper.loadImage(mapConfigValue.url())) {
            return;
        }
        int index = 0;
        DisplayData displayData = MapHelper.splitImage(imageHelper.getImage(), mapConfigValue.centered());
        for (int col = 0; col < displayData.images().length; col++) {
            for (int row = 0; row < displayData.images()[0].length; row++) {
                int offsetX = row != 0 ? 0 : displayData.offsetX();
                int offsetY = col != 0 ? 0 : displayData.offsetY();
                mapData.put(mapConfigValue.mapIds()[index], new MapTileData(displayData.images()[col][row], offsetX, offsetY));
                index++;
            }
        }
    }

    /**
     * Only one object of this class should be existing. Therefor this class is private to obtains this object this method
     * must be used. When no object of this class is existing it creates a new one.
     * @return This class
     */
    public static CachedMaps getCachedMaps() {
        if (instance == null) {
            instance = new CachedMaps();
        }
        return instance;
    }
}
