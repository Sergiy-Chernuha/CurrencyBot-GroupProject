package ua.goit.telegrambot;//package ua.goit.telegrambot;
//
//import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
//import ua.goit.userssetting.SettingUtils;
//
//import java.time.LocalTime;
//import java.util.concurrent.Executors;
//import java.util.concurrent.ScheduledExecutorService;
//import java.util.concurrent.ScheduledFuture;
//import java.util.concurrent.TimeUnit;
//
//public class ReminderTimer {
//    private final MyTelBot myTelBot;
//    private final String cronExpression;
//    private ScheduledExecutorService executorService;
//    private ScheduledFuture<?> scheduledFuture;
//
//    public ReminderTimer(MyTelBot myTelBot, String cronExpression) {
//        this.myTelBot = myTelBot;
//        this.cronExpression = cronExpression;
//    }
//
//    public void startTimer() {
//        executorService = Executors.newSingleThreadScheduledExecutor();
//        scheduledFuture = executorService.scheduleAtFixedRate(this::checkReminder, 0, 1, TimeUnit.MINUTES);
//    }
//
//    public void stopTimer() {
//        scheduledFuture.cancel(true);
//        executorService.shutdown();
//    }
//
//    private void checkReminder() {
//        if (isTimeToRemind()) {
//            sendReminderMessage();
//        }
//    }
//
//    private boolean isTimeToRemind() {
//        LocalTime now = LocalTime.now();
//        String currentTime = now.getHour() + ":" + now.getMinute();
//        return cronExpression.equals(currentTime);
//    }
//
//    private void sendReminderMessage() {
//        SendMessage sendMessage = new SendMessage();
//        sendMessage.setChatId(String.valueOf(myTelBot.getUserSettings().getChatId()));
//        sendMessage.setText(SettingUtils.getCurrentData(myTelBot.getUserSettings()));
//        myTelBot.sendNextMessage(sendMessage);
//    }
//}
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

    public void startTimer(String cronExpression) throws SchedulerException {
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
    }

    public void stopTimer() throws SchedulerException {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
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
}

