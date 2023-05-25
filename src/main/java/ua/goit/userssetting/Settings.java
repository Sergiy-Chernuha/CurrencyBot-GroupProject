package ua.goit.userssetting;

import java.io.IOException;
import java.util.List;
public interface Settings {
    boolean contains (long chatId);
    void add (long chatId, Client client);
    Client getClient (long chatId);
    List<Client>  getListOfClients () throws IOException;
}
