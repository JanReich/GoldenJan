package maps;

import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.jetbrains.annotations.NotNull;

import java.awt.image.BufferedImage;

/**
 * This class is applied to every custom Map to render the custom Map
 */
@RequiredArgsConstructor
public class ImageRenderer extends MapRenderer {

    private boolean done = false;
    private final BufferedImage image;
    private final BufferedImage emptyImage;
    private final int offsetX;
    private final int offsetY;

    /**
     * Is method is called every tick when a player is nearby. To save serverperformance the map only drawn when its
     * initialized. To avoid that the world is drawn on the map if the image not covers the whole frame there is drawn
     * a transparent image first and then renders the loaded image on top. Important is that the {@link MapView#setTrackingPosition(boolean) trackingposition}
     * is disabled.
     *
     * @param view The view of the Map
     * @param canvas The canvas where the image are rendered
     * @param player The player that views at the map
     */
    @Override
    public void render(final @NotNull MapView view, final @NotNull MapCanvas canvas,
                       final @NotNull Player player) {
        if (done) //returns to save performance - it is enough to render the images at the frist time
            return;
        canvas.drawImage(0, 0, emptyImage);
        canvas.drawImage(offsetX, offsetY, image);
        view.setTrackingPosition(false);
        done = true;
    }
}
