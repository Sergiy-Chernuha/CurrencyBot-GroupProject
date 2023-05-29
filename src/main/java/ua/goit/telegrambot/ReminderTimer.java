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
}

