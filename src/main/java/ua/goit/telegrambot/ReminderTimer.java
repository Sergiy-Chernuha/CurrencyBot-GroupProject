package ua.goit.telegrambot;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ua.goit.userssetting.SettingUtils;

public class ReminderTimer {
    private final MyTelBot myTelBot;
    private final Long chatId;
    private Scheduler scheduler;

    public ReminderTimer(MyTelBot myTelBot, Long chatId) {
        this.myTelBot = myTelBot;
        this.chatId = chatId;
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
            job.getJobDataMap().put("chatId", chatId);

            CronTrigger trigger = TriggerBuilder.newTrigger()
                    .withIdentity("reminderTrigger", "reminderGroup")
                    .withSchedule(CronScheduleBuilder.cronSchedule(cronExpression))
                    .build();

            scheduler = new StdSchedulerFactory().getScheduler();
            scheduler.start();
            scheduler.scheduleJob(job, trigger);
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    public void stopTimer() {
        try {
            if (scheduler != null && !scheduler.isShutdown()) {
                scheduler.shutdown();
            }
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    public static class ReminderJob implements Job {
        @Override
        public void execute(JobExecutionContext context) {
            MyTelBot myTelBot = (MyTelBot) context.getJobDetail().getJobDataMap().get("myTelBot");
            Long chatId = (Long) context.getJobDetail().getJobDataMap().get("chatId");
            SendMessage sendMessage = new SendMessage();

            sendMessage.setChatId(chatId);
            sendMessage.setText(SettingUtils.getCurrentData(myTelBot.getUserSetting(chatId)));
            new MyTelBot().sendNextMessage(sendMessage);
        }
    }
}
