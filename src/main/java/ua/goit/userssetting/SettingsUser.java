package ua.goit.userssetting;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonWriter;
import lombok.Data;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


@Data
public class SettingsUser implements Settings{

    private Map<Long, Client> clients;

    public SettingsUser() {
        this.clients = new HashMap<>();
    }

    @Override
    public boolean contains(long chatId) {
        return clients.containsKey(chatId);
    }

    @Override
    public void add(long chatId, Client client) {
        clients.put(chatId, client);
    }

    @Override
    public Client getClient(long chatId) {
        return clients.get(chatId);
    }

    @Override
    public Map<Long, Client> getListOfClients() throws IOException {
        Map.copyOf(clients);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonWriter jsonWriter = new JsonWriter(new FileWriter("src/main/resources/users.json", false));
        String json = gson.toJson(clients);
        jsonWriter.jsonValue(json);
        jsonWriter.close();
        return clients;
    }

}
