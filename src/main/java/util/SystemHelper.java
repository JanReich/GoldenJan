package util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class SystemHelper {

    /**
     * This method returns the date and time formatted for the log
     * @return The current date and time. Example: 30.01.1999 12:05:11
     */
    public static String getLogTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        return dateFormat.format(new Date());
    }

    /**
     * This method get the Date as String
     * @return The current date as String. Example: 01.Jan..2023
     */
    public static String getDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MMM.yyyy");
        return dateFormat.format(new Date());
    }

    /**
     * This method give the current time as String that can be used in a file format
     * @return The current time. Example: 14_55
     */
    public static String getTimeAsFilename() {
       SimpleDateFormat dateFormat = new SimpleDateFormat("HH_mm");
       return dateFormat.format(new Date());
    }
}
