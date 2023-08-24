package ua.goit;

import org.apache.log4j.BasicConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import ua.goit.telegrambot.MyTelBot;

public class AppLauncher {
    public static Logger logger = LoggerFactory.getLogger(AppLauncher.class);

    public static void main(String[] args){
        BasicConfigurator.configure();
        TelegramBotsApi bolts;

        try {
            bolts = new TelegramBotsApi(DefaultBotSession.class);
            bolts.registerBot(new MyTelBot());
        } catch (TelegramApiException e) {
            logger.error("Error occurred while registering the bot", e);
        }
    }
}
