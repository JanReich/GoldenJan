package Listener;

import config.maps.CachedMaps;
import logger.MyLogger;
import maps.ImageRenderer;
import maps.MapTileData;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.MapInitializeEvent;
import org.bukkit.map.MapView;

import java.awt.image.BufferedImage;

public class MapEvents implements Listener {

    private final CachedMaps cachedMaps;
    private final BufferedImage emptyImage;

    public MapEvents() {
        cachedMaps = CachedMaps.getCachedMaps();
        emptyImage = new BufferedImage(CachedMaps.getCachedMaps().getMAP_SCALE(),
                CachedMaps.getCachedMaps().getMAP_SCALE(), BufferedImage.TYPE_INT_ARGB);
    }

    /**
     * Is called when a map is initialized. If the map is a map with a custom image
     * the image is loaded from the cache. To terminate if the map is a custom Map this method looks into the
     * {@link CachedMaps cachedMaps} if the id is saved there with an image that means this is an custom Map with an
     * custom {@link ImageRenderer renderer}.
     * @param event The event is called everytime a map get initialized
     */
    @EventHandler
    public void onMapInitializing(final MapInitializeEvent event) {
        if (!cachedMaps.getMapData().containsKey(event.getMap().getId())) {
            return;
        }
        MapView view = event.getMap();
        view.getRenderers().clear();
        MapTileData mapTileData = cachedMaps.getMapData().get(view.getId());
        BufferedImage image = mapTileData.tileImage();
        view.addRenderer(new ImageRenderer(image, emptyImage, mapTileData.offsetX(), mapTileData.OffsetY()));
        view.setTrackingPosition(false);
    }
}
