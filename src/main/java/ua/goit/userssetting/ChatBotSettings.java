package ua.goit.userssetting;

import ua.goit.banks.Currencies;

import java.util.List;

public class ChatBotSettings {

    private final Long chatId;
    private int numberOfDecimal = 2;
    private String bank = "PrivatBank";
    private List<Currencies> choicesCurrencies = List.of(Currencies.USD);
    private int reminderTime = 0;
    private boolean reminderStarted = false;

    public ChatBotSettings(Long chatId) {
        this.chatId = chatId;
    }

    public Long getChatId() {
        return chatId;
    }

    public int getNumberOfDecimal() {
        return numberOfDecimal;
    }

    public void setNumberOfDecimal(int numberOfDecimal) {
        this.numberOfDecimal = numberOfDecimal;
    }

    public String getBank() {
        return bank;
    }

    public void setBank(String bank) {
        this.bank = bank;
    }

    public List<Currencies> getChoicesCurrencies() {
        return choicesCurrencies;
    }

    public void setChoicesCurrencies(List<Currencies> choicesCurrencies) {
        this.choicesCurrencies = choicesCurrencies;
    }

    public int getReminderTime() {
        return reminderTime;
    }

    public void setReminderTime(int reminderTime) {
        this.reminderTime = reminderTime;
    }

    public boolean isReminderStarted() {
        return reminderStarted;
    }

    public void setReminderStarted(boolean reminderStarted) {
        this.reminderStarted = reminderStarted;
    }
}
