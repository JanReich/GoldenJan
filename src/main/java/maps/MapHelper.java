package maps;

import config.maps.CachedMaps;
import logger.MyLogger;
import org.jetbrains.annotations.NotNull;

import java.awt.image.BufferedImage;

public class MapHelper {

    /**
     * This method splits the image into several smaller images. The amount of images depends on the size of the image
     * that should displayed and the size of the map.
     * @param toDisplay The image that should displayed onto different maps (amount depends on image size)
     * @param centered If ture the image is in the center of the maps | when false the image is rendered in the top left
     * @return the data of the splitted image including calculated offset and the splitted images
     */
    public static DisplayData splitImage(final @NotNull BufferedImage toDisplay, final boolean centered) {
        MyLogger.getLogger().info("Es wird damit begonnen ein Bild zu unterteilen...");
        int mapScale = CachedMaps.getCachedMaps().getMAP_SCALE();
        int imagesInWidth = (int) Math.ceil(toDisplay.getWidth() / (double) mapScale);
        int imagesInHeight = (int) Math.ceil(toDisplay.getHeight() / (double) mapScale);
        //calculation offset
        int offsetX = 0;
        int offsetY = 0;
        if (centered) {
            offsetX = (imagesInWidth * mapScale - toDisplay.getWidth()) / 2;
            offsetY = (imagesInHeight * mapScale - toDisplay.getHeight()) / 2;
        }
        //splitting image into images in map canvas size
        BufferedImage[][] subImages = new BufferedImage[imagesInHeight][imagesInWidth];
        for (int col = 0; col < subImages.length; col++) {
            for (int row = 0; row < subImages[0].length; row++) {
                subImages[col][row] = toDisplay.getSubimage(
                        row * mapScale - Math.min(row, 1) * offsetX,
                        col * mapScale - Math.min(col, 1) * offsetY,
                        Math.min(toDisplay.getWidth() - row * mapScale + offsetX, mapScale),
                        Math.min(toDisplay.getHeight() - col * mapScale + offsetY, mapScale));
            }
        }
        MyLogger.getLogger().info("Das Bild wurde erfolgreich in " + imagesInHeight * imagesInWidth + " Teile unterteilt.");
        return new DisplayData(subImages, offsetX, offsetY);
    }
}
