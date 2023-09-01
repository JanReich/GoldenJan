package commads;

import config.maps.CachedMaps;
import config.maps.MapConfig;
import config.messages.ChatMessages;
import logger.MyLogger;
import lombok.SneakyThrows;
import maps.DisplayData;
import maps.ImageRenderer;
import maps.MapHelper;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.ShulkerBox;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapView;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import util.ImageHelper;

import java.awt.image.BufferedImage;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * This class represents the <code>/imageconvert</code> command and his aliases
 * @author Janx_X
 * @since 1.0
 */
public class ImageConvert extends Command {

    private final Plugin plugin;
    private final MyLogger logger;
    private final MapConfig mapConfig;
    private final CachedMaps cachedMaps;
    private final BufferedImage emptyImage;
    private final ChatMessages chatMessages;

    private final boolean defaultPermission;

    /**
     * The constructor of the class handels the command-registration. The {@link Command superclass} need some of the
     * parameters as information for handling the command. After the superclass was created the constructor adds this
     * class to the {@link CommandMap CommandMap}. Through the command is added in runtime to the {@link CommandMap CommandMap}
     * there is no need to add the command in the plugin.yml
     * Bevor the command is registered an empty {@link BufferedImage image} is created, that will needed as a background for
     * transparent images that don't fill the complete map. This is explained in the {@link ImageRenderer ImageRenderer} class
     *
     * @param root The name of the command as string
     * @param description The description of the command, is used in the <code>/help</code> command. Only the {@param root root} is displayed in the help-message the {@param aliases aliases} are not displayed in the hep-message
     * @param usage The string with the command-syntax a {@link CommandSender sender} gets if he used an incorrect syntax of the command.
     * @param aliases A {@link List list} of strings with synonyms of the command that can used instead of the {@param root root}
     * @param mapConfig The {@link MapConfig MapConfig} holds track of all loaded maps and the standard values of maps
     * @param defaultPermission This boolean describes if the command is usable by everyone
     * @param plugin The {@link minecraft.goldenjan.GoldenJan main class} that extends the {@link Plugin plugin class}
     */
    @SneakyThrows
    public ImageConvert(final @NotNull String root, final @NotNull String description, final @NotNull String usage,
                        final List<String> aliases, final @NotNull MapConfig mapConfig, final boolean defaultPermission,
                        final @NotNull Plugin plugin) {
        super(root, description, usage, aliases);

        logger = MyLogger.getLogger();
        cachedMaps = CachedMaps.getCachedMaps();
        emptyImage = new BufferedImage(CachedMaps.getCachedMaps().getMAP_SCALE(),
                CachedMaps.getCachedMaps().getMAP_SCALE(), BufferedImage.TYPE_INT_ARGB);
        chatMessages = ChatMessages.getChatMessages();
        this.plugin = plugin;
        this.mapConfig = mapConfig;
        this.defaultPermission = defaultPermission;

        //Register command at the server //todo: if there are more than one commands outsource this code-sniped
        Field commandMap = plugin.getServer().getClass().getDeclaredField("commandMap");
        commandMap.setAccessible(true);
        ((CommandMap) commandMap.get(plugin.getServer())).register(plugin.getName(), this);
    }

