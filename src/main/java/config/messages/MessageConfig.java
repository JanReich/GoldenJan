package config.messages;

import config.Config;
import lombok.NonNull;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.plugin.Plugin;
import util.FileHelper;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.Set;

public class MessageConfig extends Config {

    /**
     * {@inheritDoc}
     * @param plugin The {@link minecraft.goldenjan.GoldenJan main class} that extends the {@link Plugin plugin class}
     */
    public MessageConfig(final Plugin plugin) {
        super(plugin);
    }

    /**
     * {@inheritDoc}
     * Not implemented yet
     */
    @Override
    public void reloadConfig() {
        logger.info("Die Message Konfiguration wird neu geladen...");
        loadConfig(getConfigFile());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadConfig(final @NonNull File configFile) {
        try {
            configuration.load(configFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }

        ChatMessages chatMessages = ChatMessages.getChatMessages();
        chatMessages.setPrefix(configuration.getString("prefix"));

        Set<String> messageKeys = Objects.requireNonNull(configuration.getConfigurationSection("de.messages"))
                .getKeys(true);
        for (String key : messageKeys) {
            chatMessages.addGermanMessage("de.messages." + key,
                    configuration.getString("de.messages." + key));
        }
        logger.info("Alle Nachrichten wurden erfolgreich geladen!");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void createDefaults(final @NonNull File configFile) {
        FileHelper.createNewFile(configFile);

        configuration.set("prefix", "§7[§6§lGolden§3§lJan§7]");
        configuration.set("de.messages.no_permission", "%prefix% §cDu besitzt nicht benötigte Berechtigung!");
        configuration.set("de.messages.config_reloaded", "%prefix% §aKonfiguration wurde erfolgreich neu geladen!");
        configuration.set("de.messages.only_player", "%prefix% §cDieser Befehl darf nur von Spielern verwendet werden!");
        configuration.set("de.messages.free_slot", "%prefix% §cDu benötigst mindestens einen freien Slot!");
        configuration.set("de.messages.loading_error_image", "%prefix% §cBei dem Laden des Bildes ist etwas schief gegangen!");
        configuration.set("de.messages.image_too_big", "%prefix% §cDas von dir ausgewählte Bild ist zu groß!" +
                " Die maximalen Maße betragen: Breite - §e%width% §cPixel, Höhe - §e%height% §cPixel");
        configuration.set("de.messages.shulker_maps", "%prefix% §a Du hast eine Shulker mit %amount% Maps erhalten.");
        configuration.set("de.messages.maps_recived", "%prefix% §a Du hast %amount% Maps mit deinem Bild erhalten.");
        configuration.set("de.messages.skin_changed", "%prefix% §a Dein Skin wurde geändert!.");

        try {
            configuration.save(configFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        loadConfig(configFile);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected @NonNull File getConfigFile() {
        return FileHelper.getFile(plugin.getName() + "/messages.yaml");
    }
}
