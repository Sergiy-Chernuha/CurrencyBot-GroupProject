package ua.goit.telegrambot;

import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import ua.goit.telegrambot.buttonmenus.ButtonValue;
import ua.goit.userssetting.ChatBotSettings;

public class SettingsKeyboardsUtils {

   public static boolean isThisNewSetting(String inputQueryMessage, ChatBotSettings userSettings) {

        String banks = userSettings.getBanks().toString();
        String numberOfDecimal = String.valueOf(userSettings.getNumberOfDecimal());
        String currencies = userSettings.getChoicesCurrencies().toString();
        String reminderTime = String.valueOf(userSettings.getReminderHours());
        String reminderStarted = String.valueOf(userSettings.isReminderStarted());

        return !banks.equals(inputQueryMessage) && !numberOfDecimal.equals(inputQueryMessage) &&
                !currencies.equals(inputQueryMessage) && !reminderTime.equals(inputQueryMessage) &&
                !reminderStarted.equals(inputQueryMessage);
    }

    private InlineKeyboardMarkup getChoiceOptionsKeyBoard() {
        ButtonValue[] buttons = new ButtonValue[]{
                new ButtonValue("Знаки після коми", "decimals")
                , new ButtonValue("Банк", "bank")
                , new ButtonValue("Валюти", "currencies")
                , new ButtonValue("Час сповіщень", "reminders")};

        return KeyboardBuilder.getSimpleKeyboard(buttons);
    }

    public void sendChoiceOptionsMessage(SendMessage sendMessage) {
        InlineKeyboardMarkup inlineKeyboardMarkup = getChoiceOptionsKeyBoard();

        sendMessage.setText("Налаштування");
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);

        new MyTelBot().sendNextMessage(sendMessage);
    }

    public void sendAnswerCallbackQuery(AnswerCallbackQuery answerCallbackQuery, boolean isNewSetting) {
        String callBackAnswer = isNewSetting ? "Налаштування оновлені." : "Ці налаштування вже встановлені.";

        answerCallbackQuery.setText(callBackAnswer);
        answerCallbackQuery.setShowAlert(false);
        answerCallbackQuery.setCacheTime(0);

        new MyTelBot().sendNextQuery(answerCallbackQuery);
    }
}
