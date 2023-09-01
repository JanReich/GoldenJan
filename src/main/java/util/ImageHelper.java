package util;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

@Getter
public class ImageHelper {

    private BufferedImage image;

    /**
     * Loading the image with {@link HttpURLConnection url connection} and reading the input stream. When the url is
     * invalid, then returns false.
     * @param url The url where the image that should loaded is located
     * @return false if there was an problem while loading the image | true if it was successful
     */
    public boolean loadImage(final String url) {
        HttpURLConnection connection = null;

        try {
            connection = (HttpURLConnection) new URL(url).openConnection();
            connection.connect();
            image = ImageIO.read(connection.getInputStream());
            connection.disconnect();
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    /**
     * Loading the image with {@link ImageIO#read(URL)}.
     * CAVE: Deprecated
     * @param url The url where the image should located
     * @return false if there was an problem while loading the image | true if it was successful
     */
    @Deprecated
    public boolean load(final String url) {
        BufferedImage image = null;
        try {
            image = ImageIO.read(new URL(url));
        } catch (final IOException e) {
            return false;
        }
        this.image = image;
        return true;
    }
}
