package ua.goit.userssetting;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonWriter;
import lombok.Data;

import java.io.*;
import java.util.HashMap;
import java.util.Map;


@Data
public class SettingsUser implements Settings {
    private Map<Long, ChatBotSettings> clients;

    public SettingsUser() {
        this.clients = new HashMap<>();
    }

    @Override
    public boolean contains(long chatId) {
        return clients.containsKey(chatId);
    }

    @Override
    public void add(long chatId, ChatBotSettings client) {
        clients.put(chatId, client);
    }

    @Override
    public ChatBotSettings getClient(long chatId) {
        return clients.get(chatId);
    }

    @Override
    public Map<Long, ChatBotSettings> getWriterListOfClients() throws IOException {
        Map.copyOf(clients);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonWriter jsonWriter = new JsonWriter(new FileWriter("src/main/resources/users.json" + clients.keySet(), false));
        String json = gson.toJson(clients);
        jsonWriter.jsonValue(json);
        jsonWriter.close();
        return clients;
    }

    public Map<Long, ChatBotSettings> getObtainingClientSettings() throws FileNotFoundException {
        ChatBotSettings client = new Gson().fromJson("src/main/resources/users.json" + clients.keySet(), ChatBotSettings.class);
        System.out.println("client = " + client);

        return clients;


    }

}
