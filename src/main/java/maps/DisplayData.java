package maps;

import lombok.NonNull;

import java.awt.image.BufferedImage;

/**
 * This record is created while splitting the loaded in the{@link MapHelper#splitImage(BufferedImage, boolean) splitImage}
 * method images into separated images in map size. The size off the map can configured in the {@link config.maps.MapConfig}.
 * @param images The images of the map saved in an 2D image array
 * @param offsetX The offset in the row, if the image is not centred the offset is 0
 * @param offsetY The offset in the colum, if the image is not centred the offset is 0
 */
public record DisplayData(@NonNull BufferedImage[][] images, int offsetX, int offsetY) { }
