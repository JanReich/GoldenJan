package minecraft.goldenjan;

import Listener.ConnectionEvents;
import Listener.MapEvents;
import commads.ImageConvert;
import config.command.CommandConfig;
import config.command.CommandRegistry;
import config.maps.MapConfig;
import config.messages.MessageConfig;
import config.sql.SqlConfig;
import database.MySQL;
import logger.MyLogger;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public final class GoldenJan extends JavaPlugin {

    private MyLogger logger;
    private MapConfig mapConfig;
    private ConnectionEvents connectionEvents;
    private MySQL mySQL;

    /**
     * Called by {@link org.bukkit.plugin.PluginManager pluginmanager} when plugin is loaded
     * Start setting up plugin
     * 1. {@link #setupLogger()}  Create-Logger} - 2. {@link #loadConfigs() Load-Config} -
     * 3. {@link #connectSql()} SQL-Connect} - 4. {@link #registerListener() Register Listener}
     */
    @Override
    public void onEnable() {
        setupLogger();
        loadConfigs();
        connectSql();
        registerListener();
    }

    /**
     * Called by {@link org.bukkit.plugin.PluginManager pluginmanager} when plugin is closed
     * Writes the online time into the config bevor server closes {@link ConnectionEvents#onServerClosed()}
     * Disconnect from database {@link MySQL#disconnect()}
     */
    @Override
    public void onDisable() {
        connectionEvents.onServerClosed();
        mySQL.disconnect();
    }

    private void setupLogger() {
        logger = MyLogger.getLogger();
        logger.setup(this);
        logger.info("Der Logger wurde erfolgreich geladen.");
    }

    private void loadConfigs() {
        new MessageConfig(this);
        mapConfig = new MapConfig(this);
        new CommandConfig(this);
        new SqlConfig(this);
    }

    private void connectSql() {
        mySQL = new MySQL();

        createDatabaseTabels();
    }

    private void registerListener() {
        Bukkit.getPluginManager().registerEvents(new MapEvents(), this);

        connectionEvents = new ConnectionEvents(mySQL);
        Bukkit.getPluginManager().registerEvents(connectionEvents, this);
    }

    private void createDatabaseTabels() {
        if (!mySQL.isConnected()) {
            logger.info("Es wird versucht mit der Datenbank zu arbeiten, aber es gibt keine aktive Verbindung!");
            return;
        }
        try {
            PreparedStatement ps = mySQL.getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS ips " +
                    "(ID INT AUTO_INCREMENT, UUID VARCHAR(255), IP VARCHAR(255), PRIMARY KEY (ID))");
            ps.executeUpdate();
            ps.close();
            ps = mySQL.getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS skins (" +
                    "UUID VARCHAR(255), skinModel VARCHAR(255), skinURL VARCHAR(255), PRIMARY KEY (UUID));");
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * This methods load all commands after they are loaded from the {@link CommandConfig config}
     * This method replaces the registration inside of the plugin.yml and cannot be automated (because the command-label
     * is not the same as the name of the class). An alternative is to create an annotation for the command. Then it is
     * not necessary to register the command in this method.
     */
    public void loadCommands() {
        CommandRegistry commandRegistry = CommandRegistry.getCommandRegistry();
        for (String registeredCommand : commandRegistry.getRegisteredCommands()) {
            String permission = (String) commandRegistry.getCommandSettings().get("imageconverter.permission");
            boolean defaultPermission = (boolean) commandRegistry.getCommandSettings().get("imageconverter.default");
            List<String> aliases = (List<String>) commandRegistry.getCommandSettings().get("imageconverter.aliases");
            String description = (String) commandRegistry.getCommandSettings().get("imageconverter.description");
            String usage = (String) commandRegistry.getCommandSettings().get("imageconverter.usage");
            switch (registeredCommand) {
                case "imageconverter" -> {
                    ImageConvert imageConvert = new ImageConvert(registeredCommand, description, usage, aliases,
                            mapConfig, defaultPermission, this);
                    imageConvert.setPermission(permission);
                }
                default -> logger.info("Ein unbekannter Befehl wurde gefunden: '" + registeredCommand +
                        "'. Dieser Befehl wurde nicht registriert!");
            }
        }
    }
}
