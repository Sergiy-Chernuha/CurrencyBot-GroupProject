package ua.goit.telegrambot;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import ua.goit.banks.Currencies;
import ua.goit.userssetting.ChatBotSettings;
import ua.goit.userssetting.SettingUtils;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

public class MyTelBot extends TelegramLongPollingBot {

    static Map<Long, ChatBotSettings> settings = new HashMap<>();
    Map<Long, ReminderTimer> timers = new HashMap<>();

    public MyTelBot() {
    }

    public ChatBotSettings getUserSetting(Long chatId) {
        return settings.get(chatId);
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            Message message = update.getMessage();
            Long chatId = update.getMessage().getChatId();
            new SettingsKeyboardsUtils().updateSettings(chatId);

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

            new SettingsKeyboardsUtils().updateSettings(chatId);

            String inputQueryMessage = String.valueOf(update.getCallbackQuery().getData());

            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(chatId);

            EditMessageReplyMarkup editMessage = new EditMessageReplyMarkup();
            editMessage.setChatId(chatId);
            editMessage.setMessageId(messageId);

            AnswerCallbackQuery answerCallbackQuery = new AnswerCallbackQuery(update.getCallbackQuery().getId());

            switch (inputQueryMessage) {
                case ("bank") -> new BankSettings().sendChoiceBankMessage(sendMessage, chatId);
                case ("decimals") -> new DecimalsSettings().sendChoiceDecimalsMessage(sendMessage, chatId);
                case ("currencies") -> new CurrencySettings().sendChoiceCurrenciesMessage(sendMessage, chatId);
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

                    boolean isNewSetting = new SettingsKeyboardsUtils().isThisNewSetting(choicesCurrenciesNow.toString(), chatId);
                    new SettingsKeyboardsUtils().sendAnswerCallbackQuery(answerCallbackQuery, isNewSetting);
                    settings.get(chatId).setChoicesCurrencies(choicesCurrenciesNow);

                    if (isNewSetting) {
                        editMessage.setReplyMarkup(new CurrencySettings().getChoiceCurrenciesKeyBoard(chatId));
                        sendNextEditMessage(editMessage);
                        SettingUtils.writeUserSettings(settings.get(chatId));
                    }
                }
                case ("2"), ("3"), ("4") -> {
                    boolean isNewSetting = new SettingsKeyboardsUtils().isThisNewSetting(inputQueryMessage, chatId);
                    new SettingsKeyboardsUtils().sendAnswerCallbackQuery(answerCallbackQuery, isNewSetting);
                    settings.get(chatId).setNumberOfDecimal(Integer.parseInt(inputQueryMessage));

                    if (isNewSetting) {
                        editMessage.setReplyMarkup(new DecimalsSettings().getChoiceDecimalsKeyBoard(chatId));
                        sendNextEditMessage(editMessage);
                        SettingUtils.writeUserSettings(settings.get(chatId));
                    }
                }
                case ("NBUBank"), ("PrivatBank"), ("MonoBank") -> {
                    boolean isNewSetting = new SettingsKeyboardsUtils().isThisNewSetting(inputQueryMessage, chatId);

                    new SettingsKeyboardsUtils().sendAnswerCallbackQuery(answerCallbackQuery, isNewSetting);
                    settings.get(chatId).setBank(inputQueryMessage);

                    if (isNewSetting) {
                        editMessage.setReplyMarkup(new BankSettings().getChoiceBankKeyBoard(chatId));
                        sendNextEditMessage(editMessage);
                        SettingUtils.writeUserSettings(settings.get(chatId));
                    }
                }
                case ("reminders") -> new RemindersSettings().sendChoiceReminderMessage(sendMessage, chatId);
                case ("9"), ("10"), ("11"), ("12"), ("13"), ("14"), ("15"), ("16"), ("17"), ("18") -> {
                    if (settings.get(chatId).isReminderStarted()) {
                        timers.get(chatId).stopTimer();
                    }

                    String cronExpression = "0 0 " + inputQueryMessage + " * * ?";
                    timers.put(chatId, new ReminderTimer(this, chatId));
                    timers.get(chatId).startTimer(cronExpression);

                    boolean isNewSetting = new SettingsKeyboardsUtils().isThisNewSetting(inputQueryMessage, chatId);

                    new SettingsKeyboardsUtils().sendAnswerCallbackQuery(answerCallbackQuery, isNewSetting);
                    settings.get(chatId).setReminderTime(Integer.parseInt(inputQueryMessage));
                    settings.get(chatId).setReminderStarted(true);

                    if (isNewSetting) {
                        editMessage.setReplyMarkup(new RemindersSettings().getChoiceReminderKeyBoard(chatId));
                        sendNextEditMessage(editMessage);
                        SettingUtils.writeUserSettings(settings.get(chatId));
                    }
                }
                case ("OffReminder") -> {
                    if (settings.get(chatId).isReminderStarted()) {
                        timers.get(chatId).stopTimer();
                        timers.remove(chatId);
                    }
                    boolean isNewSetting = new SettingsKeyboardsUtils().isThisNewSetting("false", chatId);

                    new SettingsKeyboardsUtils().sendAnswerCallbackQuery(answerCallbackQuery, isNewSetting);
                    settings.get(chatId).setReminderStarted(false);

                    if (isNewSetting) {
                        editMessage.setReplyMarkup(new RemindersSettings().getChoiceReminderKeyBoard(chatId));
                        sendNextEditMessage(editMessage);
                        SettingUtils.writeUserSettings(settings.get(chatId));
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
