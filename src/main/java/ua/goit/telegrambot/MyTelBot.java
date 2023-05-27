package ua.goit.telegrambot;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ua.goit.banks.Banks;
import ua.goit.banks.BankFactory;
import ua.goit.userssetting.ChatBotSettings;
import ua.goit.userssetting.SettingUtils;

import java.util.List;
import java.util.ArrayList;

import static ua.goit.telegrambot.KeyboardBuilder.getReminderKeyboard;
import static ua.goit.telegrambot.KeyboardBuilder.getSimpleKeyboard;

public class MyTelBot extends TelegramLongPollingBot {

    private final ChatBotSettings userSettings;
    private final ReminderTimer secondThreadReminderTime;
    private final List<String> choicesCurrencies;

    public MyTelBot() {
        choicesCurrencies = new ArrayList<>();
        userSettings = new ChatBotSettings();
        secondThreadReminderTime = new ReminderTimer(this);
        choicesCurrencies.add(userSettings.getChoicesCurrencies().get(0).toString());
        }

    public ChatBotSettings getUserSettings() {
        return userSettings;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            Message message = update.getMessage();
            Long chatId = update.getMessage().getChatId();

            if (message.hasText()) {
                String text = message.getText();
                SendMessage sendMessage = new SendMessage();

                sendMessage.setChatId(String.valueOf(chatId));

                if (text.equals("/start")) {
                    sendNextMessage(sendHelloMessage(chatId));
                    userSettings.setChatId(chatId);
                } else if (text.equals("Отримати інфо")) {
                    sendMessage.setText(SettingUtils.getCurrentData(userSettings));
                    sendNextMessage(sendMessage);
                } else if (text.equals("Налаштування")) {
                    sendNextMessage(sendChoiceOptionsMessage(sendMessage));
                } else if (text.equals("/end")) {
                    sendNextMessage(sendEndMessage(chatId));
                    System.exit(0);
                }
            }

        } else if (update.hasCallbackQuery()) {
//            Нужно убрать следующую строку перед финишем.
            System.out.println("id user= " + update.getCallbackQuery().getMessage().getChatId() + "  ");
            String inputQueryMessage = String.valueOf(update.getCallbackQuery().getData());
            SendMessage sendMessage = new SendMessage();

            sendMessage.setChatId(String.valueOf(update.getCallbackQuery().getMessage().getChatId()));

            switch (inputQueryMessage) {
                case ("bank") -> sendNextMessage(sendChoiceBankMessage(sendMessage));
                case ("decimals") -> sendNextMessage(sendChoiceDecimalsMessage(sendMessage));
                case ("currencies") -> sendNextMessage(sendChoiceCurrenciesMessage(sendMessage));
                case ("USD"), ("EUR") -> {
                    if (choicesCurrencies.contains(inputQueryMessage)) {
                        if (choicesCurrencies.size() > 1) {
                            choicesCurrencies.remove(inputQueryMessage);
                        }
                    } else {
                        choicesCurrencies.add(inputQueryMessage);
                    }
                }
                case ("2"), ("3"), ("4") -> {
                    sendNextMessage(sendUpdatedSettingMessage(sendMessage, inputQueryMessage));
                    userSettings.setNumberOfDecimal(Integer.parseInt(inputQueryMessage));
                }
                case ("NBUBank"), ("PrivatBank"), ("MonoBank") -> {
                    Banks newBank = BankFactory.getBank(inputQueryMessage);
                    sendNextMessage(sendUpdatedSettingMessage(sendMessage, inputQueryMessage));
                    userSettings.setBank(newBank);
                }
                case ("reminders") -> sendNextMessage(sendChoiceReminderMessage(sendMessage));
                case ("9"), ("10"), ("11"), ("12"), ("13"), ("14"), ("15"), ("16"), ("17"), ("18") -> {
                    sendNextMessage(sendUpdatedSettingMessage(sendMessage, inputQueryMessage));
                    userSettings.setReminderTime(Integer.parseInt(inputQueryMessage));
                    userSettings.setReminderStarted(true);
                    userSettings.setChatId(update.getCallbackQuery().getMessage().getChatId());
                    secondThreadReminderTime.start();
                }
                case ("OffReminder") -> {
                    sendNextMessage(sendUpdatedSettingMessage(sendMessage, "false"));
                    userSettings.setReminderStarted(false);
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

    private SendMessage sendHelloMessage(long chatId) {
        SendMessage sendMessage = new SendMessage();
        ReplyKeyboardMarkup replyKeyboardMarkup = getDefaultKeyBoard();

        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText("Ласкаво просимо. Цей бот допоможе відслідковувати актуальні курси валют");
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        return sendMessage;
    }

    private SendMessage sendChoiceOptionsMessage(SendMessage sendMessage) {
        InlineKeyboardMarkup inlineKeyboardMarkup = getChoiceOptionsKeyBoard();

        sendMessage.setText("Налаштування");
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);
        return sendMessage;
    }

    private SendMessage sendChoiceDecimalsMessage(SendMessage sendMessage) {
        InlineKeyboardMarkup inlineKeyboardMarkup = getChoiceDecimalsKeyBoard();

        sendMessage.setText("Виберіть кількість знаків після коми:");
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);
        return sendMessage;
    }

    private SendMessage sendChoiceCurrenciesMessage(SendMessage sendMessage) {
        InlineKeyboardMarkup inlineKeyboardMarkup = getChoiceCurrenciesKeyBoard();

        sendMessage.setText("Виберіть валюту:");
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);
        return sendMessage;
    }

