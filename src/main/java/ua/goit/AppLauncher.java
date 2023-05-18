package ua.goit;

import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import ua.goit.telegrambot.MyTelBot;

public class AppLauncher {
    public static void main(String[] args){
        TelegramBotsApi bolts;

        try {
            bolts = new TelegramBotsApi(DefaultBotSession.class);
            bolts.registerBot(new MyTelBot());
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
