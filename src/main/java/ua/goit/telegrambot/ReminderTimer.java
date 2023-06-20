package ua.goit.telegrambot;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ua.goit.userssetting.SettingUtils;

public class ReminderTimer {
    private static Scheduler scheduler;

    public static void startTimer(String cronExpression, Long chatId) {
        JobKey jobKey = JobKey.jobKey("reminderJob", "reminderGroup" + chatId + "");
        checkInit();

        try {
            if (scheduler != null && !scheduler.isShutdown() && scheduler.checkExists(jobKey)) {
                scheduler.deleteJob(jobKey);
            }

            JobDetail job = JobBuilder.newJob(ReminderJob.class)
                    .withIdentity("reminderJob", "reminderGroup" + chatId + "")
                    .build();

            CronTrigger trigger = TriggerBuilder.newTrigger()
                    .withIdentity("reminderTrigger", "reminderGroup" + chatId + "")
                    .withSchedule(CronScheduleBuilder.cronSchedule(cronExpression))
                    .build();

            job.getJobDataMap().put("chatId", chatId);
            scheduler.start();
            scheduler.scheduleJob(job, trigger);

        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    private static void checkInit() {
        if (scheduler == null) {
            try {
                scheduler = new StdSchedulerFactory()
                        .getScheduler();
            } catch (SchedulerException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void stopTimer(Long chatId) {
        JobKey jobKey = JobKey.jobKey("reminderJob", "reminderGroup" + chatId + "");

        try {
            if (scheduler != null && !scheduler.isShutdown() && scheduler.checkExists(jobKey)) {
                scheduler.deleteJob(jobKey);
            }
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    public static class ReminderJob implements Job {
        @Override
        public void execute(JobExecutionContext context) {
            Long chatId = (Long) context.getJobDetail().getJobDataMap().get("chatId");
            SendMessage sendMessage = new SendMessage();

            sendMessage.setChatId(chatId);
            sendMessage.setText(SettingUtils.getCurrentData(MyTelBot.getSettings().get(chatId)));
            new MyTelBot().sendNextMessage(sendMessage);
        }
    }
}
