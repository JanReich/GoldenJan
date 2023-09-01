package logger;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import util.FileHelper;
import util.SystemHelper;

import java.io.IOException;
import java.util.logging.*;

public class MyLogger {

    @Getter
    private String loggingFile;

    private final Logger logger;
    private static MyLogger instance;

    private MyLogger() {
        logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    }

    /**
     * In this method the logger gets setup. For the logging into the logger is created a custom {@link LogFormatter formatter}
     * @param plugin The {@link minecraft.goldenjan.GoldenJan main class} that extends the {@link Plugin plugin class}
     */
    public void setup(final Plugin plugin) {
        try {
            Logger rootLogger = Logger.getLogger("");
            Handler[] handlers = rootLogger.getHandlers();
            if (handlers[0] instanceof ConsoleHandler) {
                rootLogger.removeHandler(handlers[0]);
            }
            logger.setLevel(Level.ALL);
            //creating logging folder when it does not exist
            String loggingDir = "logging";
            FileHelper.createDir(FileHelper.getFile(loggingDir));
            //creating logging folder for the current day
            loggingDir += "/" + SystemHelper.getDate();
            FileHelper.createDir(FileHelper.getFile(loggingDir));

            loggingDir += "/log_";
            loggingFile = loggingDir + SystemHelper.getTimeAsFilename() + ".txt";
            FileHandler handler = new FileHandler(Bukkit.getPluginsFolder().getPath() + "/" + loggingFile);
            //creating formatter
            Formatter logFormatter = new LogFormatter(plugin);
            handler.setFormatter(logFormatter);
            logger.addHandler(handler);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Only one object of this class should be existing. Therefor this class is private to obtains this object this method
     * must be used. When no object of this class is existing it creates a new one.
     * @return This class
     */
    public static MyLogger getLogger() {
        if (instance == null) {
            instance = new MyLogger();
        }
        return instance;
    }

    public void debug(final String message) {
        logger.finer(message);
    }

    public void info(final String message) {
        logger.info(message);
    }

    public void warn(final String message) {
        logger.warning(message);
    }

    public void error(final String message) {
        logger.severe(message);
    }

    public void fatalError(final String message) {
        logger.severe(message);
    }
}
