package ua.goit.telegrambot;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ua.goit.userssetting.SettingUtils;

import java.time.LocalTime;

public class ReminderTimer extends Thread {
    private final MyTelBot myTelBot;
    private final Long chatId;
    private boolean timerOff = true;

    public ReminderTimer(MyTelBot myTelBot,Long chatId) {
        this.myTelBot = myTelBot;
        this.chatId = chatId;
    }

    public boolean isTimerOff() {
        return timerOff;
    }

    @Override
    public void run() {
        timerOff = false;
        while (myTelBot.getUserSettings(chatId).isReminderStarted()) {

            try {
                sleep(900);
            } catch (InterruptedException e) {
                System.out.println("SecondThreadAlertTime is abort");
            }

            LocalTime now = LocalTime.now();
            if (now.getHour() == myTelBot.getUserSettings(chatId).getReminderTime()
                    && now.getMinute() == 0
                    && now.getSecond() == 1) {

                sendReminderMessage();
            }
        }
    }

    private void sendReminderMessage() {
        SendMessage sendMessage = new SendMessage();

        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText(SettingUtils.getCurrentData(myTelBot.getUserSettings(chatId)));
        myTelBot.sendNextMessage(sendMessage);
    }
}
