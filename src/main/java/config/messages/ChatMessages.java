package config.messages;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * This class hold track of the chat messages. The prefix of the messages is handled while adding the messages to
 * the {@link #messages message-map} in the {@link #addGermanMessage(String, String) addMessage} method.
 */
@Getter
public class ChatMessages {

    private static ChatMessages instance;

    @Setter
    private String prefix;
    private final Map<String, String> messages;

    private ChatMessages() {
        messages = new HashMap<>();
    }

    /**
     * This methods add or override an message to the {@link #messages message map} where all messages from the
     * {@link MessageConfig config} are saved in.
     * @param messageKey The key the message can get from, this way the messages can easy get localized
     * @param rawMessage The message that should displayed to the player
     */
    public void addGermanMessage(final @NonNull String messageKey, final @Nullable String rawMessage) {
        if (rawMessage == null)
            return;
        String convertedMessage = rawMessage.replace("%prefix%", prefix);
        messages.put(messageKey, convertedMessage);
    }

    /**
     * Only one object of this class should be existing. Therefor this class is private to obtains this object this method
     * must be used. When no object of this class is existing it creates a new one.
     * @return This class
     */
    public static ChatMessages getChatMessages() {
        if (instance == null) {
            instance = new ChatMessages();
        }
        return instance;
    }
}
