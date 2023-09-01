package config.sql;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * This class hold the information about the sql-information
 */
@Getter
public class SqlValues {

    private static SqlValues instance;
    private final Map<String, String> values;

    private SqlValues() {
        values = new HashMap<>();
    }

    /**
     * Only one object of this class should be existing. Therefor this class is private to obtains this object this method
     * must be used. When no object of this class is existing it creates a new one.
     * @return This class
     */
    public static SqlValues getSqlValues() {
        if (instance == null) {
            instance = new SqlValues();
        }
        return instance;
    }
}
