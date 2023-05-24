package ua.goit;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ua.goit.telegrambot.MyTelBot;
import ua.goit.userssetting.ChatBotSettings;

import java.time.LocalTime;

public class SecondThreadAlertTime extends Thread {
    //    private final ChatBotSettings options;
    private final MyTelBot myTelBot;

    public SecondThreadAlertTime(MyTelBot myTelBot) {
        this.myTelBot = myTelBot;
    }


    @Override
    public void run() {

        while (myTelBot.getUserSettings().isAlerts()) {
            try {
                sleep(1000);
                System.out.println("not time");
            } catch (InterruptedException e) {
                System.out.println("SecondThreadAlertTime is abort");
            }


            if (LocalTime.now().getHour() == myTelBot.getUserSettings().getAlertTime()
                    && LocalTime.now().getMinute() == 0) {
//                if (LocalTime.now().getMinute() == 43) {
//                SettingUtils
                System.out.println("this is true time");
                sendAlertMessage();
                    try {
                        sleep(60000);
                        System.out.println("not time");
                    } catch (InterruptedException e) {
                        System.out.println("SecondThreadAlertTime is abort");
                    }
            }
        }

        System.out.println("end second thread");
    }

    private void sendAlertMessage() {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(myTelBot.getUserSettings().getChatId()));
        sendMessage.setText(myTelBot.getCurrentData());
//        try {
        myTelBot.sendNextMessage(sendMessage);
//            execute(sendMessage);
//        } catch (TelegramApiException e) {
//            e.printStackTrace();
//        }
//        sendNextMessage(sendMessage);
    }

}
