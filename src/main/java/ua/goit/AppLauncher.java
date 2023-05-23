package ua.goit;

import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import ua.goit.telegrambot.MyTelBot;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AppLauncher {


    public static void main(String[] args){
        TelegramBotsApi bolts;
        try {
            bolts = new TelegramBotsApi(DefaultBotSession.class);
            bolts.registerBot(new MyTelBot());
            new AppLauncher().programStart();
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
    public void programStart() {
        ExecutorService executor = Executors.newFixedThreadPool(2);
        executor.execute(() -> new SecondThreadAlertTime());

        executor.shutdown();
    }
}
