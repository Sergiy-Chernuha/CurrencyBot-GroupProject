package ua.goit;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ua.goit.telegrambot.MyTelBot;

import java.time.LocalTime;

public class ReminderTimer extends Thread {
    private final MyTelBot myTelBot;

    public ReminderTimer(MyTelBot myTelBot) {
        this.myTelBot = myTelBot;
    }

    @Override
    public void run() {
        while (myTelBot.getUserSettings().isReminderStarted()) {

            try {
                sleep(1000);
            } catch (InterruptedException e) {
                System.out.println("SecondThreadAlertTime is abort");
            }

            if (LocalTime.now().getHour() == myTelBot.getUserSettings().getReminderTime()
                    && LocalTime.now().getMinute() == 0) {
//                if (LocalTime.now().getMinute() == 43) {
//                SettingUtils
                sendReminderMessage();
                try {
                    sleep(60000);
                } catch (InterruptedException e) {
                    System.out.println("SecondThreadAlertTime is abort");
                }
            }
        }
    }

    private void sendReminderMessage() {
        SendMessage sendMessage = new SendMessage();

        sendMessage.setChatId(String.valueOf(myTelBot.getUserSettings().getChatId()));
        sendMessage.setText(myTelBot.getCurrentData());
        myTelBot.sendNextMessage(sendMessage);
    }
}
