package ua.goit.telegrambot;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ua.goit.userssetting.SettingUtils;

import java.time.LocalTime;

public class ReminderTimer extends Thread {
    private final MyTelBot myTelBot;
    private boolean timerOff = true;

    public ReminderTimer(MyTelBot myTelBot) {
        this.myTelBot = myTelBot;
    }

    public boolean isTimerOff() {
        return timerOff;
    }

    @Override
    public void run() {
        timerOff = false;
        while (myTelBot.getUserSettings().isReminderStarted()) {

            try {
                sleep(1000);
            } catch (InterruptedException e) {
                System.out.println("SecondThreadAlertTime is abort");
            }

            if (LocalTime.now().getHour() == myTelBot.getUserSettings().getReminderTime()
                    && LocalTime.now().getMinute() == 0) {

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
        sendMessage.setText(SettingUtils.getCurrentData(myTelBot.getUserSettings()));
        myTelBot.sendNextMessage(sendMessage);
    }
}
