package config.command;

import config.Config;
import lombok.NonNull;
import minecraft.goldenjan.GoldenJan;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.plugin.Plugin;
import util.FileHelper;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.Set;

public class CommandConfig extends Config {

    /**
     * {@inheritDoc}
     * @param plugin The {@link minecraft.goldenjan.GoldenJan main class} that extends the {@link Plugin plugin class}
     */
    public CommandConfig(final Plugin plugin) {
        super(plugin);
    }

    /**
     * {@inheritDoc}
     * Not implemented yet
     */
    @Override
    public void reloadConfig() {

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadConfig(@NonNull File configFile) {
        logger.info("Start loading commands...");
        try {
            configuration.load(configFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }

        Set<String> commands = Objects.requireNonNull(configuration.getConfigurationSection("commands"))
                .getKeys(false);
        for (String command : commands) {
            CommandRegistry commandRegistry = CommandRegistry.getCommandRegistry();
            commandRegistry.getRegisteredCommands().add(command);
            commandRegistry.getCommandSettings().put(command + ".permission",
                    configuration.getString("commands." + command + ".permission"));
            commandRegistry.getCommandSettings().put(command + ".default",
                    configuration.getBoolean("commands." + command +".default"));
            commandRegistry.getCommandSettings().put(command + ".aliases",
                    configuration.getStringList("commands." + command + ".aliases"));
            commandRegistry.getCommandSettings().put(command + ".description",
                    configuration.getString("commands." + command + ".description"));
            commandRegistry.getCommandSettings().put(command + ".usage",
                    configuration.getString("commands." + command + ".usage"));
        }
        ((GoldenJan) plugin).loadCommands();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void createDefaults(@NonNull File configFile) {
        FileHelper.createNewFile(configFile);

        configuration.set("commands.imageconverter.permission", "imageconverter.use");
        configuration.set("commands.imageconverter.default", false);
        String[] aliases = new String[2];
        aliases[0] = "converter";
        aliases[1] = "cc";
        configuration.set("commands.imageconverter.aliases", aliases);
        configuration.set("commands.imageconverter.description", "Transforms an url to an ingame image");
        configuration.set("commands.imageconverter.usage", "§c/%label% <URL> <Centered:boolean>");

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
        return FileHelper.getFile(plugin.getName() + "/command.yaml");
    }
}
