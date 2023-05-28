package ua.goit;

import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import ua.goit.telegrambot.MyTelBot;
import ua.goit.userssetting.Settings;
import ua.goit.userssetting.SettingsUser;

public class AppLauncher {

    public static void main(String[] args){
        TelegramBotsApi bolts;
        Settings clients = new SettingsUser();

        try {
            bolts = new TelegramBotsApi(DefaultBotSession.class);
            bolts.registerBot(new MyTelBot(clients));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
