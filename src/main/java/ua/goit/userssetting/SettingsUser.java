package ua.goit.userssetting;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonWriter;
import lombok.Data;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


@Data
public class SettingsUser implements Settings{

    private List<Client> clients;

    public SettingsUser() {
        this.clients = new ArrayList<>();
    }

    @Override
    public boolean contains(long chatId) {
        return clients.stream().anyMatch(client1 -> client1.getChatId() == chatId);
    }

    @Override
    public void add(long chatId, Client client) {
        for (int i = 0; i < clients.size(); i++) {
            if (clients.get(i).getChatId() == chatId) {
                clients.set(i, client);
                return;
            }
        }
        clients.add(client);
    }

    @Override
    public Client getClient(long chatId) {
        for (Client client : clients) {
            if (client.getChatId() == chatId) {
                return client;
            }
        }
        return null;
    }

    @Override
    public List<Client> getListOfClients() throws IOException {
        List.copyOf(clients);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonWriter jsonWriter = new JsonWriter(new FileWriter("src/main/resources/users.json"));
        String json = gson.toJson(clients);
        jsonWriter.jsonValue(json);
        jsonWriter.close();
        return clients;
    }

}
