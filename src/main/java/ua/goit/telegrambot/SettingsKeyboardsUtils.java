package ua.goit.telegrambot;

import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import ua.goit.userssetting.ChatBotSettings;
import ua.goit.userssetting.SettingUtils;

import java.net.URL;

public class SettingsKeyboardsUtils {

    boolean isThisNewSetting(String inputQueryMessage, Long chatId) {

        String bank = MyTelBot.settings.get(chatId).getBank();
        String numberOfDecimal = String.valueOf(MyTelBot.settings.get(chatId).getNumberOfDecimal());
        String currencies = MyTelBot.settings.get(chatId).getChoicesCurrencies().toString();
        String reminderTime = String.valueOf(MyTelBot.settings.get(chatId).getReminderTime());
        String reminderStarted = String.valueOf(MyTelBot.settings.get(chatId).isReminderStarted());

        return !bank.equals(inputQueryMessage) && !numberOfDecimal.equals(inputQueryMessage) &&
                !currencies.equals(inputQueryMessage) && !reminderTime.equals(inputQueryMessage) &&
                !reminderStarted.equals(inputQueryMessage);
    }

    void updateSettings(Long chatId) {
        MyTelBot myTelBot = new MyTelBot();

        if (!MyTelBot.settings.containsKey(chatId)) {
            URL url = MyTelBot.class.getResource("/Users/User" + chatId + ".json");
            if (url != null) {
                ChatBotSettings settingFromResource = SettingUtils.readUserSetting(chatId);

                if (settingFromResource.isReminderStarted()) {
                    ReminderTimer restartTimer = new ReminderTimer(myTelBot, chatId);
                    String cronExpression = "0 0 " + settingFromResource.getReminderTime() + " * * ?";

                    restartTimer.startTimer(cronExpression);
                    myTelBot.timers.put(chatId, restartTimer);
                } else {
                    settingFromResource.setReminderTime(0);
                }

                MyTelBot.settings.put(chatId, settingFromResource);

            } else {
                MyTelBot.settings.put(chatId, new ChatBotSettings(chatId));
            }
        }
    }

    private InlineKeyboardMarkup getChoiceOptionsKeyBoard() {
        String[] names = new String[]{"Знаки після коми", "Банк", "Валюти", "Час сповіщень"};
        String[] keys = new String[]{"decimals", "bank", "currencies", "reminders"};

        return KeyboardBuilder.getSimpleKeyboard(names, keys);
    }

    void sendChoiceOptionsMessage(SendMessage sendMessage) {
        InlineKeyboardMarkup inlineKeyboardMarkup = getChoiceOptionsKeyBoard();

        sendMessage.setText("Налаштування");
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);
        new MyTelBot().sendNextMessage(sendMessage);
    }

    void sendAnswerCallbackQuery(AnswerCallbackQuery answerCallbackQuery, boolean isNewSetting) {
        String callBackAnswer = isNewSetting ? "Налаштування оновлені." : "Ці налаштування вже встановлені.";

        answerCallbackQuery.setText(callBackAnswer);
        answerCallbackQuery.setShowAlert(false);
        answerCallbackQuery.setCacheTime(0);

        new MyTelBot().sendNextQuery(answerCallbackQuery);
    }
}
