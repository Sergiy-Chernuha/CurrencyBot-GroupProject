package ua.goit.telegrambot;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import ua.goit.banks.Currencies;
import ua.goit.userssetting.ChatBotSettings;
import ua.goit.userssetting.SettingUtils;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

public class MyTelBot extends TelegramLongPollingBot {

    Map<Long, ChatBotSettings> settings = new HashMap<>();
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
            updateSettings(chatId);

            if (message.hasText()) {
                String text = message.getText();
                SendMessage sendMessage = new SendMessage();

                sendMessage.setChatId(String.valueOf(chatId));

                switch (text) {
                    case "/start" -> sendNextMessage(sendHelloMessage(chatId));
                    case "Отримати інфо" -> {
                        sendMessage.setText(SettingUtils.getCurrentData(settings.get(chatId)));
                        sendNextMessage(sendMessage);
                    }
                    case "Налаштування" -> sendChoiceOptionsMessage(sendMessage);
//                    DELETE
                    case "/end" -> {
                        sendNextMessage(sendEndMessage(chatId));
                        System.exit(0);
                    }
                }
            }
        } else if (update.hasCallbackQuery()) {
            Long chatId = update.getCallbackQuery().getMessage().getChatId();
            Integer messageId = update.getCallbackQuery().getMessage().getMessageId();

            updateSettings(chatId);
            //            Нужно убрать следующую строку перед финишем.
            System.out.println("id user= " + chatId + "  ");
            String inputQueryMessage = String.valueOf(update.getCallbackQuery().getData());

            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(chatId);

            EditMessageReplyMarkup editMessage = new EditMessageReplyMarkup();
            editMessage.setChatId(chatId);
            editMessage.setMessageId(messageId);

            AnswerCallbackQuery answerCallbackQuery = new AnswerCallbackQuery(update.getCallbackQuery().getId());

            switch (inputQueryMessage) {
                case ("bank") -> sendChoiceBankMessage(sendMessage, chatId);
                case ("decimals") -> sendChoiceDecimalsMessage(sendMessage, chatId);
                case ("currencies") -> sendChoiceCurrenciesMessage(sendMessage, chatId);
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

                    boolean isNewSetting = isThisNewSetting(choicesCurrenciesNow.toString(), chatId);
                    sendAnswerCallbackQuery(answerCallbackQuery, isNewSetting);
                    settings.get(chatId).setChoicesCurrencies(choicesCurrenciesNow);

                    if (isNewSetting) {
                        editMessage.setReplyMarkup(getChoiceCurrenciesKeyBoard(chatId));
                        sendNextEditMessage(editMessage);
                        SettingUtils.writeUserSettings(settings.get(chatId));
                    }
                }
                case ("2"), ("3"), ("4") -> {
                    boolean isNewSetting = isThisNewSetting(inputQueryMessage, chatId);
                    sendAnswerCallbackQuery(answerCallbackQuery, isNewSetting);
                    settings.get(chatId).setNumberOfDecimal(Integer.parseInt(inputQueryMessage));

                    if (isNewSetting) {
                        editMessage.setReplyMarkup(getChoiceDecimalsKeyBoard(chatId));
                        sendNextEditMessage(editMessage);
                        SettingUtils.writeUserSettings(settings.get(chatId));
                    }
                }
                case ("NBUBank"), ("PrivatBank"), ("MonoBank") -> {
//                    Banks newBank = BankFactory.getBank(inputQueryMessage);
                    boolean isNewSetting = isThisNewSetting(inputQueryMessage, chatId);

                    sendAnswerCallbackQuery(answerCallbackQuery, isNewSetting);
                    settings.get(chatId).setBank(inputQueryMessage);

                    if (isNewSetting) {
                        editMessage.setReplyMarkup(getChoiceBankKeyBoard(chatId));
                        sendNextEditMessage(editMessage);
                        SettingUtils.writeUserSettings(settings.get(chatId));
                    }
                }
                case ("reminders") -> sendChoiceReminderMessage(sendMessage, chatId);
                case ("9"), ("10"), ("11"), ("12"), ("13"), ("14"), ("15"), ("16"), ("17"), ("18") -> {
                    if (settings.get(chatId).isReminderStarted()) {
                        timers.get(chatId).stopTimer();
                    }

//                    String cronExpression = "0 0 " + inputQueryMessage + " * * ?";
                    String cronExpression = "0/" + inputQueryMessage + " * * * * ?";
                    timers.put(chatId, new ReminderTimer(this, chatId));

                    timers.get(chatId).startTimer(cronExpression);
//
                    System.out.println(settings.get(chatId).getReminderTime());

                    boolean isNewSetting = isThisNewSetting(inputQueryMessage, chatId);

                    sendAnswerCallbackQuery(answerCallbackQuery, isNewSetting);
                    settings.get(chatId).setReminderTime(Integer.parseInt(inputQueryMessage));
                    settings.get(chatId).setReminderStarted(true);

                    if (isNewSetting) {
                        editMessage.setReplyMarkup(getChoiceReminderKeyBoard(chatId));
                        sendNextEditMessage(editMessage);
                        SettingUtils.writeUserSettings(settings.get(chatId));
                    }
                }
                case ("OffReminder") -> {
                    if (settings.get(chatId).isReminderStarted()) {
                        timers.get(chatId).stopTimer();
                        timers.remove(chatId);
                    }
                    boolean isNewSetting = isThisNewSetting("false", chatId);

                    sendAnswerCallbackQuery(answerCallbackQuery, isNewSetting);
                    settings.get(chatId).setReminderStarted(false);

                    if (isNewSetting) {
                        editMessage.setReplyMarkup(getChoiceReminderKeyBoard(chatId));
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

    private SendMessage sendHelloMessage(long chatId) {
        SendMessage sendMessage = new SendMessage();
        ReplyKeyboardMarkup replyKeyboardMarkup = getDefaultKeyBoard();

        sendMessage.setChatId(chatId);
        sendMessage.setText("Ласкаво просимо. Цей бот допоможе відслідковувати актуальні курси валют");
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        return sendMessage;
    }

    private SendMessage sendEndMessage(long chatId) {
        SendMessage sendEndMessage = new SendMessage();
        sendEndMessage.setChatId(chatId);
        sendEndMessage.setText("До зустрічі!");

        return sendEndMessage;
    }

    private void sendChoiceOptionsMessage(SendMessage sendMessage) {
        InlineKeyboardMarkup inlineKeyboardMarkup = getChoiceOptionsKeyBoard();

        sendMessage.setText("Налаштування");
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);
        sendNextMessage(sendMessage);
    }

    private void sendChoiceDecimalsMessage(SendMessage sendMessage, Long chatId) {
        InlineKeyboardMarkup inlineKeyboardMarkup = getChoiceDecimalsKeyBoard(chatId);

        sendMessage.setText("Виберіть кількість знаків після коми:");
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);
        sendNextMessage(sendMessage);
    }

    private void sendChoiceCurrenciesMessage(SendMessage sendMessage, Long chatId) {
        InlineKeyboardMarkup inlineKeyboardMarkup = getChoiceCurrenciesKeyBoard(chatId);

        sendMessage.setText("Виберіть валюту:");
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);
        sendNextMessage(sendMessage);
    }

    private void sendChoiceBankMessage(SendMessage sendMessage, Long chatId) {
        InlineKeyboardMarkup inlineKeyboardMarkup = getChoiceBankKeyBoard(chatId);

        sendMessage.setText("Виберіть банк:");
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);
        sendNextMessage(sendMessage);
    }

    private void sendChoiceReminderMessage(SendMessage sendMessage, Long chatId) {
        InlineKeyboardMarkup inlineKeyboardMarkup = getChoiceReminderKeyBoard(chatId);

        sendMessage.setText("Оберіть час сповіщення:");
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);
        sendNextMessage(sendMessage);
    }

    private boolean isThisNewSetting(String inputQueryMessage, Long chatId) {
        String bank = settings.get(chatId).getBank();
        String numberOfDecimal = String.valueOf(settings.get(chatId).getNumberOfDecimal());
        String currencies = settings.get(chatId).getChoicesCurrencies().toString();
        String reminderTime = String.valueOf(settings.get(chatId).getReminderTime());
        String reminderStarted = String.valueOf(settings.get(chatId).isReminderStarted());

        return !bank.equals(inputQueryMessage) && !numberOfDecimal.equals(inputQueryMessage) &&
                !currencies.equals(inputQueryMessage) && !reminderTime.equals(inputQueryMessage) &&
                !reminderStarted.equals(inputQueryMessage);
    }

    private void sendAnswerCallbackQuery(AnswerCallbackQuery answerCallbackQuery, boolean isNewSetting) {
        String callBackAnswer = isNewSetting ? "Налаштування оновлені." : "Ці налаштування вже встановлені.";

        answerCallbackQuery.setText(callBackAnswer);
        answerCallbackQuery.setShowAlert(false);
        answerCallbackQuery.setCacheTime(0);

        sendNextQuery(answerCallbackQuery);
    }

    private ReplyKeyboardMarkup getDefaultKeyBoard() {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setResizeKeyboard(true);
        List<KeyboardRow> keyboard = new ArrayList<>();

        KeyboardRow row1 = new KeyboardRow();
        KeyboardButton button1 = new KeyboardButton("Отримати інфо");
        row1.add(button1);

        KeyboardRow row2 = new KeyboardRow();
        KeyboardButton button2 = new KeyboardButton("Налаштування");
        row1.add(button2);

        keyboard.add(row1);
        keyboard.add(row2);

        replyKeyboardMarkup.setKeyboard(keyboard);

        return replyKeyboardMarkup;
    }

    private InlineKeyboardMarkup getChoiceBankKeyBoard(Long chatId) {
        String bankNow = settings.get(chatId).getBank();

        String button1Name = (bankNow.equals("NBUBank")) ? "✅ Національний банк України" : "Національний банк України";
        String callback1 = "NBUBank";

        String button2Name = (bankNow.equals("PrivatBank")) ? "✅ Приват Банк" : "Приват Банк";
        String callback2 = "PrivatBank";

        String button3Name = (bankNow.equals("MonoBank")) ? "✅ МоноБанк" : "МоноБанк";
        String callback3 = "MonoBank";

        String[] names = new String[]{button1Name, button2Name, button3Name};
        String[] keys = new String[]{callback1, callback2, callback3};

        return KeyboardBuilder.getSimpleKeyboard(names, keys);
    }

    private InlineKeyboardMarkup getChoiceDecimalsKeyBoard(Long chatId) {
        String button1Name = (settings.get(chatId).getNumberOfDecimal() == 2) ? "✅ 2" : "2";
        String Callback1 = "2";

        String button2Name = (settings.get(chatId).getNumberOfDecimal() == 3) ? "✅ 3" : "3";
        String Callback2 = "3";

        String button3Name = (settings.get(chatId).getNumberOfDecimal() == 4) ? "✅ 4" : "4";
        String Callback3 = "4";

        String[] names = new String[]{button1Name, button2Name, button3Name};
        String[] keys = new String[]{Callback1, Callback2, Callback3};

        return KeyboardBuilder.getSimpleKeyboard(names, keys);
    }

    private InlineKeyboardMarkup getChoiceOptionsKeyBoard() {
        String[] names = new String[]{"Знаки після коми", "Банк", "Валюти", "Час сповіщень"};
        String[] keys = new String[]{"decimals", "bank", "currencies", "reminders"};

        return KeyboardBuilder.getSimpleKeyboard(names, keys);
    }

    private InlineKeyboardMarkup getChoiceCurrenciesKeyBoard(Long chatId) {
        List<Currencies> choicesCurrenciesNow = settings.get(chatId).getChoicesCurrencies();

        String button1Name = (choicesCurrenciesNow.contains(Currencies.EUR)) ? "✅ Євро" : "Євро";
        String Callback1 = "EUR";

        String button2Name = (choicesCurrenciesNow.contains(Currencies.USD)) ? "✅ Американський долар" : "Американський долар";
        String Callback2 = "USD";

        String[] names = new String[]{button1Name, button2Name};
        String[] keys = new String[]{Callback1, Callback2};

        return KeyboardBuilder.getSimpleKeyboard(names, keys);
    }

    private InlineKeyboardMarkup getChoiceReminderKeyBoard(Long chatId) {
        String button1Name = (settings.get(chatId).getReminderTime() == 9 && settings.get(chatId).isReminderStarted()) ? "✅ 9:00" : "9:00";
        String Callback1 = "9";
        String button2Name = (settings.get(chatId).getReminderTime() == 10 && settings.get(chatId).isReminderStarted()) ? "✅ 10:00" : "10:00";
        String Callback2 = "10";
        String button3Name = (settings.get(chatId).getReminderTime() == 11 && settings.get(chatId).isReminderStarted()) ? "✅ 11:00" : "11:00";
        String Callback3 = "11";

        String button4Name = (settings.get(chatId).getReminderTime() == 12 && settings.get(chatId).isReminderStarted()) ? "✅ 12:00" : "12:00";
        String Callback4 = "12";
        String button5Name = (settings.get(chatId).getReminderTime() == 13 && settings.get(chatId).isReminderStarted()) ? "✅ 13:00" : "13:00";
        String Callback5 = "13";
        String button6Name = (settings.get(chatId).getReminderTime() == 14 && settings.get(chatId).isReminderStarted()) ? "✅ 14:00" : "14:00";
        String Callback6 = "14";

        String button7Name = (settings.get(chatId).getReminderTime() == 15 && settings.get(chatId).isReminderStarted()) ? "✅ 15:00" : "15:00";
        String Callback7 = "15";
        String button8Name = (settings.get(chatId).getReminderTime() == 16 && settings.get(chatId).isReminderStarted()) ? "✅ 16:00" : "16:00";
        String Callback8 = "16";
        String button9Name = (settings.get(chatId).getReminderTime() == 17 && settings.get(chatId).isReminderStarted()) ? "✅ 17:00" : "17:00";
        String Callback9 = "17";

        String button10Name = (settings.get(chatId).getReminderTime() == 18 && settings.get(chatId).isReminderStarted()) ? "✅ 18:00" : "18:00";
        String Callback10 = "18";
        String button11Name = !settings.get(chatId).isReminderStarted() ? "✅ Вимкнути сповіщення" : "Вимкнути сповіщення";
        String Callback11 = "OffReminder";

        String[] names = new String[]{button1Name, button2Name, button3Name, button4Name, button5Name, button6Name
                , button7Name, button8Name, button9Name, button10Name, button11Name};
        String[] keys = new String[]{Callback1, Callback2, Callback3, Callback4, Callback5, Callback6
                , Callback7, Callback8, Callback9, Callback10, Callback11};

        return KeyboardBuilder.getReminderKeyboard(names, keys);
    }

    private void updateSettings(Long chatId) {
        if (!settings.containsKey(chatId)) {
            URL url = MyTelBot.class.getResource("/Users/User" + chatId + ".json");
//
            System.out.println("check resource");
            if (url != null) {
//
                System.out.println("read from resource");
                ChatBotSettings settingFromResource = SettingUtils.readUserSetting(chatId);


                if (settingFromResource.isReminderStarted()) {
                    ReminderTimer restartTimer = new ReminderTimer(this, chatId);

                    restartTimer.startTimer("0/" + settingFromResource.getReminderTime() + " * * * * ?");
                    timers.put(chatId, restartTimer);
                        }else {
                    settingFromResource.setReminderTime(0);
                }

                settings.put(chatId, settingFromResource);

            } else {
//
                System.out.println("make new user");

                settings.put(chatId, new ChatBotSettings(chatId));
            }
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
