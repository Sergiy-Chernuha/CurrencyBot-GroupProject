package ua.goit;

import ua.goit.telegrambot.MyTelBot;
import ua.goit.userssetting.ChatBotSettings;

import java.util.Date;


@SuppressWarnings("ALL")
public class SecondThreadAlertTime extends Thread{
    private final ChatBotSettings options = new ChatBotSettings();
    private final MyTelBot myTelBot = new MyTelBot();
    boolean bol = true;
    int time;
    int hour;
    Date date = new Date();

    public void setHour(int hour) {
        this.hour = hour;
    }

    public void setBol(boolean bol) {
        this.bol = bol;
    }

    public void setTime(int time) {
        this.time = time;
    }

    @Override
    public void run() {
             setHour(date.getHours());

             System.out.print(hour + " ");
             System.out.print(time + " ");
             System.out.println(bol);

        while (bol == true){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            if(time == date.getHours()){
                myTelBot.sendMessageFromThread();
                setBol(false);
            }
        }

    }

    public void runnble() throws InterruptedException {
        while (true){
            Thread.sleep(1000);
            new SecondThreadAlertTime().run();
        }
    }
}
