package logger;

import lombok.RequiredArgsConstructor;
import org.bukkit.plugin.Plugin;
import util.SystemHelper;

import java.util.logging.Formatter;
import java.util.logging.LogRecord;

@RequiredArgsConstructor
public class LogFormatter extends Formatter {

    private final Plugin plugin;

    /**
     * This method formats the log-messages
     * @param record the log record to be formatted.
     * @return The log string formatted: [Date Time] pluginname:loglevel - message
     */
    @Override
    public String format(final LogRecord record) {
        String date = "[" + SystemHelper.getLogTime() + "] ";
        String pluginName = plugin.getName();
        return date + pluginName + ":" + record.getLevel() + " - " + record.getMessage() + "\n";
    }
}
