package config.maps;

import lombok.NonNull;

/**
 * This immutable data class will created with the data of the {@link MapConfig config}. So the map can saved in the
 * Cache with this data
 * @param url The url the image is from
 * @param centered If the image should rendered in the center true, else if it false then the image will be rendere in the top left
 * @param mapIds The id of maps the parts of the image are rendered to. The id are stored in an integer array
 */
public record MapConfigValue(@NonNull String url, boolean centered, int @NonNull [] mapIds) {
}
