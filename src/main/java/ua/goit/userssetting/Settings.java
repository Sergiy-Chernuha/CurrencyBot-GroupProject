package ua.goit.userssetting;

import java.io.IOException;
import java.util.Map;

public interface Settings {
    boolean contains (long chatId);
    void add (long chatId, ChatBotSettings client);
    ChatBotSettings getClient (long chatId);
    Map<Long, ChatBotSettings> getWriterListOfClients () throws IOException;
}
