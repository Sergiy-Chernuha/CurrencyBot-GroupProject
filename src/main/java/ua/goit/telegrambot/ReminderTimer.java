package ua.goit.telegrambot;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ua.goit.userssetting.SettingUtils;

public class ReminderTimer {
    private final MyTelBot myTelBot;
    private Scheduler scheduler;

    public ReminderTimer(MyTelBot myTelBot) {
        this.myTelBot = myTelBot;
    }

    public void startTimer(String cronExpression) {
        try {
            if (scheduler != null && !scheduler.isShutdown()) {
                scheduler.shutdown();
            }

            JobDetail job = JobBuilder.newJob(ReminderJob.class)
                    .withIdentity("reminderJob", "reminderGroup")
                    .build();

            job.getJobDataMap().put("myTelBot", myTelBot);

            CronTrigger trigger = TriggerBuilder.newTrigger()
                    .withIdentity("reminderTrigger", "reminderGroup")
                    .withSchedule(CronScheduleBuilder.cronSchedule(cronExpression))
                    .build();

            scheduler = new StdSchedulerFactory().getScheduler();
            scheduler.start();
            scheduler.scheduleJob(job, trigger);
        } catch(SchedulerException e){
            throw new RuntimeException(e);
        }

    }

    public void stopTimer() {
        try {
            if (scheduler != null && !scheduler.isShutdown()) {
                scheduler.shutdown();
            }
        } catch(SchedulerException e){
            throw new RuntimeException(e);
        }

    }

    public static class ReminderJob implements Job {
        @Override
        public void execute(JobExecutionContext context) {
            MyTelBot myTelBot = (MyTelBot) context.getJobDetail().getJobDataMap().get("myTelBot");
            sendReminderMessage(myTelBot);
        }

        private void sendReminderMessage(MyTelBot myTelBot) {
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(String.valueOf(myTelBot.getUserSettings().getChatId()));
            sendMessage.setText(SettingUtils.getCurrentData(myTelBot.getUserSettings()));
            myTelBot.sendNextMessage(sendMessage);
        }
    }
import java.time.LocalTime;

public class ReminderTimer extends Thread {
//    private final MyTelBot myTelBot;
//    private final Long chatId;
//    private boolean timerOff = true;
//
//    public ReminderTimer(MyTelBot myTelBot,Long chatId) {
//        this.myTelBot = myTelBot;
//        this.chatId = chatId;
//    }
//
//    public boolean isTimerOff() {
//        return timerOff;
//    }
//
//    @Override
//    public void run() {
//        timerOff = false;
//        while (myTelBot.getUserSettings(chatId).isReminderStarted()) {
//
//            try {
//                sleep(900);
//            } catch (InterruptedException e) {
//                System.out.println("SecondThreadAlertTime is abort");
//            }
//
//            LocalTime now = LocalTime.now();
//            if (now.getHour() == myTelBot.getUserSettings(chatId).getReminderTime()
//                    && now.getMinute() == 0
//                    && now.getSecond() == 1) {
//
//                sendReminderMessage();
//            }
//        }
//    }
//
//    private void sendReminderMessage() {
//        SendMessage sendMessage = new SendMessage();
//
//        sendMessage.setChatId(String.valueOf(chatId));
//        sendMessage.setText(SettingUtils.getCurrentData(myTelBot.getUserSettings(chatId)));
//        myTelBot.sendNextMessage(sendMessage);
//    }
}

