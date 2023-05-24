package ua.goit;

import ua.goit.telegrambot.MyTelBot;
import ua.goit.userssetting.ChatBotSettings;

import java.util.Date;


@SuppressWarnings("ALL")
public class SecondThreadAlertTime extends Thread{
    private final ChatBotSettings options = new ChatBotSettings();
    private final MyTelBot myTelBot = new MyTelBot();
    Thread thread = new Thread();
    boolean bol = true;
    int time = 9;
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


//    public void run() {
//             setHour(date.getHours());
//
//             System.out.print(hour + " ");
//             System.out.print(time + " ");
//             System.out.println(bol);
//
//        while (bol == true){
//            try {
//                Thread.sleep(1000);
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }
//            if(time+4 == date.getHours()){
//                myTelBot.sendMessageFromThread();
//                setBol(false);
//            }
//        }
//
//    }

    @Override
    public void run() {
        setHour(date.getHours());

        int countMinutes = 0;

        while (bol == true){

             System.out.print(hour + " ");
             System.out.print(time + " ");
             System.out.println(bol);

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            countMinutes += 1;

            if(time < hour){
                if((time - hour) * 60 == countMinutes) myTelBot.sendMessageFromThread();
            } else if (time > hour) {
                if(((24 - time) + hour) * 60 == countMinutes) myTelBot.sendMessageFromThread();
            } else if (time == hour) {
                myTelBot.sendMessageFromThread();
            }
        }
    }
}
