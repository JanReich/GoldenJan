package Listener;

import com.destroystokyo.paper.event.player.PlayerConnectionCloseEvent;
import com.destroystokyo.paper.profile.ProfileProperty;
import config.messages.ChatMessages;
import database.MySQL;
import logger.MyLogger;
import lombok.NonNull;
import minecraft.goldenjan.GoldenJan;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.profile.PlayerTextures;
import org.jetbrains.annotations.Nullable;

import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * This class handels the join and quit events. In the {@link AsyncPlayerPreLoginEvent asyncLoginEvent} are all database
 * request handled for example loading player data or skin data.
 */
public class ConnectionEvents implements Listener {

    private final MySQL mySQL;
    private final MyLogger logger;
    private final Map<UUID, Long> loginTime;
    private final List<UUID> changedSkin;

    /**
     * In the constructor the fields are allocated
     * @param mySQL The connection to the database and database information
     */
    public ConnectionEvents(final MySQL mySQL) {
        this.mySQL = mySQL;
        logger = MyLogger.getLogger();
        loginTime = new HashMap<>();
        changedSkin = new ArrayList<>();
    }

    /**
     * In this event the player data will processed. First the IP-Address is saved in the database if is not already saved
     * Then the values from the player Skin will saved, when they have changed the player will notified.
     * @param event The Eventinformation to process the players data.
     */
    @EventHandler
    public void onLogin(final AsyncPlayerPreLoginEvent event) {
        switch (event.getLoginResult()) {
            case ALLOWED -> {
                logger.info("Ein Spieler mit der IP-Adresse: '" + event.getAddress() +  "' versucht zu joinen!" );
                if (!mySQL.isConnected()) {
                    logger.error("Beim versuch mit der Datenbank zu arbeiten ist ein Fehler aufgetreten, es konnte keine aktive Verbindung gefunden werden!");
                    return;
                }
                try {
                    PreparedStatement ps = mySQL.getConnection().prepareStatement("SELECT UUID FROM ips WHERE UUID = ? AND ip = ?");
                    ps.setString(1, event.getUniqueId().toString());
                    ps.setString(2, event.getAddress().toString());
                    ResultSet resultSet = ps.executeQuery();
                    if (!resultSet.next()) {
                        ps = mySQL.getConnection().prepareStatement("INSERT INTO ips (UUID, IP) VALUES (?, ?)");
                        ps.setString(1, event.getUniqueId().toString());
                        ps.setString(2, event.getAddress().toString());
                        ps.executeUpdate();
                        ps.close();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                //getting Skin-data from event
                PlayerTextures.SkinModel skinModel = event.getPlayerProfile().getTextures().getSkinModel();
                URL skinURL = event.getPlayerProfile().getTextures().getSkin();
                try {
                    PreparedStatement ps = mySQL.getConnection().prepareStatement("SELECT * FROM skins WHERE UUID = ?");
                    ps.setString(1, event.getUniqueId().toString());
                    ResultSet resultSet = ps.executeQuery();
                    boolean differenz = false;
                    boolean hasResult = resultSet.next();
                    if (hasResult) {
                        if (!resultSet.getString("skinModel").equals(skinModel.toString())
                                || !Objects.requireNonNull(skinURL).toString()
                                .equals(resultSet.getString("skinURL"))) {
                            differenz = true;
                        }
                    } else {
                        logger.info("Der Spieler mit der UUID " + event.getUniqueId() + " hat zum ersten Mal den Server betreten!");
                    }
                    if (differenz || !hasResult) {
                        changedSkin.add(event.getUniqueId());
                        ps = mySQL.getConnection().prepareStatement("INSERT INTO skins (UUID, skinModel, skinURL)" +
                                " VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE skinModel = ?, skinURL = ?");
                        ps.setString(1, event.getUniqueId().toString());
                        ps.setString(2, skinModel.toString());
                        ps.setString(3, Objects.requireNonNull(skinURL).toString());
                        ps.setString(4, skinModel.toString());
                        ps.setString(5, Objects.requireNonNull(skinURL).toString());
                        ps.executeUpdate();
                        ps.close();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            case KICK_BANNED -> logger.info("Ein gebannter Spieler mit der IP-Adresse: '"
                    + event.getAddress() +  "' versuchte zu joinen!" );
            default -> logger.info("Ein Spieler mit der IP-Adresse: '"
                    + event.getAddress() +  "' versucht zu joinen, wurde aber gekickt!" );
        }
    }

    /**
     * This event starts the timer when the {@link Player player} joined to the server and when the player changed his
     * skin he gets an Message
     * @param event The information from the {@link PlayerJoinEvent event}
     */
    @EventHandler
    public void onLogin(final PlayerJoinEvent event) {
        loginTime.put(event.getPlayer().getUniqueId(), System.currentTimeMillis());
        if (changedSkin.contains(event.getPlayer().getUniqueId())) {
            changedSkin.remove(event.getPlayer().getUniqueId());
            logger.info("Der Spieler " + event.getPlayer().getName() + "[" + event.getPlayer().getUniqueId() + "]" +
                    " hat seinen Skin geändert");
            event.getPlayer().sendMessage(ChatMessages.getChatMessages().getMessages().get("de.messages.skin_changed"));
        }
    }

    /**
     * This method calculates the time the player has played on the server. When the server crashes or stops
     * this event will not called! To track the played time when the server stops or crashes
     * @param event The Event that was triggered by quited player
     */
    @EventHandler
    public void onLogout(final PlayerQuitEvent event) {
        calculatePlayedTime(event.getPlayer().getName(), event.getPlayer().getUniqueId());
    }

    /**
     * @see #onLogout(PlayerQuitEvent)
     * @param event The event when an player connection is closed
     */
    @EventHandler
    public void onServerClosed(final PlayerConnectionCloseEvent event) {
        logger.info("closed connection");
    }

    //todo: call on servercrash
    /**
     * This method is called from the {@link GoldenJan#onDisable() plugin.disable()} when the server stops. When the
     * server stops the time from all players that are currently online is saved into the {@link MyLogger log}
     */
    public void onServerClosed() {
        for (UUID uuid : loginTime.keySet()) {
            Player player = Bukkit.getPlayer(uuid);
            String name = player != null ? player.getName() : "notFound";
            calculatePlayedTime(name, uuid);
        }
    }

    private void calculatePlayedTime(final @Nullable String name, final @NonNull UUID uniqueId) {
        //Alternative the time a player have played calculated by player.getPlayerTime() but this value is in ticks played
        long timeJoined = loginTime.get(uniqueId);
        loginTime.remove(uniqueId);
        long timeLeaved = System.currentTimeMillis();
        long timePlayedInSeconds = (timeLeaved - timeJoined) / 1000;

        String duration;
        if (timePlayedInSeconds >= 3600) {
            duration = (timePlayedInSeconds / 3600) + " Stunden, " + (timePlayedInSeconds % 3600 / 60) +
                    " Minuten und " + (timePlayedInSeconds % 3600 % 60) + " Sekunden";
        } else if (timePlayedInSeconds >= 60) {
            duration = (timePlayedInSeconds / 60) + " Minuten und " + (timePlayedInSeconds % 60) + " Sekunden";
        } else {
            duration = timePlayedInSeconds + " Sekunden";
        }
        logger.info("Der Spieler: " + name + " [UUID: " + uniqueId
                + "] war: %play_duration% online.".replace("%play_duration%", duration));
    }
}
