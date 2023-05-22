package ua.goit;

import ua.goit.telegrambot.MyTelBot;
import ua.goit.userssetting.ChatBotSettings;

import java.util.Date;


public class SecondThreadAlertTime extends Thread{
    private final ChatBotSettings options = new ChatBotSettings();
    private final MyTelBot myTelBot = new MyTelBot();

    boolean bol;
    int time;
    int hour;
    Date date = new Date();

    public void setBol(boolean bol) {
        this.bol = bol;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    @Override
    public void run() {

        while (true){
            try {
                Thread.sleep(1000);

            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

             setTime(options.getAlertTime());
             setBol(options.isAlerts());
             setHour(date.getHours());

             System.out.print(hour + " ");
             System.out.print(time + " ");
             System.out.println(bol);

             if((hour == time) && (bol == true)){
                 myTelBot.sendMessageFromThread();
             }
        }
    }
}
