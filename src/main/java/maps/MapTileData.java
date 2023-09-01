package maps;

import lombok.NonNull;

import java.awt.image.BufferedImage;

/**
 * This data holds the data from one map and not from the complete image. This object is saved for every custom Map
 * that exists, with the specific data for this map.
 * @param tileImage The image that should rendered to the map
 * @param offsetX The offset in row direction, when the image is not centered it is always 0
 * @param OffsetY The offset in colum direction, when the image is not centered it is always 0
 */
public record MapTileData(@NonNull BufferedImage tileImage, int offsetX, int OffsetY) { }
