package database;

import config.sql.SqlValues;
import logger.MyLogger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQL {

    private final MyLogger logger;
    private Connection connection;

    /**
     * The constructor tries to connect to the database with the data from the {@link config.sql.SqlConfig config}
     */
    public MySQL() {
        SqlValues values = SqlValues.getSqlValues();

        logger = MyLogger.getLogger();
        if (!isConnected()) {
            try {
                connection = DriverManager.getConnection("jdbc:mysql://" + values.getValues().get("host")
                        + ":" + values.getValues().get("port") + "/" + values.getValues().get("database"),
                        values.getValues().get("username"), values.getValues().get("password"));
                logger.info("Eine MySQL-Verbindung mit der Datenbank wurde erfolgreich aufgebaut.");
            } catch (final SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * This method disconnects the connection to the database
     */
    public void disconnect() {
        if (isConnected()) {
            try {
                connection.close();
                logger.info("Die MySQL-Verbindung wurde geschlossen.");
            } catch (final SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * This method checks if currently a connection to the database exists
     * @return true if an connection was successful | false if the connection was not successful
     */
    public boolean isConnected() {
        return connection != null;
    }

    /**
     * This method return the current connection
     * @return The current connection
     */
    public Connection getConnection() {
        return connection;
    }
}