    private SendMessage sendEndMessage(long chatId) {
        SendMessage sendEndMessage = new SendMessage();
        sendEndMessage.setChatId(String.valueOf(chatId));
        sendEndMessage.setText("До зустрічі!");

        return sendEndMessage;
    }

    private SendMessage sendChoiceBankMessage(SendMessage sendMessage) {
        InlineKeyboardMarkup inlineKeyboardMarkup = getChoiceBankKeyBoard();

        sendMessage.setText("Виберіть банк:");
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);
        return sendMessage;
    }

    private SendMessage sendChoiceReminderMessage(SendMessage sendMessage) {
        InlineKeyboardMarkup inlineKeyboardMarkup = getChoiceReminderKeyBoard();

        sendMessage.setText("Оберіть час сповіщення:");
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);
        return sendMessage;
    }

    private SendMessage sendUpdatedSettingMessage(SendMessage sendMessage, String inputQueryMessage) {
        ReplyKeyboardMarkup replyKeyboardMarkup = getDefaultKeyBoard();
        String bank = userSettings.getBank().getName();
        String numberOfDecimal = String.valueOf(userSettings.getNumberOfDecimal());
        String currencies = userSettings.getChoicesCurrencies().toString();
        String reminderTime = String.valueOf(userSettings.getReminderTime());
        String reminderStarted = String.valueOf(userSettings.isReminderStarted());

        sendMessage.setReplyMarkup(replyKeyboardMarkup);

        if (bank.equals(inputQueryMessage) || numberOfDecimal.equals(inputQueryMessage) ||
                currencies.equals(inputQueryMessage) || reminderTime.equals(inputQueryMessage) ||
                reminderStarted.equals(inputQueryMessage)) {
            sendMessage.setText("Ці налаштування вже встановлені.");

            return sendMessage;
        }
        sendMessage.setText("Налаштування оновлені.");

        return sendMessage;
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
    private InlineKeyboardMarkup getChoiceOptionsKeyBoard(){
        String[] names = new String[]{"Знаки після коми", "Банк", "Валюти", "Час сповіщень"};
        String[] keys = new String[]{"decimals", "bank", "currencies", "reminders"};

        return getSimpleKeyboard(names, keys);
    }
    private InlineKeyboardMarkup getChoiceDecimalsKeyBoard(){
        String[] names = new String[]{"2", "3", "4"};
        String[] keys = new String[]{"2", "3", "4"};

        return getSimpleKeyboard(names, keys);
    }
    private InlineKeyboardMarkup getChoiceBankKeyBoard() {
        String[] names = new String[]{"Національний банк України", "Приват банк", "mono bank"};
        String[] keys = new String[]{"NBUBank", "PrivatBank", "MonoBank"};

        return getSimpleKeyboard(names, keys);
    }
    private InlineKeyboardMarkup getChoiceCurrenciesKeyBoard(){
        String[] names = new String[]{"Євро", "Американський долар"};
        String[] keys = new String[]{"EUR", "USD"};

        return getSimpleKeyboard(names, keys);
    }
    private InlineKeyboardMarkup getChoiceReminderKeyBoard(){
        String[] names = new String[]{"9:00","10:00","11:00","12:00","13:00","14:00","15:00","16:00","17:00","18:00","Вимкнути сповіщення"};
        String[] keys = new String[]{"9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "OffReminder"};

        return getReminderKeyboard(names, keys);
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