    /**
     * This method is triggered by the "imageconverter" command and its {@link Command#getAliases()} aliases}. The
     * {@link Command#getUsage() usage} of this command is: <code>/imageconvert url:string centered:boolean</code>.
     *
     * First of all this methods checks the correct syntax, permission and the correct {@link CommandSender sender}.
     * The {@link Player player} needs at least one free {@link org.bukkit.inventory.PlayerInventory inventory} space
     * else the {@link Player player} gets an error-message.
     *
     * @param sender The sender that performing the command
     * @param label The string of the command that is performed by the {@link CommandSender sender}
     * @param args The arguments followed by the {@param label label} in a string[]
     * @return always false
     */
    @Override
    public boolean execute(final @NotNull CommandSender sender, final @NotNull String label,
                           final @NotNull String[] args) {
        if (!sender.hasPermission(Objects.requireNonNull(getPermission())) || defaultPermission) {
            sender.sendMessage(chatMessages.getMessages().get("no_permission"));
            logger.info("Der Sender: " + sender + " hat den Befehl " + label + " versucht auszuführen, hat dazu aber" +
                    "keine Berechtigung.");
            return false;
        }
        if (!(sender instanceof Player player)) {
            sender.sendMessage(chatMessages.getMessages().get("de.messages.only_player"));
            logger.info("Der Sender: " + sender + " hat den Befehl " + label + " versucht auszuführen, hat dieser Befehlt" +
                    "ist nur für Spieler!");
            return false;
        }
        if (args.length == 0 || args.length > 2) {
            sender.sendMessage(chatMessages.getPrefix() + " " + getUsage().replace("%label%", label));
            logger.info("Der Spieler: " + player.getName() + "[" + player.getUniqueId() + "] hat die Usage bekommen: "
                    + getUsage().replace("%label%", label));
            return false;
        }
        int emptyInventorySlots = getSpaceInInventory(player);
        if (emptyInventorySlots <= 0) {
            player.sendMessage(chatMessages.getMessages().get("de.messages.free_slot"));
            logger.info("Der Spieler: " + player.getName() + "[" + player.getUniqueId() + "] hat den Befehl " + label +
                    "ausgeführt, aber hat keinen freien Slot im Inventar.");
            return false;
        }

        boolean tempCentered = false;
        if (args.length == 2) {
            if (args[1].equalsIgnoreCase("true") || args[1].equalsIgnoreCase("false")) {
                tempCentered = Boolean.parseBoolean(args[1]);
            } else {
                sender.sendMessage(chatMessages.getPrefix() + " " + getUsage().replace("%label%", label));
                logger.info("Der Spieler: " + player.getName() + "[" + player.getUniqueId()
                        + "] hat die Usage bekommen: " + (getUsage().replace("%label%", label)));
                return false;
            }
        }

        //loading image async and handle it
        final boolean centered = tempCentered;
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            logger.info("Starting async handling of the image - initiated by: " + player.getName() + "[" +
                    player.getUniqueId() + "]");
            handleImageConvertAsync(player, label, args, emptyInventorySlots, centered);
        });
        return false;
    }

    private void handleImageConvertAsync(final @NotNull Player player, final @NotNull String label,
                                         final String @NotNull [] args, final int emptyInventorySlots,
                                         final boolean centered) {
        ImageHelper imageHelper = new ImageHelper();
        if (!imageHelper.loadImage(args[0]) || imageHelper.getImage() == null) {
            player.sendMessage(chatMessages.getMessages().get("de.messages.loading_error_image"));
            logger.info("Der Spieler: " + player.getName() + "[" + player.getUniqueId() + "] hat den Befehl " + label +
                    "ausgeführt und beim laden ist etwas schief gelaufen. URL: " + args[0]);
            return;
        }
        //checking image size
        BufferedImage toDisplay = imageHelper.getImage();
        if (toDisplay.getWidth() > cachedMaps.getMAX_IMAGE_WIDTH() ||
                toDisplay.getHeight() > cachedMaps.getMAX_IMAGE_HEIGHT()) {
            sendImageTooLarge(player, label);
            return;
        }
        DisplayData displayData = MapHelper.splitImage(toDisplay, centered);
        if (displayData.images().length * displayData.images()[0].length > 27) {
            sendImageTooLarge(player, label);
            return;
        }

        List<Integer> mapIds = new ArrayList<>();
        List<ItemStack> items = new ArrayList<>();
        for (int col = 0; col < displayData.images().length; col++) {
            for (int row = 0; row < displayData.images()[0].length; row++) {
                MapView view = Bukkit.createMap(Bukkit.getWorlds().get(0));
                view.getRenderers().clear();
                int offsetX = row != 0 ? 0 : displayData.offsetX();
                int offsetY = col != 0 ? 0 : displayData.offsetY();

                ImageRenderer imageRenderer = new ImageRenderer(displayData.images()[col][row], emptyImage, offsetX, offsetY);
                view.addRenderer(imageRenderer);

                mapIds.add(view.getId());
                cachedMaps.addCachedMap(view.getId(), displayData.images()[col][row], offsetX, offsetY);

                ItemStack map = getMap(view);
                items.add(map);
            }
        }
        mapConfig.saveMapImage(args[0], centered, mapIds.stream().mapToInt(i -> i).toArray());
        if (emptyInventorySlots < items.size()) {
            ItemStack shulkerBox = createShulkerBox(items);
            player.getInventory().addItem(shulkerBox);
            player.sendMessage(chatMessages.getMessages().get("de.messages.shulker_maps")
                    .replace("%amount%", items.size() + ""));
            logger.info("Der Spieler: " + player.getName() + "[" + player.getUniqueId() + "] hat den Befehl " + label +
                    "ausgeführt und eine Shulkerbox mit " + items.size() + " Maps erhalten");
        } else {
            for (ItemStack item : items) {
                player.getInventory().addItem(item);
            }
            player.sendMessage(chatMessages.getMessages().get("de.messages.maps_recived")
                    .replace("%amount%", items.size() + ""));
            logger.info("Der Spieler: " + player.getName() + "[" + player.getUniqueId() + "] hat den Befehl " + label +
                    "ausgeführt und " + items.size() + " Maps erhalten");
        }
    }

    @Override
    public @NotNull List<String> tabComplete(final @NotNull CommandSender sender, final @NotNull String label,
                                             final String[] args) throws IllegalArgumentException {
        List<String> toComplete = new ArrayList<>();

        if (args.length == 1) {
            toComplete.add("<URL>");
        } else if (args.length == 2) {
            toComplete.add("true");
            toComplete.add("false");
        }
        return toComplete;
    }

    private void sendImageTooLarge(final @NotNull Player player, final @NotNull String label) {
        player.sendMessage(chatMessages.getMessages().get("de.messages.loading_error_image")
                .replace("%width%", cachedMaps.getMAX_IMAGE_WIDTH() + "")
                .replace("%height%", cachedMaps.getMAX_IMAGE_HEIGHT() + ""));
        logger.info("Der Spieler: " + player.getName() + "[" + player.getUniqueId() + "] hat den Befehl " + label +
                "ausgeführt, aber das ausgewählt Bild hatte die falsche größe.");
    }

    @NotNull
    private ItemStack getMap(final @NotNull MapView view) {
        ItemStack map = new ItemStack(Material.FILLED_MAP);
        MapMeta mapMeta = (MapMeta) map.getItemMeta();
        mapMeta.setMapView(view);
        map.setItemMeta(mapMeta);
        return map;
    }

    private int getSpaceInInventory(final @NotNull Player player) {
        int emptySlots = 0;
        for (ItemStack content : player.getInventory().getStorageContents()) {
            if (content == null || content.getType() == Material.AIR) {
                emptySlots++;
            }
        }
        return emptySlots;
    }

    private ItemStack createShulkerBox(final List<ItemStack> items) {
        ItemStack shulker = new ItemStack(Material.RED_SHULKER_BOX);
        BlockStateMeta blockStateMeta = (BlockStateMeta) shulker.getItemMeta();
        ShulkerBox shulkerBox = (ShulkerBox) blockStateMeta.getBlockState();
        Inventory shulkerInventory = shulkerBox.getInventory();
        for (int i = 0; i < items.size(); i++) {
            shulkerInventory.setItem(i, items.get(i));
        }
        blockStateMeta.setBlockState(shulkerBox);
        shulker.setItemMeta(blockStateMeta);
        shulkerBox.update();
        return shulker;
    }
}
