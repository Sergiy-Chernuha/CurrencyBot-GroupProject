package ua.goit.telegrambot;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import ua.goit.banks.Currencies;

import ua.goit.telegrambot.buttonmenus.BankMenu;
import ua.goit.telegrambot.buttonmenus.CurrencyMenu;
import ua.goit.telegrambot.buttonmenus.DecimalMenu;
import ua.goit.telegrambot.buttonmenus.RemindersMenu;
import ua.goit.userssetting.ChatBotSettings;
import ua.goit.userssetting.SettingUtils;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

public class MyTelBot extends TelegramLongPollingBot {

    private static final Map<Long, ChatBotSettings> settings = new HashMap<>();

    public static Map<Long, ChatBotSettings> getSettings() {
        return settings;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            Message message = update.getMessage();
            Long chatId = update.getMessage().getChatId();
            SettingUtils.updateSettings(chatId);

            if (message.hasText()) {
                String text = message.getText();
                SendMessage sendMessage = new SendMessage();

                sendMessage.setChatId(String.valueOf(chatId));

                switch (text) {
                    case "/start" -> sendNextMessage(new TelegramBotUtils().sendHelloMessage(chatId));
                    case "Отримати інфо" -> {
                        sendMessage.setText(SettingUtils.getCurrentData(settings.get(chatId)));
                        sendNextMessage(sendMessage);
                    }
                    case "Налаштування" -> new SettingsKeyboardsUtils().sendChoiceOptionsMessage(sendMessage);
                    case "/end" -> sendNextMessage(new TelegramBotUtils().sendEndMessage(chatId));
                }
            }
        } else if (update.hasCallbackQuery()) {
            Long chatId = update.getCallbackQuery().getMessage().getChatId();
            Integer messageId = update.getCallbackQuery().getMessage().getMessageId();

            SettingUtils.updateSettings(chatId);

            String inputQueryMessage = String.valueOf(update.getCallbackQuery().getData());

            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(chatId);

            EditMessageReplyMarkup editMessage = new EditMessageReplyMarkup();
            editMessage.setChatId(chatId);
            editMessage.setMessageId(messageId);

            AnswerCallbackQuery answerCallbackQuery = new AnswerCallbackQuery(update.getCallbackQuery().getId());

            switch (inputQueryMessage) {
                case ("bank") -> BankMenu.sendChoiceBankMessage(sendMessage, settings.get(chatId));
                case ("decimals") -> DecimalMenu.sendChoiceDecimalsMessage(sendMessage, settings.get(chatId));
                case ("currencies") -> CurrencyMenu.sendChoiceCurrenciesMessage(sendMessage, settings.get(chatId));
                case ("USD"), ("EUR") -> {
                    List<Currencies> choicesCurrenciesNow = new ArrayList<>(settings.get(chatId).getChoicesCurrencies());
                    Currencies newCurrency = Currencies.valueOf(inputQueryMessage);

                    if (choicesCurrenciesNow.contains(newCurrency)) {
                        if (choicesCurrenciesNow.size() > 1) {
                            choicesCurrenciesNow.remove(newCurrency);
                        }
                    } else {
                        choicesCurrenciesNow.add(newCurrency);
                    }

                    boolean isNewSetting = SettingsKeyboardsUtils.isThisNewSetting(choicesCurrenciesNow.toString(), settings.get(chatId));
                    new SettingsKeyboardsUtils().sendAnswerCallbackQuery(answerCallbackQuery, isNewSetting);
                    settings.get(chatId).setChoicesCurrencies(choicesCurrenciesNow);

                    if (isNewSetting) {
                        editMessage.setReplyMarkup(CurrencyMenu.getChoiceCurrenciesKeyBoard(settings.get(chatId)));
                        sendNextEditMessage(editMessage);
                        SettingUtils.writeUserSettings(settings.get(chatId));
                    }
                }
                case ("2"), ("3"), ("4") -> {
                    boolean isNewSetting = SettingsKeyboardsUtils.isThisNewSetting(inputQueryMessage, settings.get(chatId));
                    new SettingsKeyboardsUtils().sendAnswerCallbackQuery(answerCallbackQuery, isNewSetting);
                    settings.get(chatId).setNumberOfDecimal(Integer.parseInt(inputQueryMessage));

                    if (isNewSetting) {
                        editMessage.setReplyMarkup(DecimalMenu.getChoiceDecimalsKeyBoard(settings.get(chatId)));
                        sendNextEditMessage(editMessage);
                        SettingUtils.writeUserSettings(settings.get(chatId));
                    }
                }
                case ("NBUBank"), ("PrivatBank"), ("MonoBank") -> {
                    boolean isNewSetting = SettingsKeyboardsUtils.isThisNewSetting(inputQueryMessage, settings.get(chatId));

                    new SettingsKeyboardsUtils().sendAnswerCallbackQuery(answerCallbackQuery, isNewSetting);
                    settings.get(chatId).setBank(inputQueryMessage);

                    if (isNewSetting) {
                        editMessage.setReplyMarkup(BankMenu.getChoiceBankKeyBoard(settings.get(chatId)));
                        sendNextEditMessage(editMessage);
                        SettingUtils.writeUserSettings(settings.get(chatId));
                    }
                }
                case ("reminders") -> RemindersMenu.sendChoiceReminderMessage(sendMessage, settings.get(chatId));
                case ("9"), ("10"), ("11"), ("12"), ("13"), ("14"), ("15"), ("16"), ("17"), ("18") -> {

                    String cronExpression = "0 0 " + inputQueryMessage + " * * ?";
                    ReminderTimer.startTimer(cronExpression, chatId);

                    boolean isNewSetting = SettingsKeyboardsUtils.isThisNewSetting(inputQueryMessage, settings.get(chatId));

                    new SettingsKeyboardsUtils().sendAnswerCallbackQuery(answerCallbackQuery, isNewSetting);
                    settings.get(chatId).setReminderTime(Integer.parseInt(inputQueryMessage));
                    settings.get(chatId).setReminderStarted(true);

                    if (isNewSetting) {
                        editMessage.setReplyMarkup(RemindersMenu.getChoiceReminderKeyBoard(settings.get(chatId)));
                        sendNextEditMessage(editMessage);
                        SettingUtils.writeUserSettings(settings.get(chatId));
                    }
                }
                case ("OffReminder") -> {
                    boolean isNewSetting = SettingsKeyboardsUtils.isThisNewSetting("false", settings.get(chatId));

                    new SettingsKeyboardsUtils().sendAnswerCallbackQuery(answerCallbackQuery, isNewSetting);
                    settings.get(chatId).setReminderStarted(false);
                    settings.get(chatId).setReminderTime(0);

                    if (isNewSetting) {
                        editMessage.setReplyMarkup(RemindersMenu.getChoiceReminderKeyBoard(settings.get(chatId)));
                        sendNextEditMessage(editMessage);
                        SettingUtils.writeUserSettings(settings.get(chatId));
                        ReminderTimer.stopTimer(chatId);
                    }
                }
                default -> {
                    sendMessage.setText("Немає обробки цієї кнопки: " + update.getCallbackQuery().getData());
                    sendNextMessage(sendMessage);
                }
            }
        }
    }

    public void sendNextMessage(SendMessage message) {
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void sendNextQuery(AnswerCallbackQuery answerCallbackQuery) {
        try {
            execute(answerCallbackQuery);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void sendNextEditMessage(EditMessageReplyMarkup editMessage) {
        try {
            execute(editMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getBotUsername() {
        return MyTelBotConst.MY_TEL_BOT_NAME;
    }

    @Override
    public String getBotToken() {
        return MyTelBotConst.MY_TEL_BOT_TOKEN;
    }
}
