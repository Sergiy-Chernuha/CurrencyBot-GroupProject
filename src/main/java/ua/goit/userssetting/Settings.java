package ua.goit.userssetting;

import java.io.IOException;
import java.util.Map;

public interface Settings {
    boolean contains (long chatId);
    void add (long chatId, Client client);
    Client getClient (long chatId);
    Map<Long, Client> getListOfClients () throws IOException;
}
