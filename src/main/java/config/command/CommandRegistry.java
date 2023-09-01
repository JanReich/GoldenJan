package config.command;

import lombok.Getter;

import java.util.*;

/**
 * This class hold the information about the different command-information, their aliases, permission, description and
 * all values thats get read out of the config
 */
@Getter
public class CommandRegistry {

    /**
     * The List where all command-names are inside
     */
    private final List<String> registeredCommands;
    /**
     * The list hold tracks of the command-information. Example: "commandname.permission" gets the permission
     */
    private final Map<String, Object> commandSettings;

    private static CommandRegistry instance;

    private CommandRegistry() {
        registeredCommands = new ArrayList<>();
        commandSettings = new HashMap<>();
    }

    /**
     * Only one object of this class should be existing. Therefor this class is private to obtains this object this method
     * must be used. When no object of this class is existing it creates a new one.
     * @return This class
     */
    public static CommandRegistry getCommandRegistry() {
        if (instance == null) {
            instance = new CommandRegistry();
        }
        return instance;
    }
}
